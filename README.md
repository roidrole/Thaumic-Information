# Thaumic Information

Thaumcraft addon integrating Thaumcraft with JEI, HWYLA and VisualOres and adds some performance improvements

All features can be disabled in the config
## Features

### JEI
All features of Thaumic JEI, plus:
- All categories can be opened in JEI
- An infernal furnace category
- A Salis mundus category (multiblocks not supported)
- Significant improvements to the aspect cache
- Remove aspect cache auto-updating every launch (on by default in Thaumic JEI)
- Better JEI catalysts
- Special recipes (Triple Meat Treat and Salis Mundis) are shown as recipes instead of descriptions
- Configs to remove any combination of JEI tabs
- Blacklist spawn eggs from aspect checking by default 

### Waila
- Suction and amount is shown in Waila (as opposed to only contents)
- Brain in a Jar support (can show xp in points, levels or mb)
- Vis Battery support
- Support for all languages supported by Thaumcraft itself
- HWYLA Config to require goggles
- Aspects can be shown as their icon or as text

### Visual Ores
- Thaumic Dioptra will update the aura display of chunks in a 13x13 square centered on itself of every player who interacted with it.
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
- Extract/Insert liquid xp from a brain in a jar