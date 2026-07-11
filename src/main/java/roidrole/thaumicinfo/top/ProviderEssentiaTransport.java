package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import roidrole.thaumicinfo.Tags;
import roidrole.thaumicinfo.ThaumicInformationConfig;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.utils.EntityUtils;

public class ProviderEssentiaTransport implements IProbeInfoProvider {

	public static ProviderEssentiaTransport INSTANCE = new ProviderEssentiaTransport();

	private ProviderEssentiaTransport(){ }

	@Override
	public String getID() {
		return Tags.MOD_ID+":essentia_transport";
	}

	@Override
	public void addProbeInfo(
		ProbeMode probeMode,
		IProbeInfo probeInfo,
		EntityPlayer entityPlayer,
		World world,
		IBlockState blockState,
		IProbeHitData data
	) {
		TileEntity tile = world.getTileEntity(data.getPos());
		if(!(tile instanceof IEssentiaTransport)){
			return;
		}
		if(ThaumicInformationConfig.topConfig.requireGoggles && !EntityUtils.hasGoggles(entityPlayer)){
			return;
		}
		IEssentiaTransport transporter = (IEssentiaTransport) tile;
		EnumFacing side = data.getSideHit();

		int essentia = transporter.getEssentiaAmount(side);
		if(essentia != 0){
			Aspect essentiaType = transporter.getEssentiaType(side);
			probeInfo.element(new ElementContent(essentiaType, essentia));
		}

		int suction = transporter.getSuctionAmount(side);
		if(suction != 0) {
			Aspect suctionType = transporter.getSuctionType(side);
			probeInfo.element(new ElementSuction(suctionType, suction));
		}
	}
}
