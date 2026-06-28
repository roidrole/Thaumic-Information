package roidrole.thaumicinfo.mixins.accessors;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thaumcraft.common.lib.crafting.DustTriggerSimple;

@Mixin(DustTriggerSimple.class)
public interface AccessorDustTriggerSimple {
	@Accessor(remap = false)
	Block getTarget();
	@Accessor(remap = false)
	ItemStack getResult();
	@Accessor(remap = false)
	String getResearch();
}
