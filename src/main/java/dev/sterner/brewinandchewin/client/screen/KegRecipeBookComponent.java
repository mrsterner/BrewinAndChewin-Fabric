package dev.sterner.brewinandchewin.client.screen;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.lwjgl.system.NonnullDefault;

import java.util.List;

public class KegRecipeBookComponent extends RecipeBookWidget {
    protected static final Identifier RECIPE_BOOK_BUTTONS = new Identifier(BrewinAndChewin.MODID, "textures/gui/recipe_book_buttons.png");

    public KegRecipeBookComponent() {

    }

    @Override
    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextureUV(0, 0, 28, 18, RECIPE_BOOK_BUTTONS);
    }

    @NonnullDefault
    protected Text getToggleCraftableButtonText() {
        return BCTextUtils.getTranslation("container.recipe_book.fermentable");
    }

    @Override
    public void showGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
        DefaultedList<Ingredient> ingredientsList = DefaultedList.of();
        ingredientsList.addAll(recipe.getIngredients());
        ItemStack resultStack = recipe.getOutput();
        this.ghostSlots.setRecipe(recipe);
        if ((slots.get(5)).getStack().isEmpty()) {
            this.ghostSlots.addSlot(Ingredient.ofStacks(resultStack), (slots.get(5)).x, (slots.get(5)).y);
        }

        if (recipe instanceof KegRecipe kegRecipe) {
            Ingredient fluidItemStack = kegRecipe.getFluidItem();
            ItemStack containerStack = kegRecipe.getOutputContainer();
            if (!fluidItemStack.isEmpty()) {
                this.ghostSlots.addSlot(fluidItemStack, (slots.get(4)).x, (slots.get(4)).y);
                ingredientsList.remove(ingredientsList.size() - 1);
            }

            if (!containerStack.isEmpty()) {
                this.ghostSlots.addSlot(Ingredient.ofStacks(containerStack), ((Slot)slots.get(6)).x, ((Slot)slots.get(6)).y);
            }
        }

        this.alignRecipeToGrid(this.craftingScreenHandler.getCraftingWidth(), this.craftingScreenHandler.getCraftingHeight(), this.craftingScreenHandler.getCraftingResultSlotIndex(), recipe, ingredientsList.iterator(), 0);
    }
}
