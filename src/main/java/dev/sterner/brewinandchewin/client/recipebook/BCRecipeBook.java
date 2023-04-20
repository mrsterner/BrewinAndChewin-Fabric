package dev.sterner.brewinandchewin.client.recipebook;

import com.github.aws404.booking_it.RecipeBookAdder;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmeltingRecipe;

import java.util.List;

public class BCRecipeBook implements RecipeBookAdder {

    @Override
    public List<RecipeCategoryOptions> getCategories() {
        return List.of(
                RecipeBookAdder.builder("FERMENTING")
                        .addSearch()
                        .addGroup("MISC", recipe -> {
                            if (recipe instanceof KegRecipe cookingRecipe) {
                                return cookingRecipe.getOutput().getItem().getGroup() == BrewinAndChewin.ITEM_GROUP;
                            }
                            return false;
                        }, "brewinandchewin:beer")
                        .addGroup("DRINKS", recipe -> {
                            if (recipe instanceof SmeltingRecipe cookingRecipe) {
                                return cookingRecipe.getOutput().getItem().getGroup() == ItemGroup.BUILDING_BLOCKS;
                            }
                            return false;
                        }, "minecraft:cobblestone", "minecraft:dirt")

                        .build()
        );
    }
}
