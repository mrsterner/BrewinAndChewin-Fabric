package dev.sterner.brewinandchewin.datagen;

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import dev.sterner.brewinandchewin.client.recipebook.KegRecipeBookTab;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;

import java.util.function.Consumer;

public class BCRecipeProvider extends FabricRecipeProvider {
    public static final int FERMENTING_TIME = 12000;        // 5 seconds

    public BCRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {

        //Brews
        BCKegRecipeBuilder.kegRecipe(BCObjects.BEER, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.WATER_BUCKET, KegRecipeBookTab.DRINKS, 3)
                .addIngredient(Items.WHEAT)
                .addIngredient(Items.WHEAT)
                .addIngredient(Items.WHEAT)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.VODKA, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.WATER_BUCKET, KegRecipeBookTab.DRINKS, 3)
                .addIngredient(Items.POTATO)
                .addIngredient(Items.POTATO)
                .addIngredient(Items.POTATO)
                .addIngredient(Items.WHEAT)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.MEAD, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.HONEY_BOTTLE, KegRecipeBookTab.DRINKS, 3)
                .addIngredient(Items.WHEAT)
                .addIngredient(Items.WHEAT)
                .addIngredient(Items.SWEET_BERRIES)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.RICE_WINE, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.WATER_BUCKET, KegRecipeBookTab.DRINKS, 3)
                .addIngredient(ItemsRegistry.RICE.get())
                .addIngredient(ItemsRegistry.RICE.get())
                .addIngredient(ItemsRegistry.RICE.get())
                .addIngredient(ItemsRegistry.RICE.get())
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.EGG_GROG, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.MILK_BUCKET, KegRecipeBookTab.DRINKS, 3)
                .addIngredient(Items.EGG)
                .addIngredient(Items.EGG)
                .addIngredient(ItemsRegistry.CABBAGE_LEAF.get())
                .addIngredient(Items.SUGAR)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.STRONGROOT_ALE, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.BEER, KegRecipeBookTab.DRINKS, 3)
                .addIngredient(Items.BEETROOT)
                .addIngredient(Items.POTATO)
                .addIngredient(Items.BROWN_MUSHROOM)
                .addIngredient(Items.CARROT)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.SACCHARINE_RUM, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.MEAD, KegRecipeBookTab.DRINKS, 4)
                .addIngredient(Items.SWEET_BERRIES)
                .addIngredient(Items.SUGAR_CANE)
                .addIngredient(Items.SUGAR_CANE)
                .addIngredient(Items.MELON_SLICE)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.PALE_JANE, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.RICE_WINE, KegRecipeBookTab.DRINKS, 4)
                .addIngredient(Items.HONEY_BOTTLE)
                .addIngredient(ItemsRegistry.TREE_BARK.get())
                .addIngredient(Items.LILY_OF_THE_VALLEY)
                .addIngredient(Items.SUGAR)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.DREAD_NOG, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.EGG_GROG, KegRecipeBookTab.DRINKS, 1)
                .addIngredient(Items.EGG)
                .addIngredient(Items.EGG)
                .addIngredient(Items.TURTLE_EGG)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.SALTY_FOLLY, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.VODKA, KegRecipeBookTab.DRINKS, 2)
                .addIngredient(Items.SEA_PICKLE)
                .addIngredient(Items.DRIED_KELP)
                .addIngredient(Items.DRIED_KELP)
                .addIngredient(Items.SEAGRASS)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.STEEL_TOE_STOUT, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.STRONGROOT_ALE, KegRecipeBookTab.DRINKS, 1)
                .addIngredient(Items.CRIMSON_FUNGUS)
                .addIngredient(Items.IRON_INGOT)
                .addIngredient(Items.NETHER_WART)
                .addIngredient(Items.WHEAT)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.GLITTERING_GRENADINE, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, Items.WATER_BUCKET, KegRecipeBookTab.DRINKS, 2)
                .addIngredient(Items.GLOW_BERRIES)
                .addIngredient(Items.GLOW_INK_SAC)
                .addIngredient(Items.GLOWSTONE_DUST)
                .addIngredient(Items.GLOW_BERRIES)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.BLOODY_MARY, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.VODKA, KegRecipeBookTab.DRINKS, 4)
                .addIngredient(ItemsRegistry.TOMATO.get())
                .addIngredient(ItemsRegistry.TOMATO.get())
                .addIngredient(ItemsRegistry.CABBAGE_LEAF.get())
                .addIngredient(Items.SWEET_BERRIES)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.RED_RUM, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.BLOODY_MARY, KegRecipeBookTab.DRINKS, 5)
                .addIngredient(Items.CRIMSON_FUNGUS)
                .addIngredient(Items.NETHER_WART)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .addIngredient(Items.SHROOMLIGHT)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.WITHERING_DROSS, 1, FERMENTING_TIME, 0.6F, BCObjects.TANKARD, BCObjects.SALTY_FOLLY, KegRecipeBookTab.DRINKS, 5)
                .addIngredient(Items.WITHER_ROSE)
                .addIngredient(Items.INK_SAC)
                .addIngredient(Items.NETHER_WART)
                .addIngredient(Items.BONE)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.KIMCHI, 2, FERMENTING_TIME, 0.6F, KegRecipeBookTab.MISC, 3)
                .addIngredient(ItemsRegistry.CABBAGE_LEAF.get())
                .addIngredient(BCTags.VEGETABLES)
                .addIngredient(Items.KELP)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.JERKY, 3, FERMENTING_TIME, 0.6F, KegRecipeBookTab.MISC, 3)
                .addIngredient(BCTags.RAW_MEATS)
                .addIngredient(BCTags.RAW_MEATS)
                .addIngredient(BCTags.RAW_MEATS)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.PICKLED_PICKLES, 2, FERMENTING_TIME, 0.6F, Items.HONEY_BOTTLE, KegRecipeBookTab.MISC, 2)
                .addIngredient(Items.SEA_PICKLE)
                .addIngredient(Items.SEA_PICKLE)
                .addIngredient(Items.GLOWSTONE_DUST)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.KIPPERS, 3, FERMENTING_TIME, 0.6F, KegRecipeBookTab.MISC, 3)
                .addIngredient(BCTags.RAW_FISHES)
                .addIngredient(BCTags.RAW_FISHES)
                .addIngredient(Items.KELP)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.COCOA_FUDGE, 1, FERMENTING_TIME, 0.6F, Items.MILK_BUCKET, KegRecipeBookTab.MISC, 5)
                .addIngredient(Items.SUGAR)
                .addIngredient(Items.COCOA_BEANS)
                .addIngredient(Items.COCOA_BEANS)
                .build(exporter);

        //Foods
        BCKegRecipeBuilder.kegRecipe(BCObjects.UNRIPE_FLAXEN_CHEESE_WHEEL, 1, FERMENTING_TIME, 0.6F, Items.HONEYCOMB, Items.MILK_BUCKET, KegRecipeBookTab.MISC, 4)
                .addIngredient(Items.BROWN_MUSHROOM)
                .addIngredient(Items.BROWN_MUSHROOM)
                .addIngredient(Items.SUGAR)
                .build(exporter);
        BCKegRecipeBuilder.kegRecipe(BCObjects.UNRIPE_SCARLET_CHEESE_WHEEL, 1, FERMENTING_TIME, 0.6F, Items.HONEYCOMB, Items.MILK_BUCKET, KegRecipeBookTab.MISC, 4)
                .addIngredient(Items.CRIMSON_FUNGUS)
                .addIngredient(Items.CRIMSON_FUNGUS)
                .addIngredient(Items.SUGAR)
                .build(exporter);

        ShapelessRecipeJsonBuilder.create(BCObjects.HAM_AND_CHEESE_SANDWICH, 2)
                .input(Items.BREAD)
                .input(ItemsRegistry.SMOKED_HAM.get())
                .input(BCObjects.FLAXEN_CHEESE_WEDGE)
                .input(Items.BREAD)
                .criterion("has_cheese", InventoryChangedCriterion.Conditions.items(BCObjects.FLAXEN_CHEESE_WEDGE))
                .offerTo(exporter);
        //Crafting
        ShapedRecipeJsonBuilder.create(BCObjects.PIZZA)
                .pattern("fff")
                .pattern("mtp")
                .pattern("www")
                .input('w', Items.WHEAT)
                .input('m', Items.BROWN_MUSHROOM)
                .input('t', ItemsRegistry.TOMATO.get())
                .input('p', ItemsRegistry.BEEF_PATTY.get())
                .input('f', BCObjects.FLAXEN_CHEESE_WEDGE)
                .criterion("has_cheese", InventoryChangedCriterion.Conditions.items(BCObjects.FLAXEN_CHEESE_WEDGE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(BCObjects.KEG)
                .pattern("ipi")
                .pattern("ihi")
                .pattern("ppp")
                .input('i', Items.IRON_INGOT)
                .input('h', Items.HONEYCOMB)
                .input('p', ItemTags.PLANKS)
                .criterion("has_honeycomb", InventoryChangedCriterion.Conditions.items(Items.HONEYCOMB))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(BCObjects.TANKARD, 4)
                .pattern("p p")
                .pattern("i i")
                .pattern("ppp")
                .input('i', Items.IRON_NUGGET)
                .input('p', ItemTags.PLANKS)
                .criterion("has_nugget", InventoryChangedCriterion.Conditions.items(Items.IRON_NUGGET))
                .offerTo(exporter);
    }
}
