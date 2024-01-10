package dev.sterner.brewinandchewin.common.block.entity;

import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public interface KegBlockInventory extends ItemStackInventory, SidedInventory {
    @Override
    default int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{KegBlockEntity.CONTAINER_SLOT, KegBlockEntity.OUTPUT_SLOT};
        }

        if (side == Direction.UP) {
            return IntStream.range(0, KegBlockEntity.MEAL_DISPLAY_SLOT).toArray();
        }

        return new int[]{KegBlockEntity.CONTAINER_SLOT - 1};
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == null || dir.equals(Direction.UP)) {
            return slot < KegBlockEntity.MEAL_DISPLAY_SLOT;
        } else {
            return slot == KegBlockEntity.CONTAINER_SLOT - 1;
        }
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == null || dir.equals(Direction.UP)) {
            return slot < KegBlockEntity.MEAL_DISPLAY_SLOT;
        } else {
            return slot == KegBlockEntity.OUTPUT_SLOT || slot == KegBlockEntity.CONTAINER_SLOT;
        }
    }

}
