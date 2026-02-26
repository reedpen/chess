package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear();
    void createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    List<ChessGame> listGames() throws DataAccessException;
    void updateGame(int gameID) throws DataAccessException;


}
