package dev.sterner.brewinandchewin.client.integration.rei;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.client.integration.rei.fermenting.FermentingRecipeCategory;
import dev.sterner.brewinandchewin.client.integration.rei.fermenting.FermentingRecipeDisplay;
import dev.sterner.brewinandchewin.client.screen.KegScreen;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import dev.sterner.brewinandchewin.common.registry.BCRecipeTypes;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class BCREIPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<FermentingRecipeDisplay> FERMENTING = CategoryIdentifier.of(BrewinAndChewin.MODID, "fermenting");

    public static Rectangle centeredIntoRecipeBase(Point origin, int width, int height) {
        return centeredInto(new Rectangle(origin.x, origin.y, 150, 66), width, height);
    }

    public static Rectangle centeredInto(Rectangle origin, int width, int height) {
        return new Rectangle(origin.x + (origin.width - width) / 2, origin.y + (origin.height - height) / 2, width, height);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new FermentingRecipeCategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(KegRecipe.class, BCRecipeTypes.KEG_RECIPE_TYPE, FermentingRecipeDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(89, 25, 24, 17), KegScreen.class, FERMENTING);
    }
}
