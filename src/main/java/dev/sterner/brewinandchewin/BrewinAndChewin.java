package dev.sterner.brewinandchewin;

import com.nhoryzon.mc.farmersdelight.registry.LootFunctionsRegistry;
import dev.sterner.brewinandchewin.common.loot.CopyMealFunction;
import dev.sterner.brewinandchewin.common.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrewinAndChewin implements ModInitializer {
	public static final String MODID = "brewinandchewin";

	@Override
	public void onInitialize() {
		BCObjects.init();
		BCBlockEntityTypes.init();
		BCStatusEffects.init();
		BCRecipeTypes.init();
		BCScreenHandlerTypes.init();


		//Registry.register(Registry.LOOT_FUNCTION_TYPE, new Identifier(CopyMealFunction.ID.toString()), new LootFunctionType(new CopyMealFunction.Serializer()));

	}
}
