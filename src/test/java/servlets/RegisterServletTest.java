package servlets;

import data.Permissions;
import data.User;
import exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repositories.UserRepository;
import repositories.UserRepositoryInDatabase;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServletTest extends Mockito {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletInputStream is;
    private UserRepository repo;


    private User testUser;

    @BeforeEach
    public void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        is = mock(ServletInputStream.class);
        repo = mock(UserRepositoryInDatabase.class);
        testUser = new User("test", "jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=");
        testUser.addPermission(Permissions.GET_PRODUCT);

    }

    @Test
    public void passwordAndRepeatedPasswordDontMatchTest() throws IOException, ServletException {
        when(request.getInputStream()).thenReturn(is);
        when(is.readAllBytes()).thenReturn("{\"username\":\"test\",\"password\":\"123456\",\"repeat_password\":\"12345\"}".getBytes());

        new RegisterServlet(repo).doPost(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void userNameAlreadyTakenTest() throws IOException, ServletException, UserAlreadyExistsException {
        when(request.getInputStream()).thenReturn(is);
        when(is.readAllBytes()).thenReturn("{\"username\":\"test\",\"password\":\"123456\",\"repeat_password\":\"123456\"}".getBytes());
        doThrow(UserAlreadyExistsException.class).when(repo).addUser(testUser);

        new RegisterServlet(repo).doPost(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void successfulRegisterTest() throws IOException, ServletException, UserAlreadyExistsException {
        when(request.getInputStream()).thenReturn(is);
        when(is.readAllBytes()).thenReturn("{\"username\":\"test\",\"password\":\"123456\",\"repeat_password\":\"123456\"}".getBytes());

        new RegisterServlet(repo).doPost(request,response);

        verify(repo, times(1)).addUser(testUser);
        verify(response, times(1)).setStatus(200);
    }


}