package servlets;

import javax.ejb.EJB;
import com.google.gson.Gson;
import data.Product;
import exceptions.AlreadyDefinedProductException;
import repositories.ProductRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@WebServlet(urlPatterns = "/products")
public class AddProductServlet extends HttpServlet {

    @EJB(beanName = "ProductRepositoryInDatabase")
    private ProductRepository repo;

    public AddProductServlet() {}

    public AddProductServlet(ProductRepository repo){
        this.repo = repo;
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            byte[] bytes = req.getInputStream().readAllBytes();
            String productInJSON = new String(bytes);
            Gson gson = new Gson();
            Product product = gson.fromJson(productInJSON, Product.class);
            if(product.isValidObject()) {
                repo.addProduct(product);
                resp.setStatus(201);
            } else {
                resp.setStatus(400);
            }
        } catch (AlreadyDefinedProductException e) {
            resp.getWriter().write("[{ \"error\":\"data.Product already in the store!\"}]");
            resp.setStatus(409);
        } catch (NumberFormatException e){
            resp.setStatus(400);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Collection<Product> products = repo.getAllProducts();
        Gson gson = new Gson();
        String productInJSON = null;
        PrintWriter pw = resp.getWriter();
        for(Product product : products){
            productInJSON = gson.toJson(product);
            pw.write(productInJSON);
            pw.write("\n");
        }
    }
}

