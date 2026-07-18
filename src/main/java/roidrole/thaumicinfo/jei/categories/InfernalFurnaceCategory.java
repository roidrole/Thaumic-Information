package roidrole.thaumicinfo.jei.categories;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.config.Constants;
import mezz.jei.gui.elements.DrawableResource;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import roidrole.thaumicinfo.Tags;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InfernalFurnaceCategory implements IRecipeCategory<InfernalFurnaceCategory.InfernalFurnaceWrapper> {

	public static final String UUID = Tags.MOD_ID + ".infernal_furnace";

	private final IDrawable background;
	private final IDrawable furnace_background;
	private final IDrawable icon;
	private final IDrawable slot_drawable;
	private final String localizedName;
	protected final IDrawableStatic staticFlame;
	protected final IDrawableAnimated animatedFlame;
	protected final IDrawableAnimated arrow;

	public InfernalFurnaceCategory(IGuiHelper helper) {
		background = helper.createBlankDrawable(103, 54);
		furnace_background = helper.createDrawable(Constants.RECIPE_GUI_VANILLA, 0, 114, 82, 54);
		icon = new DrawableResource(new ResourceLocation(Thaumcraft.MODID, "textures/research/r_infernalfurnace.png"), 0, 0, 16, 16, 0, 0, 0, 0, 16, 16);
		localizedName = I18n.format("tile.infernal_furnace.name");
		staticFlame = helper.createDrawable(Constants.RECIPE_GUI_VANILLA, 82, 114, 14, 14);
		animatedFlame = helper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);
		slot_drawable = helper.getSlotDrawable();

		arrow = helper.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17)
			.buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Override
	public String getUid() {
		return UUID;
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public String getModName() {
		return Tags.MOD_NAME;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Nullable
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, InfernalFurnaceWrapper wrapper, IIngredients ingredients) {
		IFocus<?> focus = layout.getFocus();
		IGuiItemStackGroup guiItemStacks = layout.getItemStacks();

		guiItemStacks.init(0, true, 0, 0);
		guiItemStacks.init(1, false, 60, 18);
		guiItemStacks.init(2, false, 85, 18);

		if(focus == null || !(focus.getValue() instanceof ItemStack)) {
			guiItemStacks.set(ingredients);
		} else {
			ItemStack focusStack = (ItemStack) focus.getValue();
			List<ItemStack> inputs = ingredients.getInputs(VanillaTypes.ITEM).get(0);
			List<ItemStack> outputMain = ingredients.getOutputs(VanillaTypes.ITEM).get(0);
			List<ItemStack> outputBonus = ingredients.getOutputs(VanillaTypes.ITEM).get(1);

			List<ItemStack> toIterate;
			if(focus.getMode() == IFocus.Mode.INPUT){
				toIterate = inputs;
			} else if(focus.getMode() == IFocus.Mode.OUTPUT){
				toIterate = outputMain;
			} else {
				toIterate = Collections.emptyList();
			}
			int index = 0;
			for (; index < inputs.size(); index++) {
				if(OreDictionary.itemMatches(toIterate.get(index), focusStack, false)){
					break;
				}
			}
			if(index == inputs.size()){
				//If no match is found, we are likely focusing on the bonus output, which matches all input and output
				guiItemStacks.set(ingredients);
			} else {
				guiItemStacks.set(0, inputs.get(index));
				guiItemStacks.set(1, outputMain.get(index));
				guiItemStacks.set(2, outputBonus);
			}
		}
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		furnace_background.draw(minecraft);
		slot_drawable.draw(minecraft, 85, 18);
	}

	public static class InfernalFurnaceWrapper implements IRecipeWrapper {
		private final List<ItemStack> input;
		private final List<ItemStack> output;
		private final List<ItemStack> bonus;
		private final int chance;

		private float experience = -1;

		public InfernalFurnaceWrapper(ThaumcraftApi.SmeltBonus bonus) {
			FurnaceRecipes recipes = FurnaceRecipes.instance();
			List<ItemStack> inRaw;
			if(bonus.in instanceof ItemStack){
				ItemStack input = (ItemStack) bonus.in;
				if(input.getItemDamage() == OreDictionary.WILDCARD_VALUE){
					CreativeTabs tab = input.getItem().getCreativeTab();
					if(tab != null){
						inRaw = NonNullList.create();
						input.getItem().getSubItems(tab, (NonNullList<ItemStack>) inRaw);
					} else {
						inRaw = Collections.singletonList(input);
					}
				} else {
					inRaw = Collections.singletonList(input);
				}
			} else {
				inRaw = OreDictionary.getOres((String)bonus.in);
			}
			this.output = new ArrayList<>(inRaw.size());
			this.input = new ArrayList<>(inRaw.size());
			for(ItemStack input : inRaw){
				if(input.isEmpty()) {
					continue;
				}
				ItemStack output = recipes.getSmeltingResult(input);
				if(output.isEmpty()) {
					continue;
				}
				this.input.add(input);
				this.output.add(output);
			}
			this.bonus = Collections.singletonList(bonus.out);
			this.chance = (int) (bonus.chance * 100);
		}

		public boolean isValid(){
			return !output.isEmpty() && !output.get(0).isEmpty();
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(this.input));
			ingredients.setOutputLists(VanillaTypes.ITEM, Arrays.asList(this.output, this.bonus));
		}

		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			if (this.experience == -1) {
				this.experience = FurnaceRecipes.instance().getSmeltingExperience(output.get(0));
			}
			if (this.experience > 0) {
				String experienceString = Translator.translateToLocalFormatted("gui.jei.category.smelting.experience", experience);
				FontRenderer fontRenderer = minecraft.fontRenderer;
				int stringWidth = fontRenderer.getStringWidth(experienceString);
				fontRenderer.drawString(experienceString, recipeWidth - stringWidth, 0, Color.gray.getRGB());
			}

			String chanceString = String.valueOf(chance) + '%';
			FontRenderer fontRenderer = minecraft.fontRenderer;
			fontRenderer.drawString(
				chanceString,
				recipeWidth - fontRenderer.getStringWidth(chanceString) + 1,
				37,
				Color.white.getRGB()
			);
		}
	}
}
