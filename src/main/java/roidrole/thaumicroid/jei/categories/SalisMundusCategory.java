package roidrole.thaumicroid.jei.categories;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.config.Constants;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import roidrole.thaumicroid.HEIPlugin;
import roidrole.thaumicroid.Tags;
import roidrole.thaumicroid.mixins.accessors.AccessorDustTriggerOre;
import roidrole.thaumicroid.mixins.accessors.AccessorDustTriggerSimple;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.crafting.DustTriggerOre;
import thaumcraft.common.lib.crafting.DustTriggerSimple;

import java.util.*;

public class SalisMundusCategory extends AbstractResearchCategory<SalisMundusCategory.SalisMundusRecipeWrapper> {

	public static final String UUID = Tags.MOD_ID + ".salis_mundus";
	public static final String title = new ItemStack(ItemsTC.salisMundus).getDisplayName();
	public final IDrawable background;

	//Shared ItemStack for all wrappers
	public static final List<ItemStack> salisMundus = Collections.singletonList(new ItemStack(ItemsTC.salisMundus));

	public SalisMundusCategory(IJeiHelpers helper){
		super();
		this.background = helper.getGuiHelper()
			.drawableBuilder(Constants.RECIPE_GUI_VANILLA, 0, 168, 125, 18)
			.addPadding(14, 0, 0, 0)
			.build();
	}

	@Override
	public String getUid() {
		return UUID;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void populateRecipes() {
		this.recipes = new ArrayList<>();
		for (IDustTrigger trigger : IDustTrigger.triggers) {
			SalisMundusRecipeWrapper wrapper;
			if(trigger instanceof DustTriggerSimple){
				wrapper = new SalisMundusSimpleWrapper((DustTriggerSimple)trigger);
			}
			else if(trigger instanceof DustTriggerOre){
				wrapper = new SalisMundusOreWrapper((DustTriggerOre)trigger);
			} else {
				continue;
			}
			if(wrapper.isValid()) {
				this.recipes.add(wrapper);
			}
		}
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, SalisMundusRecipeWrapper wrapper, IIngredients ingredients) {
		IGuiIngredientGroup<ItemStack> group = layout.getIngredientsGroup(VanillaTypes.ITEM);
		//Salis Mundus
		group.init(0, true,  0, 14);

		//Block to click
		group.init(1, true,  49, 14);

		//Output
		group.init(2, false,  107, 14);

		group.set(ingredients);
	}

	public static abstract class SalisMundusRecipeWrapper extends HasResearch {
		List<List<ItemStack>> input;
		List<List<ItemStack>> output;
		String research;

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputLists(VanillaTypes.ITEM, this.input);
			ingredients.setOutputLists(VanillaTypes.ITEM, this.output);
		}

		@Override
		public String getResearch() {
			return this.research;
		}

		@Override
		public int getBarrierX() {
			return 78;
		}

		@Override
		public int getBarrierY() {
			return 0;
		}

		public boolean isValid(){
			return !this.input.get(1).isEmpty();
		}
	}

	public static class SalisMundusSimpleWrapper extends SalisMundusRecipeWrapper {
		public SalisMundusSimpleWrapper(DustTriggerSimple trigger) {
			AccessorDustTriggerSimple simpleTrigger = (AccessorDustTriggerSimple) trigger;
			Block target = simpleTrigger.getTarget();
			List<ItemStack> inputs;
			if(target == Blocks.CAULDRON){
				inputs = Collections.singletonList(new ItemStack(Items.CAULDRON));
			} else {
				inputs = NonNullList.create();
				target.getSubBlocks(target.getCreativeTab(), (NonNullList<ItemStack>) inputs);
			}
			this.input = Arrays.asList(salisMundus, inputs);
			this.output = HEIPlugin.nestedSingletonList(simpleTrigger.getResult());
			this.research = simpleTrigger.getResearch();
		}
	}
	public static class SalisMundusOreWrapper extends SalisMundusRecipeWrapper {
		public SalisMundusOreWrapper(DustTriggerOre trigger) {
			AccessorDustTriggerOre oreTrigger = (AccessorDustTriggerOre) trigger;
			this.input = Arrays.asList(salisMundus, OreDictionary.getOres(oreTrigger.getTarget()));
			this.output = HEIPlugin.nestedSingletonList(oreTrigger.getResult());
			this.research = oreTrigger.getResearch();
		}
	}
}
