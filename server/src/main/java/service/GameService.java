package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import requestsandresults.ListGamesRequest;
import requestsandresults.ListGamesResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GameService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws ResponseException {
        try {
            AuthData authData = authDAO.getAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        return new ListGamesResult(gameDAO.listGames());
    }


    public boolean clear() {
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
        return true;
    }
}
