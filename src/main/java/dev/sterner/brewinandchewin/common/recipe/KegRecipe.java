package dev.sterner.brewinandchewin.common.recipe;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.RecipeWrapper;
import com.nhoryzon.mc.farmersdelight.util.RecipeMatcher;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.registry.BCRecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class KegRecipe implements Recipe<RecipeWrapper> {
    public static final int INPUT_SLOTS = 4;

    private final Identifier id;
    private final String group;
    public final DefaultedList<Ingredient> ingredientList;
    private final Ingredient fluidItem;
    public final ItemStack output;
    private final ItemStack container;
    private final float experience;
    private final int fermentTime;
    private final int temperature;

    public KegRecipe(Identifier id, String group, DefaultedList<Ingredient> ingredientList, Ingredient fluidItem, ItemStack output, ItemStack container, float experience, int fermentTime, int temperature) {
        this.id = id;
        this.group = group;
        this.ingredientList = ingredientList;
        this.output = output;
        this.temperature = temperature;
        if (!container.isEmpty()) {
            this.container = container;
        } else if (output.getItem().getRecipeRemainder() != null) {
            this.container = new ItemStack(output.getItem().getRecipeRemainder());
        } else {
            this.container = ItemStack.EMPTY;
        }
        this.fluidItem = fluidItem;

        this.experience = experience;
        this.fermentTime = fermentTime;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        ingredients.addAll(this.ingredientList);
        if (!this.fluidItem.isEmpty()) {
            ingredients.add(this.fluidItem);
        }

        return ingredients;
    }

    @Override
    public boolean matches(RecipeWrapper inventory, World world) {
        List<ItemStack> inputs = new ArrayList();
        int i = 0;

        for (int j = 0; j < 4; ++j) {
            ItemStack itemstack = inventory.getStack(j);
            if (!itemstack.isEmpty()) {
                ++i;
                inputs.add(itemstack);
            }
        }

        if (this.fluidItem != null) {
            return i == this.ingredientList.size() && RecipeMatcher.findMatches(inputs, this.ingredientList) != null && this.fluidItem.test(inventory.getStack(4));
        } else {
            return i == this.ingredientList.size() && RecipeMatcher.findMatches(inputs, this.ingredientList) != null;
        }
    }


    @Override
    public boolean fits(int width, int height) {
        return width * height >= ingredientList.size();
    }

    @Override
    public ItemStack craft(RecipeWrapper inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.output;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BCRecipeTypes.KEG_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BCRecipeTypes.KEG_RECIPE_TYPE;
    }

    public ItemStack getOutputContainer() {
        return this.container;
    }

    public Ingredient getFluidItem() {
        return this.fluidItem;
    }

    public float getExperience() {
        return this.experience;
    }

    public int getFermentTime() {
        return this.fermentTime;
    }

    public int getTemperature() {
        return this.temperature;
    }

    public static class Serializer implements RecipeSerializer<KegRecipe> {

        @Override
        public KegRecipe read(Identifier id, JsonObject json) {
            final String groupIn = JsonHelper.getString(json, "group", "");

            final DefaultedList<Ingredient> inputItemsIn = readIngredients(JsonHelper.getArray(json, "ingredients"));
            if (inputItemsIn.isEmpty()) {
                throw new JsonParseException("No ingredients for cooking recipe");
            } else if (inputItemsIn.size() > KegRecipe.INPUT_SLOTS) {
                throw new JsonParseException("Too many ingredients for cooking recipe! The max is " + KegRecipe.INPUT_SLOTS);
            } else {
                final JsonObject jsonResult = JsonHelper.getObject(json, "result");
                final ItemStack outputIn = new ItemStack(JsonHelper.getItem(jsonResult, "item"), JsonHelper.getInt(jsonResult, "count", 1));
                Ingredient fluidItemIn = Ingredient.EMPTY;
                if (JsonHelper.hasElement(json, "fluiditem")) {
                    final JsonObject jsonContainer = JsonHelper.getObject(json, "fluiditem");
                    fluidItemIn = Ingredient.fromJson(jsonContainer);
                }

                ItemStack container = ItemStack.EMPTY;
                if (JsonHelper.hasElement(json, "container")) {
                    final JsonObject jsonContainer = JsonHelper.getObject(json, "container");
                    container = new ItemStack(JsonHelper.getItem(jsonContainer, "item"), JsonHelper.getInt(jsonContainer, "count", 1));
                }

                float experienceIn = JsonHelper.getFloat(json, "experience", 0.0F);
                int fermentTimeIn = JsonHelper.getInt(json, "fermentingtime", 200);
                int temperatureIn = JsonHelper.getInt(json, "temperature", 3);
                return new KegRecipe(id, groupIn, inputItemsIn, fluidItemIn, outputIn, container, experienceIn, fermentTimeIn, temperatureIn);
            }
        }

        private static DefaultedList<Ingredient> readIngredients(JsonArray ingredientArray) {
            DefaultedList<Ingredient> defaultedList = DefaultedList.of();
            for (int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    defaultedList.add(ingredient);
                }
            }
            return defaultedList;
        }

        @Override
        public KegRecipe read(Identifier id, PacketByteBuf buf) {
            String groupIn = buf.readString();
            int i = buf.readVarInt();
            DefaultedList<Ingredient> inputItemsIn = DefaultedList.ofSize(i, Ingredient.EMPTY);

            inputItemsIn.replaceAll(ignored -> Ingredient.fromPacket(buf));

            Ingredient fluidItem = Ingredient.fromPacket(buf);
            ItemStack outputIn = buf.readItemStack();
            ItemStack container = buf.readItemStack();
            float experienceIn = buf.readFloat();
            int fermentTimeIn = buf.readVarInt();
            int temperatureIn = buf.readVarInt();
            return new KegRecipe(id, groupIn, inputItemsIn, fluidItem, outputIn, container, experienceIn, fermentTimeIn, temperatureIn);
        }

        @Override
        public void write(PacketByteBuf buf, KegRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeVarInt(recipe.ingredientList.size());

            for (Ingredient ingredient : recipe.ingredientList) {
                ingredient.write(buf);
            }

            recipe.fluidItem.write(buf);
            buf.writeItemStack(recipe.output);
            buf.writeItemStack(recipe.container);
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.fermentTime);
            buf.writeVarInt(recipe.temperature);
        }
    }
}
