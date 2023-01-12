package dev.sterner.brewinandchewin.common.recipe;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.nhoryzon.mc.farmersdelight.util.RecipeMatcher;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.registry.BCRecipeTypes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class KegRecipe implements Recipe<Inventory>{
    public static final int INPUT_SLOTS = 4;

    private final Identifier id;
    private final String group;
    private final DefaultedList<Ingredient> ingredientList;
    private final ItemStack output;
    private final ItemStack container;
    private final float experience;
    private final int fermentTime;
    final Ingredient liquid;
    private final int temperature;

    public KegRecipe(Identifier id, String group, DefaultedList<Ingredient> ingredientList, Ingredient liquid, ItemStack output, int temperature, ItemStack container, float experience, int fermentTime) {
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
        if (!liquid.isEmpty()) {
            this.liquid = liquid;
        } else {
            this.liquid = Ingredient.EMPTY;
        }

        this.experience = experience;
        this.fermentTime = fermentTime;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        List<ItemStack> inputList = new ArrayList<>();
        int i = 0;

        for (int j = 0; j < INPUT_SLOTS; ++j) {
            ItemStack itemstack = inventory.getStack(j);
            if (!itemstack.isEmpty()) {
                ++i;
                inputList.add(itemstack);
            }
        }

        if (this.liquid != null) {
            return i == this.ingredientList.size() && RecipeMatcher.findMatches(inputList, this.ingredientList) != null && this.liquid.test(inventory.getStack(4));
        }

        return i == ingredientList.size() && RecipeMatcher.findMatches(inputList, ingredientList) != null;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= ingredientList.size();
    }

    @Override
    public ItemStack getOutput() {
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

    public Ingredient getLiquid() {
        return this.liquid;
    }

    public float getExperience() {
        return this.experience;
    }

    public int getfermentTime() {
        return this.fermentTime;
    }

    public class Range
    {
        private int low;
        private int high;

        public Range(int low, int high){
            this.low = low;
            this.high = high;
        }

        public boolean contains(int number){
            return (number >= low && number <= high);
        }
    }

    public Range getTemperature() {
        int temperature = this.temperature;
        Range frigid = new Range(-27, -9);
        Range cold = new Range(-8, -5);
        Range normal = new Range(-4, 4);
        Range warm = new Range(5, 8);
        Range hot = new Range(9, 27);
        if (temperature == 1) {
            return frigid;
        }
        if (temperature == 2) {
            return cold;
        }
        if (temperature == 3) {
            return normal;
        }
        if (temperature == 4) {
            return warm;
        }
        if (temperature == 5) {
            return hot;
        }
        return normal;
    }

    public int getTemperatureJei() {
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
                Ingredient liquid = Ingredient.EMPTY;
                if(JsonHelper.hasElement(json, "liquid")){
                    final JsonObject jsonContainer = JsonHelper.getObject(json, "liquid");
                    liquid = Ingredient.fromJson(jsonContainer);
                }

                ItemStack container = ItemStack.EMPTY;
                if (JsonHelper.hasElement(json, "container")) {
                    final JsonObject jsonContainer = JsonHelper.getObject(json, "container");
                    container = new ItemStack(JsonHelper.getItem(jsonContainer, "item"), JsonHelper.getInt(jsonContainer, "count", 1));
                }
                final float experienceIn = JsonHelper.getFloat(json, "experience", 0.0F);
                final int fermentTimeIn = JsonHelper.getInt(json, "fermentingtime", 12000);
                final int temperatureIn = JsonHelper.getInt(json, "temperature", 3);
                return new KegRecipe(id, groupIn, inputItemsIn, liquid, outputIn, temperatureIn, container, experienceIn, fermentTimeIn);
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
            String groupIn = buf.readString(32767);
            int i = buf.readVarInt();
            DefaultedList<Ingredient> inputItemsIn = DefaultedList.ofSize(i, Ingredient.EMPTY);

            inputItemsIn.replaceAll(ignored -> Ingredient.fromPacket(buf));

            ItemStack outputIn = buf.readItemStack();
            Ingredient liquid = Ingredient.fromPacket(buf);
            ItemStack container = buf.readItemStack();
            float experienceIn = buf.readFloat();
            int fermentTimeIn = buf.readVarInt();
            int temperatureIn = buf.readVarInt();
            return new KegRecipe(id, groupIn, inputItemsIn, liquid, outputIn, temperatureIn, container, experienceIn, fermentTimeIn);
        }

        @Override
        public void write(PacketByteBuf buf, KegRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeVarInt(recipe.ingredientList.size());

            for (Ingredient ingredient : recipe.ingredientList) {
                ingredient.write(buf);
            }

            buf.writeItemStack(recipe.output);
            recipe.liquid.write(buf);
            buf.writeItemStack(recipe.container);
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.fermentTime);
            buf.writeVarInt(recipe.temperature);
        }
    }
}
