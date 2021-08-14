package gateways;

import com.google.gson.GsonBuilder;
import entities.UserTypePermissions;

public class UserTypePermissionsGateway extends EntityGateway<UserTypePermissions> {
    /**
     * Constructs an UserTypePermissionsGateway Element.
     * @param path Path of relevant json file.
     */
    public UserTypePermissionsGateway(String path) {
        super(UserTypePermissions.class, path);
    }

    @Override
    protected GsonBuilder getGsonBuilder() {
        return GatewayUtility.getSimpleGsonBuilder();
    }

    @Override
    protected String getElementId(UserTypePermissions userTypePermissions) {
        return userTypePermissions.getUserType().toString();
    }
}