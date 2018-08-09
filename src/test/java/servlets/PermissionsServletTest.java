package servlets;

import data.Permissions;
import data.User;
import exceptions.AlreadyAcquiredPermissionException;
import exceptions.NoSuchUserException;
import exceptions.NotAcquiredPermissionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repositories.UserRepository;
import repositories.UserRepositoryInDatabase;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PermissionsServletTest extends Mockito {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private ServletInputStream is;
    private UserRepository repo;

    private User testUser;

    @BeforeEach
    public void setUp() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        is = mock(ServletInputStream.class);
        repo = mock(UserRepositoryInDatabase.class);

        testUser = new User("test", "jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=");
        testUser.setId(1);
        testUser.addPermission(Permissions.GET_PRODUCT);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("authenticated")).thenReturn(testUser);
        when(request.getInputStream()).thenReturn(is);
    }

    @Test
    public void givePermissionsToYourselfTest() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");

        PermissionsServlet servlet = new PermissionsServlet(repo);
        servlet.doPut(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void givePermissionsToNotExistingUserTest() throws ServletException, IOException, NoSuchUserException, AlreadyAcquiredPermissionException {
        when(request.getPathInfo()).thenReturn("/2");
        when(is.readAllBytes()).thenReturn("{\"permission\":\"delete_product\"}".getBytes());
        doThrow(NoSuchUserException.class).when(repo).addPermission(2,Permissions.DELETE_PRODUCT);

        PermissionsServlet servlet = new PermissionsServlet(repo);
        servlet.doPut(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void givePermissionsAlreadyAcquiredPermissionTest() throws ServletException, IOException, NoSuchUserException, AlreadyAcquiredPermissionException {
        when(request.getPathInfo()).thenReturn("/2");
        when(is.readAllBytes()).thenReturn("{\"permission\":\"delete_product\"}".getBytes());
        doThrow(AlreadyAcquiredPermissionException.class).when(repo).addPermission(2,Permissions.DELETE_PRODUCT);

        PermissionsServlet servlet = new PermissionsServlet(repo);
        servlet.doPut(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void givePermissionsNotExistingPermissionTest() throws ServletException, IOException, NoSuchUserException, AlreadyAcquiredPermissionException {
        when(request.getPathInfo()).thenReturn("/2");
        when(is.readAllBytes()).thenReturn("{\"permission\":\"invalid\"}".getBytes());

        PermissionsServlet servlet = new PermissionsServlet(repo);
        servlet.doPut(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void givePermissionsSuccessfullyTest() throws ServletException, IOException, NoSuchUserException, AlreadyAcquiredPermissionException {
        when(request.getPathInfo()).thenReturn("/2");
        when(is.readAllBytes()).thenReturn("{\"permission\":\"delete_product\"}".getBytes());

        PermissionsServlet servlet = new PermissionsServlet(repo);
        servlet.doPut(request,response);

        verify(repo, times(1)).addPermission(2, Permissions.DELETE_PRODUCT);
    }


    @Test
    public void takePermissionsFromYourselfTest() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");

        PermissionsServlet servlet = new PermissionsServlet(repo);
        servlet.doDelete(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void takePermissionsFromNotExistingUserTest() throws ServletException, IOException, NoSuchUserException, NotAcquiredPermissionException {
        when(request.getPathInfo()).thenReturn("/2");
        when(is.readAllBytes()).thenReturn("{\"permission\":\"delete_product\"}".getBytes());
        doThrow(NoSuchUserException.class).when(repo).deletePermission(2,Permissions.DELETE_PRODUCT);

        PermissionsServlet servlet = new PermissionsServlet(repo);
        servlet.doDelete(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void takePermissionsNotAcquiredPermissionYetTest() throws ServletException, IOException, NoSuchUserException, NotAcquiredPermissionException {
        when(request.getPathInfo()).thenReturn("/2");
        when(is.readAllBytes()).thenReturn("{\"permission\":\"delete_product\"}".getBytes());
        doThrow(NotAcquiredPermissionException.class).when(repo).deletePermission(2,Permissions.DELETE_PRODUCT);

        PermissionsServlet servlet = new PermissionsServlet(repo);
        servlet.doDelete(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void takePermissionsNotExistingPermissionTest() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/2");
        when(is.readAllBytes()).thenReturn("{\"permission\":\"invalid\"}".getBytes());

        PermissionsServlet servlet = new PermissionsServlet(repo);
        servlet.doDelete(request,response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void takePermissionsSuccessfullyTest() throws ServletException, IOException, NoSuchUserException, NotAcquiredPermissionException{
        when(request.getPathInfo()).thenReturn("/2");
        when(is.readAllBytes()).thenReturn("{\"permission\":\"get_product\"}".getBytes());

        PermissionsServlet servlet = new PermissionsServlet(repo);
        servlet.doDelete(request,response);

        verify(repo, times(1)).deletePermission(2, Permissions.GET_PRODUCT);
    }





}