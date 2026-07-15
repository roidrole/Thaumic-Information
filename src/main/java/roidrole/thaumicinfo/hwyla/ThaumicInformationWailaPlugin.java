package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.tileentity.TileEntityNote;
import roidrole.thaumicinfo.ThaumicInformationConfig;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.common.blocks.devices.BlockVisBattery;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.tiles.devices.TileJarBrain;
import thaumcraft.common.tiles.devices.TileVisGenerator;

@WailaPlugin
public class ThaumicInformationWailaPlugin implements IWailaPlugin {
	@Override
	public void register(IWailaRegistrar registrar) {
		if(ThaumicInformationConfig.hwylaConfig.essentiaTransport){
			registrar.registerBodyProvider(ProviderEssentiaTransport.INSTANCE, IEssentiaTransport.class);
			registrar.registerNBTProvider(ProviderEssentiaTransport.INSTANCE, IEssentiaTransport.class);
			registrar.registerTooltipRenderer("thaumicwaila.aspect", new RendererAspect());
		}

		if(ThaumicInformationConfig.hwylaConfig.gogglesDisplay){
			registrar.registerBodyProvider(ProviderGogglesDisplay.INSTANCE, IGogglesDisplayExtended.class);
		}

		if(ThaumicInformationConfig.hwylaConfig.visBattery) {
			registrar.registerBodyProvider(ProviderBlockVisBattery.INSTANCE, BlockVisBattery.class);
		}

		if(ThaumicInformationConfig.hwylaConfig.brainInJar) {
			registrar.registerBodyProvider(ProviderBrainJar.INSTANCE, TileJarBrain.class);
		}

		if(ThaumicInformationConfig.hwylaConfig.arcaneEar){
			registrar.registerBodyProvider(ProviderArcaneEar.INSTANCE, TileArcaneEar.class);
			registrar.registerNBTProvider(ProviderArcaneEar.INSTANCE, TileArcaneEar.class);
		}

		if(ThaumicInformationConfig.hwylaConfig.visGenerator){
			registrar.registerBodyProvider(ProviderVisGenerator.INSTANCE, TileVisGenerator.class);
		}

		if(ThaumicInformationConfig.hwylaConfig.voidSiphon){
			registrar.registerBodyProvider(ProviderVoidSiphon.INSTANCE, TileVoidSiphon.class);
		}

		if(ThaumicInformationConfig.hwylaConfig.noteBlock){
			registrar.registerNBTProvider(ProviderNoteBlock.INSTANCE, TileEntityNote.class);
			registrar.registerBodyProvider(ProviderNoteBlock.INSTANCE, TileEntityNote.class);
		}
	}

}
