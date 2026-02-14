package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDAO{
    @Override
    public String getUserData(String username) {
        return "";
    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void deleteAllUser() {

    }
}
