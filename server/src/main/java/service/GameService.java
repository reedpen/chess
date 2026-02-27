package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;

import model.AuthData;
import model.GameData;
import requestsandresults.*;


public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(String authToken) throws ResponseException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            return new ListGamesResult(gameDAO.listGames());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }

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

    public void joinGame(String authToken, JoinGameRequest request) throws ResponseException {
        if (request == null || authToken == null  || request.playerColor() == null|| request.playerColor().isEmpty()) {
            throw new ResponseException(400, "Error: bad request");
        }
        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }

            GameData oldData = gameDAO.getGame(request.gameID());
            if (oldData == null) {
                throw new ResponseException(400, "Error: bad request");
            }
            GameData newData;
            String username = authData.username();
            if ("WHITE".equals(request.playerColor())) {
                if (oldData.whiteUsername() != null) {
                    throw new ResponseException(403, "Error: already taken");
                }
                newData = new GameData(oldData.gameID(), username, oldData.blackUsername(), oldData.gameName(), oldData.game());
            } else if ("BLACK".equals(request.playerColor())) {
                if (oldData.blackUsername() != null) {
                    throw new ResponseException(403, "Error: already taken");
                }
                newData = new GameData(oldData.gameID(), oldData.whiteUsername(), username, oldData.gameName(), oldData.game());
            } else {
                throw new ResponseException(400, "Error: bad request");
            }
            gameDAO.updateGame(newData);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }


    }
}
