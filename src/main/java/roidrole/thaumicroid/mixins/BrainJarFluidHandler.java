package roidrole.thaumicroid.mixins;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import roidrole.thaumicroid.utils.JarBrainFluidCapability;
import roidrole.thaumicroid.utils.MixinIntField;
import thaumcraft.common.tiles.devices.TileJarBrain;
import thaumcraft.common.tiles.essentia.TileJar;

import javax.annotation.Nullable;

@Mixin(TileJarBrain.class)
public abstract class BrainJarFluidHandler extends TileJar implements MixinIntField {
	@Unique
	public int thaumicroid_liquidXPAmount;
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) new JarBrainFluidCapability((TileJarBrain) (Object) this);
		}
		return super.getCapability(capability, facing);
	}

	@Inject(
		method = "writeSyncNBT",
		at = @At("TAIL"),
		remap = false
	)
	private void thaumicrois_serializeLiquidBuffer(NBTTagCompound nbttagcompound, CallbackInfoReturnable<NBTTagCompound> cir){
		nbttagcompound.setByte("thaumicroid_xpbuffer", (byte) thaumicroid_liquidXPAmount);
	}

	@Inject(
		method = "readSyncNBT",
		at = @At("TAIL"),
		remap = false
	)
	private void thaumicrois_serializeLiquidBuffer(NBTTagCompound nbttagcompound, CallbackInfo ci){
		thaumicroid_liquidXPAmount = nbttagcompound.getByte("thaumicroid_xpbuffer");
	}

	@Override
	public int thaumicroid_getField() {
		return thaumicroid_liquidXPAmount;
	}

	@Override
	public void thaumicroid_setField(int value) {
		thaumicroid_liquidXPAmount = value;
	}
}
