package dev.sterner.brewinandchewin.client.integration.rei.fermenting;

import com.google.common.collect.ImmutableList;
import dev.sterner.brewinandchewin.client.integration.rei.BCREIPlugin;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FermentingRecipeDisplay extends BasicDisplay {

    private final EntryIngredient containerOutput;
    private final int cookTime;
    private final int temp;
    final EntryIngredient liquid;

    public FermentingRecipeDisplay(KegRecipe recipe) {
        super(
                EntryIngredients.ofIngredients(recipe.getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.getOutput())),
                Optional.ofNullable(recipe.getId()));

        containerOutput = EntryIngredients.of(recipe.getOutputContainer());
        cookTime = recipe.getfermentTime();
        temp = recipe.getTemperatureJei();
        liquid = EntryIngredients.ofIngredient(recipe.getLiquid());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return BCREIPlugin.FERMENTING;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> inputEntryList = new ArrayList<>(super.getInputEntries());
        inputEntryList.add(getContainerOutput());

        return ImmutableList.copyOf(inputEntryList);
    }

    public List<EntryIngredient> getIngredientEntries() {
        return super.getInputEntries();
    }

    public EntryIngredient getContainerOutput() {
        return containerOutput;
    }

    public int getCookTime() {
        return cookTime;
    }

    public int getTemp(){
        return temp;
    }

    public EntryIngredient getLiquid(){
        return EntryIngredient.of(liquid);
    }
}
