package repositories;

import data.User;
import exceptions.NoSuchUserException;
import exceptions.UserAlreadyExistsException;

public interface UserRepository {

    void addUser(User user) throws UserAlreadyExistsException;

    User getUserByName(String username) throws NoSuchUserException;

}
