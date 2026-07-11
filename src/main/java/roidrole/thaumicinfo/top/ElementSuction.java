package roidrole.thaumicinfo.top;


import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.IElementFactory;
import mcjty.theoneprobe.network.NetworkTools;
import thaumcraft.api.aspects.Aspect;

import javax.annotation.Nullable;
public class ElementSuction extends AbstractElementAspect {
	public ElementSuction(@Nullable Aspect aspect, int suction) {
		super(aspect, suction, "tc.resonator2");
	}

	@Override
	public int getID() {
		return ThaumicInformationTOPPlugin.SUCTION_ELEMENT_ID;
	}

	public static class Factory implements IElementFactory {

		@Override
		public IElement createElement(ByteBuf byteBuf) {
			return new ElementSuction(Aspect.getAspect(NetworkTools.readString(byteBuf)), byteBuf.readInt());
		}
	}
}
