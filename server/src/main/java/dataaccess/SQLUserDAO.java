package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.configureDatabase;

public class SQLUserDAO implements UserDAO{
    @Override
    public UserData getUserData(String username) throws DataAccessException {
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
    public void clear() {

    }
}
