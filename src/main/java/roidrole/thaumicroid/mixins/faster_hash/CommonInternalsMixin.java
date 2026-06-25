package roidrole.thaumicroid.mixins.faster_hash;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.api.internal.CommonInternals;

import java.util.Objects;

@Mixin(CommonInternals.class)
public abstract class CommonInternalsMixin {
	/**
	 * @author roidrole
	 * @reason Eliminate ItemStack.copy, serializeNBT
	 */
	@Overwrite(remap = false)
	public static int generateUniqueItemstackId(ItemStack stack) {
		return Objects.hash(stack.getItem().delegate.name(), stack.getItemDamage(), stack.getTagCompound());
	}

	/**
	 * @author roidrole
	 * @reason Eliminate ItemStack.copy, serializeNBT
	 */
	@Overwrite(remap = false)
	public static int generateUniqueItemstackIdStripped(ItemStack stack) {
		return Objects.hash(stack.getItem().delegate.name(), stack.getItemDamage());
	}
}
