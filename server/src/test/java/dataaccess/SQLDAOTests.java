package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.createDatabase;
import static org.junit.jupiter.api.Assertions.*;
public class SQLDAOTests {
    private SQLUserDAO userDAO;
    private SQLAuthDAO authDAO;
    private SQLGameDAO gameDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        createDatabase();
        configureDatabase();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

    }


    // AUTH TESTS
    @Test
    public void CreateAuthPositive() throws DataAccessException {
        AuthData expectedAuth = new AuthData("123", "username");
        authDAO.createAuth(expectedAuth);
        AuthData actualAuth = authDAO.getAuth("123");
        assertNotNull(actualAuth);
        assertEquals(expectedAuth.username(), actualAuth.username());
        assertEquals(expectedAuth.authToken(), actualAuth.authToken());
    }

    @Test
    public void CreateAuthNegative() throws DataAccessException {
        AuthData expectedAuth = new AuthData("123", "username");
        authDAO.createAuth(expectedAuth);
        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(new AuthData("123", "john"));
        });
    }


    @Test
    public void GetAuthPositive() throws DataAccessException {
        AuthData expectedAuth = new AuthData("123", "username");
        authDAO.createAuth(expectedAuth);
        AuthData actualAuth = authDAO.getAuth("123");
        assertNotNull(actualAuth);
        assertEquals(expectedAuth.username(), actualAuth.username());
        assertEquals(expectedAuth.authToken(), actualAuth.authToken());
    }

    @Test
    public void GetAuthNegative() throws DataAccessException {
        AuthData expectedAuth = new AuthData("123", "username");
        authDAO.createAuth(expectedAuth);
        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(new AuthData("123", "john")); // Attempt duplicate insert
        });
    }

    @Test
    public void DeleteAuthPositive() throws DataAccessException {
        AuthData expectedAuth = new AuthData("123", "username");
        authDAO.createAuth(expectedAuth);
        authDAO.deleteAuth("123");
        assertNull(authDAO.getAuth("123"));
    }
    @Test
    public void DeleteAuthNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuth("nonExistentToken");
        });
    }
    @Test
    public void AuthClear() throws DataAccessException {
        AuthData expectedAuth = new AuthData("123", "username");
        authDAO.createAuth(expectedAuth);
        authDAO.clear();
        assertNull(authDAO.getAuth("123"));
    }
    // USER TESTS
    @Test
    public void CreateUserPositive() throws DataAccessException {
        UserData expectedUser = new UserData("reed", "pwordhashed", "reed@reed.reed");
        userDAO.createUser(expectedUser);
        UserData actualUser = userDAO.getUserData("reed");
        assertNotNull(actualUser);
        assertEquals(expectedUser.username(), actualUser.username());
        assertEquals(expectedUser.password(), actualUser.password());
        assertEquals(expectedUser.email(), actualUser.email());
    }

    @Test
    public void CreateUserNegative() throws DataAccessException {
        UserData expectedUser = new UserData("reed", "reedpassword", "g@mail.com");
        userDAO.createUser(expectedUser);
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(new UserData("reed", "reedpassword", "g@mail.com"));
        });
    }

    @Test
    public void GetUserPositive() throws DataAccessException {
        UserData expectedUser = new UserData("reed", "pwordhashed", "reed@reed.reed");
        userDAO.createUser(expectedUser);
        UserData actualUser = userDAO.getUserData("reed");
        assertNotNull(actualUser);
        assertEquals(expectedUser.username(), actualUser.username());
        assertEquals(expectedUser.password(), actualUser.password());
        assertEquals(expectedUser.email(), actualUser.email());
    }

    @Test
    public void GetUserNegative() throws DataAccessException {
        UserData expectedUser = new UserData("reed", "reedpassword", "g@mail.com");
        userDAO.createUser(expectedUser);
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(new UserData("reed", "reedpassword", "g@mail.com"));
        });
    }


}
