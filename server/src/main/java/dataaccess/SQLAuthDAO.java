package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.configureDatabase;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO auth (token, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setString(1, auth.authToken());
            ps.setString(2, auth.username());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to insert user. %s", e.getMessage()));
        }
    }

    @Override
    public AuthData getAuth(String auth) throws DataAccessException {
        String query = "SELECT token, username FROM auth WHERE token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, auth);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("token"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to read auth token. %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String auth) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE token=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setString(1, auth);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to delete user. %s", e.getMessage()));
        }

    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to clear auths. %s", e.getMessage()));
        }

    }
}
