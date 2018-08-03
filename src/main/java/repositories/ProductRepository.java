package repositories;

import data.Product;
import exceptions.AlreadyDefinedProductException;
import exceptions.NoSuchProductException;

import java.util.Collection;

public interface ProductRepository {

    void addProduct(Product product) throws AlreadyDefinedProductException;

    Product getProductByName(String name) throws NoSuchProductException;

    void removeProductByName(String name) throws NoSuchProductException;

    Collection<Product> getAllProducts();

    void cleanRepository();

    boolean isEmpty();

}
