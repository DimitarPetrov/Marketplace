package data;

public enum Permissions {
    GET_PRODUCT("get_product"),
    ADD_PRODUCT("add_product"),
    DELETE_PRODUCT("delete_product"),
    GIVE_PERMISSIONS("give_permissions");

    private final String name;

    private Permissions(String name){
        this.name = name;
    }

    public static Permissions getPermissionByString(String name) {
        switch (name){
            case "get_product": return GET_PRODUCT;
            case "add_product": return ADD_PRODUCT;
            case "delete_product": return DELETE_PRODUCT;
            case "give_permissions": return GIVE_PERMISSIONS;
            default: throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
