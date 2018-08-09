package repositories;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.Permissions;
import data.Product;
import data.Requirement;
import data.User;
import exceptions.AlreadyAcquiredPermissionException;
import exceptions.NoSuchUserException;
import exceptions.NotAcquiredPermissionException;
import exceptions.UserAlreadyExistsException;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import java.io.BufferedReader;
import java.io.FileReader;
import data.Permissions;
import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


@Singleton
public class UserRepositoryInDatabase implements UserRepository {

    private static final String VIOLATED_UNIQUENESS = "23505";
    private static final String VIOLATED_FOREIGN_KEY_CONSTRAINT = "23503";

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

    public UserRepositoryInDatabase(String host, String port) {
        try {
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
        try (PreparedStatement statement = connection.prepareStatement("insert into users values(default,?,?) returning id")) {
            connection.setAutoCommit(false);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            long id = resultSet.getInt(1);
            user.setId(id);
            submitPermissions(user);
            connection.commit();
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
                "(?, ?)")) {
            Set<Permissions> permissions = user.getPermissions();
            for (Permissions permission : permissions) {
                statement.setLong(1, user.getId());
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
            resultUser.setId(resultSet.getInt(1));
            extractPermissions(resultUser);
            return resultUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByID(int id) throws NoSuchUserException {
        try (PreparedStatement statement = connection.prepareStatement("select * from users where id=?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new NoSuchUserException();
            }
            User resultUser = new User(resultSet.getString(2), resultSet.getString(3));
            resultUser.setId(resultSet.getInt(1));
            extractPermissions(resultUser);
            return resultUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void extractPermissions(User user) {
        try (PreparedStatement statement = connection.prepareStatement("select * from permissions where user_id = ?")) {
            statement.setLong(1, user.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                user.addPermission(Permissions.getPermissionByString(resultSet.getString(2)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addPermission(int UserID, Permissions permission) throws AlreadyAcquiredPermissionException, NoSuchUserException {
        try(PreparedStatement statement = connection.prepareStatement("insert into permissions values(?,?)")){
            statement.setInt(1,UserID);
            statement.setString(2,permission.toString());
            statement.executeUpdate();
        } catch (SQLException e){
            if(e.getSQLState().equals(VIOLATED_UNIQUENESS)){
                throw new AlreadyAcquiredPermissionException();
            }
            if(e.getSQLState().equals(VIOLATED_FOREIGN_KEY_CONSTRAINT)){
                throw new NoSuchUserException();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deletePermission(int UserID, Permissions permission) throws NoSuchUserException, NotAcquiredPermissionException {
        try(PreparedStatement statement = connection.prepareStatement("delete from permissions where user_id=? and permission = ?")) {
            statement.setInt(1,UserID);
            statement.setString(2,permission.toString());
            int affected = statement.executeUpdate();
            if(affected == 0){
                throw new NotAcquiredPermissionException();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanRepository() {
        try(Statement statement = connection.createStatement()){
            connection.setAutoCommit(false);
            statement.executeUpdate("delete from permissions");
            statement.executeUpdate("delete from users");
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
