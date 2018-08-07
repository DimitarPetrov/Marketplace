package data;

import java.util.HashSet;
import java.util.Set;

public class User {
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

}
