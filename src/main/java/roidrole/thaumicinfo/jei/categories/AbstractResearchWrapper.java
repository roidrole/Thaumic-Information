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
import roidrole.thaumicinfo.utils.ThaumicInfoUtils;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.client.gui.GuiResearchPage;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

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
        if(!isMouseOnBarrier(mouseX, mouseY)){
            return Collections.emptyList();
        }
        ResearchEntry firstResearch = ThaumicInfoUtils.getEntry(this.getResearch());
        if (firstResearch == null){
            return Collections.emptyList();
        }
        if (ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, getResearch())){
            return Collections.singletonList(TextFormatting.GREEN + "View in thaumonomicon");
        }
        if (knowsParents()){
            return Arrays.asList(
                TextFormatting.GOLD + "Missing research: ",
                prepareResearch(firstResearch, TextFormatting.YELLOW),
                TextFormatting.YELLOW + "View in thaumonomicon"
            );
        }
        String[] researchSplit = getResearch().split("&&");

        Set<ResearchEntry> searchesEntries = new HashSet<>();
        Queue<ResearchEntry> BFSQueue = new LinkedList<>();
        for(String research: researchSplit){
            ResearchEntry entry = ThaumicInfoUtils.getEntry(research);
            if(searchesEntries.add(entry)) {
                BFSQueue.offer(entry);
            }
        }
        LinkedHashSet<String> tooltip = new LinkedHashSet<>(researchSplit.length + 1);
        while(!BFSQueue.isEmpty()){
            ResearchEntry research = BFSQueue.poll();
            if(research == null){
                continue;
            }
            if (!ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, research.getKey())) {
				tooltip.add(prepareResearch(research, TextFormatting.RED));
                String[] parents = research.getParentsStripped();
                if(parents == null){
                    continue;
                }
                for(String parent : parents){
                    ResearchEntry entry = ThaumicInfoUtils.getEntry(parent);
                    if(searchesEntries.add(entry)) {
                        BFSQueue.offer(entry);
                    }
                }
			}
        }

        List<String> out = Arrays.asList(new String[tooltip.size() + 1]);
        int index = tooltip.size();
        for(String line : tooltip){
            out.set(index, line);
            index--;
        }
        out.set(0, TextFormatting.GOLD + "Missing research:");


        return out;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if(this.getResearch() == null){
            return;
        }
        ResourceLocation book;
        if (ThaumcraftCapabilities.knowsResearch(minecraft.player, this.getResearch())) {
            if(ThaumicInfoUtils.getEntry(this.getResearch()) == null){
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
        if(isMouseOnBarrier(mouseX, mouseY)){
            if(this.knowsParents()){
                oldGui = minecraft.currentScreen;
                minecraft.displayGuiScreen(new GuiResearchPageJEI(ThaumicInfoUtils.getEntry(this.getResearch()), null, 0, 0));
            }
        }
        return IRecipeWrapper.super.handleClick(minecraft, mouseX, mouseY, mouseButton);
    }

    private static String prepareResearch(ResearchEntry research, TextFormatting format){
        String categoryName = ResearchCategories.getCategoryName(research.getCategory());
        return "- " + format + categoryName + ": " + research.getLocalizedName();
    }

    private boolean knowsParents(){
        ResearchEntry research = ThaumicInfoUtils.getEntry(this.getResearch());
        if(research == null){
            return false;
        }
        if(research.getParents() == null){
            return true;
        }
        return ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, research.getParents());
    }

    private boolean isMouseOnBarrier(int mouseX, int mouseY){
        int barrierX = getBarrierX();
        int barrierY = getBarrierY();
		return
            mouseX >= barrierX && mouseX <= barrierX + 16 &&
            mouseY >= barrierY && mouseY <= barrierY + 16;
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
