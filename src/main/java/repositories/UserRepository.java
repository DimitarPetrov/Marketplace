package repositories;

import data.Permissions;
import data.User;
import exceptions.AlreadyAcquiredPermissionException;
import exceptions.NoSuchUserException;
import exceptions.NotAcquiredPermissionException;
import exceptions.UserAlreadyExistsException;


public interface UserRepository {

    void addUser(User user) throws UserAlreadyExistsException;

    User getUserByName(String username) throws NoSuchUserException;

    User getUserByID(int id) throws NoSuchUserException;

    void addPermission(int UserID, Permissions permission) throws AlreadyAcquiredPermissionException, NoSuchUserException;

    void deletePermission(int UserID, Permissions permission) throws NoSuchUserException, NotAcquiredPermissionException;

    void cleanRepository();

}
