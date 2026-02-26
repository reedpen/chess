package dataaccess;

import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{

    final Map<String, UserData> users = new HashMap<>();
    @Override
    public UserData getUserData(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            return users.get(username);
        } else {
            throw new DataAccessException("Username not in database");
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (!users.containsKey(userData.username())) {
            users.put(userData.username(), userData);
        } else {
            throw new DataAccessException("Username taken");
        }

    }

    @Override
    public void deleteUser(String username) throws DataAccessException{
        if (!users.containsKey(username)) {
            users.remove(username);
        } else {
            throw new DataAccessException("Username not in database");
        }
    }

    @Override
    public void deleteAllUser() {
        users.clear();
    }
}
