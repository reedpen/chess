package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{

    final Map<String, UserData> users = new HashMap<>();
    @Override
    public UserData getUserData(String username) {
        return users.get(username);
    }

    @Override
    public void createUser(UserData userData) {
        users.put(userData.username(), userData);
    }

    @Override
    public void deleteUser(String username) {
        users.remove(username);
    }

    @Override
    public void deleteAllUser() {
        users.clear();
    }
}
