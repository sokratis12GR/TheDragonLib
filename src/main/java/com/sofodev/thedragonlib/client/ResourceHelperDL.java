package com.sofodev.thedragonlib.client;

import com.sofodev.thedragonlib.TheDragonLib;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ResourceHelperDL {

    public static final String RESOURCE_PREFIX = TheDragonLib.MODID + ":";
    private static ResourceLocation vanillaParticles;
    private static Map<String, ResourceLocation> cachedResources = new HashMap<>();

    public static ResourceLocation getResource(String rs) {
        if (!cachedResources.containsKey(rs)) cachedResources.put(rs, ResourceLocation.parse(RESOURCE_PREFIX + rs));
        return cachedResources.get(rs);
    }

    public static ResourceLocation getResourceRAW(String rs) {
        if (!cachedResources.containsKey(rs)) cachedResources.put(rs, ResourceLocation.parse(rs));
        return cachedResources.get(rs);
    }
}
