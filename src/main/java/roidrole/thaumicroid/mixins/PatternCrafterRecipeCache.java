package roidrole.thaumicroid.mixins;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.common.tiles.crafting.TilePatternCrafter;

@Mixin(TilePatternCrafter.class)
public abstract class PatternCrafterRecipeCache {

	@Unique
	private IRecipe thaumicroid_lastRecipe = CraftingManager.REGISTRY.iterator().next();

	@Redirect(
		method = "craft",
		at = @At(
			value = "INVOKE",
			//CraftingManager.findMatchingRecipe
			target = "Lnet/minecraft/item/crafting/CraftingManager;func_192413_b(Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/world/World;)Lnet/minecraft/item/crafting/IRecipe;"
		),
		remap = false
	)
	private IRecipe thaumicroid_cacheLastRecipe(InventoryCrafting inventoryCrafting, World world){
		if(inventoryCrafting.isEmpty()){
			return null;
		}
		if(this.thaumicroid_lastRecipe.matches(inventoryCrafting, world)){
			return this.thaumicroid_lastRecipe;
		}
		IRecipe newRecipe = CraftingManager.findMatchingRecipe(inventoryCrafting, world);
		if(newRecipe != null){
			this.thaumicroid_lastRecipe = newRecipe;
		}
		return newRecipe;
	}

	@Redirect(
		method = "craft",
		at = @At(
			value = "INVOKE",
			//CraftingManager.getRemainingItems
			target = "Lnet/minecraft/item/crafting/CraftingManager;func_180303_b(Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/world/World;)Lnet/minecraft/util/NonNullList;"
		),
		remap = false
	)
	private NonNullList<ItemStack> thaumicroid_useCache(InventoryCrafting inventoryCrafting, World world){
		return thaumicroid_lastRecipe.getRemainingItems(inventoryCrafting);
	}
}
