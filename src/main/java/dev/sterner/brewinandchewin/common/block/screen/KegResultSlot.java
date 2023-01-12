package dev.sterner.brewinandchewin.common.block.screen;

import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemHandler;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.slot.SlotItemHandler;
import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class KegResultSlot extends SlotItemHandler {
    public final KegBlockEntity blockEntity;
    private final PlayerEntity player;
    private int removeCount;

    public KegResultSlot(PlayerEntity player, KegBlockEntity blockEntity, ItemHandler inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.blockEntity = blockEntity;
        this.player = player;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (this.hasStack()) {
            this.removeCount += Math.min(amount, this.getStack().getCount());
        }
        return super.takeStack(amount);
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);
        super.onTakeItem(player, stack);
    }


    @Override
    protected void onCrafted(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.onCrafted(stack);
    }

    @Override
    protected void onCrafted(ItemStack stack) {
        stack.onCraft(this.player.world, this.player, this.removeCount);

        if (!this.player.world.isClient()) {
            blockEntity.clearUsedRecipes(this.player);
        }

        this.removeCount = 0;
    }
}
