package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.items.IGogglesDisplayExtended;

public class ProviderGogglesDisplay extends AbstractProvider {
	public static final ProviderGogglesDisplay INSTANCE = new ProviderGogglesDisplay();
	private ProviderGogglesDisplay(){
		super("goggles_display");
	}

	@Override
	public void addProbeInfo(IProbeInfo info, TileEntity tile, IBlockState state, IProbeHitData data) {
		if(!(tile instanceof IGogglesDisplayExtended)){
			return;
		}
		for (String line : ((IGogglesDisplayExtended)tile).getIGogglesText()) {
			info.text(line);
		}
	}
}
