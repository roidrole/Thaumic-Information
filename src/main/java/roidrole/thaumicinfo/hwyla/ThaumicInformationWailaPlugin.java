package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import roidrole.thaumicinfo.Tags;
import roidrole.thaumicinfo.ThaumicInformationConfig;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.common.blocks.devices.BlockVisBattery;
import thaumcraft.common.tiles.devices.TileJarBrain;

@WailaPlugin
public class ThaumicInformationWailaPlugin implements IWailaPlugin {
	@Override
	public void register(IWailaRegistrar registrar) {
		if(ThaumicInformationConfig.hwylaConfig.essentiaTransport){
			registrar.registerBodyProvider(ProviderEssentiaTransport.INSTANCE, IEssentiaTransport.class);
			registrar.registerNBTProvider(ProviderEssentiaTransport.INSTANCE, IEssentiaTransport.class);
			registrar.registerTooltipRenderer("thaumicwaila.aspect", new RendererAspect());
			registrar.addConfig(Tags.MOD_NAME, Tags.MOD_ID+".aspects_as_text", "Show Aspects as Text", false);
		}

		if(ThaumicInformationConfig.hwylaConfig.gogglesDisplay){
			registrar.registerBodyProvider(ProviderGogglesDisplay.INSTANCE, IGogglesDisplayExtended.class);
		}

		if(ThaumicInformationConfig.hwylaConfig.visBattery) {
			registrar.registerBodyProvider(ProviderBlockVisBattery.INSTANCE, BlockVisBattery.class);
		}

		if(ThaumicInformationConfig.hwylaConfig.brainInJar && ThaumicInformationConfig.general.liquidXP.isEmpty()) {
			registrar.registerBodyProvider(ProviderBrainJar.INSTANCE, TileJarBrain.class);
		}

		registrar.addConfig(Tags.MOD_NAME, Tags.MOD_ID+".require_goggles", "Require Goggles of Revealing",  true);
	}

}
