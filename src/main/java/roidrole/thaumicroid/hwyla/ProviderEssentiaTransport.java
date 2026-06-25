package roidrole.thaumicroid.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.api.impl.ConfigHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import roidrole.thaumicroid.Tags;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.utils.EntityUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class ProviderEssentiaTransport implements IWailaDataProvider {
	public static ProviderEssentiaTransport INSTANCE = new ProviderEssentiaTransport();

	@Nonnull
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if (!(te instanceof IEssentiaTransport)) {
			return tag;
		}
		if (ConfigHandler.instance().getConfig(Tags.MOD_ID+".require_goggles") && !EntityUtils.hasGoggles(player)) {
			return tag;
		}

		IEssentiaTransport transporter = (IEssentiaTransport) te;
		NBTTagList facings = new NBTTagList();
		for (EnumFacing facing : EnumFacing.VALUES) {
			NBTTagCompound facingTag = new NBTTagCompound();
			if (transporter.getEssentiaType(facing) != null) {
				NBTTagCompound essentia = new NBTTagCompound();
				essentia.setString("tag", transporter.getEssentiaType(facing).getTag());
				essentia.setInteger("amount", transporter.getEssentiaAmount(facing));
				facingTag.setTag("essentia", essentia);
			}
			NBTTagCompound suction = new NBTTagCompound();
			if (transporter.getSuctionType(facing) != null) {
				suction.setString("tag", transporter.getSuctionType(facing).getTag());
			}
			suction.setInteger("amount", transporter.getSuctionAmount(facing));
			facingTag.setTag("suction", suction);
			facings.appendTag(facingTag);
		}
		tag.setTag("essentiatransport", facings);
		return tag;
	}

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		NBTTagCompound data = accessor.getNBTData();
		if (data.isEmpty()) {
			return tooltip;
		}
		NBTTagList facings = data.getTagList("essentiatransport", 10);
		if(facings.isEmpty()){
			return tooltip;
		}
		NBTTagCompound thisFacing = facings.getCompoundTagAt(accessor.getSide().getIndex());
		if(thisFacing.isEmpty()){
			return tooltip;
		}

		NBTTagCompound essentia = thisFacing.getCompoundTag("essentia");
		String essentiaTag = essentia.getString("tag");
		if (!essentiaTag.isEmpty()) {
			tooltip.add(
				I18n.format("tc.resonator1",
					SpecialChars.WailaSplitter +essentia.getInteger("amount"),
					RendererAspect.showAspect(essentiaTag)
				)
			);
		}

		NBTTagCompound suction = thisFacing.getCompoundTag("suction");
		String suctionTag = suction.getString("tag");
		tooltip.add(
			I18n.format("tc.resonator2",
				SpecialChars.WailaSplitter + suction.getInteger("amount"),
				RendererAspect.showAspect(suctionTag)
			)
		);

		return tooltip;
	}
}
