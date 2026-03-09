package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static dataaccess.DatabaseManager.configureDatabase;

public class SQLGameDAO implements GameDAO{
    Gson gson = new Gson();

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Error: unable to clear games. %s", e.getMessage()));
        }

    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (id, white_username, black_username, game_name, game_json) VALUES (?, null, null, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setInt(1, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(2, gameData.gameName());
            ps.setString(3, gson.toJson(gameData.game()));
            ps.executeUpdate();
            return gameData.gameID();

        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to create game. %s", e.getMessage()));
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String query = "SELECT id, white_username, black_username, game_name, game_json FROM game WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, gameID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GameData(rs.getInt("id"),
                            rs.getString("white_username"),
                            rs.getString("black_username"),
                            rs.getString("game_name"),
                            gson.fromJson(rs.getString("game_json"), ChessGame.class)
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to read game. %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        Collection res = new HashSet();

    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }
}

