package roidrole.thaumicinfo.top;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.apiimpl.client.ElementTextRender;
import mcjty.theoneprobe.network.NetworkTools;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import roidrole.thaumicinfo.AspectRenderer;
import roidrole.thaumicinfo.ThaumicInformationConfig;
import thaumcraft.api.aspects.Aspect;

import javax.annotation.Nullable;

public abstract class AbstractElementAspect implements IElement {
	@Nullable
	protected final Aspect aspect; // null represents error state
	protected final int amount;
	protected final String string;

	public AbstractElementAspect(@Nullable Aspect aspect, int amount, String translationKey) {
		this.aspect = aspect;
		this.amount = amount;
		this.string = I18n.format(
			translationKey,
			"§r" + this.amount,
			getAspectString()
		);
	}

	@Override
	public void render(int x, int y) {
		Minecraft minecraft = Minecraft.getMinecraft();
		RenderHelper.renderText(minecraft, x, y, string);
		if(aspect == null || ThaumicInformationConfig.hwylaConfig.showAspectsAsText){
			return;
		}
		int stringIndex = string.indexOf("  ");
		if(stringIndex == -1){
			return;
		}
		int stringWidth = ElementTextRender.getWidth(string.substring(0, stringIndex)) + minecraft.fontRenderer.getCharWidth(' ');
		AspectRenderer.drawAspect(aspect, x + stringWidth, y);
	}

	@Override
	public int getHeight() {
		return 10;
	}

	@Override
	public int getWidth() {
		return ElementTextRender.getWidth(string);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if(aspect == null){
			buf.writeInt(-1);
		} else {
			NetworkTools.writeString(buf, aspect.getTag());
		}
		buf.writeInt(amount);
	}

	private String getAspectString(){
		if(this.aspect == null){
			return I18n.format("tc.resonator3");
		} else if(ThaumicInformationConfig.hwylaConfig.showAspectsAsText){
			return this.aspect.getName();
		} else {
			return "  ";
		}
	}
}