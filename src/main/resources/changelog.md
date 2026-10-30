# 26.1.2 - 1.2.1

### Fixes
- Added `ConfigHelper.getOrDefault`/`ConfigHolder.getOrDefault` — a guard that returns a fallback instead of throwing `IllegalStateException` when a config value is read before its spec is loaded (e.g. datagen or other contexts where no config file is ever loaded, such as a block's `isEnabled(FeatureFlagSet)` check running under the loot table provider).

# 26.1.2 - 1.2.0

### New Utilities
- Added `util.ModCompat.isLoaded(String modId)` — a one-line check for whether another mod is loaded, replacing hand-rolled per-mod loader enums.
- Added `util.RomanNumeralUtil.generate(int)` — converts an int to a Roman numeral string.

### New Client Rendering Helpers
- Added `client.util.PoseStackHelper.translateAndScale`/`rotateItem` — common `PoseStack` transforms for drawing an `ItemStack` at an offset inside a block entity renderer.
- Added `client.util.RenderingUtils.swing`/`flap`/`bob`/`walk` — limb-animation math for `ModelPart`s.

### New Config/GUI Helpers
- Added `client.config.RegistryCandidates` — turns a built-in or datapack registry (entities, items, blocks, attributes, enchantments, biomes) into a searchable candidate list for `EntryPickerScreen`, including a fallback for datapack-driven registries (biomes, enchantments) when no world is loaded, e.g. editing config from the title screen.
- Added a second, heavier config-list editor for pipe-delimited, multi-field config rows: `client.config.RuleType` (field schema with per-field validation), `RuleSuggestionSource` (caller-supplied id lookups), `RuleListContext` (translation key prefixes), `RuleListScreen` and `RuleEditScreen`. This complements the existing append-only `PickerListScreen` for config lists that need several validated fields per row, edited in place.

### Internal Cleanup
- No breaking changes to existing public APIs.
