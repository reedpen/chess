package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MemoryAuthDAO implements AuthDAO{

    final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(String authToken, String username) throws DataAccessException {
        if (!auths.containsKey(username)){
            auths.put(username, new AuthData(authToken, username));
        } else {
            throw new DataAccessException("Username already has associated auth token");
        }

    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        if (auths.containsKey(username)){
            return auths.get(username);
        } else {
            throw new DataAccessException("Username not found in database");
        }

    }

    @Override
    public void deleteAuth(String username) throws DataAccessException {
        if (auths.containsKey(username)){
            auths.remove(username);
        } else {
            throw new DataAccessException("Username not found in database");
        }

    }

    @Override
    public void deleteAllAuth() {
        auths.clear();
    }
}
