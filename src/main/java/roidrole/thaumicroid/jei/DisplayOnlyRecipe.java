package roidrole.thaumicroid.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.List;

public class DisplayOnlyRecipe implements ICraftingRecipeWrapper {
	ItemStack output;
	List<List<ItemStack>> inputs;

	public DisplayOnlyRecipe(ItemStack output, List<List<ItemStack>> inputs) {
		this.inputs = inputs;
		this.output = output;
	}

	@Override
	public void getIngredients(IIngredients iIngredients) {
		iIngredients.setOutput(VanillaTypes.ITEM, output);
		iIngredients.setInputLists(VanillaTypes.ITEM, inputs);
	}
}
