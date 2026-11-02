package com.sofodev.thedragonlib.util;

import net.minecraftforge.fml.ModList;

/**
 * @author Sokratis Fotkatzikis
 */
public final class ModCompat {

    private ModCompat() {
    }

    public static boolean isLoaded(String modId) {
        return ModList.isLoaded(modId);
    }
}
