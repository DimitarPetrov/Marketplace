package repositories;


class ProductRepositoryInDatabaseTest extends RepositoryTestBase<ProductRepositoryInDatabase> {

    @Override
    protected ProductRepositoryInDatabase createInstance() {
        return new ProductRepositoryInDatabase("localhost", "4321");
    }
}