package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import roidrole.thaumicinfo.Tags;

import javax.annotation.Nonnull;
import java.util.List;

public class ProviderNoteBlock implements IWailaDataProvider {
	public static ProviderNoteBlock INSTANCE = new ProviderNoteBlock();
	private ProviderNoteBlock(){ }
	public static final String NBTKEY = Tags.MOD_ID+":note";

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		TileEntity te = accessor.getTileEntity();
		if (!(te instanceof TileEntityNote)) {
			return tooltip;
		}
		tooltip.add("Note: " + accessor.getNBTData().getByte(NBTKEY));
		return tooltip;
	}

	@Nonnull
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if (!(te instanceof TileEntityNote)) {
			return tag;
		}
		tag.setByte(NBTKEY, ((TileEntityNote) te).note);
		return tag;
	}
}
