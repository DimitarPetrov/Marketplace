package repositories;

import exceptions.AlreadyDefinedProductException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductRepositoryInFileTest extends RepositoryTestBase<ProductRepositoryInFile> {

    protected ProductRepositoryInFile createInstance() {
        return new ProductRepositoryInFile();
    }

    @Test
    public void getAllProductsTest() throws AlreadyDefinedProductException {
        repo.addProduct(testProduct);
        assertEquals(List.of(testProduct), repo.getAllProducts());
    }

}