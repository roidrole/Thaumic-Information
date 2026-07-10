package roidrole.thaumicinfo.mixins;

import jeresources.api.conditionals.Conditional;
import jeresources.api.conditionals.ExtendedConditional;
import jeresources.api.drop.LootDrop;
import jeresources.entry.MobEntry;
import jeresources.registry.MobRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.startup.JeiStarter;
import mezz.jei.startup.ModRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roidrole.thaumicinfo.mixins.accessors.AccessorMobEntry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.internal.CommonInternals;

import java.util.HashMap;
import java.util.List;

//This class has to exist because I need to inject code after plugins (to capture all mobs)
// But still before recipe registry (so IO works as expected)
//Would have liked a JER API, but here we are
@Mixin(JeiStarter.class)
public abstract class InjectJERCrystals {
	@Inject(
		method = "registerPlugins",
		at = @At(
			value = "TAIL"
		),
		remap = false
	)
	private static void computeJERCrystal(List<IModPlugin> plugins, ModRegistry modRegistry, CallbackInfo ci){
		//Add crystals is killed with liquid death
		HashMap<Aspect, ItemStack> crystalCache = new HashMap<>(Aspect.aspects.size() * 2);
		HashMap<String, ThaumcraftApi.EntityTags> entityTagCache = new HashMap<>(CommonInternals.scanEntities.size() * 2);

		CommonInternals.scanEntities.forEach(scanEntity -> entityTagCache.put(scanEntity.entityName, scanEntity));

		ExtendedConditional killedByLiquidDeath = new ExtendedConditional(Conditional.killedBy, I18n.format("fluid.liquid_death"));

		for (MobEntry entry : MobRegistry.getInstance().getMobs()){
			ThaumcraftApi.EntityTags scanEntity = entityTagCache.get(EntityList.getEntityString(entry.getEntity()));
			if(scanEntity == null) {
				continue;
			}
			float amount = (float)(2 + scanEntity.aspects.visSize()/10)/2.0f;
			float perCrystalChance = amount/scanEntity.aspects.size();
			for(Aspect aspect : scanEntity.aspects.aspects.keySet()){
				LootDrop drop = new LootDrop(crystalCache.computeIfAbsent(aspect, ThaumcraftApiHelper::makeCrystal), perCrystalChance);
				drop.addConditional(killedByLiquidDeath);
				drop.minDrop = 0;
				drop.maxDrop = (int)amount;
				((AccessorMobEntry)entry).getDropsSet().add(drop);
			}
		}
	}
}
