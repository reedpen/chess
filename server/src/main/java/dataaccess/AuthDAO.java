package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void deleteAllAuth() throws DataAccessException;

}
