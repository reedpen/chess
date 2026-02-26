package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUserData(String username);
    void createUser(UserData userData);
    void deleteUser(String username);
    void deleteAllUser();
}
