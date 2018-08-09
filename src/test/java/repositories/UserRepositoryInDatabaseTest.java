package repositories;

import data.Permissions;
import data.User;
import exceptions.AlreadyAcquiredPermissionException;
import exceptions.NoSuchUserException;
import exceptions.NotAcquiredPermissionException;
import exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryInDatabaseTest {

    private UserRepository repo = new UserRepositoryInDatabase();
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser  = new User("test", "jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=");
        testUser.addPermission(Permissions.GET_PRODUCT);
        repo.cleanRepository();
    }

    @Test
    public void addUserTest() throws UserAlreadyExistsException, NoSuchUserException {
        repo.addUser(testUser);
        assertEquals(testUser, repo.getUserByName("test"));
    }

    @Test
    public void addUserAlreadyExist() throws UserAlreadyExistsException {
        repo.addUser(testUser);
        assertThrows(UserAlreadyExistsException.class, () -> repo.addUser(testUser));
    }

    @Test
    public void getUserByID() throws UserAlreadyExistsException, NoSuchUserException {
        repo.addUser(testUser);
        assertEquals(testUser, repo.getUserByID((int)testUser.getId()));
    }

    @Test
    public void getNotExistingUserByID() {
        assertThrows(NoSuchUserException.class, () -> repo.getUserByID(1236));
    }

    @Test
    public void getUserByNameNotExisting() {
        assertThrows(NoSuchUserException.class, () -> repo.getUserByName("test"));
    }

    @Test
    public void addPermissionToUserTest() throws NoSuchUserException, AlreadyAcquiredPermissionException, UserAlreadyExistsException {
        repo.addUser(testUser);
        repo.addPermission((int)testUser.getId(), Permissions.DELETE_PRODUCT);
        assertEquals(repo.getUserByName("test").getPermissions(), Set.of(Permissions.GET_PRODUCT, Permissions.DELETE_PRODUCT));
    }

    @Test
    public void addAlreadyAcquiredPermissionToUserTest() throws UserAlreadyExistsException {
        repo.addUser(testUser);
        assertThrows(AlreadyAcquiredPermissionException.class, () -> repo.addPermission((int)testUser.getId(), Permissions.GET_PRODUCT));
    }

    @Test
    public void addPermissionToNotExistingUserTest() {
        assertThrows(NoSuchUserException.class, () -> repo.addPermission(1234, Permissions.GET_PRODUCT));
    }

    @Test
    public void deletePermissionToUserTest() throws NoSuchUserException, NotAcquiredPermissionException, UserAlreadyExistsException {
        repo.addUser(testUser);
        repo.deletePermission((int)testUser.getId(), Permissions.GET_PRODUCT);
        assertEquals(repo.getUserByName("test").getPermissions(), new HashSet<>());
    }

    @Test
    public void deleteNotAcquiredPermissionToUserTest() throws UserAlreadyExistsException {
        repo.addUser(testUser);
        assertThrows(NotAcquiredPermissionException.class, () -> repo.deletePermission((int)testUser.getId(), Permissions.DELETE_PRODUCT));
    }
}