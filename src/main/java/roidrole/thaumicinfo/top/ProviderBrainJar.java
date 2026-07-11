package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.NumberFormat;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileJarBrain;

public class ProviderBrainJar extends AbstractProvider {
	public static final ProviderBrainJar INSTANCE = new ProviderBrainJar();
	private ProviderBrainJar(){
		super("brain_jar");
	}

	@Override
	public void addProbeInfo(IProbeInfo info, TileEntity tile, IBlockState state, IProbeHitData data) {
		if(!(tile instanceof TileJarBrain)) {
			return;
		}

		int xp = ((TileJarBrain) tile).xp;
		int xpMax = ((TileJarBrain) tile).xpMax;
		info.progress(xp, xpMax, info.defaultProgressStyle()
			.suffix("xp")
			.filledColor(0xff16b900)
			.alternateFilledColor(0xff128c00)
			.borderColor(0xff555555)
			.numberFormat(NumberFormat.FULL));
	}
}
