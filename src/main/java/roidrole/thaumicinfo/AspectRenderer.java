package roidrole.thaumicinfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import thaumcraft.api.aspects.Aspect;

public class AspectRenderer {
	public static void drawAspect(Aspect aspect, int x, int y) {
		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(aspect.getImage());
		GlStateManager.enableBlend();

		GlStateManager.color(
			(float) ((aspect.getColor() >> 16) & 0xFF) / 255.0F,
			(float) ((aspect.getColor() >> 8) & 0xFF) / 255.0F,
			(float) ((aspect.getColor()) & 0xFF) / 255.0F
		);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 8, 8, 8, 8);
		GlStateManager.popMatrix();
	}
}
