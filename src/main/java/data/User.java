package data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {
    private long id;
    private String username;
    private String password;
    private Set<Permissions> permissionSet;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        permissionSet = new HashSet<>();
    }

    public void addPermission(Permissions permission){
        permissionSet.add(permission);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<Permissions> getPermissions() {
        return permissionSet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(permissionSet, user.permissionSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, permissionSet);
    }
}
