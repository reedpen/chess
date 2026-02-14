package dataaccess;

import model.UserData;

public interface UserDAO {
    String getUserData(String username);
    void createUser(UserData userData);
    void deleteUser(String username);
    void deleteAllUser();
}
