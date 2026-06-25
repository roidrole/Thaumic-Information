package roidrole.thaumicroid;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import roidrole.thaumicroid.jei.categories.AspectFromItemStackCategory;
import roidrole.thaumicroid.utils.ArrayMap;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CacheManager {
	private static final File JEI_CACHE = new File(ThaumicRoidConfig.general.cachePath, "jei-cache.json");
	private static final File ASPECT_CACHE = new File(ThaumicRoidConfig.general.cachePath, "aspect-cache.bin");
	private static final File ENTITY_CACHE = new File(ThaumicRoidConfig.general.cachePath, "entity-cache.bin");
	public static IModRegistry jeiRegistry;

	static {
		new File(ThaumicRoidConfig.general.cachePath).mkdirs();
	}

	public static boolean canRunCaches(){
		return ASPECT_CACHE.isFile() && ENTITY_CACHE.isFile();
	}

	public static void writeCaches(){
		boolean genAspectCache = ThaumicRoidConfig.performanceConfig.aspectCache && !ASPECT_CACHE.isFile();
		boolean genEntityCache = ThaumicRoidConfig.performanceConfig.aspectCache && !ENTITY_CACHE.isFile();
		boolean genJEICache = ThaumicRoidConfig.jeiConfig.categoryToggle.aspectFromItemStack && !JEI_CACHE.isFile();

		if(genAspectCache){
			createAspectCache(ASPECT_CACHE);
		}
		if(genEntityCache){
			createEntityCache(ENTITY_CACHE);
		}
		if(genJEICache){
			createJeiCache(JEI_CACHE);
		}
	}

	public static void createJeiCache(File aspectFile){
		Collection<ItemStack> items = jeiRegistry.getIngredientRegistry().getAllIngredients(VanillaTypes.ITEM);
		long time = System.currentTimeMillis();
		ThaumicRoid.LOGGER.info("Caching ItemStack Aspects.");
		ThaumicRoid.LOGGER.info("Trying to cache {} aspects.", items.size());
		//Filter out blacklisted items
		Set<ResourceLocation> blacklist = new HashSet<>();
		for (String string : ThaumicRoidConfig.jeiConfig.blacklistedFromAspectChecking){
			blacklist.add(new ResourceLocation(string));
		}
		blacklist.add(null);
		blacklist.add(Items.AIR.getRegistryName());


		//Because concurrency
		final AtomicInteger cachedAmount = new AtomicInteger(0);
		final AtomicLong lastTimeChecked = new AtomicLong(System.currentTimeMillis());

		Map<Aspect, ArrayMap<List<String>>> cache = items
			.parallelStream()
			.filter(stack -> !blacklist.contains(stack.getItem().delegate.name()))
			//Since Thaumcraft caches ItemStack aspects itself, filtering for empty AspectList is fine
			.filter(stack -> {
				AspectList list = AspectHelper.getObjectAspects(stack);
				if (list == null || list.size() == 0){
					cachedAmount.getAndIncrement();
					return false;
				}
				return true;
			})
			.collect(
				//So I don't have to computeIfAbsent
				() -> {
					Map<Aspect, ArrayMap<List<String>>> map = new HashMap<>();
					for(Aspect aspect : Aspect.aspects.values()){
						map.put(aspect, new ArrayMap<>());
					}
					return map;
				},
				(map, stack) -> {
					AspectList list = AspectHelper.getObjectAspects(stack);
					list.aspects.forEach((aspect, count) -> map
						.get(aspect)
						.computeIfAbsent(count, ArrayList::new)
						.add(writeItemStack(stack, count))
					);

					cachedAmount.getAndIncrement();
					if (lastTimeChecked.get() + 5000 < System.currentTimeMillis()) {
						lastTimeChecked.set(System.currentTimeMillis());
						ThaumicRoid.LOGGER.info("ItemStack Aspect checking at {}%", 100 * cachedAmount.get() / items.size());
					}
				},
				(map1, map2) -> {
					//Merge 2 in 1
					map2.forEach((key2, value2) -> {
						ArrayMap<List<String>> value1 = map1.get(key2);
						value2.forEach((count2, strings2) -> {
							value1.computeIfAbsent(count2, ArrayList::new).addAll(strings2);
						});
					});
				}
			)
		;

		ThaumicRoid.LOGGER.info("ItemStack Aspect checking at 100%");
		try (JsonWriter writer = new JsonWriter(new FileWriter(aspectFile))){
			writer.setIndent("\t");
			//Write the JSON by hand. Less annoying
			writer.beginObject();
			cache
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey(Comparator.comparing(Aspect::getTag)))
				.forEach(entry -> {
					try {
						writer.name(entry.getKey().getTag());
						writer.beginArray();
						entry.getValue().forEach(
							(count, list) -> list.forEach(stack -> {
								try {
									writer.jsonValue(stack);
								} catch (IOException ignored) { }
							})
						);
						writer.endArray();
					} catch (IOException ignored) { }
				});
			writer.endObject();
		} catch (IOException e) {
			ThaumicRoid.LOGGER.error("Can't write aspect file!", e);
		}
		ThaumicRoid.LOGGER.info("Wrote aspect file in {} ms", System.currentTimeMillis() - time);
		jeiRegistry = null;
	}

	public static void parseJeiCache(IModRegistry registry){
		if(!JEI_CACHE.isFile()){
			return;
		}
		long time = System.currentTimeMillis();
		List<AspectFromItemStackCategory.AspectFromItemStackWrapper> wrappers = new ArrayList<>();
		Map<Aspect, List<ItemStack>> cache = new LinkedHashMap<>(Aspect.aspects.size());

		try (JsonReader reader = new JsonReader(new FileReader(JEI_CACHE))){
			reader.beginObject();
			reader.setLenient(true);

			do {
				Aspect aspect = Aspect.getAspect(reader.nextName());
				List<ItemStack> list = new ArrayList<>();
				cache.put(aspect, list);
				reader.beginArray();
				while(reader.peek() != JsonToken.END_ARRAY){
					list.add(readItemStack(reader));
				}
				reader.endArray();

			} while (reader.peek() != JsonToken.END_OBJECT);
			reader.endObject();


			cache.forEach((aspect, stacks) -> {
				AspectList aspectList = new AspectList();
				aspectList.add(aspect, 0);
				int start = 0;
				while (start < stacks.size() - 36) {
					List<ItemStack> subList = stacks.subList(start, start + 36);
					wrappers.add(new AspectFromItemStackCategory.AspectFromItemStackWrapper(aspectList, subList));
					start += 36;
				}
				List<ItemStack> subList = stacks.subList(start, stacks.size());
				wrappers.add(new AspectFromItemStackCategory.AspectFromItemStackWrapper(aspectList, subList));
			});
		} catch (FileNotFoundException e) {
			ThaumicRoid.LOGGER.error("Can't read aspect file!", e);
			return;
		} catch (NBTException e) {
			ThaumicRoid.LOGGER.error("Malformed aspect file. Please regenerate", e);
		} catch (IOException e) {
			ThaumicRoid.LOGGER.error("Can't read aspect file. Please regenerate", e);
		}
		ThaumicRoid.LOGGER.info("Parsed aspect file in {} ms", System.currentTimeMillis() - time);

		registry.addRecipes(wrappers, AspectFromItemStackCategory.UUID);
	}

	//Writes ItemStack to format: [resourceLocation, count, damage, tag]
	public static String writeItemStack(ItemStack stack, int count){
		StringBuilder itemNbt = new StringBuilder(64);
		itemNbt.append("[\"");
		itemNbt.append(stack.getItem().delegate.name());
		itemNbt.append("\",");
		itemNbt.append(count);
		if(stack.getItemDamage() != 0){
			itemNbt.append(',');
			itemNbt.append(stack.getItemDamage());
		}
		if (stack.getTagCompound() != null){
			itemNbt.append(',');
			itemNbt.append(NBTTagString.quoteAndEscape(stack.getTagCompound().toString()));
		}
		itemNbt.append(']');
		return itemNbt.toString();
	}

	//Reads ItemStack from format: [resourceLocation, count, damage, tag]
	public static ItemStack readItemStack(JsonReader reader) throws IOException, NBTException {
		reader.beginArray();
		ItemStack stack = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(reader.nextString())), reader.nextInt());
		if(reader.peek() == JsonToken.NUMBER){
			stack.setItemDamage(reader.nextInt());
		}
		if(reader.peek() == JsonToken.STRING){
			stack.setTagCompound(JsonToNBT.getTagFromJson(reader.nextString()));
		}
		reader.endArray();
		return stack;
	}


	//Dumps the contents of CommonInternals.objectTags to a binary file
	public static void createAspectCache(File aspectFile){
		try(ObjectOutputStream writer = new ObjectOutputStream(Files.newOutputStream(aspectFile.toPath()))){
			writer.writeObject(CommonInternals.objectTags);
		} catch (IOException e) {
			throw new RuntimeException("Error writing aspect file", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void parseAspectCache(){
		try(ObjectInputStream reader = new ObjectInputStream(Files.newInputStream(ASPECT_CACHE.toPath()))){
			CommonInternals.objectTags = (ConcurrentHashMap<Integer, AspectList>) reader.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Error reading aspect cache. Please regenerate", e);
		}
	}


	//Dumps the contents of CommonInternals.scanEntities to a binary file
	public static void createEntityCache(File entityFile){
		try(ObjectOutputStream writer = new ObjectOutputStream(Files.newOutputStream(entityFile.toPath()))){
			writer.writeObject(CommonInternals.scanEntities);
		} catch (IOException e) {
			throw new RuntimeException("Error writing aspect file", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void parseEntityCache(){
		try(ObjectInputStream reader = new ObjectInputStream(Files.newInputStream(ENTITY_CACHE.toPath()))){
			CommonInternals.scanEntities = (ArrayList<thaumcraft.api.ThaumcraftApi.EntityTags>) reader.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Error reading entity cache. Please regenerate", e);
		}
	}
}
