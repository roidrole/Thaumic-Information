package roidrole.thaumicsjw;

import net.minecraftforge.common.config.Config;

@Config(
	modid = Tags.MOD_ID,
	category = ""
)
public class ThaumicSJWConfig {

	@Config.Name("General Configs")
	public static final General general = new General();
	public static class General {
		@Config.Comment("The path in which the aspect caches will be written")
		public String cachePath = "cache/" + Tags.MOD_ID;

		@Config.Comment("Allows rendering the ItemStacks aspects in all GUI")
		public boolean aspectTooltipInAllGUI = true;
	}


	@Config.Name("Speedup Configs")
	public static final Speedup speedupConfig = new Speedup();
	public static class Speedup {
		@Config.Comment({
			"Optimizes Thaumcraft's hash for ItemStacks",
			"Thaumcraft internally uses this hash to map Aspects to ItemStacks and to handle oredict scanning",
			"Toggling this option will require you to delete the itemstack cache and the jei cache",
			"Note that this option will make ItemStacks aspect matching disregard capabilities",
			"If you have Thaumic Speedup installed, toggle this off."
		})
		public boolean fasterHash = false;

		@Config.Comment({
			"Implements FastWorkbench for the pattern crafter",
			"Shouldn't cause much issue",
		})
		public boolean patternCrafterRecipeCache = true;

		@Config.Comment({
			"Optimizes the acquisition of oreDicts ending in a wildcard i.e. ingot*",
			"Shouldn't cause much issue",
		})
		public boolean fasterOreDictWildcard = true;

		@Config.Comment({
			"Caches the entity and itemstack aspects on first launch",
			"Limits the amount of cached different aspects and the quantity of any aspect to 255",
			"This limit does not apply on aspects computed through recipes"
		})
		public boolean aspectCache = true;
	}


	@Config.Name("JEI Configs")
	public static final JEI jeiConfig = new JEI();
	public static class JEI {
		@Config.Comment("Hide recipes from JEI if you don't have the research for it")
		public boolean hideRecipesIfMissingResearch = true;

		@Config.Comment("Items blacklisted from the checking in the Aspect For ItemStack. Format: 'minecraft:stone'")
		@Config.Name("jeiBlacklist")
		public String[] blacklistedFromAspectChecking = {
			"minecraft:spawn_egg"
		};

		@Config.Comment("Should the crafting recipe for Salis Mundus and Triple Meat Treat appear in JEI?")
		public boolean showSpecialRecipes = true;

		@Config.Name("Category Toggles")
		@Config.Comment("Toggles to unregister any JEI Category")
		public final CategoryToggle categoryToggle = new CategoryToggle();

		public static class CategoryToggle {
			@Config.Name("Arcane Workbench")
			public boolean arcaneWorkbench = true;

			@Config.Name("Aspect Compound")
			public boolean aspectCompound = true;

			@Config.Name("Aspect from ItemStack")
			public boolean aspectFromItemStack = true;

			@Config.Name("Crucible")
			public boolean crucible = true;

			@Config.Name("Salis Mundus")
			public boolean salisMundus = true;

			@Config.Name("Infusion Crafting")
			public boolean infusion = true;

			@Config.Name("Infernal Furnace")
			public boolean infernalFurnace = true;
		}
	}
}
