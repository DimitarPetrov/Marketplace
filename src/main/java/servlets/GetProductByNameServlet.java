package servlets;

import com.google.gson.Gson;
import data.Product;
import exceptions.NoSuchProductException;
import repositories.ProductRepository;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet( urlPatterns = "/product/*")
public class GetProductByNameServlet extends HttpServlet {

    @EJB(beanName = "ProductRepositoryInDatabase")
    private ProductRepository repo;

    public GetProductByNameServlet() {}

    public GetProductByNameServlet(ProductRepository repo){
        this.repo = repo;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            String productName = req.getPathInfo().split("/")[1];
            Product product = repo.getProductByName(productName);
            Gson gson = new Gson();
            String productInJSON = gson.toJson(product);
            resp.getWriter().write(productInJSON);
        } catch (NoSuchProductException e){
            resp.setStatus(404);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String productName = req.getPathInfo().split("/")[1];
            repo.removeProductByName(productName);
        } catch (NoSuchProductException e){
            resp.setStatus(404);
        }
    }
}
