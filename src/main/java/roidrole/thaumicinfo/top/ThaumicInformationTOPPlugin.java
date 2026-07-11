package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import roidrole.thaumicinfo.ThaumicInformationConfig;

import java.util.function.Function;

//TODO: Arcane Note, Arcane Ear show notes (+ HWYLA), Congrega Mystica
//TODO: Fix discrepancy in WAILA showing 0 suction and TOP not.
public class ThaumicInformationTOPPlugin {
	public static int SUCTION_ELEMENT_ID = -1;
	public static int CONTENT_ELEMENT_ID = -1;

	public static void preInit(){
		FMLInterModComms.sendFunctionMessage(TheOneProbe.MODID, "getTheOneProbe", ThaumicInformationTOPPlugin.Initializer.class.getName());
	}

	public static class Initializer implements Function<ITheOneProbe, Void> {
		@Override
		public Void apply(ITheOneProbe probe) {
			SUCTION_ELEMENT_ID = probe.registerElementFactory(new ElementSuction.Factory());
			CONTENT_ELEMENT_ID = probe.registerElementFactory(new ElementContent.Factory());

			if(ThaumicInformationConfig.topConfig.essentiaTransport){
				probe.registerProvider(ProviderEssentiaTransport.INSTANCE);
			}

			if(ThaumicInformationConfig.topConfig.gogglesDisplay){
				/*
				registrar.registerBodyProvider(ProviderGogglesDisplay.INSTANCE, IGogglesDisplayExtended.class);
				*/
			}

			if(ThaumicInformationConfig.topConfig.visBattery) {
				/*
				registrar.registerBodyProvider(ProviderBlockVisBattery.INSTANCE, BlockVisBattery.class);
				*/
			}

			if(ThaumicInformationConfig.topConfig.brainInJar) {
				/*
				registrar.registerBodyProvider(ProviderBrainJar.INSTANCE, TileJarBrain.class);
				*/
			}
			return null;
		}
	}
}