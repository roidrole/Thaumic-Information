package roidrole.thaumicinfo;

import com.invadermonky.thaumicapi.api.ThaumicAPIJEIPlugin;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import roidrole.thaumicinfo.jei.DisplayOnlyRecipe;
import roidrole.thaumicinfo.jei.ResearchManager;
import roidrole.thaumicinfo.jei.categories.*;
import roidrole.thaumicinfo.jei.gui.FocalManipulatorAdvancedGuiHandler;
import roidrole.thaumicinfo.jei.gui.ResearchTableAdvancedGuiHandler;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.gui.GuiArcaneWorkbench;
import thaumcraft.common.container.ContainerArcaneWorkbench;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.StreamSupport;

@JEIPlugin
public class HEIPlugin implements IModPlugin {

	public static final IIngredientType<AspectList> ASPECT_LIST = ThaumicAPIJEIPlugin.ASPECT_INGREDIENT;

	@Override
	public void registerSubtypes(ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(ItemsTC.crystalEssence);
		subtypeRegistry.useNbtForSubtypes(ItemsTC.phial);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
		AbstractResearchCategory.categories = new ArrayList<>(8);
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.arcaneWorkbench){
			registry.addRecipeCategories(new ArcaneWorkbenchCategory());
		}
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.crucible){
			registry.addRecipeCategories(new CrucibleCategory());
		}
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.infusion){
			registry.addRecipeCategories(new InfusionCategory());
		}
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.salisMundus){
			registry.addRecipeCategories(new SalisMundusCategory(registry.getJeiHelpers()));
		}
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.aspectFromItemStack){
			registry.addRecipeCategories(new AspectFromItemStackCategory());
		}
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.aspectCompound){
			registry.addRecipeCategories(new AspectCompoundCategory(helper));
		}
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.infernalFurnace){
			registry.addRecipeCategories(new InfernalFurnaceCategory(helper));
		}
		AbstractResearchCategory.categories.trimToSize();
	}

	@Override
	public void register(@Nonnull IModRegistry registry){
		CacheManager.jeiRegistry = registry;
		//Since they are added to the list during the constructor, these already encompass their config option
		for(AbstractResearchCategory<?> category : AbstractResearchCategory.categories){
			category.populateRecipes();
			registry.addRecipes(category.recipes, category.getUid());
		}
		//We still have to set non-recipes separately
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.arcaneWorkbench){
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.arcaneWorkbench), ArcaneWorkbenchCategory.UUID);
			registry.addRecipeClickArea(GuiArcaneWorkbench.class, 108, 56, 32, 32, ArcaneWorkbenchCategory.UUID);
			registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerArcaneWorkbench.class, ArcaneWorkbenchCategory.UUID, 1, 9, 16, 36);
		}
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.crucible){
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.crucible), CrucibleCategory.UUID);
		}
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.infusion){
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.infusionMatrix), InfusionCategory.UUID);
		}
		if(ThaumicInformationConfig.jeiConfig.categoryToggle.salisMundus){
			registry.addRecipeCatalyst(SalisMundusCategory.salisMundus.get(0), SalisMundusCategory.UUID);
		}

		if(ThaumicInformationConfig.jeiConfig.categoryToggle.aspectFromItemStack){
			CacheManager.parseJeiCache(registry);

			registry.addRecipeCatalyst(new ItemStack(BlocksTC.smelterBasic), AspectFromItemStackCategory.UUID);
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.smelterThaumium), AspectFromItemStackCategory.UUID);
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.smelterVoid), AspectFromItemStackCategory.UUID);
		}

		if(ThaumicInformationConfig.jeiConfig.categoryToggle.aspectCompound){
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.centrifuge), AspectCompoundCategory.UUID);

			List<AspectCompoundCategory.AspectCompoundWrapper> compoundWrappers = new ArrayList<>();
			for (Aspect aspect : Aspect.getCompoundAspects()) {
				compoundWrappers.add(new AspectCompoundCategory.AspectCompoundWrapper(aspect));
			}
			registry.addRecipes(compoundWrappers, AspectCompoundCategory.UUID);
		}

		if(ThaumicInformationConfig.jeiConfig.categoryToggle.infernalFurnace){
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.infernalFurnace), InfernalFurnaceCategory.UUID);
			registry.addRecipeCatalyst(new ItemStack(BlocksTC.bellows), InfernalFurnaceCategory.UUID);

			List<InfernalFurnaceCategory.InfernalFurnaceWrapper> compoundWrappers = new ArrayList<>();
			for (ThaumcraftApi.SmeltBonus bonus : CommonInternals.smeltingBonus) {
				InfernalFurnaceCategory.InfernalFurnaceWrapper wrapper = new InfernalFurnaceCategory.InfernalFurnaceWrapper(bonus);
				if(wrapper.isValid()){
					compoundWrappers.add(wrapper);
				}
			}
			registry.addRecipes(compoundWrappers, InfernalFurnaceCategory.UUID);
		}

		if(ThaumicInformationConfig.jeiConfig.showSpecialRecipes){
			NonNullList<ItemStack> crystal = NonNullList.create();
			ItemsTC.crystalEssence.getSubItems(ItemsTC.crystalEssence.getCreativeTab(), crystal);
			NonNullList<ItemStack> nuggetMeat = OreDictionary.getOres("nuggetMeat");
			for (ItemStack stack : nuggetMeat) {
				stack.setStackDisplayName("3 Different Meat Nuggets");
			}
			for (ItemStack stack : crystal) {
				stack.setStackDisplayName("3 Different Vis Crystals");
			}
			registry.addRecipes(
				Arrays.asList(
					//Salis Mundus
					new DisplayOnlyRecipe(
						new ItemStack(ItemsTC.salisMundus),
						Arrays.asList(
							Collections.singletonList(new ItemStack(Items.FLINT)),
							Collections.singletonList(new ItemStack(Items.BOWL)),
							Collections.singletonList(new ItemStack(Items.REDSTONE)),
							crystal,
							crystal,
							crystal
						)
					),
					//Triple meat treat
					new DisplayOnlyRecipe(
						new ItemStack(ItemsTC.tripleMeatTreat),
						Arrays.asList(
							nuggetMeat,
							nuggetMeat,
							nuggetMeat,
							Collections.singletonList(new ItemStack(Items.SUGAR))
						)
					)
				),
				VanillaRecipeCategoryUid.CRAFTING
			);
		}

		registry.addRecipeCatalyst(new ItemStack(BlocksTC.infernalFurnace), VanillaRecipeCategoryUid.SMELTING);


		registry.addAdvancedGuiHandlers(new ResearchTableAdvancedGuiHandler());
		registry.addAdvancedGuiHandlers(new FocalManipulatorAdvancedGuiHandler());

	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
		//Hide Arcane recipes from the generic crafting table tab
		IRecipeRegistry registry = jeiRuntime.getRecipeRegistry();
		StreamSupport.stream(CraftingManager.REGISTRY.spliterator(), false)
			.filter(recipe -> recipe instanceof IArcaneRecipe)
			.map(recipe -> registry.getRecipeWrapper(recipe, VanillaRecipeCategoryUid.CRAFTING))
			.filter(Objects::nonNull)
			.forEach(recipe -> registry.hideRecipe(recipe, VanillaRecipeCategoryUid.CRAFTING));

		//First synchronization is called from ResearchManager itself (its own handler)
		if(ThaumicInformationConfig.jeiConfig.hideRecipesIfMissingResearch){
			ResearchManager.runtime = jeiRuntime;
		}
	}

	//Helper method to work with JEI
	public static <T> List<List<T>> nestedSingletonList(T element){
		return Collections.singletonList(Collections.singletonList(element));
	}
}