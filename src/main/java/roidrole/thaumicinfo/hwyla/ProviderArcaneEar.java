package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import roidrole.thaumicinfo.Tags;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.devices.TileArcaneEar;

import javax.annotation.Nonnull;
import java.util.List;

public class ProviderArcaneEar implements IWailaDataProvider {
	public static final ProviderArcaneEar INSTANCE = new ProviderArcaneEar();
	private ProviderArcaneEar(){ }

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		TileEntity tile = accessor.getTileEntity();
		if(!(tile instanceof TileArcaneEar)){
			return tooltip;
		}
		if (config.getConfig(Tags.MOD_ID+".require_goggles") && !EntityUtils.hasGoggles(accessor.getPlayer())) {
			return tooltip;
		}
		tooltip.add("Note: " + ((TileArcaneEar)accessor.getTileEntity()).note);
		return tooltip;
	}
}
