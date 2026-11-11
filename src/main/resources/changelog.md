# 26.2 - 1.1.0

Brings the 26.1.2 - 1.4.1 rule editor to 26.2.

### Rule Editor
- Added `client.config.RuleFormat` — how a config line maps to the fields of a `RuleType`. `RuleFormat.PIPE` is the previous positional format; mods can now edit other syntaxes (prefixes, `key=value` pairs, lines saved in an older layout) and provide their own cross-field checks and row summaries.
- Added the field kinds `BLOCK`, `CUSTOM` (a picker whose options the mod provides by `Field.source()`), `CHOICE` and `TOGGLE` (buttons instead of text inputs).
- Added `client.config.RegistrySuggestionSource` — a `RuleSuggestionSource` backed by `RegistryCandidates`, which mods extend for their `CUSTOM` fields.
- `RuleSuggestionSource.Option` can have an icon, shown in the suggestion list.
- `RuleEditScreen` rows now sit in a scrolling list, so a rule can have any number of fields.
- Added `Field.asInline()` — a field shares a row with the field before it when there's room.
- Added `Field.withFallback(String)` — an empty value shows (and summarizes as) the fallback instead of blank.
- Added `Field.withIcon(Identifier)` — a `TOGGLE` field can be a small icon button without its own label.
- Added `Field.withRowLabel(String)` — labels the row with a different `field.<name>` translation, for fields that share one label.
- Number inputs and buttons are sized to fit their largest value; pickers share what's left of the row. A picker's resolved name shows inline at the end of its input when it fits.
- Added Copy and Paste buttons that round-trip a rule line through the clipboard; pasted text is validated before it replaces the open rule's values.
- `RuleListScreen` can take an `EntryPicker` to add several entries at once next to the Add button, and `RuleListScreen.withDefaults` adds a Reset button.
- `RuleListScreen` rows wrap long summaries instead of cutting them off and show the raw saved line as a hover tooltip; the screen sizes itself to the widest entry (between 308px and 560px).
- Long explanations under the screen titles are cut off with the full text on hover instead of running off the screen.
- Fixed a crash in `RuleType.validate` when a min/max field pair was optional and left empty, and `ATTRIBUTE_VALUE` fields assuming the attribute is the line's second field.

### Breaking Changes
- `RuleSuggestionSource` now takes the `RuleType.Field` (and the line's current values where they matter) instead of a `Kind` and a context string.