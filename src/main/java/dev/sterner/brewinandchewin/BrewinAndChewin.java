package dev.sterner.brewinandchewin;

import dev.sterner.brewinandchewin.common.registry.*;
import net.fabricmc.api.ModInitializer;

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
