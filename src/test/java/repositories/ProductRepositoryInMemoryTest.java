package repositories;

class ProductRepositoryInMemoryTest extends RepositoryTestBase<ProductRepositoryInMemory> {

    protected ProductRepositoryInMemory createInstance() {
        return new ProductRepositoryInMemory();
    }

}