package net.thedragonteam.thedragonlib.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.thedragonteam.thedragonlib.util.LogHelper;

import java.util.Arrays;

public class ModConfigProcessor {

    private Class configClass;
    private Configuration config;

    public ModConfigProcessor() {
    }

    public void processConfig(Class configClass, Configuration config) {
        this.configClass = configClass;
        this.config = config;
        Arrays.stream(configClass.getFields()).filter(field -> field.isAnnotationPresent(ModConfigProperty.class)).forEachOrdered(field -> {
            ModConfigProperty property = field.getAnnotation(ModConfigProperty.class);
            try {
                Object defaultValue = field.get(null);
                Object newValue = getConfigValue(defaultValue, config, property);
                field.set(null, newValue);
            } catch (Exception e) {
                LogHelper.error("Something when wrong while loading config value [" + property.name() + "]");
                e.printStackTrace();
            }
        });

        saveConfig();
    }

    public static Object getConfigValue(Object defaultValue, Configuration configuration, ModConfigProperty property) throws Exception {
        String category = property.category();
        String name = property.name();
        String comment = property.comment();
        if (defaultValue instanceof Boolean) {
            return configuration.get(category, name, (Boolean) defaultValue, comment).getBoolean((Boolean) defaultValue);
        } else if (defaultValue instanceof boolean[]) {
            return configuration.get(category, name, (boolean[]) defaultValue, comment).getBooleanList();
        } else if (defaultValue instanceof Double) {
            return configuration.get(category, name, (Double) defaultValue, comment).getDouble((Double) defaultValue);
        } else if (defaultValue instanceof double[]) {
            return configuration.get(category, name, (double[]) defaultValue, comment).getDoubleList();
        } else if (defaultValue instanceof Integer) {
            return configuration.get(category, name, (Integer) defaultValue, comment).getInt((Integer) defaultValue);
        } else if (defaultValue instanceof int[]) {
            return configuration.get(category, name, (int[]) defaultValue, comment).getIntList();
        } else if (defaultValue instanceof String) {
            return configuration.get(category, name, (String) defaultValue, comment).getString();
        } else if (defaultValue instanceof String[]) {
            return configuration.get(category, name, (String[]) defaultValue, comment).getStringList();
        }
        throw new Exception("Config data class is unknown");
    }

    /**
     * Returns a config property fur the purpose of editing config values.
     * saveConfig() must be called to write the changes to disk.
     * This will not change the static values that have already been loaded so those will have to also be changed if
     * you make a change to the property associated with them.
     *
     * @param category The property category.
     * @param name     The name or key for the property.
     * @return The config property if it exists or null if it could not be found.
     */
    public Property findProperty(String category, String name) {
        return config.getCategory(category) != null ? config.getCategory(category).get(name) : null;
    }

    public void saveConfig() {
        if (config != null && config.hasChanged()) {
            config.save();
        }
    }
}