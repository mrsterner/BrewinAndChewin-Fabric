package dev.sterner.brewinandchewin.common.registry;

import com.nhoryzon.mc.farmersdelight.loot.function.CopyMealFunctionSerializer;
import com.nhoryzon.mc.farmersdelight.loot.function.CopySkilletFunctionSerializer;
import com.nhoryzon.mc.farmersdelight.loot.function.SmokerCookFunctionSerializer;
import dev.sterner.brewinandchewin.common.loot.CopyDrinkFunction;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public enum BCLootFunctionsRegistry {
    COPY_DRINK("copy_drink", CopyDrinkFunction.Serializer::new);

    private final String pathName;
    private final Supplier<ConditionalLootFunction.Serializer<? extends LootFunction>> lootFunctionSerializerSupplier;
    private ConditionalLootFunction.Serializer<? extends LootFunction> serializer;
    private LootFunctionType type;

    BCLootFunctionsRegistry(String pathName, Supplier lootFunctionSerializerSupplier) {
        this.pathName = pathName;
        this.lootFunctionSerializerSupplier = lootFunctionSerializerSupplier;
    }

    public static void init() {
        BCLootFunctionsRegistry[] var0 = values();

        for (BCLootFunctionsRegistry value : var0) {
            Registry.register(Registry.LOOT_FUNCTION_TYPE, new Identifier("brewinandchewin", value.pathName), value.type());
        }

    }

    public LootFunctionType type() {
        if (this.type == null) {
            this.type = new LootFunctionType(this.serializer());
        }

        return this.type;
    }

    public ConditionalLootFunction.Serializer<? extends LootFunction> serializer() {
        if (this.serializer == null) {
            this.serializer = this.lootFunctionSerializerSupplier.get();
        }

        return this.serializer;
    }
}