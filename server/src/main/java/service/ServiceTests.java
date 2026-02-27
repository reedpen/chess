package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;
import requestsandresults.ListGamesRequest;
import requestsandresults.LoginRequest;
import requestsandresults.RegisterRequest;
import requestsandresults.UserResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceTests {
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
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
        authDAO.clear();
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
        authDAO.clear();
        LoginRequest lReq = new LoginRequest("reed", "1213");

        ResponseException e = assertThrows(ResponseException.class, () -> service.login(lReq));
    }
    @Test
    void loginWrongUser() throws ResponseException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("reed", "123", "reed@gmail.com");
        UserResult res = service.register(req);
        LoginRequest lReq = new LoginRequest("reed1", "123");

        ResponseException e = assertThrows(ResponseException.class, () -> service.login(lReq));
    }
    @Test
    void loginEmpty() throws ResponseException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("reed", "123", "reed@gmail.com");
        UserResult res = service.register(req);
        LoginRequest lReq = new LoginRequest("", "");

        ResponseException e = assertThrows(ResponseException.class, () -> service.login(lReq));
    }

    @Test
    void logoutPositive() throws ResponseException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("reed", "123", "reed@gmail.com");
        UserResult res = service.register(req);
        assertTrue(service.logout(res.authToken()));
    }
    @Test
    void logoutNegative() throws DataAccessException, ResponseException {
        UserService service = new UserService(authDAO, userDAO);
        RegisterRequest req = new RegisterRequest("reed", "123", "reed@gmail.com");
        UserResult res = service.register(req);
        authDAO.clear();
        ResponseException e = assertThrows(ResponseException.class, () -> service.logout(res.authToken()));
    }

    @Test
    void listGamesPositive() throws ResponseException, DataAccessException {
        GameService service = new GameService(authDAO, userDAO, gameDAO);
        authDAO.createAuth(new AuthData("1", "r"));
        GameData data = new GameData(1, "jason", "reed", "the game", new ChessGame());
        GameData data2 = new GameData(2, "josh", "kaleb", "the game but 2", new ChessGame());
        gameDAO.createGame(data);
        gameDAO.createGame(data2);
        Collection<GameData> list = new ArrayList<>();
        Collection<GameData> actual = service.listGames(new ListGamesRequest("1")).games();
        list.add(data);
        list.add(data2);
        assertEquals(list.size(), actual.size());
        assertTrue(actual.containsAll(list));
    }
    @Test
    void listGamesNegative() throws ResponseException, DataAccessException {
        GameService service = new GameService(authDAO, userDAO, gameDAO);
        authDAO.createAuth(new AuthData("1", "r"));
        GameData data = new GameData(1, "jason", "reed", "the game", new ChessGame());
        GameData data2 = new GameData(2, "josh", "kaleb", "the game but 2", new ChessGame());
        gameDAO.createGame(data);
        gameDAO.createGame(data2);
        ResponseException e = assertThrows(ResponseException.class, () -> service.listGames(new ListGamesRequest("2")));
    }

}
