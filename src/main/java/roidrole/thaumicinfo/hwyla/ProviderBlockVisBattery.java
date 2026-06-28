package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import roidrole.thaumicinfo.Tags;
import thaumcraft.common.blocks.devices.BlockVisBattery;
import thaumcraft.common.lib.utils.EntityUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class ProviderBlockVisBattery implements IWailaDataProvider {
	public static ProviderBlockVisBattery INSTANCE = new ProviderBlockVisBattery();

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (config.getConfig(Tags.MOD_ID+".require_goggles") && !EntityUtils.hasGoggles(accessor.getPlayer())) {
			return tooltip;
		}
		Block block = accessor.getBlock();
		IBlockState state = accessor.getBlockState();
		int charge = block.getMetaFromState(state);
		int max = getMax();
		tooltip.add(String.format("%s / %s", charge, max));

		return tooltip;
	}

	private int max = 0;

	public int getMax() {
		if (max != 0) {
			return max;
		}
		max = BlockVisBattery.CHARGE.getAllowedValues().size() - 1;
		return max;
	}
}
