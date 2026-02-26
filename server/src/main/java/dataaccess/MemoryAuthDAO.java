package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import service.AuthService;

public class MemoryAuthDAO implements AuthDAO{

    final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(String username) {
        auths.put(username, new AuthData(AuthService.createAuthToken(), username));
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
