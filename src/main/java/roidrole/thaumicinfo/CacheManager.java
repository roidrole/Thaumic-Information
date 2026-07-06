package roidrole.thaumicinfo;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.ints.*;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import roidrole.thaumicinfo.jei.categories.AspectFromItemStackCategory;
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
	private static final File JEI_CACHE = new File(ThaumicInformationConfig.general.cachePath, "jei-cache.json");
	private static final File ASPECT_CACHE = new File(ThaumicInformationConfig.general.cachePath, "aspect-cache.bin");
	private static final File ENTITY_CACHE = new File(ThaumicInformationConfig.general.cachePath, "entity-cache.bin");
	public static IModRegistry jeiRegistry;

	static {
		new File(ThaumicInformationConfig.general.cachePath).mkdirs();
	}

	public static boolean canRunCaches(){
		return ASPECT_CACHE.isFile() && ENTITY_CACHE.isFile();
	}

	public static void writeCaches(){
		boolean genAspectCache = ThaumicInformationConfig.performanceConfig.aspectCache && !ASPECT_CACHE.isFile();
		boolean genEntityCache = ThaumicInformationConfig.performanceConfig.aspectCache && !ENTITY_CACHE.isFile();
		//jeiRegistry is null on dedicated servers or if JEI is not installed
		boolean genJEICache = jeiRegistry != null && ThaumicInformationConfig.jeiConfig.categoryToggle.aspectFromItemStack && !JEI_CACHE.isFile();
		
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
		ThaumicInformation.LOGGER.info("Caching ItemStack Aspects.");
		ThaumicInformation.LOGGER.info("Trying to cache {} aspects.", items.size());
		//Filter out blacklisted items
		Set<ResourceLocation> blacklist = new HashSet<>();
		for (String string : ThaumicInformationConfig.jeiConfig.jeiBlacklist){
			blacklist.add(new ResourceLocation(string));
		}
		blacklist.add(null);
		blacklist.add(Items.AIR.getRegistryName());


		//Because concurrency
		final AtomicInteger cachedAmount = new AtomicInteger(0);
		final AtomicLong lastTimeChecked = new AtomicLong(System.currentTimeMillis());

		Map<Aspect, Int2ObjectSortedMap<List<String>>> cache = items
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
				HashMap::new,
				(map, stack) -> {
					AspectList list = AspectHelper.getObjectAspects(stack);
					int totalCount = list.visSize();
					if(totalCount == 0){
						return;
					}
					list.aspects.forEach((aspect, count) -> map
						.computeIfAbsent(aspect, key -> new Int2ObjectRBTreeMap<>(Comparator.reverseOrder()))
						.computeIfAbsent(count, c -> new ArrayList<>())
						.add(writeItemStack(stack, count, (191 * count / totalCount) + 32))
					);

					cachedAmount.getAndIncrement();
					if (lastTimeChecked.get() + 5000 < System.currentTimeMillis()) {
						lastTimeChecked.set(System.currentTimeMillis());
						ThaumicInformation.LOGGER.info("ItemStack Aspect checking at {}%", 100 * cachedAmount.get() / items.size());
					}
				},
				(map1, map2) -> {
					//Merge 2 in 1
					map2.forEach((aspect2, value2) -> {
						map1.merge(aspect2, value2, (entries1, entries2) -> {
							entries2.forEach((count2, items2) -> {
								entries1.merge(count2, items2, (stacks1, stacks2) -> {
									stacks1.addAll(stacks2);
									return stacks1;
								});
							});
							return entries1;
						});
					});
				}
			)
		;

		ThaumicInformation.LOGGER.info("ItemStack Aspect checking at 100%");
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
							(count, list) -> list
								.stream()
								.sorted(Comparator.comparingInt(string -> -Integer.parseInt(string.substring(1, string.indexOf(',')))))
								.forEach(stack -> {
									try {
										writer.jsonValue(stack);
									} catch (IOException ignored) { }
								}
							)
						);
						writer.endArray();
					} catch (IOException ignored) { }
				});
			writer.endObject();
		} catch (IOException e) {
			ThaumicInformation.LOGGER.error("Can't write aspect file!", e);
		}
		ThaumicInformation.LOGGER.info("Wrote aspect file in {} ms", System.currentTimeMillis() - time);
		jeiRegistry = null;
	}

	public static void parseJeiCache(IModRegistry registry){
		if(!JEI_CACHE.isFile()){
			return;
		}
		long time = System.currentTimeMillis();
		List<AspectFromItemStackCategory.AspectFromItemStackWrapper> wrappers = new ArrayList<>();

		try (JsonReader reader = new JsonReader(new FileReader(JEI_CACHE))){
			reader.beginObject();
			reader.setLenient(true);

			do {
				//Read
				Aspect aspect = Aspect.getAspect(reader.nextName());
				List<ItemStack> list = new ArrayList<>();
				IntList purityList = new IntArrayList();
				reader.beginArray();
				while(reader.peek() != JsonToken.END_ARRAY){
					reader.beginArray();
					purityList.add(reader.nextInt());
					list.add(readItemStack(reader));
					reader.endArray();
				}
				reader.endArray();

				//Interpret
				AspectList aspectList = new AspectList();
				aspectList.add(aspect, 0);
				int start = 0;
				while (start < list.size() - 36) {
					List<ItemStack> subList = list.subList(start, start + 36);
					IntList subPurityList = purityList.subList(start, start + 36);
					wrappers.add(new AspectFromItemStackCategory.AspectFromItemStackWrapper(aspectList, subList, subPurityList));
					start += 36;
				}
				List<ItemStack> subList = list.subList(start, list.size());
				IntList subPurityList = purityList.subList(start, purityList.size());
				wrappers.add(new AspectFromItemStackCategory.AspectFromItemStackWrapper(aspectList, subList, subPurityList));

			} while (reader.peek() != JsonToken.END_OBJECT);

			reader.endObject();
		} catch (FileNotFoundException e) {
			ThaumicInformation.LOGGER.error("Can't read aspect file!", e);
			return;
		} catch (NBTException e) {
			ThaumicInformation.LOGGER.error("Malformed aspect file. Please regenerate", e);
		} catch (IOException e) {
			ThaumicInformation.LOGGER.error("Can't read aspect file. Please regenerate", e);
		}
		ThaumicInformation.LOGGER.info("Parsed aspect file in {} ms", System.currentTimeMillis() - time);

		registry.addRecipes(wrappers, AspectFromItemStackCategory.UUID);
	}

	//Writes ItemStack to format: [purity, resourceLocation, count, damage, tag]
	public static String writeItemStack(ItemStack stack, int count, int purity){
		StringBuilder itemNbt = new StringBuilder(64);
		itemNbt.append('[');
		itemNbt.append(purity);
		itemNbt.append(",\"");
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

	//Reads ItemStack from format: resourceLocation, count, damage, tag
	public static ItemStack readItemStack(JsonReader reader) throws IOException, NBTException {
		ItemStack stack = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(reader.nextString())), reader.nextInt());
		if(reader.peek() == JsonToken.NUMBER){
			stack.setItemDamage(reader.nextInt());
		}
		if(reader.peek() == JsonToken.STRING){
			stack.setTagCompound(JsonToNBT.getTagFromJson(reader.nextString()));
		}
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
