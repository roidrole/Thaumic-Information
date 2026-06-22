package roidrole.thaumicsjw;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class LateMixinLoader implements ILateMixinLoader {
	@Override
	public List<String> getMixinConfigs() {
		ArrayList<String> mixinConfigs = new ArrayList<>(8);
		if(ThaumicSJWConfig.general.aspectTooltipInAllGUI){
			mixinConfigs.add("mixins.thaumicsjw.aspect_tooltip_everywhere.json");
		}
		if(ThaumicSJWConfig.speedupConfig.fasterHash){
			mixinConfigs.add("mixins.thaumicsjw.faster_hash.json");
		}
		if(ThaumicSJWConfig.speedupConfig.patternCrafterRecipeCache){
			mixinConfigs.add("mixins.thaumicsjw.patterncrafter_recipe_cache.json");
		}
		if(ThaumicSJWConfig.speedupConfig.fasterOreDictWildcard){
			mixinConfigs.add("mixins.thaumicsjw.faster_oredict_wildcard.json");
		}
		if(ThaumicSJWConfig.speedupConfig.aspectCache){
			mixinConfigs.add("mixins.thaumicsjw.aspect_cache.json");
		}
		mixinConfigs.add("mixins.thaumicsjw.accessors.json");
		return mixinConfigs;
	}
}
