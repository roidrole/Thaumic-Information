package roidrole.thaumicroid;

import hellfall.visualores.VisualOres;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class LateMixinLoader implements ILateMixinLoader {
	@Override
	public List<String> getMixinConfigs() {
		ArrayList<String> mixinConfigs = new ArrayList<>(8);
		if(ThaumicRoidConfig.general.aspectTooltipInAllGUI){
			mixinConfigs.add("mixins.thaumicroid.aspect_tooltip_everywhere.json");
		}
		if(ThaumicRoidConfig.performanceConfig.fasterHash){
			mixinConfigs.add("mixins.thaumicroid.faster_hash.json");
		}
		if(ThaumicRoidConfig.performanceConfig.patternCrafterRecipeCache){
			mixinConfigs.add("mixins.thaumicroid.patterncrafter_recipe_cache.json");
		}
		if(ThaumicRoidConfig.performanceConfig.fasterOreDictWildcard){
			mixinConfigs.add("mixins.thaumicroid.faster_oredict_wildcard.json");
		}
		if(ThaumicRoidConfig.performanceConfig.aspectCache){
			mixinConfigs.add("mixins.thaumicroid.aspect_cache.json");
		}
		if(Loader.isModLoaded("visualores")){
			if(ThaumicRoidConfig.visualOresConfig.dioptraUpdatesAura){
				mixinConfigs.add("mixins.thaumicroid.dioptra_aura.json");
			}
			if(ThaumicRoidConfig.visualOresConfig.recolourOverlay){
				mixinConfigs.add("mixins.thaumicroid.recolour_overlay.json");
			}
		}
		mixinConfigs.add("mixins.thaumicroid.accessors.json");
		return mixinConfigs;
	}
}
