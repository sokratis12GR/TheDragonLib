package net.thedragonteam.thedragonlib.config;

import net.minecraft.item.ItemBlock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FeatureWrapper {
    private final String name;
    private final boolean isActive;
    private final boolean isConfigurable;
    private final String[] variantMap;
    private final Class<? extends ItemBlock> itemBlockClass;

    public final Map<Integer, String> nameMap = new HashMap<>();

    public FeatureWrapper(Feature feature) {
        this.name = feature.name();
        this.isActive = feature.isActive();
        this.isConfigurable = feature.isConfigurable();
        this.variantMap = feature.variantMap();
        this.itemBlockClass = feature.itemBlock();

        if (feature.variantMap().length > 0) {
            Arrays.stream(feature.variantMap()).forEachOrdered(s -> {
                int meta = Integer.parseInt(s.substring(0, s.indexOf(":")));
                nameMap.put(meta, s.substring(s.indexOf(":") + 1));
            });
        }
    }

    public String name() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isConfigurable() {
        return isConfigurable;
    }

    public String[] variantMap() {
        return variantMap;
    }

    public Class<? extends ItemBlock> getItemBlock() {
        return itemBlockClass;
    }

}