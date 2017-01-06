package net.thedragonteam.thedragonlib.client.util;

import net.thedragonteam.thedragonlib.util.LogHelper;

import java.net.URI;

public class ClientUtills {

    public static void openLink(String url) {
        try {
            URI uri = new URI(url);
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
            oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, uri);
        } catch (Throwable throwable) {
            LogHelper.error("Couldn\'t open link");
            throwable.printStackTrace();
        }
    }
}