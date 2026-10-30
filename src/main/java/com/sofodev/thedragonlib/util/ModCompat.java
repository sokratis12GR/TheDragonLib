package com.sofodev.thedragonlib.util;

import net.neoforged.fml.ModList;

/**
 * @author Sokratis Fotkatzikis
 */
public final class ModCompat {

    private ModCompat() {
    }

    public static boolean isLoaded(String modId) {
        return ModList.get().getModContainerById(modId).isPresent();
    }
}
