package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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
        var statement = "INSERT INTO game (id, white_username, black_username, game_name, game_json) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setInt(1, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(2, gameData.whiteUsername());
            ps.setString(3, gameData.blackUsername());
            ps.setString(4, gameData.gameName());
            ps.setString(5, gson.toJson(gameData.game()));
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
    public Collection<GameData> listGames() throws DataAccessException{
        String query = "SELECT id, white_username, black_username, game_name, game_json FROM game";
        Collection<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();

             PreparedStatement ps = conn.prepareStatement(query)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    games.add( new GameData(rs.getInt("id"),
                            rs.getString("white_username"),
                            rs.getString("black_username"),
                            rs.getString("game_name"),
                            gson.fromJson(rs.getString("game_json"), ChessGame.class))
                    );
                }

            }
            return games;
        }

        catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to list games. %s", e.getMessage()));
        }

    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        String query = "UPDATE game SET white_username=?, black_username=?, game_name=?, game_json=? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, gameData.whiteUsername());
            ps.setString(2, gameData.blackUsername());
            ps.setString(3, gameData.gameName());
            ps.setString(4, gson.toJson(gameData.game()));
            ps.setInt(5, gameData.gameID());

            try (ResultSet rs = ps.executeQuery()) {

            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to update game. %s", e.getMessage()));
        }
    }
}

