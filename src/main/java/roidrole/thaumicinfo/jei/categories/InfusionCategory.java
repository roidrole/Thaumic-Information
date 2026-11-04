package roidrole.thaumicinfo.jei.categories;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import roidrole.thaumicinfo.HEIPlugin;
import roidrole.thaumicinfo.Tags;
import roidrole.thaumicinfo.jei.AlphaDrawable;
import roidrole.thaumicinfo.utils.ThaumicInfoUtils;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfusionCategory extends AbstractResearchCategory<InfusionCategory.InfusionWrapper> {

    public static final String UUID = Tags.MOD_ID + ".infusion";
    public static final int ASPECT_Y = 130;
    public static final int ASPECT_X = 76;
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
        return new AlphaDrawable(new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png"), 413, 154, 86, 86, 40, 64, 30, 30);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png"));
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(27 + 30, 0, 40, 6, 32, 32, 512, 512);
        GlStateManager.disableBlend();
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, InfusionWrapper recipeWrapper, IIngredients ingredients) {

        //ItemStacks
        IGuiItemStackGroup guiGroup = recipeLayout.getItemStacks();
        guiGroup.init(0, false, 34 + 30, 7);
        guiGroup.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        int slot = 1;
        float currentRotation = -90.0F;
        for (List<ItemStack> stacks : ingredients.getInputs(VanillaTypes.ITEM)) {
            if (slot == 1) {
                guiGroup.init(slot, true, 34 + 30, 75);
            }
            else {
                float angle = (float) (currentRotation / 180.0F * Math.PI);
                guiGroup.init(slot, true,
                    30 + (int) (MathHelper.cos(angle) * 40.0F) + 34,
                    (int) (MathHelper.sin(angle) * 40.0F) + 75
                );
            }
            guiGroup.set(slot, stacks);
            currentRotation += (360f / recipeWrapper.recipe.getComponents().size());
            ++slot;
        }

        //Aspects
        IGuiIngredientGroup<AspectList> guiGroupAspects = recipeLayout.getIngredientsGroup(HEIPlugin.ASPECT_LIST);
        List<List<AspectList>> aspects = ingredients.getInputs(HEIPlugin.ASPECT_LIST);
        int rowAmount = MathUtil.divideCeil(aspects.size(), 7);
        int minY = (rowAmount == 1)? ASPECT_Y + SPACE/2: ASPECT_Y;
		for (int y = 0; y < rowAmount; y++) {
            int yCoord = minY + SPACE * y;
            int maxX = Math.min(aspects.size() - 7 * y, 7);
            int startX = ASPECT_X - (maxX * SPACE) / 2;
            for (int x = 0; x < maxX; x++) {
                slot = y * 7 + x;
                guiGroupAspects.init(slot, true, startX + x * SPACE, yCoord);
                guiGroupAspects.set(slot, aspects.get(slot));
            }
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
            List<List<ItemStack>> inputs = new ArrayList<>(recipe.getComponents().size() + 1);
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
            ingredients.setInputs(HEIPlugin.ASPECT_LIST, ThaumicInfoUtils.splitAspectList(recipe.aspects));
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
            int instability = Math.min(5, recipe.instability / 2);
            String inst = TextFormatting.DARK_GRAY + new TextComponentTranslation("tc.inst").getFormattedText() + new TextComponentTranslation("tc.inst." + instability).getUnformattedText();
            minecraft.fontRenderer.drawString(inst, (recipeWidth / 2) - (minecraft.fontRenderer.getStringWidth(inst) / 2), 178, 0);
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
        public String getCategory() {
            return InfusionCategory.UUID;
        }

        @Override
        public String getResearch() {
            return recipe.research;
        }
    }

}
