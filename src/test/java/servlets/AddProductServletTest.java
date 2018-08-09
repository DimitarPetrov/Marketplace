package servlets;

import com.google.gson.Gson;
import data.Product;
import exceptions.AlreadyDefinedProductException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repositories.ProductRepository;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AddProductServletTest extends Mockito {

    private static final String TEST_PRODUCT_IN_JSON =
            "{\"name\":\"protein\",\"weight\":100.0,\"brand\":\"asd\",\"price\":100.0,\"expiryDate\":\"01/01/1980\"}";
    private static final String INVALID_TEST_PRODUCT_IN_JSON =
            "{\"name\":\"protein\",\"weight\":100.0,\"brand\":\"asd\",\"price\":100.0,\"expiryDate\":\"01.01.1980\"}";

    private static Product validProduct;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletInputStream servletInputStream;
    private ProductRepository repo;

    @BeforeAll
    public static void constructProducts() {
        Gson gson = new Gson();
        validProduct = gson.fromJson(TEST_PRODUCT_IN_JSON, Product.class);
    }

    @BeforeEach
    public void setUpMockedObjects() throws IOException, ServletException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        servletInputStream = mock(ServletInputStream.class);
        repo = mock(ProductRepository.class);

    }

    @Test
    public void testAddProductPostRequest() throws IOException, ServletException, AlreadyDefinedProductException {
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.readAllBytes()).thenReturn(TEST_PRODUCT_IN_JSON.getBytes());

        new AddProductServlet(repo).doPost(request,response);

        verify(repo, times(1)).addProduct(validProduct);
        verify(response, times(1)).setStatus(201);
    }

    @Test
    public void testAddInvalidFormatProductPostRequest() throws IOException, ServletException {
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.readAllBytes()).thenReturn(INVALID_TEST_PRODUCT_IN_JSON.getBytes());

        new AddProductServlet(repo).doPost(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void addAlreadyExistingProductTest() throws AlreadyDefinedProductException, IOException, ServletException {
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.readAllBytes()).thenReturn(TEST_PRODUCT_IN_JSON.getBytes());
        doThrow(AlreadyDefinedProductException.class).when(repo).addProduct(validProduct);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        new AddProductServlet(repo).doPost(request,response);

        verify(repo, times(1)).addProduct(validProduct);
        verify(response, times(1)).getWriter();
        verify(response, times(1)).setStatus(409);
        assertEquals("[{ \"error\":\"data.Product already in the store!\"}]", stringWriter.toString());
        stringWriter.close();
        printWriter.close();
    }

    @Test
    public void getAllProductsGetRequest() throws IOException, ServletException {
        when(repo.getAllProducts()).thenReturn(List.of(validProduct));
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        new AddProductServlet(repo).doGet(request, response);

        verify(repo, times(1)).getAllProducts();
        verify(response, times(1)).getWriter();
        assertEquals(validProduct, new Gson().fromJson(stringWriter.toString(), Product[].class)[0]);
        stringWriter.close();
        printWriter.close();
    }



}