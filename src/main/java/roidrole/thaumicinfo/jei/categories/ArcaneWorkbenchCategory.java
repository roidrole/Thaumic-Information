package roidrole.thaumicinfo.jei.categories;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.crafting.IShapedRecipe;
import roidrole.thaumicinfo.Tags;
import roidrole.thaumicinfo.jei.AlphaDrawable;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ArcaneWorkbenchCategory extends AbstractResearchCategory<ArcaneWorkbenchCategory.ArcaneWorkbenchWrapper> {

    //Thaumic Energistics takes this category by UUID. Though it pains me, this is what we must
    public static final String UUID = "THAUMCRAFT_ARCANE_WORKBENCH";

    //Map<Aspect, index>
    private static final Object2IntMap<Aspect> recipeAspects = new Object2IntOpenHashMap<Aspect>(){{
        put(Aspect.AIR, 0);
        put(Aspect.EARTH, 1);
        put(Aspect.WATER, 2);
        put(Aspect.FIRE, 3);
        put(Aspect.ORDER, 4);
        put(Aspect.ENTROPY, 5);
    }};
    private static int getCrystalYPos(int index){
        return 4 + 22 * index;
    }

    private static final ResourceLocation crystalOverlay = new ResourceLocation(Tags.MOD_ID, "textures/gui/crystal_background.png");

    @Override
    public String getUid() {
        return UUID;
    }

    @Override
    public String getTitle() {
        return new ItemStack(BlocksTC.arcaneWorkbench).getDisplayName();
    }

    @Override
    public IDrawable getBackground() {
        return new AlphaDrawable(new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png"), 225, 31, 102, 102, 36, 0, 30, 30);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(new ResourceLocation(Thaumcraft.MODID, "textures/gui/gui_researchbook_overlay.png"));
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(51 - 16 + 30, 0, 40, 6, 32, 32, 512, 512);
        Gui.drawModalRectWithCustomSizedTexture(-18 + 30, 4, 135, 152, 23, 23, 512, 512);

        minecraft.getTextureManager().bindTexture(crystalOverlay);
        recipeAspects.object2IntEntrySet().forEach(entry -> {
            int color = entry.getKey().getColor();
            float red = (float)(color >> 16 & 255) / 255.0F;
            float green = (float)(color >> 8 & 255) / 255.0F;
            float blue = (float)(color & 255) / 255.0F;
            GlStateManager.color(red, green, blue);
            Gui.drawModalRectWithCustomSizedTexture(141, getCrystalYPos(entry.getIntValue()), 16, 16, 16, 16, 16, 16);
        });
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, ArcaneWorkbenchWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
        if (recipeWrapper.getRecipe() instanceof ShapelessArcaneRecipe) {
            recipeLayout.setShapeless();
        }
        IGuiItemStackGroup stackGroup = recipeLayout.getItemStacks();
        int slot = 0;

        //Note that index 0 is the list of crystals, slightly breaking convention
        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);

        //Output
        stackGroup.init(slot, false, 51 - 9 + 30, 7);
        stackGroup.set(slot, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        slot++;

        //Input
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                if(slot >= inputs.size()){
                    break;
                }
                stackGroup.init(slot, true, 11 + (x * 32) + 30, 11 + (y * 31) + 36);
                List<ItemStack> items = inputs.get(slot);
                if (items != null) {
                    stackGroup.set(slot, items);
                }
                slot++;
            }
        }

        //Crystals
        List<ItemStack> crystals = inputs.get(0);
        for (int i = 0; i < crystals.size(); i++) {
            ItemStack crystal = crystals.get(i);
            if(crystal == null){
                continue;
            }
            int yPos = getCrystalYPos(i);
            stackGroup.init(slot, false, 118 + 23, yPos);
            stackGroup.set(slot, crystal);
            slot++;
        }
    }

    @Override
    public void populateRecipes() {
        final Map<Aspect, Int2ObjectMap<ItemStack>> canonicalisationMap = new HashMap<>();

        //Populate the canonicalisationMap with the crystals we know we need
        recipeAspects.keySet().forEach(aspect -> canonicalisationMap.compute(aspect, (key, value) -> {
            ItemStack crystal = new ItemStack(ItemsTC.crystalEssence);
            ((ItemCrystalEssence) ItemsTC.crystalEssence).setAspects(crystal, new AspectList().add(key, 1));
            Int2ObjectMap<ItemStack> output = new Int2ObjectOpenHashMap<>();
            output.put(1, crystal);
            return output;
        }));

        this.recipes = StreamSupport.stream(CraftingManager.REGISTRY.spliterator(), false)
            .filter(iRecipe -> iRecipe instanceof IArcaneRecipe)
            .map(iRecipe -> new ArcaneWorkbenchCategory.ArcaneWorkbenchWrapper((IArcaneRecipe) iRecipe, canonicalisationMap))
            .collect(Collectors.toList())
        ;
    }

    public static class ArcaneWorkbenchWrapper extends AbstractResearchWrapper {

        private final IArcaneRecipe recipe;
        private final List<ItemStack> crystals;

        public ArcaneWorkbenchWrapper(IArcaneRecipe recipe, Map<Aspect, Int2ObjectMap<ItemStack>> canonicalisationMap) {
            this.recipe = recipe;

            //Compute crystals
            AspectList aspectList = recipe.getCrystals();
            if(aspectList == null || aspectList.aspects.isEmpty()){
                this.crystals = Collections.emptyList();
            } else {
                List<ItemStack> crystals = Arrays.asList(new ItemStack[recipeAspects.size()]);
                aspectList.aspects.forEach((aspect, count) -> {
                    Int2ObjectMap<ItemStack> aspectMap = canonicalisationMap.get(aspect);
                    ItemStack crystal = aspectMap.computeIfAbsent(count, countIn -> {
                        ItemStack crystal2 = new ItemStack(ItemsTC.crystalEssence, countIn);
                        //Because crystals have their aspect in pure NBT, we can share the tag compound.
                        crystal2.setTagCompound(aspectMap.get(1).getTagCompound());
                        return crystal2;
                    });
                    crystals.set(recipeAspects.getInt(aspect), crystal);
                });
                this.crystals = crystals;
            }
        }

        @Override
        public void getIngredients(@Nonnull IIngredients ingredients) {
            List<Ingredient> input;
            ItemStack output = recipe.getRecipeOutput();

            //For a 2x2 recipes to still be a square, we pad with Ingredient.EMPTY
            if (recipe instanceof IShapedRecipe){
                input = new ArrayList<>(9);
                IShapedRecipe shapedRecipe = (IShapedRecipe) recipe;
                int sizeX = shapedRecipe.getRecipeWidth();
                int sizeY = shapedRecipe.getRecipeHeight();
                int slot = 0;
                int y = 0;
                for (; y < sizeY; ++y) {
                    int x = 0;
                    for (; x < sizeX; ++x) {
						input.add(recipe.getIngredients().get(slot));
						++slot;
					}
                    for (; x < 3; x++) {
                        input.add(Ingredient.EMPTY);
                    }
                }
                for (; y < 3; y++) {
                    //If it is out of bounds of y, it is out of bounds
                    for (int x = 0; x < 3; x++) {
                        input.add(Ingredient.EMPTY);
                    }
                }
            } else {
                input = recipe.getIngredients();
            }

            List<List<ItemStack>> inputLists = new ArrayList<>(input.size() + 1);
            inputLists.add(this.crystals);
            for (Ingredient ingredient : input) {
                inputLists.add(Arrays.asList(ingredient.getMatchingStacks()));
            }
            ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
            ingredients.setOutput(VanillaTypes.ITEM, output);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
            minecraft.fontRenderer.drawString(TextFormatting.DARK_GRAY + String.valueOf(recipe.getVis()), 50 - minecraft.fontRenderer.getStringWidth(String.valueOf(recipe.getVis())) / 2, 12, 0);
        }

        @Override
        public int getBarrierX() {
            return 108;
        }

        @Override
        public int getBarrierY() {
            return 8;
        }

        @Override
        public String getCategory() {
            return ArcaneWorkbenchCategory.UUID;
        }

        @Override
        public List<String> getTooltipStrings(int mouseX, int mouseY) {
            if (mouseX > 34 && mouseX < 60 && mouseY > 4 && mouseY < 28) {
                return Collections.singletonList("Vis Cost");
            }
            return super.getTooltipStrings(mouseX, mouseY);
        }

        public IArcaneRecipe getRecipe() {
            return recipe;
        }

        @Override
        public String getResearch() {
            return recipe.getResearch();
        }
    }
}
