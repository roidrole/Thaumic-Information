package roidrole.thaumicroid.jei.categories;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import roidrole.thaumicroid.HEIPlugin;
import roidrole.thaumicroid.Tags;
import roidrole.thaumicroid.jei.AlphaDrawable;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.AspectList;

import java.util.List;

public class AspectFromItemStackCategory implements IRecipeCategory<AspectFromItemStackCategory.AspectFromItemStackWrapper> {

    public static final String UUID = Tags.MOD_ID + ".aspect_from_itemstack";

    @Override
    public String getUid() {
        return UUID;
    }

    @Override
    public String getTitle() {
        return I18n.format("thaumicjei.category.aspect_from_itemstack.title");
    }

    @Override
    public String getModName() {
        return Tags.MOD_NAME;
    }

    @Override
    public IDrawable getBackground() {
        return new AlphaDrawable(
            new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png"),
            40,
            6,
            32,
            32,
            0,
            18 * 4 + 5,
            72,
            72
        );
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(new ResourceLocation("thaumicjei", "textures/gui/gui.png"));
        GL11.glEnable(3042);
        Gui.drawModalRectWithCustomSizedTexture(-66 + 81 - 9, 31, 0, 0, 163, 74, 256, 256);
        GL11.glDisable(3042);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AspectFromItemStackWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).init(0, false, 8 + 81 - 9, 8);
        recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).set(0, ingredients.getOutputs(HEIPlugin.ASPECT_LIST).get(0));
        int slot = 0;
        int row = 9;
        for (List<ItemStack> stacks : ingredients.getInputs(VanillaTypes.ITEM)) {
            recipeLayout.getItemStacks().init(slot + 1, true, (slot % row) * 18 - 18 * 3 - 21 + 81, (slot / row) * 18 + 32);
            recipeLayout.getItemStacks().set(slot + 1, stacks);
            ++slot;
        }
    }

    public static class AspectFromItemStackWrapper implements IRecipeWrapper {

        private final AspectList aspect;
        private final List<ItemStack> stacks;

        public AspectFromItemStackWrapper(AspectList aspect, List<ItemStack> stacks) {
            this.aspect = aspect;
            this.stacks = stacks;
        }


        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setOutput(HEIPlugin.ASPECT_LIST, aspect);
            ingredients.setInputs(VanillaTypes.ITEM, stacks);
        }
    }

}
