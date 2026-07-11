package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.NumberFormat;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.blocks.devices.BlockVisBattery;

import java.util.Collections;

public class ProviderVisBattery extends AbstractProvider {
	public static final ProviderVisBattery INSTANCE = new ProviderVisBattery();
	private ProviderVisBattery(){
		super("vis_battery");
	}

	@Override
	public void addProbeInfo(IProbeInfo info, TileEntity tile, IBlockState state, IProbeHitData data) {
		if(!(state.getBlock() instanceof BlockVisBattery)) {
			return;
		}
		int charge = state.getValue(BlockVisBattery.CHARGE);
		int maxCharge = Collections.max(BlockVisBattery.CHARGE.getAllowedValues());
		info.progress(charge, maxCharge, info.defaultProgressStyle()
			.showText(false)
			.filledColor(0xff00b7ec)
			.alternateFilledColor(0xff008fb9)
			.borderColor(0xff555555)
			.numberFormat(NumberFormat.FULL));
	}
}
