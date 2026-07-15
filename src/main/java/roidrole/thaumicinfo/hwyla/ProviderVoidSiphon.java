package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import roidrole.thaumicinfo.ThaumicInformationConfig;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;

import javax.annotation.Nonnull;
import java.util.List;

public class ProviderVoidSiphon implements IWailaDataProvider {
	public static ProviderVoidSiphon INSTANCE = new ProviderVoidSiphon();
	private ProviderVoidSiphon(){ }

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		TileEntity tile = accessor.getTileEntity();
		if(!(tile instanceof TileVoidSiphon)){
			return tooltip;
		}
		if (ThaumicInformationConfig.hwylaConfig.requireGoggles && !EntityUtils.hasGoggles(accessor.getPlayer())) {
			return tooltip;
		}

		String progress = String.valueOf(((TileVoidSiphon) tile).progress);
		String prog_req = String.valueOf(((TileVoidSiphon) tile).PROGREQ);
		String progressBar = SpecialChars.getRenderString("waila.progress", progress, prog_req);
		tooltip.add(progressBar);

		return tooltip;
	}
}
