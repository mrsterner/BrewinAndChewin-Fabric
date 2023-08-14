package dev.sterner.brewinandchewin.common.block.entity;

import com.nhoryzon.mc.farmersdelight.entity.block.SyncedBlockEntity;
import dev.sterner.brewinandchewin.common.item.BoozeBlockItem;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TankardBlockEntity extends SyncedBlockEntity {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    public TankardBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.TANKARD, pos, state);
    }

    public ActionResult onUse(World world, BlockState state, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getMainHandStack();
        int occupiedCount = (int)getItems().stream().filter(i -> !i.isEmpty()).count();
        if (player.isSneaking() && hand == Hand.MAIN_HAND) {
            if (occupiedCount == 0) {
                return ActionResult.PASS;
            }

            boolean bl = false;
            if (ItemStack.canCombine(inventory.get(occupiedCount - 1), itemStack)) {
                itemStack.increment(1);
                inventory.set(occupiedCount - 1, ItemStack.EMPTY);
                bl = true;
            } else if (itemStack.isEmpty()) {
                player.setStackInHand(Hand.MAIN_HAND, inventory.get(occupiedCount - 1));
                inventory.set(occupiedCount - 1, ItemStack.EMPTY);
                bl = true;
            }
            if (occupiedCount == 1 && bl) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        } else {
            if (itemStack.getItem() instanceof BoozeBlockItem boozeItem) {
                if (occupiedCount == 1) {
                    inventory.set(1, new ItemStack(boozeItem, 1));
                    player.getMainHandStack().decrement(1);
                } else if (occupiedCount == 2) {
                    inventory.set(2, new ItemStack(boozeItem, 1));
                    player.getMainHandStack().decrement(1);
                } else {
                    return ActionResult.CONSUME;
                }
            }
        }
        inventoryChanged();

        return ActionResult.PASS;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
    }

    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }
}
