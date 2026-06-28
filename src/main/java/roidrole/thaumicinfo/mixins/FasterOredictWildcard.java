package roidrole.thaumicinfo.mixins;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import thaumcraft.api.ThaumcraftApiHelper;

import java.util.List;

@Mixin(ThaumcraftApiHelper.class)
public abstract class FasterOredictWildcard {
	@Unique
	private static String[] thaumicinfo_oreNames;

	/**
	 * @author Rongmario
	 * @reason A little optimization to not retrieve a new array every time a wildcard entry is dealt with + cache trimmed entry
	 */
	@Overwrite(remap = false)
	public static List<ItemStack> getOresWithWildCards(String oreDict) {
		oreDict = oreDict.trim();
		if (oreDict.endsWith("*")) {
			final List<ItemStack> ores = new ObjectArrayList<>();
			if (thaumicinfo_oreNames == null) {
				thaumicinfo_oreNames = OreDictionary.getOreNames();
			}
			String wildcard = oreDict.replaceAll("\\*", "");
			for (String ore : thaumicinfo_oreNames) {
				if (ore.startsWith(wildcard)) {
					ores.addAll(OreDictionary.getOres(ore, false));
				}
			}
			return ores;
		} else {
			return OreDictionary.getOres(oreDict, false);
		}
	}
}