package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileCondenser;

public class ProviderCondenser extends AbstractProvider {
	public static final ProviderCondenser INSTANCE = new ProviderCondenser();
	private ProviderCondenser(){
		super("condenser");
	}

	@Override
	public void addProbeInfo(IProbeInfo info, TileEntity tile, IBlockState state, IProbeHitData data) {
		if(!(tile instanceof TileCondenser)){
			return;
		}

		int cost = ((TileCondenser) tile).cost;
		int interval = ((TileCondenser) tile).interval;
		info.text(
			I18n.format("tc.condenser1",
				"§r" + cost
			)
		);
		info.text(
			I18n.format("tc.condenser2",
				"§r"+ interval,
				interval / 20
			)
		);
	}
}
