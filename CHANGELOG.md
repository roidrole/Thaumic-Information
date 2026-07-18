## 1.2.0
**Added**:
- A research → recipes cache for recipe hiding
  - This was inspired by [Thaumic JEI Unofficial](https://github.com/JodieRuth/Thaumic-JEI-Unofficial)
- Automatic updating of caches if item count changes
  - This was inspired by [Thaumic JEI Unofficial](https://github.com/JodieRuth/Thaumic-JEI-Unofficial)
- Config for dioptra range and delay
- TOP Support
  - All features from Thaumic One Proble
  - All TOP support from Congrega Mystica
- Feature parity between HWYLA and TOP (added missing methods to both)

**Changed**: 
- hideRecipesIfMissingResearch no longer requires a Minecraft/World restart
- The infernal recipe category will now show the input/output corresponding to the focused one instead of iterating through all possibilities. Thanks to WaitingIdly for pointing this out and helping with implementation
- The configs for HWYLA and TOP are now in the same category
- Moved some HWYLA configs from the hwyla's config to Thaumic Information's

**Removed**: 
- Brain in a jar fluid handling (included in ThaumicTweaker)

**Fixed**: OreDict recipes for the infernal furnace category not working

## 1.1.3
**Fixed** #4 — crash related to JER integration

## 1.1.2
- Fixed crash on negative aspect count
- Slightly improve the aspect cache creation

## 1.1.1
- Fixed crash on dedicated servers
- Removed hard dependency on JEI

## 1.1.0:

**Added**: 
- JER integration for liquid death crystal drops
- Hot Reloading of configs
- Comments to overlay multiplier config options
- Purity background to AspectFromItemStackCategory (lighter the more % of the item's aspect this one is)

**Fixed**: 
- Off-by-one AspectFromItemStackCategory background
- Crash if an aspect does not exist in Aspect.aspects
- Brain in a jar not working with modded fluids

**Changed**: Rebranded as Thaumic Information (thaumicinfo)

## 1.0.0:
First release