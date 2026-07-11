package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileArcaneEar;

public class ProviderArcaneEar extends AbstractProvider {
	public static final ProviderArcaneEar INSTANCE = new ProviderArcaneEar();
	private ProviderArcaneEar(){
		super("arcane_ear");
	}

	@Override
	public void addProbeInfo(IProbeInfo info, TileEntity tile, IBlockState state, IProbeHitData data) {
		if(!(tile instanceof TileArcaneEar)){
			return;
		}
		info.text("Note: " + ((TileArcaneEar) tile).note);
	}
}
