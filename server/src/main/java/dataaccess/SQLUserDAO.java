package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO{
    @Override
    public UserData getUserData(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
