package roidrole.thaumicinfo;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class LateMixinLoader implements ILateMixinLoader {
	@Override
	public List<String> getMixinConfigs() {
		ArrayList<String> mixinConfigs = new ArrayList<>(11);
		if(ThaumicInformationConfig.general.aspectTooltipInAllGUI){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".aspect_tooltip_everywhere.json");
		}
		if(ThaumicInformationConfig.performanceConfig.fasterHash){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".faster_hash.json");
		}
		if(ThaumicInformationConfig.performanceConfig.patternCrafterRecipeCache){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".patterncrafter_recipe_cache.json");
		}
		if(ThaumicInformationConfig.performanceConfig.fasterOreDictWildcard){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".faster_oredict_wildcard.json");
		}
		if(ThaumicInformationConfig.performanceConfig.aspectCache){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".aspect_cache.json");
		}
		//ThaumcraftAPI.exists doesn't call CommonInternals.exists(), which is a problem if the hash is changed
		if(ThaumicInformationConfig.performanceConfig.fasterHash || (ThaumicInformationConfig.performanceConfig.aspectCache && Loader.isModLoaded("thaumicspeedup"))){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".thaumcraftapi_use_hash.json");
		}
		if(Loader.isModLoaded("visualores")){
			if(ThaumicInformationConfig.visualOresConfig.dioptra.enabled){
				mixinConfigs.add("mixins."+Tags.MOD_ID+".dioptra_aura.json");
			}
			if(ThaumicInformationConfig.visualOresConfig.overlay.enabled){
				mixinConfigs.add("mixins."+Tags.MOD_ID+".recolour_overlay.json");
			}
		}
		if(ThaumicInformationConfig.jeiConfig.jerCrystals && Loader.isModLoaded("jeresources")){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".injectjercrystals.json");
		}
		mixinConfigs.add("mixins."+Tags.MOD_ID+".accessors.json");
		return mixinConfigs;
	}
}
