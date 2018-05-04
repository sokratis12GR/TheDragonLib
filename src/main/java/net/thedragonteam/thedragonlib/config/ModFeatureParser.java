package net.thedragonteam.thedragonlib.config;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thedragonteam.thedragonlib.util.LogHelper;

import java.util.*;

public class ModFeatureParser {

    private String modid;
    private CreativeTabs[] modTabs;
    private static final String CATEGORY_BLOCKS = "Blocks";
    private static final String CATEGORY_ITEMS = "Items";

    private static final Map<Object, Boolean> featureStates = new HashMap<>();
    private static final List<FeatureEntry> featureEntries = new ArrayList<>();

    /**
     * @param modid   modid of the mod implementing this instance of ModFeatureParser
     * @param modTabs list of creative tabs that belong to the mod
     */
    public ModFeatureParser(String modid, CreativeTabs[] modTabs) {
        this.modid = modid;
        this.modTabs = modTabs;
    }

    public void loadFeatures(Class collection) {
        Arrays.stream(collection.getFields()).filter(field -> field.isAnnotationPresent(Feature.class)).forEachOrdered(field -> {
            try {
                featureEntries.add(new FeatureEntry(field.get(null), field.getAnnotation(Feature.class)));
            } catch (IllegalAccessException e) {
                LogHelper.error("Error Loading Feature!!! [" + field.getAnnotation(Feature.class).name() + "]");
                e.printStackTrace();
            }
        });
    }

    /**
     * Generates or reads the config setting for every loaded feature.
     * Must be called AFTER loadFeatures
     *
     * @param configuration the mods configuration
     */
    public void loadFeatureConfig(Configuration configuration) {
        try {
            for (FeatureEntry entry : featureEntries) {
                if (!entry.feature.isConfigurable()) {
                    featureStates.put(entry.featureObj, true);
                    continue;
                }

                String category = entry.featureObj instanceof Block ? CATEGORY_BLOCKS : CATEGORY_ITEMS;

                entry.enabled = configuration.get(category, entry.feature.name(), entry.feature.isActive()).getBoolean(entry.feature.isActive());
                featureStates.put(entry.featureObj, entry.enabled);
            }

        } catch (Exception var4) {
            LogHelper.error("Error Loading Block/Item Config");
            var4.printStackTrace();
        } finally {
            if (configuration.hasChanged()) configuration.save();
        }
    }

    /**
     * Registers all features that are not disabled via the config.
     * Must be called AFTER loadFeatureConfig
     */
    public void registerFeatures() {
        featureEntries.stream().filter(entry -> entry.enabled).filter(
                entry -> entry.featureObj instanceof ICustomRegistry
        ).forEachOrdered(
                entry -> ((ICustomRegistry) entry.featureObj).registerFeature(entry.feature)
        );
    }

    /**
     * Registers the rendering for all loaded and enabled features.
     * Must be called AFTER registerFeatures, during Pre Initialization and from your Client Proxy
     */
    @SideOnly(Side.CLIENT)
    public void registerRendering() {
        for (FeatureEntry entry : featureEntries) {
            if (!entry.enabled) continue;

            if (entry.featureObj instanceof ICustomRender) {
                ICustomRender customRender = (ICustomRender) entry.featureObj;
                customRender.registerRenderer(entry.feature);

                if (!customRender.registerNormal(entry.feature)) {
                    continue;
                }
            }

            if (entry.featureObj instanceof Block) {
                Block block = (Block) entry.featureObj;

                if (entry.feature.variantMap().length > 0) {
                    registerVariants(Item.getItemFromBlock(block), entry.feature);
                } else {
                    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(modid.toLowerCase() + ":" + entry.feature.name()));
                }
            } else if (entry.featureObj instanceof Item) {
                Item item = (Item) entry.featureObj;

                if (!entry.feature.stateOverride().isEmpty()) {
                    String s = entry.feature.stateOverride().substring(0, entry.feature.stateOverride().indexOf("#"));
                    s += entry.feature.stateOverride().substring(entry.feature.stateOverride().indexOf("#")).toLowerCase();
                    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(modid.toLowerCase() + ":" + s));
                } else if (entry.feature.variantMap().length > 0) {
                    registerVariants(item, entry.feature);
                } else {
                    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(modid.toLowerCase() + ":" + entry.feature.name()));
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void registerVariants(Item item, Feature feature) {
        Arrays.stream(feature.variantMap()).forEachOrdered(s -> {
            int meta = Integer.parseInt(s.substring(0, s.indexOf(":")));
            String fullName = modid.toLowerCase() + ":" + feature.name();
            String variant = s.substring(s.indexOf(":") + 1);
            ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(fullName, variant));
        });
    }

    /**
     * Returns true if this object has been registered with a ModFeatureParser
     */
    public static boolean isFeature(Object object) {
        return featureStates.containsKey(object);
    }

    /**
     * Returns true if feature is enabled. Applies to all mods using a ModFeatureParser instance
     */
    public static boolean isEnabled(Object feature) {
        return featureStates.getOrDefault(feature, false);
    }

    private static class FeatureEntry {

        private final Object featureObj;
        private final Feature feature;
        public boolean enabled;

        private FeatureEntry(Object featureObj, Feature feature) {

            this.featureObj = featureObj;
            this.feature = feature;
            this.enabled = feature.isActive();
        }
    }
}