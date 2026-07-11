package roidrole.thaumicinfo;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import roidrole.thaumicinfo.jei.ResearchManager;
import roidrole.thaumicinfo.visualores.PacketAuraToClient;

@Config(
	modid = Tags.MOD_ID,
	category = ""
)
public class ThaumicInformationConfig {

	@Config.Name("General Configs")
	public static final General general = new General();
	public static class General {
		@Config.RequiresMcRestart
		@Config.Comment("The path in which the aspect caches will be written")
		public String cachePath = "cache/" + Tags.MOD_ID;

		@Config.RequiresMcRestart
		@Config.Comment("Allows rendering the ItemStacks aspects in all GUI")
		public boolean aspectTooltipInAllGUI = true;

		@Config.RequiresMcRestart
		@Config.Comment("Should the caches (JEI, aspect and entity) be updated automatically?")
		public boolean autoUpdateCaches = true;
	}


	@Config.Name("Performance Configs")
	public static final Performance performanceConfig = new Performance();
	public static class Performance {
		@Config.RequiresMcRestart
		@Config.Comment({
			"Optimizes Thaumcraft's hash for ItemStacks",
			"Toggling this option will require you to delete the itemstack cache",
			"Note that this option will make ItemStacks aspect matching disregard capabilities",
			"This might be more stable than Thaumic Speedup's hash if the aspect cache is enabled"
		})
		public boolean fasterHash = false;

		@Config.RequiresMcRestart
		@Config.Comment("Implements FastWorkbench for the pattern crafter")
		public boolean patternCrafterRecipeCache = true;

		@Config.RequiresMcRestart
		@Config.Comment("Optimizes the acquisition of oreDicts ending in a wildcard i.e. ingot*")
		public boolean fasterOreDictWildcard = true;

		@Config.RequiresMcRestart
		@Config.Comment({
			"Caches the entity and itemstack aspects on first launch",
			"Limits the amount of cached different aspects and the quantity of any aspect to 255",
			"This limit does not apply on aspects computed through recipes unless they are computed during launch",
			"This option might mess up aspects. You should use Thaumic Speedup instead"
		})
		public boolean aspectCache = false;
	}


	@Config.Name("JEI Configs")
	public static final JEI jeiConfig = new JEI();
	public static class JEI {
		@Config.Comment("Hide recipes from JEI if you don't have the research for it")
		public boolean hideRecipesIfMissingResearch = false;

		@Config.RequiresMcRestart
		@Config.Comment("Items blacklisted from the checking in the Aspect For ItemStack. Format: 'minecraft:stone'")
		public String[] jeiBlacklist = {
			"minecraft:spawn_egg"
		};

		@Config.RequiresMcRestart
		@Config.Comment("Should the crafting recipe for Salis Mundus and Triple Meat Treat appear in JEI?")
		public boolean showSpecialRecipes = true;

		@Config.RequiresMcRestart
		@Config.Comment({
			"Make JER mob loot include crystals dropped when killed by liquid death",
			"Requires Just Enough Resources"
		})
		public boolean jerCrystals = true;

		@Config.Comment("Makes the Aspect From Itemstack category show a background that is clearer the purer the item's aspect is")
		public boolean purityBackground = false;

		@Config.Name("Category Toggles")
		@Config.Comment("Toggles to unregister any JEI Category")
		public final CategoryToggle categoryToggle = new CategoryToggle();
		public static class CategoryToggle {
			@Config.RequiresMcRestart
			@Config.Name("Arcane Workbench")
			public boolean arcaneWorkbench = true;

			@Config.RequiresMcRestart
			@Config.Name("Aspect Compound")
			public boolean aspectCompound = true;

			@Config.RequiresMcRestart
			@Config.Name("Aspect from ItemStack")
			public boolean aspectFromItemStack = true;

			@Config.RequiresMcRestart
			@Config.Name("Crucible")
			public boolean crucible = true;

			@Config.RequiresMcRestart
			@Config.Name("Salis Mundus")
			public boolean salisMundus = true;

			@Config.RequiresMcRestart
			@Config.Name("Infusion Crafting")
			public boolean infusion = true;

			@Config.RequiresMcRestart
			@Config.Name("Infernal Furnace")
			public boolean infernalFurnace = true;
		}
	}

	@Config.Name("The One Probe Configs")
	@Config.Comment({
		"Only the config to disable the handlers are here.",
		"The other configurations are in the in-game HWYLA config."
	})
	public static final TOP topConfig = new TOP();
	public static class TOP {
		@Config.RequiresMcRestart
		@Config.Comment({
			"Integration for the brain in a jar. Shows xp contents",
			"Automatically disabled if the brain in a jar acts as a fluid tank for xp."
		})
		public boolean brainInJar = true;

		@Config.RequiresMcRestart
		@Config.Comment("Integration for essentia tubes. Shows contents and suction")
		public boolean essentiaTransport = true;

		@Config.RequiresMcRestart
		@Config.Comment("Integration for goggles display. Only used for the Infusion Matrix's stability")
		public boolean gogglesDisplay = true;

		@Config.RequiresMcRestart
		@Config.Comment("Integration for the vis battery. Shows current and max stored vis")
		public boolean visBattery = true;

		@Config.RequiresMcRestart
		@Config.Comment("Integration for the vis generator. Shows current energy")
		public boolean visGenerator = true;

		@Config.RequiresMcRestart
		@Config.Comment("Integration for the void siphon. Shows current progress")
		public boolean voidSiphon = true;

		@Config.RequiresMcRestart
		@Config.Comment("Integration for the arcane ear. Shows note and tone")
		public boolean arcaneEar = true;

		@Config.Comment("Whether most information requires the goggles")
		public boolean requireGoggles = true;

		@Config.Comment("If enabled, the aspects will be shown as text instead of their icons. Might help readability")
		public boolean showAspectsAsText = false;

		@Config.Comment("Integration for the vanilla note block. Shows the currently selected note. Useful for the arcane ear.")
		public boolean noteBlock = true;

	}

	@Config.Name("VisualOres Configs")
	public static final VisualOres visualOresConfig = new VisualOres();
	public static class VisualOres {

		@Config.Comment({
			"If enabled, the thaumic dioptra will send aura data to every online player that interacted with it",
			"This makes the dioptra auto-update the VisualOres map in square centered on the dioptra"
		})
		public final Dioptra dioptra = new Dioptra();
		public static class Dioptra {
			@Config.Comment({
				"Should this be allowed",
				"Warning: toggling this off will make dioptras forget which players interacted with it"
			})
			@Config.RequiresMcRestart
			public boolean enabled = true;

			@Config.Comment({
				"The radius of the dioptra update square, in chunks",
				"A radius of 0 means only this chunk it is in, a radius of 1 means a 3x3 square",
				"The default is 6, so a 13x13 square, which covers the same area as the dioptra visual display"
			})
			@Config.RangeInt(min = 0)
			public int radius = 6;

			@Config.Comment({
				"The interval (in ticks) between synchronizations"
			})
			@Config.RangeInt(min = 1)
			public int delay = 64;
		}

		@Config.Comment({
			"Replaces the default overlay with a constant purple overlay whose opacity depends on flux",
			"The formulas for opacity are separated between the center and the border of chunks and are min(max_value, floor(fluxAmount * mutliplier))"
		})
		public final Overlay overlay = new Overlay();
		public static class Overlay {
			@Config.RequiresMcRestart
			@Config.Comment("Allows modification of the overlay")
			public boolean enabled = true;

			@Config.Comment("The max opacity of the center. 0 is invisible, 255 is opaque")
			@Config.RangeInt(min = 0, max = 255)
			public int max_value_center = 0x77;
			@Config.Comment("The max opacity of the center. 0 is invisible, 255 is opaque")
			@Config.RangeInt(min = 0, max = 255)
			public int max_value_border = 0xFF;
			@Config.Comment("The multiplier parameter for the center opacity computation")
			public float multiplier_center = 6.15f;
			@Config.Comment("The multiplier parameter for the border opacity computation")
			public float multiplier_border = 6.25f;
			@Config.Comment("The color of the hue, in format 0xRRGGBB converted to decimal because forge configs")
			public int color = 0x6F167C;
		}
	}

	@Config.Name("HWYLA Configs")
	@Config.Comment({
		"Only the config to disable the handlers are here.",
		"The other configurations are in the in-game HWYLA config."
	})
	public static final HWYLA hwylaConfig = new HWYLA();
	public static class HWYLA {
		@Config.RequiresMcRestart
		@Config.Comment({
			"Integration for the brain in a jar. Shows xp contents",
			"Automatically disabled if the brain in a jar acts as a fluid tank for xp."
		})
		public boolean brainInJar = true;
		@Config.RequiresMcRestart
		@Config.Comment("Integration for essentia tubes. Shows contents and suction")
		public boolean essentiaTransport = true;
		@Config.RequiresMcRestart
		@Config.Comment("Integration for goggles display. Only used for the Infusion Matrix's stability")
		public boolean gogglesDisplay = true;
		@Config.RequiresMcRestart
		@Config.Comment("Integration for the vis battery. Shows current and max stored vis")
		public boolean visBattery = true;

	}

	@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
	public static class ConfigEventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (!event.getModID().equals(Tags.MOD_ID)) {
				return;
			}
			ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
			if(ThaumicInformationConfig.jeiConfig.hideRecipesIfMissingResearch){
				ResearchManager.init();
			} else {
				ResearchManager.deInit();
			}
			PacketAuraToClient.setRadius(ThaumicInformationConfig.visualOresConfig.dioptra.radius);
		}
	}

	static {
		ConfigAnytime.register(ThaumicInformationConfig.class);
	}
}
