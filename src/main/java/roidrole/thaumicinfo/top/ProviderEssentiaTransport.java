package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

public class ProviderEssentiaTransport extends AbstractProvider {
	public static final ProviderEssentiaTransport INSTANCE = new ProviderEssentiaTransport();
	private ProviderEssentiaTransport(){
		super("essentia_transport");
	}

	@Override
	public void addProbeInfo(IProbeInfo info, TileEntity tile, IBlockState state, IProbeHitData data) {
		if(!(tile instanceof IEssentiaTransport)){
			return;
		}

		IEssentiaTransport transporter = (IEssentiaTransport) tile;
		EnumFacing side = data.getSideHit();

		int essentia = transporter.getEssentiaAmount(side);
		if(essentia != 0){
			Aspect essentiaType = transporter.getEssentiaType(side);
			info.element(new ElementContent(essentiaType, essentia));
		}

		int suction = transporter.getSuctionAmount(side);
		Aspect suctionType = transporter.getSuctionType(side);
		info.element(new ElementSuction(suctionType, suction));
	}
}
