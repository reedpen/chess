package service;

import dataaccess.*;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;
import requestsandresults.RegisterRequest;
import requestsandresults.RegisterResult;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceTests {
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    @Test
    void registerPositive() throws ResponseException, DataAccessException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("reedpen", "1234", "reed@gmail.com");
        RegisterResult res = service.register(req);

        assertEquals(res.username(), req.username());
        assertNotNull(res.authToken());
        assertNotNull(userDAO.getUserData(req.username()));
        assertNotNull(authDAO.getAuth("reedpen"));
        userDAO.deleteAllUser();
        authDAO.deleteAllAuth();
    }
    @Test
    void registerNegativeEmpty() throws ResponseException, DataAccessException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("", "", "");


        ResponseException e = assertThrows(ResponseException.class, () -> service.register(req));
        userDAO.deleteAllUser();
        authDAO.deleteAllAuth();
    }
    @Test
    void registerNegativeTaken() throws ResponseException, DataAccessException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("reed", "123", "reed@gmail.com");
        RegisterResult res = service.register(req);
        RegisterRequest req2 = new RegisterRequest("reed", "1213", "reed2@gmail.com");

        ResponseException e = assertThrows(ResponseException.class, () -> service.register(req2));
        userDAO.deleteAllUser();
        authDAO.deleteAllAuth();
    }
}
