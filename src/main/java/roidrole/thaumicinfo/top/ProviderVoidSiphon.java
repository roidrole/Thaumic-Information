package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;

public class ProviderVoidSiphon extends AbstractProvider {
	public static final ProviderVoidSiphon INSTANCE = new ProviderVoidSiphon();
	private ProviderVoidSiphon(){
		super("void_syphon");
	}

	@Override
	public void addProbeInfo(IProbeInfo info, TileEntity tile, IBlockState state, IProbeHitData data) {
		if(!(tile instanceof TileVoidSiphon)) {
			return;
		}
		int progress = ((TileVoidSiphon) tile).progress;
		int maxProgress = ((TileVoidSiphon) tile).PROGREQ;
		int percent = (int) ((float) progress / maxProgress * 100);
		info.progress(percent, 100, info.defaultProgressStyle()
			.suffix("%"));
	}
}
