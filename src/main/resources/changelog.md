# 26.1.2 - 1.4.1

### Rule Editor
- Number inputs and buttons are now sized to fit their largest value instead of sharing the row evenly with pickers; pickers share what's left of the row.
- Added `Field.withIcon(Identifier)` — a `TOGGLE` field can be a small icon button without its own label.
- Added `Field.withRowLabel(String)` — labels the row with a different `field.<name>` translation, for fields (e.g. several icon toggles) that share one label.
- A picker's resolved name now shows inline at the end of its input when it fits, instead of always on a note line below.
- `RuleListScreen` now sizes itself to the widest entry (between 308px and 560px) instead of always being as wide as possible, and centers row text vertically.

# 26.1.2 - 1.4.0

### Rule Editor
- `RuleEditScreen` rows now sit in a scrolling list instead of shrinking to fit, so a rule can have any number of fields.
- Added `Field.asInline()` — a field shares a row with the field before it when there's room, instead of always getting its own row.
- Added `Field.withFallback(String)` — an empty value shows (and summarizes as) the fallback instead of blank.
- Added Copy and Paste buttons that round-trip a rule line through the clipboard; pasted text is validated before it replaces the open rule's values.
- `RuleListScreen` rows show the raw saved line as a hover tooltip instead of always rendering it below the summary when the rule is valid.

# 26.1.2 - 1.3.1

### Rule Editor
- `RuleListScreen` rows now wrap long summaries and rule lines instead of cutting them off, each row grows to fit its text, and rows can be up to 560px wide (was 400px). The hover tooltip for cut-off text is gone.

# 26.1.2 - 1.3.0

### Rule Editor
- Added `client.config.RuleFormat` — how a config line maps to the fields of a `RuleType`. `RuleFormat.PIPE` is the previous positional format; mods can now edit other syntaxes (prefixes, `key=value` pairs, lines saved in an older layout) and provide their own cross-field checks and row summaries.
- Added the field kinds `BLOCK`, `CUSTOM` (a picker whose options the mod provides by `Field.source()`), `CHOICE` and `TOGGLE` (buttons instead of text inputs).
- Added `client.config.RegistrySuggestionSource` — a `RuleSuggestionSource` backed by `RegistryCandidates`, which mods extend for their `CUSTOM` fields.
- `RuleListScreen` can take an `EntryPicker` to add several entries at once next to the Add button.
- `RuleListScreen.withDefaults` adds a Reset button.
- `RuleSuggestionSource.Option` can have an icon, shown in the suggestion list.
- `RuleEditScreen` rows now shrink on short screens so rules with many fields stay above the error line and buttons.
- Long explanations under the screen titles are cut off with the full text on hover instead of running off the screen.
- Fixed a crash in `RuleType.validate` when a min/max field pair was optional and left empty, and `ATTRIBUTE_VALUE` fields assuming the attribute is the line's second field.

### Breaking Changes
- `RuleSuggestionSource` now takes the `RuleType.Field` (and the line's current values where they matter) instead of a `Kind` and a context string.

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
