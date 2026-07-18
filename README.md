# Thaumic Information

Thaumcraft addon integrating Thaumcraft with JEI, HWYLA, The One Probe and VisualOres and adds some performance improvements

All features can be disabled in the config
## Features

### JEI
All features of Thaumic JEI, plus:
- All researches associated to recipes can be opened from JEI
- An infernal furnace category
- A Salis mundus category (multiblocks not yet supported)
- Significant improvements to the aspect cache (including to the refreshing behavior)
- Only check for unlocking JEI recipes related to the unlocked research
- Better JEI catalysts
- Special recipes (Triple Meat Treat and Salis Mundus) are shown as recipes instead of descriptions
- Configs to remove any combination of JEI tabs
- Blacklist spawn eggs from aspect checking by default 
- Just Enough Resources integration for vis crystal drop on liquid death kill
- Purity background in AspectFromItemstack (white means only this aspect, black means 0% of total aspects)

### Waila/TOP
There is complete feature parity between the HWYLA and TOP support

All features from Thaumic One Probe, ThaumicWaila and Congrega Mystica TOP support have been merged

- Arcane Ear (and noteblock) note
- Vis Battery fill level
- Brain Jar experience
- Any essentia container/transporter's content and suction
- Infusion Altar instability
- Vis Generator's charge
- Void Siphon progress
- Config to require goggles
- Config to show the aspects as text or icons

### Visual Ores
- Thaumic Dioptra will update the aura display of chunks in a configurable square centered on itself of every player who interacted with it.
- Replace the default overlay with a configurable overlay whose opacity depends on the flux level, disregarding vis

### Performance
- Aspect cache
- ItemStack hash
- Recipe cache (FastWorkbench) for the pattern crafter
- Faster oredict wildcard (direct port of Thaumic Speedup's removed feature)

The Aspect cache and itemstack hash are disabled by default. You should use [Thaumic Speedup](https://github.com/LoliKingdom/Thaumic-Speedup) instead. 
They were developed in an attempt to make Thaumic Speedup's caching more stable, but Thaumic Speedup 5.0 does it better.

### Miscellaneous
- Show Thaumcraft's aspect tooltip anywhere
