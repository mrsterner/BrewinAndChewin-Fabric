package dev.sterner.brewinandchewin.datagen;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.registry.BCRecipeTypes;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class BCKegRecipeBuilder {
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Item result;
    private final int count;
    private final int cookingTime;
    private final float experience;
    private final Item container;
    private final Item liquid;
    private final int temperature;

    private BCKegRecipeBuilder(ItemConvertible resultIn, int count, int cookingTime, float experience, @Nullable ItemConvertible container, @Nullable ItemConvertible liquid, int temperature) {
        this.result = resultIn.asItem();
        this.count = count;
        this.liquid = liquid != null ? liquid.asItem() : null;
        this.cookingTime = cookingTime;
        this.experience = experience;
        this.container = container != null ? container.asItem() : null;
        this.temperature = temperature;
    }

    public static BCKegRecipeBuilder kegRecipe(ItemConvertible mainResult, int count, int cookingTime, float experience, ItemConvertible liquid, int temperature) {
        return new BCKegRecipeBuilder(mainResult, count, cookingTime, experience, null, liquid, temperature);
    }

    public static BCKegRecipeBuilder kegRecipe(ItemConvertible mainResult, int count, int cookingTime, float experience, int temperature) {
        return new BCKegRecipeBuilder(mainResult, count, cookingTime, experience, null, null, temperature);
    }

    public static BCKegRecipeBuilder kegRecipe(ItemConvertible mainResult, int count, int cookingTime, float experience, ItemConvertible container, ItemConvertible liquid, int temperature) {
        return new BCKegRecipeBuilder(mainResult, count, cookingTime, experience, container, liquid, temperature);
    }

    public BCKegRecipeBuilder addIngredient(TagKey<Item> tagIn) {
        return this.addIngredient(Ingredient.fromTag(tagIn));
    }

    public BCKegRecipeBuilder addIngredient(ItemConvertible itemIn) {
        return this.addIngredient(itemIn, 1);
    }

    public BCKegRecipeBuilder addIngredient(ItemConvertible itemIn, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.addIngredient(Ingredient.ofItems(itemIn));
        }
        return this;
    }

    public BCKegRecipeBuilder addIngredient(Ingredient ingredientIn) {
        return this.addIngredient(ingredientIn, 1);
    }

    public BCKegRecipeBuilder addIngredient(Ingredient ingredientIn, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredientIn);
        }
        return this;
    }

    public void build(Consumer<RecipeJsonProvider> consumerIn) {
        Identifier location = Registries.ITEM.getId(this.result);
        this.build(consumerIn, BrewinAndChewin.MODID + ":fermenting/" + location.getPath());
    }

    public void build(Consumer<RecipeJsonProvider> consumerIn, String save) {
        Identifier resourcelocation = Registries.ITEM.getId(this.result);
        if ((new Identifier(save)).equals(resourcelocation)) {
            throw new IllegalStateException("Fermenting Recipe " + save + " should remove its 'save' argument");
        } else {
            this.build(consumerIn, new Identifier(save));
        }
    }

    public void build(Consumer<RecipeJsonProvider> consumerIn, Identifier id) {
        consumerIn.accept(new BCKegRecipeBuilder.Result(id, this.result, this.count, this.ingredients, this.cookingTime, this.experience, this.container, this.liquid, this.temperature));
    }

    public static class Result implements RecipeJsonProvider {
        private final Identifier id;
        private final List<Ingredient> ingredients;
        private final Item result;
        private final int count;
        private final int cookingTime;
        private final float experience;
        private final Item container;
        private final Item liquid;
        private final int temperature;

        public Result(Identifier idIn, Item resultIn, int countIn, List<Ingredient> ingredientsIn, int cookingTimeIn, float experienceIn, @Nullable Item containerIn, @Nullable Item liquidIn, int temperatureIn) {
            this.id = idIn;
            this.ingredients = ingredientsIn;
            this.result = resultIn;
            this.count = countIn;
            this.cookingTime = cookingTimeIn;
            this.experience = experienceIn;
            this.container = containerIn;
            this.liquid = liquidIn;
            this.temperature = temperatureIn;
        }

        @Override
        public void serialize(JsonObject json) {
            JsonArray arrayIngredients = new JsonArray();

            for (Ingredient ingredient : this.ingredients) {
                arrayIngredients.add(ingredient.toJson());
            }
            json.add("ingredients", arrayIngredients);

            JsonObject objectResult = new JsonObject();
            objectResult.addProperty("item", Registries.ITEM.getKey(this.result).get().getValue().toString());
            if (this.count > 1) {
                objectResult.addProperty("count", this.count);
            }
            json.add("result", objectResult);

            if (this.container != null) {
                JsonObject objectContainer = new JsonObject();
                objectContainer.addProperty("item", Registries.ITEM.getKey(this.container).get().getValue().toString());
                json.add("container", objectContainer);
            }
            if (this.liquid != null) {
                JsonObject objectLiquid = new JsonObject();
                objectLiquid.addProperty("item", Registries.ITEM.getKey(this.liquid).get().getValue().toString());
                json.add("liquid", objectLiquid);
            }
            if (this.experience > 0) {
                json.addProperty("experience", this.experience);
            }
            json.addProperty("cookingtime", this.cookingTime);
            json.addProperty("temperature", this.temperature);
        }

        @Override
        public Identifier getRecipeId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return BCRecipeTypes.KEG_RECIPE_SERIALIZER;
        }

        @Nullable
        @Override
        public JsonObject toAdvancementJson() {
            return null;
        }

        @Nullable
        @Override
        public Identifier getAdvancementId() {
            return null;
        }
    }
}