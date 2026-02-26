package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(String username);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void deleteAllAuth();

}
