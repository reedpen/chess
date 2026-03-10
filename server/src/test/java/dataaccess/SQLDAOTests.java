package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.HashSet;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.createDatabase;
import static org.junit.jupiter.api.Assertions.*;

public class SQLDAOTests {
    private SQLUserDAO userDAO;
    private SQLAuthDAO authDAO;
    private SQLGameDAO gameDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        initializeDaos();
        createDatabase();
        configureDatabase();
        clearAllTables();
    }

    private void initializeDaos() throws DataAccessException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
    }

    private void clearAllTables() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    // AUTH TESTS
    @Test
    public void createAndGetAuthPositive() throws DataAccessException {
        AuthData expectedAuth = new AuthData("123", "username");
        authDAO.createAuth(expectedAuth);
        AuthData actualAuth = authDAO.getAuth("123");
        assertNotNull(actualAuth);
        assertEquals(expectedAuth.username(), actualAuth.username());
        assertEquals(expectedAuth.authToken(), actualAuth.authToken());
    }

    @Test
    public void createAuthNegative() throws DataAccessException {
        AuthData expectedAuth = new AuthData("123", "username");
        authDAO.createAuth(expectedAuth);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(new AuthData("123", "john")));
    }

    @Test
    public void getAuthNegative() throws DataAccessException {
        AuthData actualAuth = authDAO.getAuth("nonExistentToken");
        assertNull(actualAuth);
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        AuthData expectedAuth = new AuthData("123", "username");
        authDAO.createAuth(expectedAuth);
        authDAO.deleteAuth("123");
        assertNull(authDAO.getAuth("123"));
    }

    @Test
    public void deleteAuthNegative() {
        assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("nonExistentToken"));
    }

    @Test
    public void authClear() throws DataAccessException {
        AuthData expectedAuth = new AuthData("123", "username");
        authDAO.createAuth(expectedAuth);
        authDAO.clear();
        assertNull(authDAO.getAuth("123"));
    }

    // USER TESTS
    @Test
    public void createAndGetUserPositive() throws DataAccessException {
        UserData expectedUser = new UserData("reed", "pwordhashed", "reed@reed.reed");
        userDAO.createUser(expectedUser);
        UserData actualUser = userDAO.getUserData("reed");
        assertNotNull(actualUser);
        assertEquals(expectedUser.username(), actualUser.username());
        assertEquals(expectedUser.password(), actualUser.password());
        assertEquals(expectedUser.email(), actualUser.email());
    }

    @Test
    public void createUserNegative() throws DataAccessException {
        UserData expectedUser = new UserData("reed", "reedpassword", "g@mail.com");
        userDAO.createUser(expectedUser);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(new UserData("reed", "reedpassword", "g@mail.com")));
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        UserData actualUser = userDAO.getUserData("nonExistentUser");
        assertNull(actualUser);
    }

    @Test
    public void userClear() throws DataAccessException {
        UserData expectedUser = new UserData("123", "username", "1234");
        userDAO.createUser(expectedUser);
        userDAO.clear();
        assertNull(userDAO.getUserData("123"));
    }


    // GAME TESTS
    @Test
    public void gameClear() throws DataAccessException {
        GameData expectedGame = new GameData(1, "reed", "jason", "reed and jason's game", new ChessGame());
        gameDAO.createGame(expectedGame);
        gameDAO.clear();
        assertNull(gameDAO.getGame(1));
    }

    @Test
    public void createAndGetGamePositive() throws DataAccessException {
        GameData expectedGame = new GameData(1, "reed", "jason", "reed and jason's game", new ChessGame());
        gameDAO.createGame(expectedGame);
        GameData actualGame = gameDAO.getGame(1);
        assertNotNull(actualGame);
        assertEquals(expectedGame.gameID(), actualGame.gameID());
        assertEquals(expectedGame.whiteUsername(), actualGame.whiteUsername());
        assertEquals(expectedGame.blackUsername(), actualGame.blackUsername());
        assertEquals(expectedGame.gameName(), actualGame.gameName());
        assertEquals(expectedGame.game(), actualGame.game());
    }

    @Test
    public void createGameNegative() throws DataAccessException {
        GameData expectedGame = new GameData(1, "reed", "jason", "reed and jason's game", new ChessGame());
        gameDAO.createGame(expectedGame);
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(new GameData(1, null, null, "reed's game", null)));
    }

    @Test
    public void getGameNegative() throws DataAccessException {
        GameData actualGame = gameDAO.getGame(9999);
        assertNull(actualGame);
    }

    @Test
    public void createGameNegativeNullFields() {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(new GameData(2, null, null, null, null)));
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        HashSet<GameData> set = new HashSet<>();
        GameData expectedGame = new GameData(1, "reed", "jason", "reed and jason's game", new ChessGame());
        GameData expectedGame2 = new GameData(2, "reed", "jason", "reed and jason's game", new ChessGame());
        GameData expectedGame3 = new GameData(3, "reed", "jason", "reed and jason's game", new ChessGame());
        GameData expectedGame4 = new GameData(4, "reed", "jason", "reed and jason's game", new ChessGame());

        set.add(expectedGame2);
        set.add(expectedGame3);
        set.add(expectedGame);
        set.add(expectedGame4);

        gameDAO.createGame(expectedGame);
        gameDAO.createGame(expectedGame2);
        gameDAO.createGame(expectedGame3);
        gameDAO.createGame(expectedGame4);
        Collection<GameData> output = gameDAO.listGames();
        assertEquals(4, output.size());
        for (GameData data : output) {
            assertTrue(set.contains(data));
        }
    }

    @Test
    public void listGamesNegative() throws DataAccessException {
        gameDAO.clear();
        Collection<GameData> output = gameDAO.listGames();
        assertNotNull(output);
        assertTrue(output.isEmpty());
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        GameData initialGame = new GameData(1, "whiteUser", "blackUser", "MyGame", new ChessGame());
        gameDAO.createGame(initialGame);

        GameData updatedGame = new GameData(1, "newWhite", "newBlack", "MyGame", new ChessGame());
        gameDAO.updateGame(updatedGame);

        GameData fetchedGame = gameDAO.getGame(1);
        assertEquals("newWhite", fetchedGame.whiteUsername());
        assertEquals("newBlack", fetchedGame.blackUsername());
    }

    @Test
    public void updateGameNegative() throws DataAccessException {
        try (java.sql.Connection conn = DatabaseManager.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("DROP TABLE IF EXISTS game")) {
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            fail("Test setup failed: unable to drop table.");
        }

        GameData testData = new GameData(1, "white", "black", "game", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(testData));

        DatabaseManager.configureDatabase();
    }
}