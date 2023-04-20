package dev.sterner.brewinandchewin.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CopyDrinkFunction extends ConditionalLootFunction {
    public static final Identifier ID = new Identifier(BrewinAndChewin.MODID, "copy_meal");

    private CopyDrinkFunction(LootCondition[] conditions) {
        super(conditions);
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return builder(CopyDrinkFunction::new);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        BlockEntity tile = context.get(LootContextParameters.BLOCK_ENTITY);
        if (tile instanceof KegBlockEntity) {
            NbtCompound tag = ((KegBlockEntity) tile).writeMeal(new NbtCompound());
            if (!tag.isEmpty()) {
                stack.setSubNbt("BlockEntityTag", tag);
            }
        }
        return stack;
    }

    @Override
    @Nullable
    public LootFunctionType getType() {
        return null;
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<CopyDrinkFunction> {

        @Override
        public CopyDrinkFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return new CopyDrinkFunction(conditions);
        }
    }
}