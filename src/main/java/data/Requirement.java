package data;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Requirement {

    private String pathPattern;
    private String method;
    private Set<Permissions> permissions;

    public Requirement(){
        permissions = new HashSet<>();
    }

    public Requirement(String pathPattern, String method, Set<Permissions> permissions) {
        this.pathPattern = pathPattern;
        this.method = method;
        this.permissions = permissions;
    }

    public boolean match(String path, String method){
        return this.method.equals(method) && path.matches(pathPattern);
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public String getMethod() {
        return method;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }
}
