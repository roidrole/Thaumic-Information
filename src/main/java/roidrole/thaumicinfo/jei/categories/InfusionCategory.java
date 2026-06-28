package roidrole.thaumicinfo.jei.categories;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import roidrole.thaumicinfo.HEIPlugin;
import roidrole.thaumicinfo.Tags;
import roidrole.thaumicinfo.jei.AlphaDrawable;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InfusionCategory extends AbstractResearchCategory<InfusionCategory.InfusionWrapper> {

    public static final String UUID = Tags.MOD_ID + ".infusion";
    public static final int ASPECT_Y = 135;
    public static final int ASPECT_X = 46;
    public static final int SPACE = 22;

    @Override
    public String getUid() {
        return UUID;
    }

    @Override
    public String getTitle() {
        return I18n.format("thaumicjei.category.infusion.title");
    }

    @Override
    public IDrawable getBackground() {
        return new AlphaDrawable(new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png"), 413, 154, 86, 86, 40, 44, 30, 30);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png"));
        GL11.glEnable(3042);
        Gui.drawModalRectWithCustomSizedTexture(27 + 30, 0, 40, 6, 32, 32, 512, 512);
        GL11.glDisable(3042);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, InfusionWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, false, 34 + 30, 7);
        recipeLayout.getItemStacks().set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        int slot = 1;
        float currentRotation = -90.0F;
        for (List<ItemStack> stacks : ingredients.getInputs(VanillaTypes.ITEM)) {
            if (slot == 1) recipeLayout.getItemStacks().init(slot, true, 34 + 30, 75);
            else
                recipeLayout.getItemStacks().init(slot, true, 30 + (int) (MathHelper.cos((float) (currentRotation / 180.0F * Math.PI)) * 40.0F) + 34, (int) (MathHelper.sin(currentRotation / 180.0F * 3.1415927F) * 40.0F) + 75);
            recipeLayout.getItemStacks().set(slot, stacks);
            currentRotation += (360f / recipeWrapper.recipe.getComponents().size());
            ++slot;
        }
        int center = (ingredients.getInputs(HEIPlugin.ASPECT_LIST).size() * SPACE) / 2;
        int x = 0;
        for (List<AspectList> aspectList : ingredients.getInputs(HEIPlugin.ASPECT_LIST)) {
            recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).init(x + slot, true, 30 + ASPECT_X - center + x * SPACE, ASPECT_Y);
            recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST).set(x + slot, aspectList);
            ++x;
        }
    }

    @Override
    public void populateRecipes() {
        ArrayList<InfusionWrapper> list = new ArrayList<>();
        for (IThaumcraftRecipe recipe : ThaumcraftApi.getCraftingRecipes().values()) {
            if (recipe instanceof InfusionRecipe) {
                InfusionRecipe infusion = (InfusionRecipe) recipe;
                if (infusion.getRecipeInput() != null && infusion.recipeOutput != null) {
                    list.add(new InfusionCategory.InfusionWrapper(infusion));
                }
            }
        }
        list.trimToSize();
        this.recipes = list;
    }

    public static class InfusionWrapper extends AbstractResearchWrapper {

        private final InfusionRecipe recipe;

        public InfusionWrapper(InfusionRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<>();
            inputs.add(Arrays.asList(recipe.getRecipeInput().getMatchingStacks()));
            if (recipe.recipeOutput instanceof ItemStack) {
                ingredients.setOutput(VanillaTypes.ITEM, ((ItemStack) recipe.recipeOutput).copy());
            } else if (recipe.recipeOutput != null) {
                for (ItemStack stack : inputs.get(0)) {
                    if (stack != null) {
                        Object[] objects = (Object[]) recipe.recipeOutput;
                        ItemStack copied = stack.copy();
                        copied.setTagInfo((String) objects[0], (NBTBase) objects[1]);
                        ingredients.setOutput(VanillaTypes.ITEM, copied);
                    }
                }
            }
            for (Ingredient comp : recipe.getComponents()) {
                inputs.add(Arrays.asList(comp.getMatchingStacks()));
            }
            ingredients.setInputLists(VanillaTypes.ITEM, inputs);
            ingredients.setInputs(HEIPlugin.ASPECT_LIST, Arrays.stream(recipe.aspects.getAspectsSortedByAmount()).map(aspect -> new AspectList().add(aspect, recipe.aspects.getAmount(aspect))).collect(Collectors.toList()));
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
            int instability = Math.min(5, recipe.instability / 2);
            String inst = TextFormatting.DARK_GRAY + new TextComponentTranslation("tc.inst").getFormattedText() + new TextComponentTranslation("tc.inst." + instability).getUnformattedText();
            minecraft.fontRenderer.drawString(inst, (recipeWidth / 2) - (minecraft.fontRenderer.getStringWidth(inst) / 2), 158, 0);
        }

        @Override
        public int getBarrierX() {
            return 92;
        }

        @Override
        public int getBarrierY() {
            return 9;
        }

        @Override
        public String getResearch() {
            return recipe.research;
        }
    }

}
