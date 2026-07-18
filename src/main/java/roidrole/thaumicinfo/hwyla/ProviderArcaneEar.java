package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import roidrole.thaumicinfo.ThaumicInformationConfig;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.devices.TileArcaneEar;

import javax.annotation.Nonnull;
import java.util.List;

import static roidrole.thaumicinfo.hwyla.ProviderNoteBlock.NBTKEY;

public class ProviderArcaneEar implements IWailaDataProvider {
	public static final ProviderArcaneEar INSTANCE = new ProviderArcaneEar();
	private ProviderArcaneEar(){ }

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		TileEntity tile = accessor.getTileEntity();
		if (!(tile instanceof TileArcaneEar)) {
			return tooltip;
		}
		if (ThaumicInformationConfig.hwylaConfig.requireGoggles && !EntityUtils.hasGoggles(accessor.getPlayer())) {
			return tooltip;
		}
		tooltip.add("Note: " + accessor.getNBTData().getByte(NBTKEY));
		return tooltip;
	}

	@Nonnull
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if (!(te instanceof TileArcaneEar)) {
			return tag;
		}
		tag.setByte(NBTKEY, ((TileArcaneEar) te).note);
		return tag;
	}
}
