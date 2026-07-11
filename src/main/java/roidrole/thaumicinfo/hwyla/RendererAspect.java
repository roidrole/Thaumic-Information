package roidrole.thaumicinfo.hwyla;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.api.impl.ConfigHandler;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.text.WordUtils;
import roidrole.thaumicinfo.AspectRenderer;
import roidrole.thaumicinfo.Tags;
import thaumcraft.api.aspects.Aspect;

import javax.annotation.Nonnull;
import java.awt.*;

public class RendererAspect implements IWailaTooltipRenderer {
	@Nonnull
	@Override
	public Dimension getSize(String[] args, IWailaCommonAccessor accessor) {
		return new Dimension(8, 8);
	}

	@Override
	public void draw(String[] args, IWailaCommonAccessor accessor) {
		Aspect aspect = Aspect.getAspect(args[0]);
		AspectRenderer.drawAspect(aspect, 0, 0);
	}

	/*
	 * Utility method compatible with Show Aspect As String in-game config
	 */
	public static String showAspect(String essentiaString){
		if(essentiaString.isEmpty()){
			return I18n.format("tc.resonator3");
		}
		if(ConfigHandler.instance().getConfig(Tags.MOD_ID+".aspects_as_text")){
			return WordUtils.capitalizeFully(essentiaString);
		}
		return SpecialChars.WailaSplitter + SpecialChars.getRenderString("thaumicwaila.aspect", essentiaString);
	}
}
