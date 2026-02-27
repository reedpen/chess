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
    public void updateGame(GameData gameData) throws DataAccessException {
        if (games.containsKey(gameData.gameID())) {
            games.put(gameData.gameID(), gameData);
        } else
        {
            throw new DataAccessException("GameID not found in database");
        }
    }
}
