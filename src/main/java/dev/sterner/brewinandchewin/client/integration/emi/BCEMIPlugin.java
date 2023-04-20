package dev.sterner.brewinandchewin.client.integration.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCRecipeTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;

public class BCEMIPlugin implements EmiPlugin {
    private static final EmiStack ICON = EmiStack.of(BCObjects.KEG);
    public static final EmiRecipeCategory FERMENTING_CATEGORY = new EmiRecipeCategory(
            new Identifier(BrewinAndChewin.MODID, "fermenting"), ICON
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(FERMENTING_CATEGORY);
        RecipeManager manager = registry.getRecipeManager();
        for (KegRecipe recipe : manager.listAllOfType(BCRecipeTypes.KEG_RECIPE_TYPE)) {
            registry.addRecipe(new FermentingEMIRecipe(recipe));
        }
    }
}
