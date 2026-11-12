# 26.1.2 - 1.5.0

### Rule Editor
- Added `client.config.RuleClipboard` — copied lines start with a `# <mod>:<key>` header naming the list they came from. Paste also accepts lines copied straight from the `.toml` file.
- Added Copy all and Paste to `RuleListScreen`. Paste adds every valid line on the clipboard, skips invalid lines and duplicates, and lists the skipped ones with the reason on hover. The Paste tooltip previews what would be added.
- Added `client.config.RuleConversions` and `RuleListScreen.withConversions` — lines copied from the mod's other rule lists are converted field by field (by name), with defaults for required values the source doesn't have.
- Added `client.config.FieldExtension` (set on `RuleListContext`) — extra data a picker field carries after its id (e.g. `minecraft:diamond_sword[damage=5]` or `minecraft:zombie{IsBaby:1b}`), edited in its own screen from a button next to the input. Suggestions, validation and names use the id alone, and picking another id keeps the data.
- Added `RuleListScreen.withGrouping` — lines sharing a value in one field collapse into a single row (with Copy, Open and Delete) once there are enough of them. Only the display changes; new lines inside a group start with the group's value.
- Added `client.config.UnsavedChangesScreen` — leaving a rule list or rule editor with changes asks to Save, Discard or Keep editing. Done still saves straight away.
- Rule screens now show breadcrumb titles (e.g. "Mod › List › Edit line 3"), with a yellow dot while there are unsaved changes.
- Added `Field.withToggleValues(on, off)` — a `TOGGLE` can save values other than `true`/`false`. Made optional with its off value as the fallback, it leaves the value out while off.
- Added `RuleSuggestionSource.selectors` — options like tags (`#id`) or whole mods (`modid:*`), offered after `*` by fields that accept wildcards and rejected by fields that don't.
- `EntryPickerScreen.Candidate` can have a custom detail line, and `EntryPickerScreen.withEmptyText` shows a message when nothing matches.
- `EntryPickerScreen` titles and entries are cut off with the full text on hover instead of running past the screen edge.
- Fixed the suggestion list staying up on the previous picker after focusing a number, toggle or other field, and text cursors showing in more than one input.