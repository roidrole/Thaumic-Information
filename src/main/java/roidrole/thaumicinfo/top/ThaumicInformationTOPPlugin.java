package roidrole.thaumicinfo.top;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import roidrole.thaumicinfo.ThaumicInformationConfig;

import java.util.function.Function;

//TODO: Merge HWYLA and TOP configs
public class ThaumicInformationTOPPlugin {
	public static int SUCTION_ELEMENT_ID = -1;
	public static int CONTENT_ELEMENT_ID = -1;

	public static void preInit(){
		FMLInterModComms.sendFunctionMessage(TheOneProbe.MODID, "getTheOneProbe", ThaumicInformationTOPPlugin.Initializer.class.getName());
	}

	public static class Initializer implements Function<ITheOneProbe, Void> {
		@Override
		public Void apply(ITheOneProbe probe) {
			if(ThaumicInformationConfig.topConfig.essentiaTransport){
				SUCTION_ELEMENT_ID = probe.registerElementFactory(new ElementSuction.Factory());
				CONTENT_ELEMENT_ID = probe.registerElementFactory(new ElementContent.Factory());

				probe.registerProvider(ProviderEssentiaTransport.INSTANCE);
			}

			if(ThaumicInformationConfig.topConfig.arcaneEar){
				probe.registerProvider(ProviderArcaneEar.INSTANCE);
			}

			if(ThaumicInformationConfig.topConfig.gogglesDisplay){
				probe.registerProvider(ProviderGogglesDisplay.INSTANCE);
			}

			if(ThaumicInformationConfig.topConfig.visBattery) {
				probe.registerProvider(ProviderVisBattery.INSTANCE);
			}

			if(ThaumicInformationConfig.topConfig.visGenerator){
				probe.registerProvider(ProviderVisGenerator.INSTANCE);
			}

			if(ThaumicInformationConfig.topConfig.voidSiphon){
				probe.registerProvider(ProviderVoidSiphon.INSTANCE);
			}

			if(ThaumicInformationConfig.topConfig.brainInJar) {
				probe.registerProvider(ProviderBrainJar.INSTANCE);
			}

			if(ThaumicInformationConfig.topConfig.noteBlock){
				probe.registerProvider(ProviderNote.INSTANCE);
			}
			return null;
		}
	}
}