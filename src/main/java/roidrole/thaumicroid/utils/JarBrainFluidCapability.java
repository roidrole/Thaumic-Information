package roidrole.thaumicroid.utils;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import roidrole.thaumicroid.ThaumicRoidConfig;
import thaumcraft.common.tiles.devices.TileJarBrain;

import javax.annotation.Nullable;

import static roidrole.thaumicroid.ThaumicRoid.LOGGER;

public class JarBrainFluidCapability implements IFluidHandler {
	public static final Fluid liquidXP = FluidRegistry.getFluid(ThaumicRoidConfig.general.liquidXP);

	public final TileJarBrain jarBrain;

	public JarBrainFluidCapability(TileJarBrain jarBrain) {
		this.jarBrain = jarBrain;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[]{
			new FluidTankProperties(
				new FluidStack(liquidXP, jarBrain.xp * ThaumicRoidConfig.general.xpPointToMb),
				jarBrain.xpMax * ThaumicRoidConfig.general.xpPointToMb,
				true,
				true
			)
		};
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(resource.getFluid() != liquidXP){
			return 0;
		}
		LOGGER.info("Trying to fill {} mb. doFill is {}", resource.amount, doFill);
		int capacityXP = (jarBrain.xpMax - jarBrain.xp);
		//Handle < xpPointsToMb IO
		if(capacityXP > 0 && resource.amount < ThaumicRoidConfig.general.xpPointToMb){
			if(!doFill){
				return resource.amount;
			}
			int fluidBuffer = ((MixinIntField) jarBrain).thaumicroid_getField() + resource.amount;
			if(fluidBuffer >= 25){
				jarBrain.xp += 1;
				jarBrain.syncTile(false);
				fluidBuffer -= 25;
			}
			((MixinIntField) jarBrain).thaumicroid_setField(fluidBuffer);
			return resource.amount;
		}
		int capacityMb = capacityXP * ThaumicRoidConfig.general.xpPointToMb;
		LOGGER.info("Capacity is {} xp or {} mb", capacityXP, capacityMb);
		int filledXP;
		int filledMb;
		if(capacityMb > resource.amount){
			filledXP = resource.amount / ThaumicRoidConfig.general.xpPointToMb;
			filledMb = filledXP * ThaumicRoidConfig.general.xpPointToMb;
		} else {
			//So it is an integer amount of mb
			//Highest multiple of xpPointToMb that is lower than resource.amount
			//Since int/int is floordiv, (a/b)*b is confirmed to be a multiple of b
			filledXP = capacityXP;
			filledMb = filledXP * ThaumicRoidConfig.general.xpPointToMb;
		}
		LOGGER.info("Filling {} xp or {} mb", filledXP, filledMb);
		if(doFill){
			jarBrain.xp += filledXP;
			jarBrain.syncTile(false);
		}
		return filledMb;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(resource.getFluid() == liquidXP){
			return drain(resource.amount, doDrain);
		}
		return null;
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		int capacityXP = jarBrain.xp;
		int capacityMb = capacityXP * ThaumicRoidConfig.general.xpPointToMb;
		int drainedXP;
		int drainedMb;
		if(capacityMb < maxDrain){
			//Drain everything - everything % xpPointsToMb
			drainedMb = capacityMb;
			drainedXP = drainedMb / ThaumicRoidConfig.general.xpPointToMb;
		} else {
			//So it is an integer amount of mb
			//Highest multiple of xpPointToMb that is lower than resource.amount
			//Since int/int is floordiv, (a/b)*b is confirmed to be a multiple of b
			drainedXP = maxDrain / ThaumicRoidConfig.general.xpPointToMb;
			drainedMb = drainedXP * ThaumicRoidConfig.general.xpPointToMb;
		}
		if(drainedMb == 0){
			return null;
		}
		if(doDrain){
			jarBrain.xp -= drainedXP;
			jarBrain.syncTile(false);
		}

		return new FluidStack(liquidXP, drainedMb);
	}
}
