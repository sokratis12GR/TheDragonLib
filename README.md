# TheDragonLib (TDL)

[![CurseForge downloads](http://cf.way2muchnoise.eu/full_thedragonlib_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/thedragonlib)
[![Supported versions](http://cf.way2muchnoise.eu/versions/thedragonlib.svg)](https://www.curseforge.com/minecraft/mc-mods/thedragonlib/files/all)
[![License](https://img.shields.io/github/license/sokratis12GR/TheDragonLib.svg)](./LICENSE)

**[Download](https://www.curseforge.com/minecraft/mc-mods/thedragonlib/files)** · **[Issues](https://github.com/sokratis12GR/TheDragonLib/issues)**

## About

TheDragonLib is a shared library mod for **NeoForge**, created and maintained by **[sokratis12GR | SoFoDev](https://sokratis.space)**. It has no content of its own — it's a utility and GUI toolkit used by (most of) SoFoDev's other mods, such as **[ArmorPlus](https://www.curseforge.com/minecraft/mc-mods/armorplus)**, **Sword-Displays** and **Mob-Settings**, to avoid reimplementing the same config boilerplate, GUI pickers and small utilities in every mod.

### Features

- **Config helpers** — build and register a `ModConfigSpec` in one call instead of repeating NeoForge's builder/register boilerplate per mod.
- **Config screen GUI widgets** — a searchable, category-filterable entry picker (`EntryPickerScreen`), a registry-backed candidate source for it (`RegistryCandidates`), a pick-or-type-manually list screen (`PickerListScreen`), and a heavier multi-field validated rule editor (`RuleType`/`RuleListScreen`/`RuleEditScreen`) for config lists that need more than a flat list of ids.
- **Item and null-check utilities** (`CheckUtils`, `ItemStackUtils`) — common guard checks and `ItemStack` lookups.
- **Client rendering helpers** (`PoseStackHelper`, `RenderingUtils`, `GLStateHelper`, `ClientUtills`) — `PoseStack` transforms and limb-swing animation math for block entity/model renderers.
- **Mod compatibility** (`ModCompat`) — a one-line `isLoaded(modId)` check.
- **Misc utilities** — Roman numeral formatting (`RomanNumeralUtil`), logging (`LogHelper`), text/array helpers (`TextHelper`, `ArrayUtils`), and more.

## For Players

TheDragonLib doesn't add any content or features visible by itself — install it only because another mod (e.g. ArmorPlus) lists it as a required dependency.

## For Modders

See [Adding TheDragonLib to your mod](#adding-thedragonlib-to-your-mod) and [Usage Examples](#usage-examples) below for how to depend on and use the library in your own NeoForge mod.

## Download

### [Get the latest version from CurseForge](https://www.curseforge.com/minecraft/mc-mods/thedragonlib/files)

See all supported Minecraft versions on the **[CurseForge files page](https://www.curseforge.com/minecraft/mc-mods/thedragonlib/files/all)**.

## Community

Bug reports and feature requests go on the **[issue tracker](https://github.com/sokratis12GR/TheDragonLib/issues)**.

## Adding TheDragonLib to your mod

Add CurseMaven and depend on TheDragonLib's file id (check the [files page](https://www.curseforge.com/minecraft/mc-mods/thedragonlib/files) for the latest id):

```groovy
repositories {
    maven { url = 'https://cursemaven.com'; content { includeGroup 'curse.maven' } }
}

dependencies {
    implementation "curse.maven:thedragonlib-248055:<fileId>"
}
```

And declare the dependency in `neoforge.mods.toml`:

```toml
[[dependencies.yourmodid]]
    modId = "thedragonlib"
    type = "required"
    versionRange = "[1.2.0,)"
    ordering = "NONE"
    side = "BOTH"
```

## Usage Examples

### Config: building and registering a `ModConfigSpec`

`ConfigHelper`/`ConfigHolder` wrap the usual `ModConfigSpec.Builder().configure(...)` boilerplate:

```java
public class MyConfig {
    public static final ConfigHolder<MyConfig> HOLDER = ConfigHelper.build(MyConfig::new);

    public final ModConfigSpec.IntValue someNumber;
    public final ModConfigSpec.ConfigValue<List<? extends String>> someList;

    public MyConfig(ModConfigSpec.Builder builder) {
        someNumber = builder.comment("Some number").defineInRange("someNumber", 10, 0, 100);
        someList = builder.comment("Some ids").defineList("someList", List.of(), () -> "", o -> true);
    }
}

// in your mod constructor:
MyConfig.HOLDER.register(modContainer, ModConfig.Type.COMMON);
// or in one call:
ConfigHolder<MyConfig> holder = ConfigHelper.buildAndRegister(modContainer, ModConfig.Type.COMMON, MyConfig::new);
```

### Config screen: wiring NeoForge's built-in `ConfigurationScreen`

If none of your config lists need a picker button, this is all you need:

```java
ConfigScreenHelper.registerDefaultScreen(modContainer);
```

If a list should offer a searchable picker (ids from a registry, entities, etc.) instead of manual
text entry, register a custom section screen and swap in a `PickerListScreen` for that one key:

```java
ConfigScreenHelper.registerSectionScreen(modContainer, (configScreen, type, modConfig, title) ->
        new ConfigurationScreen.ConfigurationSectionScreen(configScreen, type, modConfig, title) {
            @Override
            protected <T> ConfigurationListScreen<T> createList(String key, Component title,
                    ModConfigSpec.ListValueSpec spec, ModConfigSpec.ConfigValue<List<T>> valueList) {
                if (!key.equals("someList")) return super.createList(key, title, spec, valueList);
                return (ConfigurationListScreen<T>) new PickerListScreen<>(this, key, title, spec,
                        (ModConfigSpec.ConfigValue<List<String>>) (Object) valueList,
                        myEntryPicker, Component.literal("Pick"),
                        Component.literal("No world loaded"), Component.literal("Add manually"));
            }
        });
```

### Picking entries from a registry (`RegistryCandidates` + `EntryPickerScreen`)

`RegistryCandidates` turns a built-in or datapack registry into a searchable `Candidate` list,
including a title-screen fallback for datapack registries (biomes, enchantments) when no world is
loaded. Combine it with `EntryPickerScreen` via your own `EntryPicker`:

```java
public class ItemPicker implements EntryPicker {
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void open(Screen parent, List<?> current, Consumer<List<String>> add) {
        List<EntryPickerScreen.Candidate> candidates = RegistryCandidates.of(RegistryCandidates.Source.ITEM);
        List<EntryPickerScreen.Category> categories = List.of(
                new EntryPickerScreen.Category(Component.literal("All"), c -> true));
        Set<String> existing = current.stream().map(String::valueOf).collect(Collectors.toSet());
        EntryPickerScreen.Labels labels = new EntryPickerScreen.Labels(
                Component.literal("Category"), Component.literal("Search"), Component.literal("Select all"),
                n -> Component.literal("Add " + n), id -> Component.literal("Already added"));

        Minecraft.getInstance().setScreen(new EntryPickerScreen(Component.literal("Pick an item"),
                parent, candidates, categories, existing, false, labels, add));
    }
}
```

### A heavier, multi-field config list (`RuleType`/`RuleListScreen`)

For pipe-delimited rows with several validated fields (e.g. `"entry_id|other_id|1|0.05"`), edited in
place rather than appended-and-forgotten, implement `RuleSuggestionSource` for your id lookups and
describe the row layout with `RuleType`:

```java
RuleType myRule = new RuleType("myRule", List.of(
        RuleType.picker("entity", RuleType.Kind.ENTITY, false),
        RuleType.picker("item", RuleType.Kind.ITEM, false),
        RuleType.integer("amount", 1, 64),
        RuleType.chance(true)
), -1, -1);

RuleListContext ctx = new RuleListContext("mymod.ui.", "mymod.configuration.", mySuggestionSource);

Minecraft.getInstance().setScreen(new RuleListScreen(parent, myRule, ctx, currentRules,
        updatedRules -> myConfig.someList.set(updatedRules)));
```

### Item/null checks

```java
if (CheckUtils.isNotNullNorEmpty(someString) && CheckUtils.isNotEmpty(someStack)) {
    // ...
}

ItemStack result = ItemStackUtils.getItemStack("minecraft", "diamond", 1);
```

### Checking if another mod is loaded

```java
if (ModCompat.isLoaded("tconstruct")) {
    // register Tinkers' Construct compat
}
```

### Roman numerals

```java
String tier = RomanNumeralUtil.generate(4); // "IV"
```

### Client rendering helpers

```java
// Drawing an ItemStack inside a block entity renderer:
PoseStackHelper.translateAndScale(poseStack, new Vec3(0.5, 0.8, 0.5), new Vec3(0.5, 0.5, 0.5));
PoseStackHelper.rotateItem(poseStack, 0, rotationDegrees, 0);

// Limb-swing animation math for a ModelPart:
RenderingUtils.swing(rightArm, 1.0f, 1.0f, 0.0f, 0.0f, limbSwing, limbSwingAmount);
```

### Logging

```java
LogHelper.info("Loaded %d entries", entries.size());
LogHelper.errorError("Failed to load %s", exception, fileName);
```

## Repository

[![GitHub stars](https://img.shields.io/github/stars/sokratis12gr/thedragonlib.svg?style=social&label=Stars)](https://github.com/sokratis12GR/TheDragonLib/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/sokratis12gr/thedragonlib.svg?style=social&label=Fork)](https://github.com/sokratis12GR/TheDragonLib/fork)
[![GitHub watchers](https://img.shields.io/github/watchers/sokratis12gr/thedragonlib.svg?style=social&label=Watch)](https://github.com/sokratis12GR/TheDragonLib/watchers)

[![Open issues](https://img.shields.io/github/issues/sokratis12gr/thedragonlib.svg?colorB=green)](https://github.com/sokratis12GR/TheDragonLib/issues)
[![Closed issues](https://img.shields.io/github/issues-closed/sokratis12gr/thedragonlib.svg?colorB=ff5900)](https://github.com/sokratis12GR/TheDragonLib/issues?q=is%3Aissue+is%3Aclosed)
[![Open pull requests](https://img.shields.io/github/issues-pr/sokratis12gr/thedragonlib.svg?colorB=green)](https://github.com/sokratis12GR/TheDragonLib/pulls)
[![Closed pull requests](https://img.shields.io/github/issues-pr-closed/sokratis12gr/thedragonlib.svg?colorB=ff5900)](https://github.com/sokratis12GR/TheDragonLib/pulls?q=is%3Apr+is%3Aclosed)
[![Contributors](https://img.shields.io/github/contributors/sokratis12gr/thedragonlib.svg)](https://github.com/sokratis12GR/TheDragonLib/graphs/contributors)
[![Commit activity](https://img.shields.io/github/commit-activity/m/sokratis12gr/thedragonlib.svg)](https://github.com/sokratis12GR/TheDragonLib/commits)

## License

TheDragonLib is licensed under the LGPLv3 license. Full license is in [LICENSE](./LICENSE).
