package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.createDatabase;

public class SQLUserDAO implements UserDAO{


    public void SQLUserDAO() throws DataAccessException {
        createDatabase();
        configureDatabase();
    }

    @Override
    public UserData getUserData(String username) throws DataAccessException {
        String query = "SELECT username, password, email FROM user WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to get user. %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)" ;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.setString(3, user.email());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to insert user. %s", e.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException{
        var statement = "TRUNCATE user";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Error: unable to clear users. %s", e.getMessage()));
        }

    }
}
