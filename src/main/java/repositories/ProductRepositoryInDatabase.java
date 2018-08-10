package repositories;

import com.google.gson.JsonObject;
import data.Product;
import exceptions.AlreadyDefinedProductException;
import exceptions.NoSuchProductException;
import util.Utilities;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import java.sql.*;
import java.util.*;

@Singleton
public class ProductRepositoryInDatabase implements ProductRepository {

    private static final String VIOLATED_UNIQUENESS = "23505";

    private Connection connection;



    public ProductRepositoryInDatabase(){
        try {
            String env = System.getenv("VCAP_SERVICES");
            Class.forName("org.postgresql.Driver");
            if(env != null){
                JsonObject object = Utilities.parseCloudFoundryDatabaseCredentials(env);
                connection = DriverManager.getConnection("jdbc:postgresql://" + object.get("hostname").getAsString() + ":" + object.get("port").getAsString() + "/" + object.get("dbname").getAsString() + "?sslmode=disable",
                        object.get("username").getAsString(), object.get("password").getAsString());
            } else {
                String host = System.getenv("DATABASE_HOST");
                String port = System.getenv("DATABASE_PORT");
                connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/postgres?sslmode=disable", "postgres", "postgres");
            }
        } catch (ClassNotFoundException | SQLException e){
            throw new RuntimeException(e);
        }

    }

    public ProductRepositoryInDatabase(String host, String port){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/postgres", "postgres", "postgres");

        } catch (ClassNotFoundException | SQLException e){
            throw new RuntimeException(e);
        }

    }

    @PreDestroy
    public void preDestroy() throws SQLException {
        connection.close();
    }

    @Override
    public void addProduct(Product product) throws AlreadyDefinedProductException {
        try(PreparedStatement statement = connection.prepareStatement("insert into products values(?,?,?,?,?)")){
            statement.setString(1,product.getName());
            statement.setDouble(2, product.getWeight());
            statement.setString(3, product.getBrand());
            statement.setDouble(4, product.getPrice());
            statement.setString(5, product.getExpiryDate());
            statement.executeUpdate();
        } catch (SQLException e){
            if(e.getSQLState().equals(VIOLATED_UNIQUENESS)){
                throw new AlreadyDefinedProductException();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Product getProductByName(String name) throws NoSuchProductException {
        try(PreparedStatement statement = connection.prepareStatement("select * from products where name = ?")){
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                throw new NoSuchProductException();
            }
            Product result;
            result = Product.createProduct(resultSet.getString(1), resultSet.getDouble(2),
                    resultSet.getString(3), resultSet.getDouble(4), resultSet.getString(5));
            return result;
        } catch (SQLException | InvalidPropertiesFormatException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeProductByName(String name) throws NoSuchProductException {
        try(PreparedStatement statement = connection.prepareStatement("delete from products where name = ?")){
            statement.setString(1, name);
            int deleted = statement.executeUpdate();
            if(deleted == 0){
                throw new NoSuchProductException();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Product> getAllProducts() {
        Set<Product> products = new HashSet<>();
        try(PreparedStatement statement = connection.prepareStatement("select * from products")) {
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Product product = Product.createProduct(resultSet.getString(1), resultSet.getDouble(2),
                        resultSet.getString(3), resultSet.getDouble(4), resultSet.getString(5));
                products.add(product);
            }
            return products;

        } catch (SQLException | InvalidPropertiesFormatException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void cleanRepository() {
        try(PreparedStatement statement = connection.prepareStatement("delete from products")){
            statement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEmpty() {
        try (PreparedStatement statement = connection.prepareStatement("select * from products")){
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                return true;
            }
            return false;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
