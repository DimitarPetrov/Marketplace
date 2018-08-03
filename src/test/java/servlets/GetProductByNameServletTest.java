package servlets;

import com.google.gson.Gson;
import data.Product;
import exceptions.NoSuchProductException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetProductByNameServletTest extends Mockito {

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
    public void getProductByValidName() throws NoSuchProductException, IOException, ServletException{
        when(repo.getProductByName("protein")).thenReturn(validProduct);
        when(request.getPathInfo()).thenReturn("/protein");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        new GetProductByNameServlet(repo).doGet(request,response);

        verify(repo, times(1)).getProductByName("protein");
        verify(response, times(1)).getWriter();
        assertEquals(validProduct, new Gson().fromJson(stringWriter.toString(), Product.class));
        stringWriter.close();
        printWriter.close();
    }

    @Test
    public void getProductByInvalidName() throws NoSuchProductException, ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/test");
        when(repo.getProductByName("test")).thenThrow(NoSuchProductException.class);

        new GetProductByNameServlet(repo).doGet(request,response);

        verify(response, times(1)).setStatus(404);
    }

    @Test
    public void deleteProductByValidName() throws NoSuchProductException, ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/protein");

        new GetProductByNameServlet(repo).doDelete(request,response);

        verify(request, times(1)).getPathInfo();
        verify(repo, times(1)).removeProductByName("protein");
    }

    @Test
    public void deleteProductByInvalidName() throws NoSuchProductException, ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/test");
        doThrow(NoSuchProductException.class).when(repo).removeProductByName("test");

        new GetProductByNameServlet(repo).doDelete(request,response);

        verify(response, times(1)).setStatus(404);
    }

}