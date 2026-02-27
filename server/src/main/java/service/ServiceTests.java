package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import requestsandresults.*;

import java.util.ArrayList;
import java.util.Collection;

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
        assertDoesNotThrow(() -> service.logout(res.authToken()));
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
        GameService service = new GameService(authDAO, gameDAO);
        authDAO.createAuth(new AuthData("1", "r"));
        GameData data = new GameData(1, "jason", "reed", "the game", new ChessGame());
        GameData data2 = new GameData(2, "josh", "kaleb", "the game but 2", new ChessGame());
        gameDAO.createGame(data);
        gameDAO.createGame(data2);
        Collection<GameData> list = new ArrayList<>();
        Collection<GameData> actual = service.listGames("1").games();
        list.add(data);
        list.add(data2);
        assertEquals(list.size(), actual.size());
        assertTrue(actual.containsAll(list));
    }
    @Test
    void listGamesNegative() throws ResponseException, DataAccessException {
        GameService service = new GameService(authDAO, gameDAO);
        authDAO.createAuth(new AuthData("1", "r"));
        GameData data = new GameData(1, "jason", "reed", "the game", new ChessGame());
        GameData data2 = new GameData(2, "josh", "kaleb", "the game but 2", new ChessGame());
        gameDAO.createGame(data);
        gameDAO.createGame(data2);
        ResponseException e = assertThrows(ResponseException.class, () -> service.listGames("2"));
    }
    @Test
    void createGamePositive() throws ResponseException, DataAccessException {
        GameService service = new GameService(authDAO,  gameDAO);
        authDAO.createAuth(new AuthData("1", "r"));
        CreateGameRequest req = new CreateGameRequest("reed game");
        CreateGameResult res = service.createGame("1", req);
        assertEquals(gameDAO.getGame(res.gameID()).gameName(), req.gameName());
        assertNotNull(gameDAO.getGame(res.gameID()));
    }
    @Test
    void createGameNegative() throws ResponseException, DataAccessException {
        GameService service = new GameService(authDAO, gameDAO);
        authDAO.createAuth(new AuthData("1", "r"));
        CreateGameRequest req = new CreateGameRequest("reed game");
        CreateGameResult res = service.createGame("1", req);
        ResponseException e = assertThrows(ResponseException.class, () -> service.createGame("2", new CreateGameRequest("2")));
    }

    @Test
    void joinGamePositive() throws ResponseException, DataAccessException {
        GameService service = new GameService(authDAO,  gameDAO);
        authDAO.createAuth(new AuthData("1", "r"));
        CreateGameRequest greq = new CreateGameRequest("reed game");
        CreateGameResult gres = service.createGame("1", greq);
        JoinGameRequest req = new JoinGameRequest("WHITE", gres.gameID());
        assertDoesNotThrow(() -> {service.joinGame("1", req);});
        assertEquals("r", gameDAO.getGame(req.gameID()).whiteUsername());
        assertNull(gameDAO.getGame(req.gameID()).blackUsername());
    }
    @Test
    void joinGameTaken() throws ResponseException, DataAccessException {
        GameService service = new GameService(authDAO,  gameDAO);
        authDAO.createAuth(new AuthData("1", "r"));
        authDAO.createAuth(new AuthData("2", "j"));
        CreateGameRequest greq = new CreateGameRequest("reed game");
        CreateGameResult gres = service.createGame("1", greq);
        JoinGameRequest req = new JoinGameRequest("WHITE", gres.gameID());
        service.joinGame("1", req);
        JoinGameRequest req2 = new JoinGameRequest("WHITE", gres.gameID());
        ResponseException e = assertThrows(ResponseException.class, () -> service.joinGame("1", req));
    }

    @Test
    void clearTest() throws ResponseException, DataAccessException {
        ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);

        userDAO.createUser(new UserData("clearUser", "pass", "clear@email.com"));
        authDAO.createAuth(new AuthData("clearToken", "clearUser"));
        gameDAO.createGame(new GameData(10, "w", "b", "clearGame", new ChessGame()));

        assertNotNull(userDAO.getUserData("clearUser"));
        assertNotNull(authDAO.getAuth("clearToken"));
        assertFalse(gameDAO.listGames().isEmpty());

        clearService.clear();

        assertNull(userDAO.getUserData("clearUser"));
        assertNull(authDAO.getAuth("clearToken"));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}
