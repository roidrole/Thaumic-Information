package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import roidrole.thaumicinfo.ThaumicInformationConfig;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.devices.TileCondenser;

import javax.annotation.Nonnull;
import java.util.List;

public class ProviderCondenser implements IWailaDataProvider {
	public static ProviderCondenser INSTANCE = new ProviderCondenser();
	private ProviderCondenser(){ }

	@Nonnull
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if (!(te instanceof TileCondenser)) {
			return tag;
		}
		if (ThaumicInformationConfig.hwylaConfig.requireGoggles && !EntityUtils.hasGoggles(player)) {
			return tag;
		}

		tag.setInteger("cost", ((TileCondenser) te).cost);
		tag.setInteger("interval", ((TileCondenser) te).interval);

		return tag;
	}

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (ThaumicInformationConfig.hwylaConfig.requireGoggles && !EntityUtils.hasGoggles(accessor.getPlayer())) {
			return tooltip;
		}
		NBTTagCompound data = accessor.getNBTData();
		int cost = data.getInteger("cost");
		int interval = data.getInteger("interval");
		tooltip.add(
			I18n.format("tc.condenser1",
				SpecialChars.WailaSplitter + cost
			)
		);
		tooltip.add(
			I18n.format("tc.condenser2",
				SpecialChars.WailaSplitter + interval,
				interval / 20
			)
		);
		return tooltip;
	}
}
