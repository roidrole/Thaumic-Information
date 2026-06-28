package roidrole.thaumicinfo.mixins.accessors;

import jeresources.api.drop.LootDrop;
import jeresources.entry.MobEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(MobEntry.class)
public interface AccessorMobEntry {
	//Because JER doesn't allow multiple NBT-differentiated loots, therefore ignoring our entries
	@Accessor(value = "drops", remap = false)
	Set<LootDrop> getDropsSet();
}
