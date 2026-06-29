package roidrole.thaumicinfo;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import roidrole.thaumicinfo.utils.MixinIntField;
import thaumcraft.common.tiles.devices.TileJarBrain;

import javax.annotation.Nullable;

import static roidrole.thaumicinfo.ThaumicInformation.LOGGER;

public class JarBrainFluidCapability implements IFluidHandler {
	private static Fluid liquidXP = null;

	public final TileJarBrain jarBrain;

	public JarBrainFluidCapability(TileJarBrain jarBrain) {
		if(liquidXP == null){
			liquidXP = FluidRegistry.getFluid(ThaumicInformationConfig.general.liquidXP);
			if(liquidXP == null){
				ThaumicInformation.LOGGER.error("The liquid xp fluid in the config in thaumicinfo.cfg -> general -> liquidXp is invalid! Setting to water.");
				liquidXP = FluidRegistry.WATER;
			}
		}
		this.jarBrain = jarBrain;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[]{
			new FluidTankProperties(
				new FluidStack(liquidXP, jarBrain.xp * ThaumicInformationConfig.general.xpPointToMb),
				jarBrain.xpMax * ThaumicInformationConfig.general.xpPointToMb,
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
		if(capacityXP > 0 && resource.amount < ThaumicInformationConfig.general.xpPointToMb){
			if(!doFill){
				return resource.amount;
			}
			int fluidBuffer = ((MixinIntField) jarBrain).thaumicinfo_getField() + resource.amount;
			if(fluidBuffer >= 25){
				jarBrain.xp += 1;
				jarBrain.syncTile(false);
				fluidBuffer -= 25;
			}
			((MixinIntField) jarBrain).thaumicinfo_setField(fluidBuffer);
			return resource.amount;
		}
		int capacityMb = capacityXP * ThaumicInformationConfig.general.xpPointToMb;
		LOGGER.info("Capacity is {} xp or {} mb", capacityXP, capacityMb);
		int filledXP;
		int filledMb;
		if(capacityMb > resource.amount){
			filledXP = resource.amount / ThaumicInformationConfig.general.xpPointToMb;
			filledMb = filledXP * ThaumicInformationConfig.general.xpPointToMb;
		} else {
			//So it is an integer amount of mb
			//Highest multiple of xpPointToMb that is lower than resource.amount
			//Since int/int is floordiv, (a/b)*b is confirmed to be a multiple of b
			filledXP = capacityXP;
			filledMb = filledXP * ThaumicInformationConfig.general.xpPointToMb;
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
		int capacityMb = capacityXP * ThaumicInformationConfig.general.xpPointToMb;
		int drainedXP;
		int drainedMb;
		if(capacityMb < maxDrain){
			//Drain everything - everything % xpPointsToMb
			drainedMb = capacityMb;
			drainedXP = drainedMb / ThaumicInformationConfig.general.xpPointToMb;
		} else {
			//So it is an integer amount of mb
			//Highest multiple of xpPointToMb that is lower than resource.amount
			//Since int/int is floordiv, (a/b)*b is confirmed to be a multiple of b
			drainedXP = maxDrain / ThaumicInformationConfig.general.xpPointToMb;
			drainedMb = drainedXP * ThaumicInformationConfig.general.xpPointToMb;
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
