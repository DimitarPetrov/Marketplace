package repositories;

import data.Product;
import exceptions.AlreadyDefinedProductException;
import exceptions.NoSuchProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class RepositoryTestBase<T extends ProductRepository> {

    protected T repo = createInstance();
    protected static Product testProduct = new Product("test", 1, "test", 1, "1/1/1999");

    protected abstract T createInstance();

    @BeforeEach
    public void cleanUpRepository() {
        repo.cleanRepository();
    }

    @Test
    public void addProductInRepositoryTest() throws AlreadyDefinedProductException, NoSuchProductException {
        repo.addProduct(testProduct);
        assertEquals(repo.getProductByName("test"), testProduct);
    }

    @Test
    public void addProductAlreadyExistsTest() throws AlreadyDefinedProductException {
        repo.addProduct(testProduct);
        assertThrows(AlreadyDefinedProductException.class, () -> repo.addProduct(testProduct));
    }

    @Test
    public void removeProductTest() throws NoSuchProductException, AlreadyDefinedProductException {
        repo.addProduct(testProduct);
        repo.removeProductByName("test");
        assertTrue(repo.isEmpty());
    }

    @Test
    public void removeNotExistingProductTest() {
        assertThrows(NoSuchProductException.class, () -> repo.removeProductByName("test"));
    }

    @Test
    public void getNotExistingProductTest() {
        assertThrows(NoSuchProductException.class, () -> repo.getProductByName("test"));
    }
}
