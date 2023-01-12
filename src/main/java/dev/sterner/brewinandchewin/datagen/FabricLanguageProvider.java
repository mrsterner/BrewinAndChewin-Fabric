package dev.sterner.brewinandchewin.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public abstract class FabricLanguageProvider implements DataProvider {
    Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected final FabricDataGenerator dataGenerator;
    private final String languageCode;

    protected FabricLanguageProvider(FabricDataGenerator dataGenerator) {
        this(dataGenerator, "en_us");
    }

    protected FabricLanguageProvider(FabricDataGenerator dataGenerator, String languageCode) {
        this.dataGenerator = dataGenerator;
        this.languageCode = languageCode;
    }

    /**
     * Implement this method to register languages.
     *
     * <p>Call {@link TranslationBuilder#add(String, String)} to add a translation.
     */
    public abstract void generateTranslations(TranslationBuilder translationBuilder);


    @Override
    public void run(DataCache cache) throws IOException {
        TreeMap<String, String> translationEntries = new TreeMap<>();

        generateTranslations((String key, String value) -> {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);

            if (translationEntries.containsKey(key)) {
                throw new RuntimeException("Existing translation key found - " + key + " - Duplicate will be ignored.");
            }

            translationEntries.put(key, value);
        });

        JsonObject langEntryJson = new JsonObject();

        for (Map.Entry<String, String> entry : translationEntries.entrySet()) {
            langEntryJson.addProperty(entry.getKey(), entry.getValue());
        }
        DataProvider.writeToPath(GSON, cache, langEntryJson, getLangFilePath(this.languageCode));
    }

    private Path getLangFilePath(String code) {
        return dataGenerator.getOutput().resolve("assets/%s/lang/%s.json".formatted(dataGenerator.getModId(), code));
    }

    @Override
    public String getName() {
        return "Language";
    }

    /**
     * A consumer used by {@link FabricLanguageProvider#generateTranslations(TranslationBuilder)}.
     */
    @ApiStatus.NonExtendable
    @FunctionalInterface
    public interface TranslationBuilder {
        /**
         * Adds a translation.
         *
         * @param translationKey  The key of the translation.
         * @param value        The value of the entry.
         */
        void add(String translationKey, String value);

        /**
         * Adds a translation for an {@link Item}.
         * @param item The {@link Item} to get the translation key from.
         * @param value The value of the entry.
         */
        default void add(Item item, String value) {
            add(item.getTranslationKey(), value);
        };

        /**
         * Adds a translation for a {@link Block}.
         * @param block The {@link Block} to get the translation key from.
         * @param value The value of the entry.
         */
        default void add(Block block, String value) {
            add(block.getTranslationKey(), value);
        }

        /**
         * Adds a translation for an {@link ItemGroup}.
         * @param group The {@link ItemGroup} to get the translation key from.
         * @param value The value of the entry.
         */
        default void add(ItemGroup group, String value) {
            add("itemGroup." + group.getName(), value);
        }

        /**
         * Adds a translation for an {@link EntityType}.
         * @param entityType The {@link EntityType} to get the translation key from.
         * @param value The value of the entry.
         */
        default void add(EntityType<?> entityType, String value) {
            add(entityType.getTranslationKey(), value);
        }

        /**
         * Adds a translation for an {@link Enchantment}.
         * @param enchantment The {@link Enchantment} to get the translation key from.
         * @param value The value of the entry.
         */
        default void add(Enchantment enchantment, String value) {
            add(enchantment.getTranslationKey(), value);
        }

        /**
         * Adds a translation for an {@link EntityAttribute}.
         * @param entityAttribute The {@link EntityAttribute} to get the translation key from.
         * @param value The value of the entry.
         */
        default void add(EntityAttribute entityAttribute, String value) {
            add(entityAttribute.getTranslationKey(), value);
        }

        /**
         * Adds a translation for a {@link StatType}.
         * @param statType The {@link StatType} to get the translation key from.
         * @param value The value of the entry.
         */
        default void add(StatType<?> statType, String value) {
            add(statType.getTranslationKey(), value);
        }

        /**
         * Adds a translation for a {@link StatusEffect}.
         * @param statusEffect The {@link StatusEffect} to get the translation key from.
         * @param value The value of the entry.
         */
        default void add(StatusEffect statusEffect, String value) {
            add(statusEffect.getTranslationKey(), value);
        }

        /**
         * Adds a translation for an {@link Identifier}.
         * @param identifier The {@link Identifier} to get the translation key from.
         * @param value The value of the entry.
         */
        default void add(Identifier identifier, String value) {
            add(identifier.toString(), value);
        }

        /**
         * Merges an existing language file into the generated language file.
         * @param existingLanguageFile The path to the existing language file.
         * @throws IOException If loading the language file failed.
         */
        default void add(Path existingLanguageFile) throws IOException {
            try (Reader reader = Files.newBufferedReader(existingLanguageFile)) {
                JsonObject translations = JsonParser.parseReader(reader).getAsJsonObject();

                for (String key : translations.keySet()) {
                    add(key, translations.get(key).getAsString());
                }
            }
        }
    }
}