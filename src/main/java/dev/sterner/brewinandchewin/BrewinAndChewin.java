package dev.sterner.brewinandchewin;

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import dev.sterner.brewinandchewin.common.loot.CopyDrinkFunction;
import dev.sterner.brewinandchewin.common.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BrewinAndChewin implements ModInitializer {
	public static final String MODID = "brewinandchewin";
	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "main"), () -> new ItemStack(BCObjects.BEER));

	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		BCObjects.init();
		BCBlockEntityTypes.init();
		BCStatusEffects.init();
		BCRecipeTypes.init();
		BCScreenHandlerTypes.init();
		BCLootFunctionsRegistry.init();
	}
}
