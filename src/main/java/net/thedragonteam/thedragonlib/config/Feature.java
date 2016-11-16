package net.thedragonteam.thedragonlib.config;

import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Feature {
    /**
     * used For All
     */
    String name();

    /**
     * used For All
     */
    boolean isActive() default true;

    /**
     * used For All
     */
    boolean isConfigurable() default true;

    /**
     * used For All
     */
    String[] variantMap() default {};

    /**
     * used For Items with a single variant
     */
    String stateOverride() default "";

    /**
     * used For Blocks
     */
    Class<? extends ItemBlock> itemBlock() default ItemBlock.class;

    /**
     * used For Blocks
     */
    Class<? extends TileEntity> tileEntity() default TileEntity.class;

    /**
     * used For All
     * set to -1 to exclude from creative tab.
     */
    int cTab() default 0;
}