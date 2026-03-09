package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

import static dataaccess.DatabaseManager.configureDatabase;

public class SQLGameDAO implements GameDAO{
    public SQLGameDAO() throws DataAccessException{
        configureDatabase();
    }
    @Override
    public void clear() {

    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }
}
