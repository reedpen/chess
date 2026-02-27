package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import requestsandresults.CreateGameRequest;
import requestsandresults.CreateGameResult;
import requestsandresults.ListGamesRequest;
import requestsandresults.ListGamesResult;


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
            if (authData == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
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

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws ResponseException {
        if (request == null || request.gameName() == null || request.gameName().isEmpty()) {
            throw new ResponseException(400, "Error: bad request");
        }
        try {
            AuthData data = authDAO.getAuth(authToken);
            if (data == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            GameData newGame = new GameData(0, null, null, request.gameName(), new ChessGame());
            int gameID = gameDAO.createGame(newGame);
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }

    }
}
