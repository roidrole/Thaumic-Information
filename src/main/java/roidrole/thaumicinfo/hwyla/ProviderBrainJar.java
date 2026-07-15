package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import roidrole.thaumicinfo.ThaumicInformationConfig;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.devices.TileJarBrain;

import javax.annotation.Nonnull;
import java.util.List;

public class ProviderBrainJar implements IWailaDataProvider {
	public static ProviderBrainJar INSTANCE = new ProviderBrainJar();
	private ProviderBrainJar(){ }

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (ThaumicInformationConfig.hwylaConfig.requireGoggles && !EntityUtils.hasGoggles(accessor.getPlayer())) {
			return tooltip;
		}

		tooltip.add("§9Contains §r" + ((TileJarBrain)accessor.getTileEntity()).xp + " xp");

		return tooltip;
	}

}
