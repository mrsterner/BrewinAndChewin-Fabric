package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface BCRecipeTypes {
    Map<RecipeSerializer<?>, Identifier> RECIPE_SERIALIZERS = new LinkedHashMap<>();
    Map<RecipeType<?>, Identifier> RECIPE_TYPES = new LinkedHashMap<>();

    RecipeSerializer<KegRecipe> KEG_RECIPE_SERIALIZER = register("fermenting", new KegRecipe.Serializer());
    RecipeType<KegRecipe> KEG_RECIPE_TYPE = register("fermenting");

    static <T extends Recipe<?>> RecipeSerializer<T> register(String name, RecipeSerializer<T> serializer) {
        RECIPE_SERIALIZERS.put(serializer, new Identifier(BrewinAndChewin.MODID, name));
        return serializer;
    }

    static <T extends Recipe<?>> RecipeType<T> register(String name) {
        RecipeType<T> type = new RecipeType<>() {
            @Override
            public String toString() {
                return name;
            }
        };
        RECIPE_TYPES.put(type, new Identifier(BrewinAndChewin.MODID, name));
        return type;
    }

    static void init() {
        RECIPE_SERIALIZERS.keySet().forEach(recipeSerializer -> Registry.register(Registry.RECIPE_SERIALIZER, RECIPE_SERIALIZERS.get(recipeSerializer), recipeSerializer));
        RECIPE_TYPES.keySet().forEach(recipeType -> Registry.register(Registry.RECIPE_TYPE, RECIPE_TYPES.get(recipeType), recipeType));
    }
}
