package roidrole.thaumicroid.mixins;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.client.lib.events.RenderEventHandler;

@Mixin(RenderEventHandler.class)
public abstract class AspectTooltipEverywhere {
	@Redirect(
		method = "tooltipEvent(Lnet/minecraftforge/event/entity/player/ItemTooltipEvent;)V",
		at = @At(
			value = "CONSTANT",
			args = "classValue=net/minecraft/client/gui/inventory/GuiContainer"
		),
		remap = false
	)
	private static boolean skipComparingTooltipEvent(Object targetObj, Class<?> classValue){
		return true;
	}


	//Thaumcraft calls a method to render the tooltip that specifically takes a GuiScreen to only call .zLevel.
	// We can therefore create a class extending GuiContainer that is a wrapper for a zLevel.
	@ModifyVariable(
		method = "tooltipEvent(Lnet/minecraftforge/client/event/RenderTooltipEvent$PostBackground;)V",
		at = @At("STORE"),
		name = "gui",
		remap = false
	)
	private static GuiScreen skipComparingPostBackground(GuiScreen gui){
		return new ZLevelGuiContainer(gui.zLevel);
	}

	private static class ZLevelGuiContainer extends GuiContainer {
		public ZLevelGuiContainer(float zLevel){
			super(null);
			this.zLevel = zLevel;
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			//No-op
		}
	}
}

