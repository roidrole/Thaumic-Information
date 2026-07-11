package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.apiimpl.ProbeConfig;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import mcjty.theoneprobe.config.Config;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileVisGenerator;

public class ProviderVisGenerator extends AbstractProvider {
	public static final ProviderVisGenerator INSTANCE =  new ProviderVisGenerator();
	private ProviderVisGenerator(){
		super("vis_generator");
	}

	@Override
	public void addProbeInfo(IProbeInfo info, TileEntity tile, IBlockState state, IProbeHitData data) {
		if(!(tile instanceof TileVisGenerator)) {
			return;
		}
		int energy = ((TileVisGenerator) tile).getEnergyStored();
		int maxEnergy = ((TileVisGenerator) tile).getMaxEnergyStored();
		ProbeConfig config = Config.getDefaultConfig();
		if (config.getRFMode() == 1) {
			info.progress(energy, maxEnergy, info.defaultProgressStyle().suffix("RF")
				.filledColor(Config.rfbarFilledColor)
				.alternateFilledColor(Config.rfbarAlternateFilledColor)
				.borderColor(Config.rfbarBorderColor)
				.numberFormat(Config.rfFormat));
		} else {
			info.text(TextStyleClass.PROGRESS + "RF: " + ElementProgress.format(energy, Config.rfFormat, "RF"));
		}
	}
}
