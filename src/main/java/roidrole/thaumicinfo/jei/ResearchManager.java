package roidrole.thaumicinfo.jei;

import mezz.jei.api.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import roidrole.thaumicinfo.jei.categories.AbstractResearchCategory;
import roidrole.thaumicinfo.jei.categories.AbstractResearchWrapper;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ResearchManager {
    private final IJeiRuntime runtime;
    private final Map<String, Collection<AbstractResearchWrapper>> researchCache;

    //DO NOT CALL THIS.
    //This class is a singleton and should only be called from HEIPlugin
    //Since it doesn't need to be accessed, I forgo the INSTANCE static field
    public ResearchManager(IJeiRuntime runtime){
        this.runtime = runtime;
        MinecraftForge.EVENT_BUS.register(this);

        //Build the researchCache
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        researchCache = AbstractResearchCategory.categories.stream()
            .flatMap(category -> category.recipes.stream())
            .collect(
                HashMap::new,
                (map, wrapper) -> {
                    String research = wrapper.getResearch();
                    if(research == null || ThaumcraftCapabilities.knowsResearchStrict(player, research)){
                        return;
                    }
                    if(research.contains("&&")){
                        for(String researchSplit : wrapper.getResearch().split("&&")){
                            if(researchSplit.contains("||")){
                                for(String researchSplitSplit : wrapper.getResearch().split("\\|\\|")){
                                    map.computeIfAbsent(normalizeResearchKey(researchSplitSplit), key -> new ArrayList<>()).add(wrapper);
                                }
                            } else {
                                map.computeIfAbsent(normalizeResearchKey(researchSplit), key -> new ArrayList<>()).add(wrapper);
                            }
                        }
                    } else if(research.contains("||")){
                        for(String researchSplit : wrapper.getResearch().split("\\|\\|")){
                            map.computeIfAbsent(normalizeResearchKey(researchSplit), key -> new ArrayList<>()).add(wrapper);
                        }
                    } else {
                        map.computeIfAbsent(normalizeResearchKey(research), key -> new ArrayList<>()).add(wrapper);
                    }
                },
                //Merge 2 in 1
                (map1, map2) -> {
                    map2.forEach((key2, value2) ->
                        map1.merge(key2, value2, (exising, incoming) -> {
                            exising.addAll(incoming);
                            return exising;
                        })
                    );
                }
            )
        ;
        //Only recipes in the cache are those that the player has not unlocked the research for.
        researchCache.values().forEach(collection ->
            collection.forEach(wrapper -> runtime.getRecipeRegistry().hideRecipe(wrapper, wrapper.getCategory()))
        );
    }

    @SubscribeEvent
    public void onResearch(ResearchEvent.Research event) {
        String research_direct = event.getResearchKey();
        if(research_direct == null || research_direct.isEmpty()){
            //TODO: is this legal?
            return;
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        String research = normalizeResearchKey(research_direct);
        Collection<AbstractResearchWrapper> candidates;
        if(ThaumcraftCapabilities.knowsResearchStrict(player, research)){
            //If research was acquired fully, we can remove the whole entry from the cache as it will never get triggered again, saving a bit of memory
            candidates = researchCache.remove(research);
        } else {
            candidates = researchCache.get(research);
        }
        candidates.forEach(wrapper -> {
            if(ThaumcraftCapabilities.knowsResearch(player, wrapper.getResearch())){
                runtime.getRecipeRegistry().unhideRecipe(wrapper, wrapper.getCategory());
            }
        });
    }

    /**
     * Remove the @ symbol indicating a research stage
     * Taken from Thaumic JEI Unofficial
     * @return A string without the research stage and any leading/trailing whitespace
    */
    private static String normalizeResearchKey(@Nonnull String research) {
		String trimmed = research.trim();
        int atIndex = trimmed.indexOf('@');
        return atIndex >= 0 ? trimmed.substring(0, atIndex) : trimmed;
    }
}
