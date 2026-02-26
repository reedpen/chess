package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MemoryAuthDAO implements AuthDAO{
    final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        if (!auths.containsKey(authData.username())){
            auths.put(authData.username(), authData);
        } else {
            throw new DataAccessException("Username already has associated auth token");
        }

    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        return auths.getOrDefault(username, null);

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
