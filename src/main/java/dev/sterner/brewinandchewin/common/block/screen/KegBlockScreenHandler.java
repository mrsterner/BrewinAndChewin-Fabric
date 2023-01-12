package dev.sterner.brewinandchewin.common.block.screen;

import com.mojang.datafixers.util.Pair;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemHandler;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.slot.SlotItemHandler;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCScreenHandlerTypes;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.fabricmc.api.EnvType.CLIENT;

public class KegBlockScreenHandler extends ScreenHandler {
    private static final int INV_INDEX_MEAL_DISPLAY = 5;
    private static final int INV_INDEX_CONTAINER_INPUT = INV_INDEX_MEAL_DISPLAY + 1;
    private static final int INV_INDEX_OUTPUT = INV_INDEX_CONTAINER_INPUT + 1;
    private static final int INV_INDEX_START_PLAYER_INV = INV_INDEX_OUTPUT + 1;
    private static final int INV_INDEX_END_PLAYER_INV = INV_INDEX_START_PLAYER_INV + 36;
    public static final Identifier EMPTY_CONTAINER_SLOT_MUG = new Identifier(BrewinAndChewin.MODID, "item/empty_container_slot_mug");

    public final KegBlockEntity blockEntity;
    public final ItemHandler inventory;
    private final PropertyDelegate kegData;
    private final ScreenHandlerContext canInteractWithCallable;

    public KegBlockScreenHandler(final int windowId, final PlayerInventory playerInventory, final KegBlockEntity blockEntity, PropertyDelegate kegData) {
        super(BCScreenHandlerTypes.KEG_SCREEN_HANDLER, windowId);
        this.blockEntity = blockEntity;
        this.inventory = blockEntity.getInventory();
        this.kegData = kegData;
        this.canInteractWithCallable = ScreenHandlerContext.create(blockEntity.getWorld(), blockEntity.getPos());

        // Ingredient Slots - 2 Rows x 2 Columns
        int startX = 8;
        int startY = 18;
        int inputStartX = 33;
        int inputStartY = 28;
        int borderSlotSize = 18;
        for (int row = 0; row < 2; ++row) {
            for (int column = 0; column < 2; ++column) {
                this.addSlot(new SlotItemHandler(inventory, (row * 2) + column,
                        inputStartX + (column * borderSlotSize),
                        inputStartY + (row * borderSlotSize)));
            }
        }
        this.addSlot(new SlotItemHandler(inventory, 4, 85, 18));

        // Meal Display
        this.addSlot(new KegMealSlot(inventory, 5, 122, 23));

        // Bowl Input
        this.addSlot(new SlotItemHandler(inventory, 6, 90, 55)
        {
            @Environment(CLIENT)
            public Pair<Identifier, Identifier> getNoItemIcon() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_CONTAINER_SLOT_MUG);
            }
        });

        // Bowl Output
        this.addSlot(new KegResultSlot(playerInventory.player, blockEntity, inventory, 7, 122, 55));

        // Main Player Inventory
        int startPlayerInvY = startY * 4 + 12;
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * borderSlotSize),
                        startPlayerInvY + (row * borderSlotSize)));
            }
        }

        // Hotbar
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, startX + (column * borderSlotSize), 142));
        }

        this.addProperties(kegData);
    }

    public KegBlockScreenHandler(final int windowId, final PlayerInventory playerInventory, final PacketByteBuf data) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data), new ArrayPropertyDelegate(4));
    }

    private static KegBlockEntity getBlockEntity(final PlayerInventory playerInventory, final PacketByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.world.getBlockEntity(data.readBlockPos());

        if (tileAtPos instanceof KegBlockEntity kegBlockEntity) {
            return kegBlockEntity;
        }

        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean canUse(PlayerEntity playerIn) {
        return canUse(canInteractWithCallable, playerIn, BCObjects.KEG);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerIn, int index) {
        if (index > slots.size() - 1) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasStack()) {
            ItemStack slotItemStack = slot.getStack();
            itemStack = slotItemStack.copy();
            if (index == INV_INDEX_OUTPUT) {
                if (!insertItem(slotItemStack, INV_INDEX_START_PLAYER_INV, INV_INDEX_END_PLAYER_INV, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index > INV_INDEX_OUTPUT) {
                if ((slotItemStack.getItem() == BCObjects.TANKARD && !insertItem(slotItemStack, INV_INDEX_CONTAINER_INPUT, INV_INDEX_OUTPUT, false))
                        || !insertItem(slotItemStack, 0, INV_INDEX_MEAL_DISPLAY, false)
                        || !insertItem(slotItemStack, INV_INDEX_CONTAINER_INPUT, INV_INDEX_OUTPUT, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!insertItem(slotItemStack, INV_INDEX_START_PLAYER_INV, INV_INDEX_END_PLAYER_INV, false)) {
                return ItemStack.EMPTY;
            }

            if (slotItemStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotItemStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(playerIn, slotItemStack);
        }

        return itemStack;
    }
    public int getFermentingTicks() {
        return this.kegData.get(0);
    }

    @Environment(CLIENT)
    public int getFermentProgressionScaled() {
        int i = this.kegData.get(0);
        int j = this.kegData.get(1);
        return j != 0 && i != 0 ? i * 33 / j : 0;
    }

    @Environment(CLIENT)
    public int getTemperature() {
        return this.blockEntity.heat - this.blockEntity.cold;
    }
}
