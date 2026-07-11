package roidrole.thaumicinfo.top;


import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mcjty.theoneprobe.network.NetworkTools;
import thaumcraft.api.aspects.Aspect;

import javax.annotation.Nullable;

public class ElementContent extends AbstractElementAspect {
	public ElementContent(@Nullable Aspect aspect, int amount) {
		super(aspect, amount, "tc.resonator1");
	}

	@Override
	public int getID() {
		return ThaumicInformationTOPPlugin.CONTENT_ELEMENT_ID;
	}

	public static class Factory implements IElementFactory {

		@Override
		public IElement createElement(ByteBuf byteBuf) {
			return new ElementContent(Aspect.getAspect(NetworkTools.readString(byteBuf)), byteBuf.readInt());
		}
	}
}
