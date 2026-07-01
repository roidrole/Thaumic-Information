package roidrole.thaumicinfo.jei.categories;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import roidrole.thaumicinfo.HEIPlugin;
import roidrole.thaumicinfo.Tags;
import roidrole.thaumicinfo.jei.AlphaDrawable;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CrucibleCategory extends AbstractResearchCategory<CrucibleCategory.CrucibleWrapper> {

    public static final String UUID = Tags.MOD_ID + ".crucible";
    public static final int ASPECT_Y = 66;
    public static final int ASPECT_X = 66;
    public static final int SPACE = 22;

    @Override
    public String getUid() {
        return UUID;
    }

    @Override
    public String getTitle() {
        return new ItemStack(BlocksTC.crucible).getDisplayName();
    }

    @Override
    public IDrawable getBackground() {
        return new AlphaDrawable(new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png"), 2, 5, 109, 129, 0, 0, 10, 10);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CrucibleWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, false, 61 - 6, 8);
        recipeLayout.getItemStacks().set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

        recipeLayout.getItemStacks().init(1, true, 2, 2);
        recipeLayout.getItemStacks().set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        int center = (ingredients.getInputs(HEIPlugin.ASPECT_LIST).size() * SPACE) / 2;
        int x = 0;
        for (List<AspectList> aspectList : ingredients.getInputs(HEIPlugin.ASPECT_LIST)) {
            recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).init(x + 1, true, ASPECT_X - center + x * SPACE, ASPECT_Y);
            recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).set(x + 1, aspectList);
            ++x;
        }
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook_overlay.png"));
        GL11.glEnable(3042);
        Gui.drawModalRectWithCustomSizedTexture(16, 6, 199, 168, 26, 26, 512, 512);
        GL11.glDisable(3042);
    }

    @Override
    public void populateRecipes() {
        ArrayList<CrucibleWrapper> list = new ArrayList<>();
        for (IThaumcraftRecipe recipe : ThaumcraftApi.getCraftingRecipes().values()) {
            if (recipe instanceof CrucibleRecipe) {
                list.add(new CrucibleCategory.CrucibleWrapper((CrucibleRecipe) recipe));
            }
        }
        list.trimToSize();
        this.recipes = list;
    }

    public static class CrucibleWrapper extends AbstractResearchWrapper {

        private final CrucibleRecipe recipe;


        public CrucibleWrapper(CrucibleRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(Arrays.asList(recipe.getCatalyst().getMatchingStacks())));
            ingredients.setInputs(HEIPlugin.ASPECT_LIST, Arrays.stream(recipe.getAspects().getAspectsSortedByAmount()).map(aspect -> new AspectList().add(aspect, recipe.getAspects().getAmount(aspect))).collect(Collectors.toList()));
            ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
        }

        @Override
        public int getBarrierX() {
            return 87;
        }

        @Override
        public int getBarrierY() {
            return 10;
        }

        @Override
        public String getCategory() {
            return CrucibleCategory.UUID;
        }

        @Override
        public String getResearch() {
            return recipe.getResearch();
        }
    }

}
