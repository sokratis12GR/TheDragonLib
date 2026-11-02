# 26.1.2 - 1.1.0

### New Config Core
- Added `config.ConfigHelper`/`ConfigHolder` — cuts out the `ForgeConfigSpec.Builder().configure(...)` boilerplate per `ModConfig.Type`, plus a `getOrDefault` guard that returns a fallback instead of throwing `IllegalStateException` when a config value is read before its spec is loaded (e.g. datagen).

### New Config GUI - Full Spec Editor
- Added `client.config.ConfigScreenHelper`/`ConfigSpecScreen` — a reflection-driven editor for an entire `ForgeConfigSpec`: booleans/enums as cycle buttons, numbers/strings as validated text boxes, lists opening a list screen, and nested sections as child screens of the same kind. Wired up through Forge's config-screen extension point without pulling `Screen` into common code.

### New Config GUI - List Editors
- Added `client.config.StringListScreen` — a generic add/edit/remove editor for a `List`-typed `ForgeConfigSpec.ConfigValue`.
- Added `client.config.PickerListScreen`/`EntryPicker`/`EntryPickerScreen` — extends `StringListScreen` with a searchable, category-filterable, single- or multi-select picker for adding entries to a config list.
- Added `client.config.RegistryCandidates` — turns a built-in or datapack registry (entities, items, blocks, attributes, enchantments, biomes) into a searchable candidate list for `EntryPickerScreen`, including a fallback for datapack-driven registries when no world is loaded, e.g. editing config from the title screen.

### New Config GUI - Rule List Editor
- Added `client.config.RuleType`/`RuleSuggestionSource`/`RuleListContext`/`RuleListScreen`/`RuleEditScreen` — a heavier config-list editor for pipe-delimited, multi-field config rows with per-field validation and id suggestions, for config lists that need several validated fields per row.

### New Client Rendering/Utility Helpers
- Added `client.util.PoseStackHelper.translateAndScale`/`rotateItem` — common `PoseStack` transforms for drawing an `ItemStack` at an offset inside a block entity renderer.
- Added `client.util.RenderingUtils.swing`/`flap`/`bob`/`walk` — limb-animation math for `ModelPart`s.
- Added `util.ModCompat.isLoaded(String modId)` — a one-line check for whether another mod is loaded, replacing hand-rolled per-mod loader enums.
- Added `util.RomanNumeralUtil.generate(int)` — converts an int to a Roman numeral string.

### Internal Cleanup
- No breaking changes to existing public APIs.
