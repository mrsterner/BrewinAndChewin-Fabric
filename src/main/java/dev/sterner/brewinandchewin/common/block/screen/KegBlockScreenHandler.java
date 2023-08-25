package dev.sterner.brewinandchewin.common.block.screen;

import com.mojang.datafixers.util.Pair;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.slot.CookingPotMealSlot;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCScreenHandlerTypes;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Objects;

import static net.fabricmc.api.EnvType.CLIENT;
import static net.minecraft.client.texture.SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;

public class KegBlockScreenHandler extends ScreenHandler {
    public static final Identifier EMPTY_CONTAINER_SLOT_MUG = new Identifier(BrewinAndChewin.MODID, "item/empty_container_slot_mug");

    public final KegBlockEntity blockEntity;
    private final PropertyDelegate kegData;
    private final ScreenHandlerContext canInteractWithCallable;
    protected final World world;

    public KegBlockScreenHandler(final int windowId, final PlayerInventory playerInventory, final KegBlockEntity blockEntity, PropertyDelegate kegData) {
        super(BCScreenHandlerTypes.KEG_SCREEN_HANDLER, windowId);
        this.blockEntity = blockEntity;
        this.kegData = kegData;
        this.canInteractWithCallable = ScreenHandlerContext.create(blockEntity.getWorld(), blockEntity.getPos());
        this.world = playerInventory.player.getWorld();

        // Ingredient Slots - 2 Rows x 2 Columns
        int startX = 8;
        int startY = 18;
        int inputStartX = 28;
        int inputStartY = 28;
        int borderSlotSize = 18;
        for (int row = 0; row < 2; ++row) {
            for (int column = 0; column < 2; ++column) {
                this.addSlot(new Slot(blockEntity, (row * 2) + column,
                        inputStartX + (column * borderSlotSize),
                        inputStartY + (row * borderSlotSize)));
            }
        }

        this.addSlot(new Slot(this.blockEntity, 4, 80, 18));
        this.addSlot(new CookingPotMealSlot(this.blockEntity, 5, 117, 23));
        this.addSlot(new Slot(this.blockEntity, 6, 85, 55) {
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_CONTAINER_SLOT_MUG);
            }
        });
        this.addSlot(new KegResultSlot(playerInventory.player, blockEntity, 7, 117, 55));
        this.addSlot(new KegResultSlot(playerInventory.player, blockEntity, 8, 143, 55));

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
        this(windowId, playerInventory, getBlockEntity(playerInventory, data), new ArrayPropertyDelegate(5));
    }

    private static KegBlockEntity getBlockEntity(final PlayerInventory playerInventory, final PacketByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.getWorld().getBlockEntity(data.readBlockPos());

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
        int indexDrinkDisplay = 4;
        int indexFluidItemInput = 5;
        int indexContainerInput = 6;
        int indexOutput = 7;
        int indexContainerOutput = 8;
        int startPlayerInv = indexContainerOutput + 1;
        int endPlayerInv = startPlayerInv + 36;
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index != indexOutput && index != indexContainerOutput) {
                if (index > indexContainerOutput) {
                    if (itemstack1.getItem() == BCObjects.TANKARD && !this.insertItem(itemstack1, indexContainerInput, indexContainerInput + 1, false)) {
                        return ItemStack.EMPTY;
                    }

                    if (!this.insertItem(itemstack1, 0, indexFluidItemInput, false)) {
                        return ItemStack.EMPTY;
                    }

                    if (!this.insertItem(itemstack1, 0, indexDrinkDisplay, false)) {
                        return ItemStack.EMPTY;
                    }

                    if (!this.insertItem(itemstack1, indexContainerInput, indexOutput, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(itemstack1, startPlayerInv, endPlayerInv, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemstack1, startPlayerInv, endPlayerInv, true)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Environment(CLIENT)
    public int getFermentProgressionScaled() {
        int i = this.kegData.get(0);
        int j = this.kegData.get(1);
        return j != 0 && i != 0 ? i * 33 / j : 0;
    }

    @Environment(CLIENT)
    public int getFermentingTicks() {
        return this.kegData.get(0);
    }

    @Environment(CLIENT)
    public int getTemperature() {
        return this.kegData.get(2);
    }

    @Environment(CLIENT)
    public int getAdjustedTemperature() {
        return this.kegData.get(3);
    }
}
