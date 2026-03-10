package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.SQLUserDAO;
import model.AuthData;
import model.UserData;
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
            authDAO.deleteAuth("123"); // Attempt duplicate insert
        });
    }


}
