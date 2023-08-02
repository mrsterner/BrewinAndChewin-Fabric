package dev.sterner.brewinandchewin.common.registry;

import com.nhoryzon.mc.farmersdelight.item.ConsumableItem;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.*;
import dev.sterner.brewinandchewin.common.item.BoozeItem;
import dev.sterner.brewinandchewin.common.item.DreadNogItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface BCObjects {
    Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();
    Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

    Item TANKARD = register("tankard", new Item(settings()));

    Item BEER = register("beer", new BoozeItem(1, 8, drinkItem()));
    Item VODKA = register("vodka", new BoozeItem(1, 12, drinkItem()));
    Item MEAD = register("mead", new BoozeItem(1, 8, drinkItem()));
    Item RICE_WINE = register("rice_wine", new BoozeItem(1, 5, drinkItem().food(BCFoodComponents.RICE_WINE)));
    Item EGG_GROG = register("egg_grog", new BoozeItem(1, 0, drinkItem().food(BCFoodComponents.EGG_GROG)));
    Item STRONGROOT_ALE = register("strongroot_ale", new BoozeItem(2, 12, drinkItem().food(BCFoodComponents.STRONGROOT_ALE)));
    Item SACCHARINE_RUM = register("saccharine_rum", new BoozeItem(2, 8, drinkItem().food(BCFoodComponents.SACCHARINE_RUM)));
    Item PALE_JANE = register("pale_jane", new BoozeItem(2, 5, drinkItem().food(BCFoodComponents.PALE_JANE)));

    Item DREAD_NOG = register("dread_nog", new DreadNogItem(3, 5, drinkItem()));

    Item SALTY_FOLLY = register("salty_folly", new BoozeItem(2, 10, drinkItem().food(BCFoodComponents.SALTY_FOLLY)));
    Item STEEL_TOE_STOUT = register("steel_toe_stout", new BoozeItem(3, 10, drinkItem().food(BCFoodComponents.STEEL_TOE_STOUT)));
    Item GLITTERING_GRENADINE = register("glittering_grenadine", new BoozeItem(1, 5, drinkItem().food(BCFoodComponents.GLITTERING_GRENADINE)));
    Item BLOODY_MARY = register("bloody_mary", new BoozeItem(1, 12, drinkItem().food(BCFoodComponents.BLOODY_MARY)));
    Item RED_RUM = register("red_rum", new BoozeItem(1, 18, drinkItem().food(BCFoodComponents.RED_RUM)));
    Item WITHERING_DROSS = register("withering_dross", new BoozeItem(3, 20, drinkItem().food(BCFoodComponents.WITHERING_DROSS)));
    Item KOMBUCHA = register("kombucha", new BoozeItem(1, 5, drinkItem().food(BCFoodComponents.KOMBUHCA)));

    Item KIMCHI = register("kimchi", new Item(settings().food(BCFoodComponents.KIMCHI)));
    Item JERKY = register("jerky", new Item(settings().food(BCFoodComponents.JERKY)));
    Item PICKLED_PICKLES = register("pickled_pickles", new Item(settings().food(BCFoodComponents.PICKLED_PICKLES)));
    Item KIPPERS = register("kippers", new Item(settings().food(BCFoodComponents.KIPPERS)));
    Item COCOA_FUDGE = register("cocoa_fudge", new Item(settings().food(BCFoodComponents.COCOA_FUDGE)));

    Item FLAXEN_CHEESE_WEDGE = register("flaxen_cheese_wedge", new Item(settings().food(BCFoodComponents.FLAXEN_CHEESE)));
    Item SCARLET_CHEESE_WEDGE = register("scarlet_cheese_wedge", new Item(settings().food(BCFoodComponents.SCARLET_CHEESE)));

    Item FIERY_FONDUE = register("fiery_fondue", new Item(settings().food(BCFoodComponents.FIERY_FONDUE)));
    Item QUICHE_SLICE = register("quiche_slice", new Item(settings().food(BCFoodComponents.QUICHE_SLICE)));

    Item VEGETABLE_OMELET = register("vegetable_omelet", new ConsumableItem(bowlFoodItem().food(BCFoodComponents.VEGETABLE_OMELET)));
    Item CHEESY_PASTA = register("cheesy_pasta", new ConsumableItem(bowlFoodItem().food(BCFoodComponents.CHEESY_PASTA)));
    Item CREAMY_ONION_SOUP = register("creamy_onion_soup", new ConsumableItem(bowlFoodItem().food(BCFoodComponents.CREAMY_ONION_SOUP)));
    Item SCARLET_PIEROGIES = register("scarlet_pierogies", new ConsumableItem(bowlFoodItem().food(BCFoodComponents.SCARLET_PIEROGIES)));
    Item HORROR_LASAGNA = register("horror_lasagna", new ConsumableItem(bowlFoodItem().food(BCFoodComponents.HORROR_LASAGNA)));

    Block QUICHE = register("quiche", new QuicheBlock(AbstractBlock.Settings.copy(Blocks.CAULDRON)), settings(), true);
    Block FIERY_FONDUE_POT = register("fiery_fondue_pot", new FieryFonduePotBlock(AbstractBlock.Settings.copy(Blocks.CAKE)), settings().maxCount(1), true);


    Block FLAXEN_CHEESE_WHEEL = register("flaxen_cheese_wheel", new RipeCheeseWheelBlock(FLAXEN_CHEESE_WEDGE, FabricBlockSettings.copy(Blocks.CAKE)), settings().maxCount(16), true);
    Block SCARLET_CHEESE_WHEEL = register("scarlet_cheese_wheel", new RipeCheeseWheelBlock(SCARLET_CHEESE_WEDGE, FabricBlockSettings.copy(Blocks.CAKE)), settings().maxCount(16), true);
    Block UNRIPE_FLAXEN_CHEESE_WHEEL = register("unripe_flaxen_cheese_wheel", new UnripeCheeseWheelBlock(FLAXEN_CHEESE_WHEEL, FabricBlockSettings.copy(Blocks.CAKE)), settings().maxCount(16), true);
    Block UNRIPE_SCARLET_CHEESE_WHEEL = register("unripe_scarlet_cheese_wheel", new UnripeCheeseWheelBlock(SCARLET_CHEESE_WHEEL, FabricBlockSettings.copy(Blocks.CAKE)), settings().maxCount(16), true);

    Item PIZZA_SLICE = register("pizza_slice", new Item(settings().food(BCFoodComponents.PIZZA_SLICE)));
    Block PIZZA = register("pizza", new PizzaBlock(FabricBlockSettings.copy(Blocks.CAKE)), settings().maxCount(16), true);

    Item HAM_AND_CHEESE_SANDWICH = register("ham_and_cheese_sandwich", new Item(settings().food(BCFoodComponents.HAM_AND_CHEESE_SANDWICH)));

    Block KEG = register("keg", new KegBlock(), settings(), true);
    Block COASTER = register("item_coaster", new ItemCoasterBlock(), settings(), true);
    Block FERMENTATION_CONTROLLER = register("fermentation_controller", new FermentationControllerBlock(), settings(), true);

    static Item.Settings drinkItem() {
        return (new Item.Settings()).recipeRemainder(TANKARD).maxCount(16).group(BrewinAndChewin.ITEM_GROUP);
    }

    static Item.Settings bowlFoodItem() {
        return (new Item.Settings()).recipeRemainder(Items.BOWL).maxCount(16).group(BrewinAndChewin.ITEM_GROUP);
    }

    static Item.Settings settings() {
        return new Item.Settings().group(BrewinAndChewin.ITEM_GROUP);
    }

    static <T extends Item> T register(String name, T item) {
        ITEMS.put(item, new Identifier(BrewinAndChewin.MODID, name));
        return item;
    }

    static <T extends Block> T register(String name, T block, Item.Settings settings, boolean createItem) {
        BLOCKS.put(block, new Identifier(BrewinAndChewin.MODID, name));
        if (createItem) {
            ITEMS.put(new BlockItem(block, settings), BLOCKS.get(block));
        }
        return block;
    }

    static void init() {
        BLOCKS.keySet().forEach(block -> Registry.register(Registry.BLOCK, BLOCKS.get(block), block));
        ITEMS.keySet().forEach(item -> Registry.register(Registry.ITEM, ITEMS.get(item), item));
    }
}
