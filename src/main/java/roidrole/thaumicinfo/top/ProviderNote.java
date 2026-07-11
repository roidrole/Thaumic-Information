package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;

public class ProviderNote extends AbstractProvider {
	public static ProviderNote INSTANCE = new ProviderNote();
	private ProviderNote() {
		super("note");
	}

	@Override
	public void addProbeInfo(IProbeInfo info, TileEntity tile, IBlockState state, IProbeHitData data) {
		if (!(tile instanceof TileEntityNote)) {
			return;
		}
		info.text("Note: " + ((TileEntityNote)tile).note);
	}
}
