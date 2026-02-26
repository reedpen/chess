package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MemoryAuthDAO implements AuthDAO{

    final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(String username)  {
        if (!auths.containsKey(username)){
            auths.put(username, new AuthData(createAuthToken(), username));
        } else {
            throw new DataAccessException("Username already taken");
        }

    }

    @Override
    public AuthData getAuth(String username) {
        return auths.get(username);
    }

    @Override
    public void deleteAuth(String username) {
        auths.remove(username);
    }
    public static String createAuthToken(){
        return UUID.randomUUID().toString();
    }
    @Override
    public void deleteAllAuth() {
        auths.clear();2
    }
}
