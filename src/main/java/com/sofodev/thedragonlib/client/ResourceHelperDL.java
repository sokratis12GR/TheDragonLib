package com.sofodev.thedragonlib.client;

import com.sofodev.thedragonlib.TheDragonLib;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ResourceHelperDL {

    public static final String RESOURCE_PREFIX = TheDragonLib.MODID + ":";
    private static Identifier vanillaParticles;
    private static Map<String, Identifier> cachedResources = new HashMap<>();

    public static Identifier getResource(String rs) {
        if (!cachedResources.containsKey(rs)) cachedResources.put(rs, Identifier.parse(RESOURCE_PREFIX + rs));
        return cachedResources.get(rs);
    }

    public static Identifier getResourceRAW(String rs) {
        if (!cachedResources.containsKey(rs)) cachedResources.put(rs, Identifier.parse(rs));
        return cachedResources.get(rs);
    }
}