package roidrole.thaumicinfo.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import roidrole.thaumicinfo.jei.categories.AbstractResearchCategory;
import roidrole.thaumicinfo.jei.categories.AbstractResearchWrapper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchEvent;

public class ResearchManager {
    private static int timeToSync = -1;

    public static IJeiRuntime runtime;

    @SubscribeEvent
    public static void onResearch(ResearchEvent.Research event) {
        //Setting a delay to be sure that the research is completed
        timeToSync = 5;
    }

    @SubscribeEvent
    public static void onJoin(WorldEvent.Load event) {
        if (event.getWorld() instanceof WorldClient) {
            timeToSync = 20;
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (timeToSync >= 0 && event.phase == TickEvent.Phase.END) {
            if (timeToSync == 0) {
                sync();
                return;
            }
            timeToSync--;
        }
    }

    private static void sync() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null || runtime == null) {
            timeToSync = 20;
            return;
        }
        if (!player.hasCapability(ThaumcraftCapabilities.KNOWLEDGE, null)){
            return;
        }

        for (AbstractResearchCategory<?> category : AbstractResearchCategory.categories){
            for (IRecipeWrapper wrapper : category.recipes) {
                runtime.getRecipeRegistry().hideRecipe(wrapper, category.getUid());
            }
        }

        IPlayerKnowledge knowledge = player.getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
        if (knowledge == null) {
            timeToSync = 100;
            return;
        }
        for (AbstractResearchCategory<?> category : AbstractResearchCategory.categories){
            for(AbstractResearchWrapper wrapper : category.recipes){
                if(wrapper.getResearch() == null){
                    runtime.getRecipeRegistry().unhideRecipe(wrapper, category.getUid());
                }
                boolean hasAll = true;
                for (String subResearch : wrapper.getResearch().split("&&")) {
                    if (!knowledge.isResearchComplete(subResearch)) {
                        hasAll = false;
                        break;
                    }
                }
                if (hasAll) {
                    runtime.getRecipeRegistry().unhideRecipe(wrapper, category.getUid());
                }
            }
        }
    }

}
