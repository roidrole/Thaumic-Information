package roidrole.thaumicinfo.jei.categories;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import roidrole.thaumicinfo.Tags;
import roidrole.thaumicinfo.jei.AlphaDrawable;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ArcaneWorkbenchCategory extends AbstractResearchCategory<ArcaneWorkbenchCategory.ArcaneWorkbenchWrapper> {

    public static final String UUID = Tags.MOD_ID + ".arcane_workbench";

    @Override
    public String getUid() {
        return UUID;
    }

    @Override
    public String getTitle() {
        return new ItemStack(BlocksTC.arcaneWorkbench).getDisplayName();
    }

    @Override
    public IDrawable getBackground() {
        return new AlphaDrawable(new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png"), 225, 31, 102, 102, 36, 0, 30, 30);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png"));
        GL11.glEnable(3042);
        Gui.drawModalRectWithCustomSizedTexture(51 - 16 + 30, 0, 40, 6, 32, 32, 512, 512);
        Gui.drawModalRectWithCustomSizedTexture(-18 + 30, 4, 135, 152, 23, 23, 512, 512);
        GL11.glDisable(3042);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ArcaneWorkbenchWrapper recipeWrapper, IIngredients ingredients) {
        if (recipeWrapper.getRecipe() instanceof ShapelessArcaneRecipe) {
            recipeLayout.setShapeless();
        }
        recipeLayout.getItemStacks().init(0, false, 51 - 9 + 30, 7);
        int sizeX = 3;
        int sizeY = 3;
        int slot = 1;
        for (int y = 0; y < sizeY; ++y) {
            for (int x = 0; x < sizeX; ++x) {
                if (ingredients.getInputs(VanillaTypes.ITEM).size() >= slot) {
                    recipeLayout.getItemStacks().init(slot, true, 12 + (x * 30) + 30, 36 + 12 + (y) * 30);
                    if (ingredients.getInputs(VanillaTypes.ITEM).get(slot - 1) != null) {
                        recipeLayout.getItemStacks().set(slot, ingredients.getInputs(VanillaTypes.ITEM).get(slot - 1));
                    }
                    ++slot;
                }
            }
        }
        int crystalAmount = 0;
        if (recipeWrapper.getRecipe().getCrystals() != null) {
            for (Aspect aspect : recipeWrapper.getRecipe().getCrystals().getAspectsSortedByAmount()) {
                ItemStack crystal = new ItemStack(ItemsTC.crystalEssence);
                ((ItemCrystalEssence) ItemsTC.crystalEssence).setAspects(crystal, new AspectList().add(aspect, 1));
                crystal.setCount(recipeWrapper.getRecipe().getCrystals().getAmount(aspect));
                recipeLayout.getItemStacks().init(slot + crystalAmount, false, 118 + 23, 6 + 22 * crystalAmount);
                recipeLayout.getItemStacks().set(slot + crystalAmount, crystal);
                ++crystalAmount;
            }
        }

        recipeLayout.getItemStacks().set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void populateRecipes() {
        this.recipes = StreamSupport.stream(CraftingManager.REGISTRY.spliterator(), false)
            .filter(iRecipe -> iRecipe instanceof IArcaneRecipe)
            .map(iRecipe -> new ArcaneWorkbenchCategory.ArcaneWorkbenchWrapper((IArcaneRecipe) iRecipe))
            .collect(Collectors.toList())
        ;
    }

    public static class ArcaneWorkbenchWrapper extends AbstractResearchWrapper {

        private final IArcaneRecipe recipe;

        public ArcaneWorkbenchWrapper(IArcaneRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> lists = new ArrayList<>();
            List<Ingredient> input = new ArrayList<>();
            ItemStack output = null;
            if (recipe instanceof ShapelessArcaneRecipe) {
                input = recipe.getIngredients();
                output = recipe.getRecipeOutput();
            } else if (recipe instanceof ShapedArcaneRecipe){
                ShapedArcaneRecipe shapedArcaneRecipe = (ShapedArcaneRecipe) recipe;
                int sizeX = shapedArcaneRecipe.getRecipeWidth();
                int sizeY = shapedArcaneRecipe.getRecipeHeight();
                int slot = 0;
                for (int y = 0; y < 3; ++y) {
                    for (int x = 0; x < 3; ++x) {
                        if (y < sizeY && x < sizeX){
                            input.add(recipe.getIngredients().get(slot));
                            ++slot;
                        } else {
                            input.add(Ingredient.EMPTY);
                        }
                    }
                }
                output = recipe.getRecipeOutput();
            }
            for (Ingredient ingredient : input) {
                lists.add(Arrays.asList(ingredient.getMatchingStacks()));
            }
            ingredients.setInputLists(VanillaTypes.ITEM, lists);
            ingredients.setOutput(VanillaTypes.ITEM, output);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
            minecraft.fontRenderer.drawString(TextFormatting.DARK_GRAY + String.valueOf(recipe.getVis()), 50 - minecraft.fontRenderer.getStringWidth(String.valueOf(recipe.getVis())) / 2, 12, 0);
        }

        @Override
        public int getBarrierX() {
            return 108;
        }

        @Override
        public int getBarrierY() {
            return 8;
        }

        @Override
        public String getCategory() {
            return ArcaneWorkbenchCategory.UUID;
        }

        @Override
        public List<String> getTooltipStrings(int mouseX, int mouseY) {
            if (mouseX > 34 && mouseX < 60 && mouseY > 4 && mouseY < 28) {
                return Collections.singletonList("Vis Cost");
            }
            return super.getTooltipStrings(mouseX, mouseY);
        }

        public IArcaneRecipe getRecipe() {
            return recipe;
        }

        @Override
        public String getResearch() {
            return recipe.getResearch();
        }
    }
}
