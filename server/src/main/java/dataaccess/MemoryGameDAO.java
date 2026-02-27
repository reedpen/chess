package dataaccess;

import chess.ChessGame;
import model.GameData;


import java.util.*;

public class MemoryGameDAO implements GameDAO{
    final Map<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int createGame(GameData gameData){
        int currentID = nextID++;
        GameData newGame = new GameData(currentID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        games.put(currentID, newGame);
        return currentID;


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
