package roidrole.thaumicroid.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import roidrole.thaumicroid.Tags;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.common.lib.utils.EntityUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;


public class ProviderGogglesDisplay implements IWailaDataProvider {
	public static ProviderGogglesDisplay INSTANCE = new ProviderGogglesDisplay();
	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		TileEntity te = accessor.getTileEntity();
		if (!(te instanceof IGogglesDisplayExtended)) {
			return tooltip;
		}
		if (config.getConfig(Tags.MOD_ID+".require_goggles") && !EntityUtils.hasGoggles(accessor.getPlayer())) {
			return tooltip;
		}

		IGogglesDisplayExtended display = (IGogglesDisplayExtended) te;
		tooltip.addAll(Arrays.asList(display.getIGogglesText()));
		return tooltip;
	}
}