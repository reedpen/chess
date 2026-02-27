package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import requestsandresults.LoginRequest;
import requestsandresults.RegisterRequest;
import requestsandresults.UserResult;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceTests {
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    @Test
    void registerPositive() throws ResponseException, DataAccessException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("reedpen", "1234", "reed@gmail.com");
        UserResult res = service.register(req);

        assertEquals(res.username(), req.username());
        assertNotNull(res.authToken());
        assertNotNull(userDAO.getUserData(req.username()));
        assertNotNull(authDAO.getAuth(res.authToken()));
    }
    @Test
    void registerNegativeEmpty() throws ResponseException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("", "", "");


        ResponseException e = assertThrows(ResponseException.class, () -> service.register(req));
    }
    @Test
    void registerNegativeTaken() throws ResponseException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("reed", "123", "reed@gmail.com");
        UserResult res = service.register(req);
        RegisterRequest req2 = new RegisterRequest("reed", "1213", "reed2@gmail.com");

        ResponseException e = assertThrows(ResponseException.class, () -> service.register(req2));

    }

    @Test
    void loginPositive() throws DataAccessException, ResponseException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest regReq = new RegisterRequest("reed", "123", "reed@gmail.com");
        service.register(regReq);
        authDAO.deleteAllAuth();
        LoginRequest logReq = new LoginRequest("reed", "123");
        UserResult res = service.login(logReq);
        assertEquals(userDAO.getUserData(logReq.username()).password(), logReq.password());
        assertEquals(userDAO.getUserData(logReq.username()).username(), logReq.username());
        assertNotNull(authDAO.getAuth(res.authToken()));
    }

    @Test
    void loginWrongPassword() throws DataAccessException, ResponseException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("reed", "123", "reed@gmail.com");
        UserResult res = service.register(req);
        authDAO.deleteAllAuth();
        LoginRequest lReq = new LoginRequest("reed", "1213");

        ResponseException e = assertThrows(ResponseException.class, () -> service.login(lReq));
    }
    @Test
    void loginWrongUser() throws DataAccessException, ResponseException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("reed", "123", "reed@gmail.com");
        UserResult res = service.register(req);
        LoginRequest lReq = new LoginRequest("reed1", "123");

        ResponseException e = assertThrows(ResponseException.class, () -> service.login(lReq));
    }
}
