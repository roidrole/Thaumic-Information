package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.devices.TileVisGenerator;

import javax.annotation.Nonnull;
import java.util.List;

public class ProviderVisGenerator implements IWailaDataProvider {
	public static ProviderVisGenerator INSTANCE = new ProviderVisGenerator();
	private ProviderVisGenerator(){ }

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		TileEntity tile = accessor.getTileEntity();
		if(!(tile instanceof TileVisGenerator)){
			return tooltip;
		}
		if (!config.getConfig("capability.energyinfo")) {
			return tooltip;
		}

		tooltip.add(String.format("%d / %d FE", ((TileVisGenerator) tile).getEnergyStored(), ((TileVisGenerator) tile).getMaxEnergyStored()));

		return tooltip;
	}
}
