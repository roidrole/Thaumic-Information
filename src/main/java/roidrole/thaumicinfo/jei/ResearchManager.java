package roidrole.thaumicinfo.jei;

import mezz.jei.api.IJeiRuntime;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;
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
    private Map<String, List<AbstractResearchWrapper>> researchCache;
    private final Collection<Consumer<EntityPlayerSP>> scheduled;

    /**
     * Both init() and deInit() methods are intended to be called during config changed event.
     * They are designed not to require a Minecraft/world restart, simply rebuilding/deleting the hiding tree.
     * As such, init() assumes that hideRecipesIfMissingResearch is true, and deInit() assumes the opposite.
     * setRuntime should be called regardless when the JEI runtime is available
     */
    public static void init(){
        if(instance != null){
            return;
        }
		instance = new ResearchManager();
		MinecraftForge.EVENT_BUS.register(instance);
        instance.scheduled.add(instance::buildResearchCache);
	}
    public static void deInit(){
        if(instance == null){
            return;
        }
        MinecraftForge.EVENT_BUS.unregister(instance);
        AbstractResearchCategory.categories.stream()
            .flatMap(category -> category.recipes.stream())
            .forEach(wrapper -> runtime.getRecipeRegistry().unhideRecipe(wrapper, wrapper.getCategory()));
        //So the instance (and the cache) get GCed and to mark it as disabled
        instance = null;
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
            for(Consumer<EntityPlayerSP> consumer : scheduled){
                consumer.accept((EntityPlayerSP) event.player);
            }
            scheduled.clear();
        }
    }

    //TODO: asynchronous? Profile and see
    private void buildResearchCache(EntityPlayerSP player){
        ThaumicInformation.LOGGER.info("Hiding locked recipes...");
        long start = System.currentTimeMillis();

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
                                    addToMap(map, normalizeResearchKey(researchSplitSplit), wrapper);
                                }
                            } else {
                                addToMap(map, normalizeResearchKey(researchSplit), wrapper);
                            }
                        }
                    } else if(research.contains("||")){
                        for(String researchSplit : wrapper.getResearch().split("\\|\\|")){
                            addToMap(map, normalizeResearchKey(researchSplit), wrapper);
                        }
                    } else {
                        addToMap(map, normalizeResearchKey(research), wrapper);
                    }
                },
                //Merge 2 in 1
                (map1, map2) ->
                    map2.forEach((key2, value2) ->
                        //Setting it to a fixed-sized list is safe because Java doesn't call the accumulator on the result of the combiner.
                        map1.merge(key2, value2, (existing, incoming) -> {
                            int sizeExisting = existing.size();
                            int sizeIncoming = incoming.size();
                            Object[] newArray = new Object[sizeExisting + sizeIncoming];
                            //This technically means ArrayList is wasting a single copy — worth it to avoid reflection
                            System.arraycopy(existing.toArray(), 0, newArray, 0, sizeExisting);
                            System.arraycopy(incoming.toArray(), 0, newArray, sizeExisting, sizeIncoming);
                            return Arrays.asList((AbstractResearchWrapper[])newArray);
                        })
                    )
            )
        ;
        //Only recipes in the cache are those that the player has not unlocked the research for.
        researchCache.values().forEach(list ->
            list.forEach(wrapper -> runtime.getRecipeRegistry().hideRecipe(wrapper, wrapper.getCategory()))
        );
        ThaumicInformation.LOGGER.info("Built research cache in {} ms", System.currentTimeMillis() - start);
    }
    private <K, V> void addToMap(Map<K, List<V>> map, K key, V value){
        map.compute(key, (k, existing) -> {
            if(existing == null){
                return Collections.singletonList(value);
            } else if(existing.size() == 1) {
                List<V> newList = new ArrayList<>(2);
                newList.add(existing.get(0));
                newList.add(value);
                return newList;
            } else {
                existing.add(value);
                return existing;
            }
        });
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
