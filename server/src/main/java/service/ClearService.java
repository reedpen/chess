package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService{
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    public ClearService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() {
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }
}
