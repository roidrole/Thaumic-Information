package roidrole.thaumicinfo.jei;

import mezz.jei.api.IJeiRuntime;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import roidrole.thaumicinfo.ThaumicInformation;
import roidrole.thaumicinfo.jei.categories.AbstractResearchCategory;
import roidrole.thaumicinfo.jei.categories.AbstractResearchWrapper;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class ResearchManager {
    //If this is not null, the ResearchManager is working
    private static @Nullable ResearchManager instance;
    private static IJeiRuntime runtime;
    private Map<String, Collection<AbstractResearchWrapper>> researchCache;
    private final Collection<Consumer<EntityPlayerSP>> scheduled;

    /**
     * Both init() and deinit() methods are intended t be called during config changed event.
     * They are designed not to require a Minecraft/world restart, simply rebuilding the hiding tree.
     * As such, init() assumes that hideRecipesIfMissingResearch is true, and deInit() assumes the opposite.
     * setRuntime should be called regardless when the JEI runtime is available
     */
    public static void init(){
        if(instance != null){
            return;
        }
		instance = new ResearchManager();
		MinecraftForge.EVENT_BUS.register(instance);
	}
    public static void deInit(){
        if(instance == null){
            return;
        }
        MinecraftForge.EVENT_BUS.unregister(instance);
        AbstractResearchCategory.categories.stream()
            .flatMap(category -> category.recipes.stream())
            .forEach(wrapper -> runtime.getRecipeRegistry().unhideRecipe(wrapper, wrapper.getCategory()));
    }
    public static void setRuntime(IJeiRuntime runtime){
        ResearchManager.runtime = runtime;
    }

    private ResearchManager(){
        this.scheduled = new ArrayList<>(2);
    }

    //Because the world scheduler executes immediately.
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event){

        if(event.phase == TickEvent.Phase.END && event.player instanceof EntityPlayerSP) {
            scheduled.forEach(consumer -> consumer.accept((EntityPlayerSP) event.player));
            scheduled.clear();
        }
    }

    @SubscribeEvent
    public void onWorldJoin(WorldEvent.Load event){
        if(event.getWorld() instanceof WorldClient) {
            scheduled.add(this::buildResearchCache);
        }
    }

    //TODO: asynchronous? Profile and see
    private void buildResearchCache(EntityPlayerSP player){
        ThaumicInformation.LOGGER.info("Hiding locked recipes...");

        //Build the researchCache
        //Has to be scheduled because it calls ThaumcraftCapabilities.knowsResearchStrict
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
            return;
        }

        String research = normalizeResearchKey(research_direct);

        //Thaumcraft posts the research event *before* actually adding the research.
        // Also, Minecraft.getMinecraft().addScheduledTask executes instantly, so a custom scheduler is needed
        scheduled.add((player) -> {
            Collection<AbstractResearchWrapper> candidates;
            if(ThaumcraftCapabilities.knowsResearchStrict(player, research)){
                //If research was acquired fully, we can remove the whole entry from the cache as it will never get triggered again, saving a bit of memory
                candidates = researchCache.remove(research);
            } else {
                candidates = researchCache.get(research);
            }
            //Some researches are not linked to any recipes.
            if(candidates == null){
                return;
            }
            candidates.forEach(wrapper -> {
                if(ThaumcraftCapabilities.knowsResearch(player, wrapper.getResearch())){
                    runtime.getRecipeRegistry().unhideRecipe(wrapper, wrapper.getCategory());
                }
            });
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
