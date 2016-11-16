package net.thedragonteam.thedragonlib.client.util;

import java.net.URI;

import net.thedragonteam.thedragonlib.util.LogHelper;

public class ClientUtills {

    public static void openLink(String url) {
        try {
            URI uri = new URI(url);
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new Object[]{uri});
        } catch (Throwable throwable) {
            LogHelper.error("Couldn\'t open link");
            throwable.printStackTrace();
        }
    }
}