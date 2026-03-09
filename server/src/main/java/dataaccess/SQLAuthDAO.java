package dataaccess;

import model.AuthData;
import service.ResponseException;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() {

    }


}
