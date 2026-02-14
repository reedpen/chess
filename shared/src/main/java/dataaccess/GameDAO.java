package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear();
    void createGame(GameData gameData);
    GameData getGame(int gameID);
    List<ChessGame> listGames();
    void updateGame(int gameID);


}
