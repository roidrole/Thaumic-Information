package roidrole.thaumicroid.jei.categories;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import roidrole.thaumicroid.HEIPlugin;
import roidrole.thaumicroid.Tags;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AspectCompoundCategory implements IRecipeCategory<AspectCompoundCategory.AspectCompoundWrapper> {

    public static final String UUID = Tags.MOD_ID + ".aspect_compound";

    private final IGuiHelper helper;

    public AspectCompoundCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public String getUid() {
        return UUID;
    }

    @Override
    public String getTitle() {
        return I18n.format("thaumicjei.category.aspect_compound.title");
    }

    @Override
    public String getModName() {
        return Tags.MOD_NAME;
    }

    @Override
    public IDrawable getBackground() {
        return helper.createBlankDrawable(108, 24);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.fontRenderer.drawString(TextFormatting.DARK_GRAY + "=", 32, 6, 0);
        minecraft.fontRenderer.drawString(TextFormatting.DARK_GRAY + "+", 70, 6, 0);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AspectCompoundCategory.AspectCompoundWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).init(0, true, 8, 2);
        recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).init(1, false, 46, 2);
        recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).init(2, false, 82, 2);
        recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).set(0, ingredients.getInputs(HEIPlugin.ASPECT_LIST).get(0));
        recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).set(1, ingredients.getOutputs(HEIPlugin.ASPECT_LIST).get(0));
        recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).set(2, ingredients.getOutputs(HEIPlugin.ASPECT_LIST).get(1));
    }


    public static class AspectCompoundWrapper implements IRecipeWrapper {

        private final Aspect aspect;

        public AspectCompoundWrapper(Aspect aspect) {
            this.aspect = aspect;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInput(HEIPlugin.ASPECT_LIST, new AspectList().add(this.aspect, 1));
            ingredients.setOutputs(
                HEIPlugin.ASPECT_LIST,
                Arrays.stream(this.aspect.getComponents())
                    .map(aspect -> new AspectList().add(aspect, 1))
                    .collect(Collectors.toList())
            );
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glScaled(0.5, 0.5, 0.5);
            String name;
            name = TextFormatting.DARK_GRAY + aspect.getName();
            minecraft.fontRenderer.drawString(name, 32 - minecraft.fontRenderer.getStringWidth(name) / 2, 36, 0);
            name = TextFormatting.DARK_GRAY + aspect.getComponents()[0].getName();
            minecraft.fontRenderer.drawString(name, 109 - minecraft.fontRenderer.getStringWidth(name) / 2, 36, 0);
            name = TextFormatting.DARK_GRAY + aspect.getComponents()[1].getName();
            minecraft.fontRenderer.drawString(name, 181 - minecraft.fontRenderer.getStringWidth(name) / 2, 36, 0);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
    }
}
