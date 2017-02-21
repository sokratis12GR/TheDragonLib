package net.thedragonteam.thedragonlib.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.thedragonteam.thedragonlib.util.LogHelper;

import java.lang.reflect.Field;

public class ModConfigProcessor {

    private Class configClass;
    private Configuration config;

    public ModConfigProcessor() {
    }

    public void processConfig(Class configClass, Configuration config) {
        this.configClass = configClass;
        this.config = config;
        for (Field field : configClass.getFields()) {
            if (field.isAnnotationPresent(ModConfigProperty.class)) {
                ModConfigProperty property = field.getAnnotation(ModConfigProperty.class);
                try {
                    Object defaultValue = field.get(null);
                    Object newValue = getConfigValue(defaultValue, config, property);
                    field.set(null, newValue);
                } catch (Exception e) {
                    LogHelper.error("Something when wrong while loading config value [" + property.name() + "]");
                    e.printStackTrace();
                }
            }
        }

        saveConfig();
    }

    public static Object getConfigValue(Object defaultValue, Configuration configuration, ModConfigProperty property) throws Exception {
        if (defaultValue instanceof Boolean) {
            return configuration.get(property.category(), property.name(), (Boolean) defaultValue, property.comment()).getBoolean((Boolean) defaultValue);
        } else if (defaultValue instanceof boolean[]) {
            return configuration.get(property.category(), property.name(), (boolean[]) defaultValue, property.comment()).getBooleanList();
        } else if (defaultValue instanceof Double) {
            return configuration.get(property.category(), property.name(), (Double) defaultValue, property.comment()).getDouble((Double) defaultValue);
        } else if (defaultValue instanceof double[]) {
            return configuration.get(property.category(), property.name(), (double[]) defaultValue, property.comment()).getDoubleList();
        } else if (defaultValue instanceof Integer) {
            return configuration.get(property.category(), property.name(), (Integer) defaultValue, property.comment()).getInt((Integer) defaultValue);
        } else if (defaultValue instanceof int[]) {
            return configuration.get(property.category(), property.name(), (int[]) defaultValue, property.comment()).getIntList();
        } else if (defaultValue instanceof String) {
            return configuration.get(property.category(), property.name(), (String) defaultValue, property.comment()).getString();
        } else if (defaultValue instanceof String[]) {
            return configuration.get(property.category(), property.name(), (String[]) defaultValue, property.comment()).getStringList();
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
     *
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