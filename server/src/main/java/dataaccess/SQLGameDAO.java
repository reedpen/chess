package dataaccess;

import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static dataaccess.DatabaseManager.configureDatabase;

public class SQLGameDAO implements GameDAO{
    public SQLGameDAO() throws DataAccessException{
        configureDatabase();
    }
    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Error: unable to insert user. %s", e.getMessage()));
        }

    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (id, white_username, black_username, game_name, game_json) VALUES (?, null, null, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setInt(1, gameData.gameID());
            ps.setString(2, gameData.gameName());
            ps.setString(3, new Gson().toJson(gameData.game()));
            ps.executeUpdate();
            return gameData.gameID();

        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to insert user. %s", e.getMessage()));
        }
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

