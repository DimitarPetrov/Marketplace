package servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.jms.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LogOutServletTest extends Mockito {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void setUp(){
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @Test
    public void logOutWithoutPreviousLogInTest() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);

        new LogOutServlet().doPost(request,response);

        verify(response, times(1)).setStatus(200);
    }

    @Test
    public void logOutInvalidateSessionTest() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);

        new LogOutServlet().doPost(request,response);

        verify(session, times(1)).invalidate();
        verify(response, times(1)).setStatus(200);
    }


}