package repositories;

import com.google.gson.Gson;
import data.Product;
import exceptions.AlreadyDefinedProductException;
import exceptions.NoSuchProductException;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class ProductRepositoryInFile implements ProductRepository {

    private PrintWriter printWriter;
    private FileInputStream fis;
    private BufferedReader bufferedReader;
    private Map<String, Integer> entities;
    private static Gson gson = new Gson();

    public ProductRepositoryInFile() {
        try {
            printWriter = new PrintWriter(new FileOutputStream(new File("repositoryFile"), true));
            fis = new FileInputStream("repositoryFile");
            setUpReader();
            entities = new ConcurrentHashMap<>();
            mapEntitiesFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @PreDestroy
    public void preDestroy() throws IOException {
        printWriter.close();
        bufferedReader.close();
    }

    private void truncate() throws IOException {
        printWriter.close();
        printWriter = new PrintWriter("repositoryFile");
    }

    private void setUpReader() throws IOException {
        fis.getChannel().position(0);
        bufferedReader = new BufferedReader(new InputStreamReader(fis));
    }

    private void mapEntitiesFromFile() throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null){
            entities.put(line.substring(line.indexOf(':'),line.indexOf(',')), entities.size());
        }
        setUpReader();
    }

    @Override
    public void addProduct(Product product) throws AlreadyDefinedProductException {
        if(hasProduct(product.getName())){
            throw new AlreadyDefinedProductException();
        }
        printWriter.write(gson.toJson(product) + "\n");
        printWriter.flush();
        entities.put(product.getName(), entities.size());
    }

    private boolean hasProduct(String name) {
        return entities.get(name) != null;
    }

    @Override
    public Product getProductByName(String name) throws NoSuchProductException {
        try {
            if (!hasProduct(name)) {
                throw new NoSuchProductException();
            }
            Stream<String> lines = bufferedReader.lines();
            String productInJSON = lines.skip(entities.get(name)).findFirst().get();
            setUpReader();
            return gson.fromJson(productInJSON, Product.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeProductByName(String name) throws NoSuchProductException {
        try {
            if (!hasProduct(name)) {
                throw new NoSuchProductException();
            }
            Stream<String> lines = bufferedReader.lines();
            lines.filter((s -> !entities.get(name).equals(entities.get(s)))).forEach(s -> printWriter.write(s));
            setUpReader();
            entities.remove(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Collection<Product> getAllProducts() {
        try {
            Collection<Product> result = bufferedReader.lines().map(s -> gson.fromJson(s, Product.class)).collect(Collectors.toList());
            setUpReader();
            return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanRepository() {
        try {
            truncate();
            setUpReader();
            entities.clear();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEmpty() {
        return entities.isEmpty();
    }
}

