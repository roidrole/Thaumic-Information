package roidrole.thaumicinfo.mixins;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;

@Mixin(ThaumcraftApi.class)
public abstract class ThaumcraftAPIUseHash {
	/**
	 * @author roidrole
	 * @reason Eliminate copying, use better hash method, cleanup deobf code
	 */
	@Overwrite(remap = false)
	public static boolean exists(ItemStack item) {
		if(CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(item)) != null){
			return true;
		}
		int oldDamage = item.getItemDamage();

		if (item.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			for (int i = 0; i < 16; i++) {
				item.setItemDamage(i);
				if (CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(item)) != null) {
					item.setItemDamage(oldDamage);
					return true;
				}
			}
		} else {
			item.setItemDamage(OreDictionary.WILDCARD_VALUE);
			if (CommonInternals.objectTags.get(CommonInternals.generateUniqueItemstackId(item)) != null) {
				item.setItemDamage(oldDamage);
				return true;
			}
		}
		item.setItemDamage(oldDamage);
		return false;
	}

	@Inject(
		method = "registerObjectTag(Lnet/minecraft/item/ItemStack;Lthaumcraft/api/aspects/AspectList;)V",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	private static void disallowEmptyStacks(ItemStack item, AspectList aspects, CallbackInfo ci) {
		if (item == null || item.isEmpty()) {
			ci.cancel();
		}
	}

	@Inject(
		method = "registerComplexObjectTag(Lnet/minecraft/item/ItemStack;Lthaumcraft/api/aspects/AspectList;)V",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	private static void disallowComplexEmptyStacks(ItemStack item, AspectList aspects, CallbackInfo ci) {
		if (item == null || item.isEmpty()) {
			ci.cancel();
		}
	}
}
