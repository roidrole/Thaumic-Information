package roidrole.thaumicroid.mixins.accessors;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thaumcraft.common.lib.crafting.DustTriggerOre;

@Mixin(DustTriggerOre.class)
public interface AccessorDustTriggerOre {
	@Accessor(remap = false)
	String getTarget();
	@Accessor(remap = false)
	ItemStack getResult();
	@Accessor(remap = false)
	String getResearch();
}
