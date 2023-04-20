package dev.sterner.brewinandchewin.client.recipebook;

import com.github.aws404.booking_it.RecipeBookAdder;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;

import java.util.List;

public class BCRecipeBook implements RecipeBookAdder {

    @Override
    public List<RecipeCategoryOptions> getCategories() {
        return List.of(
                RecipeBookAdder.builder("FERMENTING")
                        .addSearch()
                        .addGroup("DRINKS", recipe -> {
                            if (recipe instanceof KegRecipe kegRecipe) {
                                return kegRecipe.getOutput().getItem().getGroup() == BrewinAndChewin.ITEM_GROUP;
                            }
                            return false;
                        }, "brewinandchewin:beer")
                        .addGroup("MISC", recipe -> {
                            if (recipe instanceof KegRecipe kegRecipe) {
                                return kegRecipe.getOutput().getItem().getGroup() == BrewinAndChewin.ITEM_GROUP;
                            }
                            return false;
                        }, "brewinandchewin:flaxen_cheese_wheel")
                        .build()
        );
    }
}
