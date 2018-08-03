package repositories;

import data.Product;
import exceptions.AlreadyDefinedProductException;
import exceptions.NoSuchProductException;

import javax.ejb.Singleton;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ProductRepositoryInMemory implements ProductRepository {

    private Map<String, Product> products;

    public ProductRepositoryInMemory(){
        this.products = new ConcurrentHashMap<>();
    }

    public void addProduct(Product product) throws AlreadyDefinedProductException {
        if(hasProduct(product.getName())){
            throw new AlreadyDefinedProductException();
        }
        products.put(product.getName(), product);
    }

    public Product getProductByName(String name) throws NoSuchProductException {
        if(!hasProduct(name)){
            throw new NoSuchProductException();
        }
        return products.get(name);
    }

    public void removeProductByName(String name) throws NoSuchProductException {
        if(!hasProduct(name)){
            throw new NoSuchProductException();
        }
        products.remove(name);
    }

    private boolean hasProduct(String name){
        return products.get(name) != null;
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public void cleanRepository() {
        products.clear();
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }
}
