package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;

public interface UserDAO {
    UserData getUserData(String username) throws DataAccessException;
    void createUser(UserData userData) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    void clear();
}
