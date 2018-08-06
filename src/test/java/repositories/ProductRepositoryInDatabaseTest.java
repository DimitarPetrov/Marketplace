package repositories;

import data.Product;
import exceptions.AlreadyDefinedProductException;
import exceptions.NoSuchProductException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.sql.*;
import java.util.InvalidPropertiesFormatException;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryInDatabaseTest extends RepositoryTestBase<ProductRepositoryInDatabase> {

    @Override
    protected ProductRepositoryInDatabase createInstance() {
        return new ProductRepositoryInDatabase();
    }
}