package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import roidrole.thaumicinfo.Tags;
import roidrole.thaumicinfo.ThaumicInformationConfig;
import thaumcraft.common.lib.utils.EntityUtils;

public abstract class AbstractProvider implements IProbeInfoProvider {
	private final String name;

	protected AbstractProvider(String name) {
		this.name = name;
	}

	@Override
	public String getID() {
		return Tags.MOD_ID + ':' + name;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
		if(ThaumicInformationConfig.hwylaConfig.requireGoggles && !EntityUtils.hasGoggles(player)){
			return;
		}
		TileEntity tile = world.getTileEntity(data.getPos());
		this.addProbeInfo(info, tile, state, data);
	}

	public abstract void addProbeInfo(IProbeInfo info, TileEntity tile, IBlockState state, IProbeHitData data);
}
