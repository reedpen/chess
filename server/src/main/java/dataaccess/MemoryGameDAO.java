package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.GameService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    final Map<String, Integer> games = new HashMap<>();

    @Override
    public void clear() {
        games.clear();
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
