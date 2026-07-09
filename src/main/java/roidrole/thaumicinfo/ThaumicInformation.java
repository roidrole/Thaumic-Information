package roidrole.thaumicinfo;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import roidrole.thaumicinfo.visualores.PacketHandler;


@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION,
    dependencies =
        "after:jei@[1.12.2-4.15.0.275,);" +
        "required-after:thaumcraft@[6.1.BETA20,);" +
        "required-after:thaumicapi;" +
        "required-after:mixinbooter;" +
        "required-after:configanytime;" +
        //To force our hash to be used if enabled
        "before:thaumicspeedup;"
)
public class ThaumicInformation {
    public static Logger LOGGER;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        if(ThaumicInformationConfig.visualOresConfig.dioptra.enabled && Loader.isModLoaded("visualores")){
            PacketHandler.preInit();
        }
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event){
        CacheManager.writeCaches();
    }
}