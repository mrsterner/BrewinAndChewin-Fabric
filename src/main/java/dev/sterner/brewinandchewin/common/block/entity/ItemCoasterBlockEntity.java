package dev.sterner.brewinandchewin.common.block.entity;

import com.nhoryzon.mc.farmersdelight.entity.block.SyncedBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackHandler;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ItemCoasterBlockEntity extends SyncedBlockEntity {

    private final ItemStackHandler inventory = new ItemStackHandler() {
        @Override
        public int getMaxCountForSlot(int slot) {
            return 1;
        }

        @Override
        protected void onInventorySlotChanged(int slot) {
            ItemCoasterBlockEntity.this.inventoryChanged();
        }
    };

    private boolean isItemCarvingBoard = false;

    public ItemCoasterBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.COASTER, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.isItemCarvingBoard = tag.getBoolean("IsItemCarved");
        this.inventory.readNbt(tag.getCompound("Inventory"));
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("Inventory", this.inventory.writeNbt(new NbtCompound()));
        tag.putBoolean("IsItemCarved", this.isItemCarvingBoard);
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

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }
}
