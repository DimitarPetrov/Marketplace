package servlets;

import data.User;
import exceptions.NoSuchUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repositories.UserRepository;
import repositories.UserRepositoryInDatabase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LogInServletTest extends Mockito {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private UserRepository repo;

    private User testUser  = new User("test", "jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=");




    @BeforeEach
    public void setUpMockedObjects() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        repo = mock(UserRepositoryInDatabase.class);
        when(request.getSession()).thenReturn(session);

    }

    @Test
    public void alreadyLoggedInTest() throws ServletException, IOException {
        when(session.getAttribute("authenticated")).thenReturn(testUser);
        LogInServlet servlet = new LogInServlet(repo);
        servlet.doPost(request,response);

        verify(response, times(1)).setStatus(200);
    }

    @Test
    public void logInNotExistingUser() throws NoSuchUserException, ServletException, IOException {
        when(session.getAttribute("authenticated")).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Basic dGVzdDoxMjM0NTY=");
        when(repo.getUserByName("test")).thenThrow(NoSuchUserException.class);

        LogInServlet servlet = new LogInServlet(repo);
        servlet.doPost(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void logInWrongPassword() throws NoSuchUserException, ServletException, IOException {
        when(session.getAttribute("authenticated")).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Basic dGVzdDoxMjM0NQ==");
        when(repo.getUserByName("test")).thenReturn(testUser);

        LogInServlet servlet = new LogInServlet(repo);
        servlet.doPost(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void successfulLogIn() throws NoSuchUserException, ServletException, IOException {
        when(session.getAttribute("authenticated")).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Basic dGVzdDoxMjM0NTY=");
        when(repo.getUserByName("test")).thenReturn(testUser);

        LogInServlet servlet = new LogInServlet(repo);
        servlet.doPost(request,response);

        verify(session, times(1)).setAttribute("authenticated", testUser);
        verify(response, times(1)).setStatus(200);
    }
}