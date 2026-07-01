package roidrole.thaumicinfo.jei.categories;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import roidrole.thaumicinfo.Tags;
import roidrole.thaumicinfo.mixins.accessors.AccessorGuiResearchPage;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.client.gui.GuiResearchPage;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * TODO: turn this class into an IIngredientType (ResearchEntry) instead
 * Cheating in that IIngredientType can mean getting the research paper for it
 * The recipe for it would be opening it in JEI
 * And Thaumcraft already has all the assets to display it.
 */

public abstract class AbstractResearchWrapper implements IRecipeWrapper {
    @Nullable
    public abstract String getResearch();
    public abstract int getBarrierX();
    public abstract int getBarrierY();
    public abstract String getCategory();

    static GuiScreen oldGui = null;
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
        if(this.getResearch() == null){
            return;
        }
        ResourceLocation book;
        if (ThaumcraftCapabilities.knowsResearch(minecraft.player, this.getResearch())) {
            if(this.getResearchEntry() == null){
                return;
            }
            book = new ResourceLocation(Tags.MOD_ID, "textures/gui/thaumonomicon_green.png");
        } else if(knowsParents()){
            book = new ResourceLocation(Thaumcraft.MODID, "textures/items/thaumonomicon.png");
        } else {
            book = new ResourceLocation(Tags.MOD_ID, "textures/gui/thaumonomicon_red.png");
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
                oldGui = minecraft.currentScreen;
                minecraft.displayGuiScreen(new GuiResearchPageJEI(this.getResearchEntry(), null, 0, 0));
            }
        }
        return IRecipeWrapper.super.handleClick(minecraft, mouseX, mouseY, mouseButton);
    }

    private ResearchEntry getResearchEntry(){
        if(this.getResearch() == null){
            return null;
        }
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

    public static class GuiResearchPageJEI extends GuiResearchPage {

        public GuiResearchPageJEI(ResearchEntry research, ResourceLocation recipe, double x, double y) {
            super(research, recipe, x, y);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode != this.mc.gameSettings.keyBindInventory.getKeyCode() && keyCode != 1){
                super.keyTyped(typedChar, keyCode);
                return;
            }

            if(
                AccessorGuiResearchPage.getShownRecipe() == null &&
                !((AccessorGuiResearchPage)this).getShowingAspects() &&
                !((AccessorGuiResearchPage)this).getShowingKnowledge()
            ){
                history.clear();
                this.mc.displayGuiScreen(oldGui);
                oldGui = null;
            } else {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }
}
