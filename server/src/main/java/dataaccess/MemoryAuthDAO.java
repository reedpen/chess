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
        if (auths.get(authData.authToken()) == null){
            auths.put(authData.authToken(), authData);
        } else {
            throw new DataAccessException("Username already has associated auth token");
        }

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.getOrDefault(authToken, null);

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (auths.containsKey(authToken)){
            auths.remove(authToken);
        } else {
            throw new DataAccessException("Username not found in database");
        }

    }

    @Override
    public void deleteAllAuth() {
        auths.clear();
    }
}
