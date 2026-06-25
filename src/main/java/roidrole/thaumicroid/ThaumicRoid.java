package roidrole.thaumicroid;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import roidrole.thaumicroid.jei.ResearchManager;
import roidrole.thaumicroid.visualores.PacketHandler;


@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION,
    dependencies =
        "required-after:jei@[1.12.2-4.15.0.275,);" +
        "required-after:thaumcraft@[6.1.BETA20,);" +
        "required-after:thaumicapi;" +
        "required-after:mixinbooter"
)
public class ThaumicRoid {
    public static Logger LOGGER;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        if(event.getSide() == Side.CLIENT && ThaumicRoidConfig.jeiConfig.hideRecipesIfMissingResearch){
            MinecraftForge.EVENT_BUS.register(ResearchManager.class);
        }
        if(ThaumicRoidConfig.visualOresConfig.dioptraUpdatesAura && Loader.isModLoaded("visualores")){
            PacketHandler.preInit();
        }
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event){
        CacheManager.writeCaches();
    }
}