package roidrole.thaumicinfo.jei.categories;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import roidrole.thaumicinfo.HEIPlugin;
import roidrole.thaumicinfo.Tags;
import roidrole.thaumicinfo.jei.AlphaDrawable;
import roidrole.thaumicinfo.utils.ThaumicInfoUtils;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;

import java.util.*;

public class CrucibleCategory extends AbstractResearchCategory<CrucibleCategory.CrucibleWrapper> {

    public static final String UUID = Tags.MOD_ID + ".crucible";
    public static final int ASPECT_Y = 80;
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

        IGuiIngredientGroup<AspectList> ingredientGroup = recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST);
        List<List<AspectList>> inputs = ingredients.getInputs(HEIPlugin.ASPECT_LIST);
        int maxY = MathUtil.divideCeil(inputs.size(), 4);
        int startY = ASPECT_Y - (int)((float)maxY/2 * SPACE);
        for (int y = 0; y < maxY; y++) {
            int yCoord = startY + SPACE * y;
            int maxX = Math.min(4, inputs.size() - 4 * y);
            int startX = ASPECT_X - (maxX * SPACE) / 2;
            for (int x = 0; x < maxX; x++) {
                int slot = y * 4 + x;
                List<AspectList> ingredient = inputs.get(slot);
                ingredientGroup.init(slot, true, startX + x * SPACE, yCoord);
                ingredientGroup.set(slot, ingredient);
            }
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
            ingredients.setInputs(HEIPlugin.ASPECT_LIST, ThaumicInfoUtils.splitAspectList(recipe.getAspects()));
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
