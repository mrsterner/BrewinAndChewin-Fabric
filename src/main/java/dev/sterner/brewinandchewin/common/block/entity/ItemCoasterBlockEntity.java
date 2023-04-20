package dev.sterner.brewinandchewin.common.block.entity;

import com.nhoryzon.mc.farmersdelight.entity.block.SyncedBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackHandler;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ItemCoasterBlockEntity extends SyncedBlockEntity {

    private final ItemStackHandler inventory = this.createHandler();

    private Identifier lastRecipeID;
    private boolean isItemCarvingBoard = false;

    public ItemCoasterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.ITEM_COASTER, pos, state);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.isItemCarvingBoard = compound.getBoolean("IsItemCarved");
        this.inventory.readNbt(compound.getCompound("Inventory"));
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        compound.put("Inventory", this.inventory.writeNbt(compound));
        compound.putBoolean("IsItemCarved", this.isItemCarvingBoard);
    }

    public boolean addItem(ItemStack itemStack) {
        if (this.isEmpty() && !itemStack.isEmpty()) {
            this.inventory.setStack(0, itemStack.split(1));
            this.isItemCarvingBoard = false;
            this.inventoryChanged();
            return true;
        } else {
            return false;
        }
    }

    public boolean carveToolOnBoard(ItemStack tool) {
        if (this.addItem(tool)) {
            this.isItemCarvingBoard = true;
            return true;
        } else {
            return false;
        }
    }

    public ItemStack removeItem() {
        if (!this.isEmpty()) {
            this.isItemCarvingBoard = false;
            ItemStack item = this.getStoredItem().split(1);
            this.inventoryChanged();
            return item;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public ItemStack getStoredItem() {
        return this.inventory.getStack(0);
    }

    public boolean isEmpty() {
        return this.inventory.getStack(0).isEmpty();
    }

    public boolean isItemCarvingBoard() {
        return this.isItemCarvingBoard;
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler() {
            @Override
            public int getMaxCountForSlot(int slot) {
                return 1;
            }

            @Override
            protected void onInventorySlotChanged(int slot) {
                ItemCoasterBlockEntity.this.inventoryChanged();
            }
        };
    }
}
