package dev.sterner.brewinandchewin.common.block;

import dev.sterner.brewinandchewin.common.block.entity.ItemCoasterBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class ItemCoasterBlock extends BlockWithEntity implements Waterloggable {
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;

    public ItemCoasterBlock() {
        super(Settings.copy(Blocks.WHITE_CARPET).sounds(BlockSoundGroup.WOOD).strength(0.2F));
        this.setDefaultState(((this.getDefaultState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ItemCoasterBlockEntity(pos, state);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ItemCoasterBlockEntity itemCoasterBlockEntity) {
            if (!itemCoasterBlockEntity.isEmpty()) {
                return itemCoasterBlockEntity.getStoredItem();
            }
        }
        return super.getPickStack(world, pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ItemCoasterBlockEntity itemCoasterBlockEntity) {
            ItemStack heldStack = player.getStackInHand(hand);
            ItemStack offhandStack = player.getOffHandStack();
            if (itemCoasterBlockEntity.isEmpty()) {
                if (!offhandStack.isEmpty()) {
                    if (hand.equals(Hand.MAIN_HAND) && !offhandStack.isIn(BCTags.OFFHAND_EQUIPMENT) && !(heldStack.getItem() instanceof BlockItem)) {
                        return ActionResult.PASS;
                    }

                    if (hand.equals(Hand.OFF_HAND) && offhandStack.isIn(BCTags.OFFHAND_EQUIPMENT)) {
                        return ActionResult.PASS;
                    }
                }

                if (heldStack.isEmpty()) {
                    return ActionResult.PASS;
                }

                if (itemCoasterBlockEntity.addItem(player.getAbilities().creativeMode ? heldStack.copy() : heldStack)) {
                    world.playSound(null, ((float) pos.getX() + 0.5F), pos.getY(), (float) pos.getZ() + 0.5F, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.5F, 0.8F);
                    return ActionResult.SUCCESS;
                }

                if (!heldStack.isEmpty()) {
                    return ActionResult.CONSUME;
                }
            } else if (hand.equals(Hand.MAIN_HAND)) {
                if (!player.isCreative()) {
                    if (!player.getInventory().insertStack(itemCoasterBlockEntity.removeItem())) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemCoasterBlockEntity.removeItem());
                    }
                } else {
                    itemCoasterBlockEntity.removeItem();
                }

                world.playSound(null, (float) pos.getX() + 0.5F, pos.getY(), (float) pos.getZ() + 0.5F, SoundEvents.BLOCK_WOOL_HIT, SoundCategory.BLOCKS, 0.5F, 0.5F);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }


    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ItemCoasterBlockEntity) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ((ItemCoasterBlockEntity) tileEntity).getStoredItem());
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean canMobSpawnInside(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluid = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() :
                super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos floorPos = pos.down();
        return hasTopRim(world, floorPos) || sideCoversSmallSquare(world, floorPos, Direction.UP);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World worldIn, BlockPos pos) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof ItemCoasterBlockEntity) {
            return !((ItemCoasterBlockEntity) tileEntity).isEmpty() ? 15 : 0;
        } else {
            return 0;
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, BlockMirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.get(FACING)));
    }


    static {
        FACING = Properties.HORIZONTAL_FACING;
        WATERLOGGED = Properties.WATERLOGGED;
        SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    }
}
