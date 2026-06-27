package roidrole.thaumicroid;

import net.minecraftforge.fml.common.Loader;
import roidrole.thaumicroid.utils.JarBrainFluidCapability;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class LateMixinLoader implements ILateMixinLoader {
	@Override
	public List<String> getMixinConfigs() {
		ArrayList<String> mixinConfigs = new ArrayList<>(9);
		if(ThaumicRoidConfig.general.aspectTooltipInAllGUI){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".aspect_tooltip_everywhere.json");
		}
		if(ThaumicRoidConfig.performanceConfig.fasterHash){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".faster_hash.json");
		}
		if(ThaumicRoidConfig.performanceConfig.patternCrafterRecipeCache){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".patterncrafter_recipe_cache.json");
		}
		if(ThaumicRoidConfig.performanceConfig.fasterOreDictWildcard){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".faster_oredict_wildcard.json");
		}
		if(ThaumicRoidConfig.performanceConfig.aspectCache){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".aspect_cache.json");
		}
		//ThaumcraftAPI.exists doesn't call CommonInternals.exists(), which is a problem if the hash is changed
		if(ThaumicRoidConfig.performanceConfig.fasterHash || (ThaumicRoidConfig.performanceConfig.aspectCache && Loader.isModLoaded("thaumicspeedup"))){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".thaumcraftapi_use_hash.json");
		}
		if(Loader.isModLoaded("visualores")){
			if(ThaumicRoidConfig.visualOresConfig.dioptraUpdatesAura){
				mixinConfigs.add("mixins."+Tags.MOD_ID+".dioptra_aura.json");
			}
			if(ThaumicRoidConfig.visualOresConfig.overlay.enabled){
				mixinConfigs.add("mixins."+Tags.MOD_ID+".recolour_overlay.json");
			}
		}
		if(JarBrainFluidCapability.liquidXP != null){
			mixinConfigs.add("mixins."+Tags.MOD_ID+".brainjarfluidhandler.json");
		}
		mixinConfigs.add("mixins."+Tags.MOD_ID+".accessors.json");
		return mixinConfigs;
	}
}
