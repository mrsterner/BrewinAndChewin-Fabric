package dev.sterner.brewinandchewin.client.recipebook;

import com.github.aws404.booking_it.RecipeBookAdder;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import net.minecraft.item.ItemGroup;
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
                                return cookingRecipe.getRecipeBookTab() == KegRecipeBookTab.MISC;
                            }
                            return false;
                        }, "brewinandchewin:beer")
                        .addGroup("DRINKS", recipe -> {
                            if (recipe instanceof KegRecipe cookingRecipe) {
                                return cookingRecipe.getRecipeBookTab() == KegRecipeBookTab.DRINKS;
                            }
                            return false;
                        }, "brewinandchewin:beer")
                        .addGroup("BLOCKS", recipe -> {
                            if (recipe instanceof SmeltingRecipe cookingRecipe) {
                                return cookingRecipe.getOutput().getItem().getGroup() == ItemGroup.BUILDING_BLOCKS;
                            }
                            return false;
                        }, "minecraft:cobblestone", "minecraft:dirt")

                        .build()
        );
    }
}
