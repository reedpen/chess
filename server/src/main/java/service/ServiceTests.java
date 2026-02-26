package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import requestsandresults.UserRequest;
import requestsandresults.UserResult;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceTests {
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    @Test
    void registerPositive() throws ResponseException, DataAccessException {
        UserService service = new UserService(authDAO, userDAO);
        UserRequest req = new UserRequest("reedpen", "1234", "reed@gmail.com");
        UserResult res = service.register(req);

        assertEquals(res.username(), req.username());
        assertNotNull(res.authToken());
        assertNotNull(userDAO.getUserData(req.username()));
        assertNotNull(authDAO.getAuth("reedpen"));
    }
    @Test
    void registerNegativeEmpty() throws ResponseException, DataAccessException {
        UserService service = new UserService(authDAO, userDAO);
        UserRequest req = new UserRequest("", "", "");


        ResponseException e = assertThrows(ResponseException.class, () -> service.register(req));
    }
    @Test
    void registerNegativeTaken() throws ResponseException, DataAccessException {
        UserService service = new UserService(authDAO, userDAO);
        UserRequest req = new UserRequest("reed", "123", "reed@gmail.com");
        UserResult res = service.register(req);
        UserRequest req2 = new UserRequest("reed", "1213", "reed2@gmail.com");

        ResponseException e = assertThrows(ResponseException.class, () -> service.register(req2));

    }
}
