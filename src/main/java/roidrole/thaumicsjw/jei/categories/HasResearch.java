package roidrole.thaumicsjw.jei.categories;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.client.gui.GuiResearchPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class HasResearch implements IRecipeWrapper {
    public abstract String getResearch();
    public abstract int getBarrierX();
    public abstract int getBarrierY();

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if(mouseX < getBarrierX() || mouseX > getBarrierX() + 16 || mouseY < getBarrierY() || mouseY > getBarrierY() + 16){
            return Collections.emptyList();
        }
        if (this.getResearchEntry() == null){
            return Collections.emptyList();
        }
        if (ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, getResearch())){
            return Collections.singletonList(TextFormatting.GREEN + "View in thaumonomicon");
        }
        if (knowsParents()){
            return Arrays.asList(
                TextFormatting.GOLD + "Missing research: ",
                "- " + TextFormatting.YELLOW + this.getResearchEntry().getLocalizedName(),
                TextFormatting.YELLOW + "View in thaumonomicon"
            );
        }
        List<String> list = new ArrayList<>();
        list.add(TextFormatting.GOLD + "Missing research:");
        for (String s : getResearch().split("&&")) {
            if (!ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, s)) {
                ResearchEntry entry = ResearchCategories.getResearch(s.contains("@") ? s.split("@")[0] : s);
                if (entry != null) {
                    list.add("- " + TextFormatting.RED + entry.getLocalizedName());
                }
                else {
                    list.add("- " + TextFormatting.RED + s);
                }
            }
        }

        return list;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        ResourceLocation book;
        if (ThaumcraftCapabilities.knowsResearch(minecraft.player, this.getResearch())) {
            book = new ResourceLocation("thaumicjei", "textures/gui/thaumonomicon_green.png");
        } else if(knowsParents()){
            book = new ResourceLocation(Thaumcraft.MODID, "textures/items/thaumonomicon.png");
        } else {
            book = new ResourceLocation("thaumicjei", "textures/gui/thaumonomicon_red.png");
        }
        minecraft.getTextureManager().bindTexture(book);

        GlStateManager.enableBlend();
        Gui.drawScaledCustomSizeModalRect(getBarrierX(), getBarrierY(), 0f, 0f, 16, 16, 16, 16, 16, 16);
        GlStateManager.disableBlend();

    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        if(mouseX > getBarrierX() && mouseX < getBarrierX() + 16 && mouseY > getBarrierY() && mouseY < getBarrierY() + 16){
            if(this.knowsParents()){
                minecraft.displayGuiScreen(new GuiResearchPage(this.getResearchEntry(), null, 0, 0));
            }
        }
        return IRecipeWrapper.super.handleClick(minecraft, mouseX, mouseY, mouseButton);
    }

    private ResearchEntry getResearchEntry(){
        ResearchEntry research;
        for(ResearchCategory category : ResearchCategories.researchCategories.values()){
            research = category.research.get(this.getResearch().split("@")[0]);
            if(research != null){
                return research;
            }
        }
        return null;
    }

    private boolean knowsParents(){
        ResearchEntry research = getResearchEntry();
        if(research == null){
            return false;
        }
        if(research.getParents() == null){
            return true;
        }
        return ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, research.getParents());
    }

}
