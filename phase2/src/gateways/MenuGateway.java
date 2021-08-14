package gateways;

import com.google.gson.GsonBuilder;
import entities.Menu;

public class MenuGateway extends EntityGateway<Menu> {
    /**
     * Constructs an MenuGateway Element.
     * @param path Path of relevant json file.
     */
    public MenuGateway(String path) {
        super(Menu.class, path);
    }

    @Override
    protected GsonBuilder getGsonBuilder() {
        return GatewayUtility.getSimpleGsonBuilder();
    }

    @Override
    protected String getElementId(Menu menu) {
        return menu.getCommand().getName();
    }
}