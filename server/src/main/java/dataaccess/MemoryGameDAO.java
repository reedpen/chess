package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public class MemoryGameDAO implements GameDAO{
    @Override
    public void clear() {

    }

    @Override
    public void createGame(GameData gameData) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public List<ChessGame> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(int gameID) {

    }
}
