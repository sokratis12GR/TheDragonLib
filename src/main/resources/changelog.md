# 1.21.1 - 1.2.0

Brings the rule editor up to date with 26.1.2 - 1.4.1.

### Rule Editor
- Added `client.config.RuleFormat` — how a config line maps to the fields of a `RuleType`. `RuleFormat.PIPE` is the previous positional format; mods can now edit other syntaxes (prefixes, `key=value` pairs, lines saved in an older layout) and provide their own cross-field checks and row summaries.
- Added the field kinds `BLOCK`, `CUSTOM` (a picker whose options the mod provides by `Field.source()`), `CHOICE` and `TOGGLE` (buttons instead of text inputs).
- Added `client.config.RegistrySuggestionSource` — a `RuleSuggestionSource` backed by `RegistryCandidates`, which mods extend for their `CUSTOM` fields.
- Added `Field.asInline()` — a field shares a row with the field before it when there's room, instead of always getting its own row.
- Added `Field.withFallback(String)` — an empty value shows (and summarizes as) the fallback instead of blank.
- Added `Field.withIcon(ResourceLocation)` — a `TOGGLE` field can be a small icon button without its own label.
- Added `Field.withRowLabel(String)` — labels the row with a different `field.<name>` translation, for fields (e.g. several icon toggles) that share one label.
- `RuleEditScreen` rows now sit in a scrolling list, so a rule can have any number of fields.
- Added Copy and Paste buttons that round-trip a rule line through the clipboard; pasted text is validated before it replaces the open rule's values.
- Number inputs and buttons are sized to fit their largest value; pickers share what's left of the row.
- A picker's resolved name shows inline at the end of its input when it fits.
- `RuleListScreen` can take an `EntryPicker` to add several entries at once next to the Add button.
- `RuleListScreen.withDefaults` adds a Reset button.
- `RuleListScreen` rows wrap long summaries and rule lines instead of cutting them off, show the raw saved line as a hover tooltip, and the list sizes itself to the widest entry (between 308px and 560px).
- `RuleSuggestionSource.Option` can have an icon, shown in the suggestion list.
- Long explanations under the screen titles are cut off with the full text on hover.
- Fixed a crash in `RuleType.validate` when a min/max field pair was optional and left empty, and `ATTRIBUTE_VALUE` fields assuming the attribute is the line's second field.

### Differences from 26.1.2
- 1.21.1 lists can't size rows individually, so all `RuleListScreen` rows are as tall as the tallest entry needs, and `RuleEditScreen` rows all get room for a note when any field is an `ATTRIBUTE_VALUE`.
- `RuleListScreen.subtitle` takes the `Screen` that shows the tooltip as its first parameter.

### Breaking Changes
- `RuleSuggestionSource` now takes the `RuleType.Field` (and the line's current values where they matter) instead of a `Kind` and a context string.

# 1.21.1 - 1.1.0

### New Utilities
- Added `util.ModCompat.isLoaded(String modId)` — a one-line check for whether another mod is loaded, replacing hand-rolled per-mod loader enums.
- Added `util.RomanNumeralUtil.generate(int)` — converts an int to a Roman numeral string.

### New Client Rendering Helpers
- Added `client.util.PoseStackHelper.translateAndScale`/`rotateItem` — common `PoseStack` transforms for drawing an `ItemStack` at an offset inside a block entity renderer.
- Added `client.util.RenderingUtils.swing`/`flap`/`bob`/`walk` — limb-animation math for `ModelPart`s.

### New Config/GUI Helpers
- Added `client.config.RegistryCandidates` — turns a built-in or datapack registry (entities, items, blocks, attributes, enchantments, biomes) into a searchable candidate list for `EntryPickerScreen`, including a fallback for datapack-driven registries (biomes, enchantments) when no world is loaded, e.g. editing config from the title screen.
- Added a second, heavier config-list editor for pipe-delimited, multi-field config rows: `client.config.RuleType` (field schema with per-field validation), `RuleSuggestionSource` (caller-supplied id lookups), `RuleListContext` (translation key prefixes), `RuleListScreen` and `RuleEditScreen`. This complements the existing append-only `PickerListScreen` for config lists that need several validated fields per row, edited in place.
- Added `ConfigHelper.getOrDefault`/`ConfigHolder.getOrDefault` — a guard that returns a fallback instead of throwing `IllegalStateException` when a config value is read before its spec is loaded (e.g. datagen or other contexts where no config file is ever loaded, such as a block's `isEnabled(FeatureFlagSet)` check running under the loot table provider).

### Internal Cleanup
- No breaking changes to existing public APIs.
