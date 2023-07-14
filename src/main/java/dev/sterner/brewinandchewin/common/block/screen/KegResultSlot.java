package dev.sterner.brewinandchewin.common.block.screen;

import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class KegResultSlot extends Slot {
    public final KegBlockEntity blockEntity;
    private final PlayerEntity player;
    private int removeCount;

    public KegResultSlot(PlayerEntity player, KegBlockEntity blockEntity, int index, int xPosition, int yPosition) {
        super(blockEntity, index, xPosition, yPosition);
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
            blockEntity.unlockLastRecipe(this.player);
        }

        this.removeCount = 0;
    }
}
