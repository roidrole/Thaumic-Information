package roidrole.thaumicinfo.mixins.accessors;

import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thaumcraft.client.gui.GuiResearchPage;

@Mixin(GuiResearchPage.class)
public interface AccessorGuiResearchPage {
	@Accessor(value = "shownRecipe",  remap = false)
	static ResourceLocation getShownRecipe(){
		throw new AssertionError();
	}

	@Accessor(value = "showingKnowledge",  remap = false)
	boolean getShowingKnowledge();

	@Accessor(value = "showingAspects",  remap = false)
	boolean getShowingAspects();
}
