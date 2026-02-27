package dataaccess;

import chess.ChessGame;
import model.GameData;


import java.util.*;

public class MemoryGameDAO implements GameDAO{
    final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        if (games.get(gameData.gameID()) == null){
            games.put(gameData.gameID(), gameData);
        } else {
            throw new DataAccessException("Game with this ID already exists");
        }

    }

    @Override
    public GameData getGame(int gameID) {
        return games.getOrDefault(gameID, null);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
        if (games.containsKey(gameID)) {
            games.put(gameID, gameData);
        } else
        {
            throw new DataAccessException("GameID not found in database");
        }
    }
}
