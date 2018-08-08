package repositories;

import data.Permissions;
import data.User;
import exceptions.NoSuchUserException;
import exceptions.UserAlreadyExistsException;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import java.sql.*;
import java.util.Set;


@Singleton
public class UserRepositoryInDatabase implements UserRepository {

    private static final String VIOLATED_UNIQUENESS = "23505";

    private Connection connection;


    public UserRepositoryInDatabase() {
        try {
            String host = System.getenv("DATABASE_HOST");
            String port = System.getenv("DATABASE_PORT");
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/postgres", "postgres", "postgres");

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void preDestroy() throws SQLException {
        connection.close();
    }

    @Override
    public void addUser(User user) throws UserAlreadyExistsException {
        try (PreparedStatement statement = connection.prepareStatement("insert into users values(default,?,?)")) {
            connection.setAutoCommit(false);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
            submitPermissions(user);
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            if (e.getSQLState().equals(VIOLATED_UNIQUENESS)) {
                throw new UserAlreadyExistsException();
            }
            throw new RuntimeException(e);
        }
    }

    private void submitPermissions(User user) {
        try (PreparedStatement statement = connection.prepareStatement("insert into permissions values " +
                "((select id from users where user_name = ?), ?)")) {
            Set<Permissions> permissions = user.getPermissions();
            for (Permissions permission : permissions) {
                statement.setString(1, user.getUsername());
                statement.setString(2, permission.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByName(String username) throws NoSuchUserException {
        try (PreparedStatement statement = connection.prepareStatement("select * from users where user_name=?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new NoSuchUserException();
            }
            User resultUser = new User(resultSet.getString(2), resultSet.getString(3));
            extractPermissions(resultUser);
            return resultUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void extractPermissions(User user) {
        try (PreparedStatement statement = connection.prepareStatement("select * from permissions where user_id = (select id from users where user_name = ?)")) {
            statement.setString(1, user.getUsername());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                user.addPermission(Permissions.getPermissionByString(resultSet.getString(2)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
