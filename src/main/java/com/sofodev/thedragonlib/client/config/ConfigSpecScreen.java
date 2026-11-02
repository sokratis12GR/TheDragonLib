package com.sofodev.thedragonlib.client.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A reflection-driven editor for a {@link ForgeConfigSpec}: one row per entry at the current
 * section (booleans/enums as cycle buttons, numbers/strings as validated text boxes, lists as a
 * button opening a {@link StringListScreen} or caller-supplied {@link ListScreenFactory}), and
 * nested sections as a button pushing a child screen of the same kind. Edits apply immediately via
 * {@link ForgeConfigSpec.ConfigValue#set}; there is no undo/redo.
 */
public class ConfigSpecScreen extends Screen {

    /** Builds the screen used to edit one list-typed entry; return null to fall back to a plain {@link StringListScreen}. */
    public interface ListScreenFactory {
        @Nullable
        Screen create(Screen parent, String key, ForgeConfigSpec.ConfigValue<?> value, ForgeConfigSpec.ValueSpec valueSpec);
    }

    private final Screen parent;
    private final ForgeConfigSpec spec;
    private final UnmodifiableConfig specSection;
    private final UnmodifiableConfig valuesSection;
    private final List<String> path;
    @Nullable
    private final ListScreenFactory listScreenFactory;

    private Rows rows;

    public ConfigSpecScreen(Component title, Screen parent, ForgeConfigSpec spec, @Nullable ListScreenFactory listScreenFactory) {
        this(title, parent, spec, spec.getSpec(), spec.getValues(), List.of(), listScreenFactory);
    }

    private ConfigSpecScreen(Component title, Screen parent, ForgeConfigSpec spec, UnmodifiableConfig specSection,
                              UnmodifiableConfig valuesSection, List<String> path, @Nullable ListScreenFactory listScreenFactory) {
        super(title);
        this.parent = parent;
        this.spec = spec;
        this.specSection = specSection;
        this.valuesSection = valuesSection;
        this.path = path;
        this.listScreenFactory = listScreenFactory;
    }

    @Override
    protected void init() {
        rows = addRenderableWidget(new Rows(minecraft, width, height - 36 - 32, 32));
        for (UnmodifiableConfig.Entry entry : valuesSection.entrySet()) {
            rows.add(entry.getKey());
        }
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .bounds(width / 2 - 100, height - 28, 200, 20).build());
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(font, title, width / 2, 10, 0xFFFFFFFF);
    }

    private List<String> childPath(String key) {
        List<String> next = new ArrayList<>(path);
        next.add(key);
        return next;
    }

    private Component label(String key, ForgeConfigSpec.@Nullable ValueSpec valueSpec) {
        String translationKey = valueSpec != null ? valueSpec.getTranslationKey() : spec.getLevelTranslationKey(childPath(key));
        if (translationKey != null && I18n.exists(translationKey)) {
            return Component.translatable(translationKey);
        }
        return Component.literal(key);
    }

    private void openChild(String key) {
        UnmodifiableConfig childSpec = specSection.get(List.of(key));
        UnmodifiableConfig childValues = valuesSection.get(List.of(key));
        minecraft.setScreen(new ConfigSpecScreen(label(key, null), this, spec, childSpec, childValues, childPath(key), listScreenFactory));
    }

    @SuppressWarnings("unchecked")
    private void openList(String key, ForgeConfigSpec.ConfigValue<?> value, ForgeConfigSpec.ValueSpec valueSpec) {
        Screen custom = listScreenFactory == null ? null : listScreenFactory.create(this, key, value, valueSpec);
        minecraft.setScreen(custom != null ? custom : new StringListScreen(this, label(key, valueSpec), value));
    }

    @SuppressWarnings("unchecked")
    private static void setValue(ForgeConfigSpec.ConfigValue value, Object newValue) {
        value.set(newValue);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> AbstractWidget enumCycle(ForgeConfigSpec.ConfigValue<Object> value, Enum<?> current) {
        E typedCurrent = (E) current;
        E[] constants = typedCurrent.getDeclaringClass().getEnumConstants();
        return CycleButton.builder((E e) -> Component.literal(e.name()), typedCurrent)
                .withValues(List.of(constants))
                .create(0, 0, 120, 20, Component.empty(), (btn, val) -> setValue(value, val));
    }

    @Nullable
    private static Number parseNumber(String text, Number sameTypeAs) {
        try {
            if (sameTypeAs instanceof Integer) return Integer.parseInt(text);
            if (sameTypeAs instanceof Long) return Long.parseLong(text);
            if (sameTypeAs instanceof Float) return Float.parseFloat(text);
            if (sameTypeAs instanceof Byte) return Byte.parseByte(text);
            if (sameTypeAs instanceof Short) return Short.parseShort(text);
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private final class Rows extends ContainerObjectSelectionList<Rows.Row> {

        Rows(Minecraft minecraft, int width, int height, int y) {
            super(minecraft, width, height, y, 24);
        }

        void add(String key) {
            addEntry(new Row(key));
        }

        @Override
        public int getRowWidth() {
            return Math.min(360, width - 40);
        }

        final class Row extends ContainerObjectSelectionList.Entry<Row> {

            private final String key;
            private final AbstractWidget editor;
            private final boolean isSection;
            @Nullable
            private final Component help;

            @SuppressWarnings("unchecked")
            Row(String key) {
                this.key = key;
                Object rawValue = valuesSection.get(List.of(key));
                this.isSection = rawValue instanceof UnmodifiableConfig;

                if (isSection) {
                    this.help = null;
                    editor = Button.builder(label(key, null), b -> openChild(key)).size(Rows.this.getRowWidth(), 20).build();
                } else {
                    ForgeConfigSpec.ValueSpec valueSpec = specSection.get(List.of(key));
                    this.help = valueSpec.getComment() != null ? Component.literal(valueSpec.getComment()) : null;
                    ForgeConfigSpec.ConfigValue<Object> value = (ForgeConfigSpec.ConfigValue<Object>) rawValue;
                    Object current = value.get();
                    if (current instanceof Boolean bool) {
                        editor = CycleButton.builder((Boolean b) -> Component.literal(b.toString()), bool)
                                .withValues(List.of(Boolean.TRUE, Boolean.FALSE))
                                .create(0, 0, 120, 20, Component.empty(), (btn, val) -> setValue(value, val));
                    } else if (current instanceof Enum<?> currentEnum) {
                        editor = enumCycle(value, currentEnum);
                    } else if (current instanceof List<?> list) {
                        editor = Button.builder(Component.literal("Edit list (" + list.size() + ")"),
                                b -> openList(key, value, valueSpec)).size(150, 20).build();
                    } else if (current instanceof Number number) {
                        EditBox box = new EditBox(font, 0, 0, 120, 20, Component.empty());
                        box.setValue(String.valueOf(number));
                        box.setResponder(text -> {
                            Number parsed = parseNumber(text, number);
                            boolean valid = parsed != null && (valueSpec == null || valueSpec.test(parsed));
                            box.setTextColor(valid ? 0xFFFFFFFF : 0xFFFF5555);
                            if (valid) {
                                setValue(value, parsed);
                            }
                        });
                        editor = box;
                    } else {
                        EditBox box = new EditBox(font, 0, 0, 200, 20, Component.empty());
                        box.setMaxLength(1024);
                        box.setValue(String.valueOf(current));
                        box.setResponder(text -> setValue(value, text));
                        editor = box;
                    }
                }
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                int y = getContentY() + (getContentHeight() - 20) / 2;
                if (isSection) {
                    editor.setPosition(getContentX(), y);
                } else {
                    Component label = label(key, specSection.get(List.of(key)));
                    graphics.text(font, label.getString(), getContentX(), y + 6, 0xFFFFFFFF);
                    editor.setPosition(getContentRight() - editor.getWidth(), y);
                }
                editor.extractRenderState(graphics, mouseX, mouseY, a);
                if (help != null && hovered) {
                    graphics.setTooltipForNextFrame(font.split(help, 250), mouseX, mouseY);
                }
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of(editor);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(editor);
            }
        }
    }
}
