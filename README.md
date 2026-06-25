# Thaumic Roid
Roid's Thaumcraft addon

Thaumcraft addon integrating Thaumcraft with JEI, HWYLA and VisualOres and adds some performance improvements

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
- Brain in a Jar support
- Vis Battery support
- Support for all languages supported by Thaumcraft itself
- HWYLA Config to require goggles
- Aspects can be shown as their icon or as text

### Visual Ores
If VisualOres is loaded, the dioptra will update the aura display of chunks in a 13x13 square centered on itself of every player who interacted with it.

### Performance
- Aspect cache (better than the one Thaumic Speedup had)
- ItemStack hash (incompatible with Thaumic Speedup)
- Recipe cache (FastWorkbench) for the pattern crafter
- Faster oredict wildcard (direct port of Thaumic Speedup's removed feature)