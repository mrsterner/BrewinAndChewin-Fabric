package dev.sterner.brewinandchewin.common.block;

import dev.sterner.brewinandchewin.common.registry.BCObjects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class PizzaBlock extends Block {
    public static final IntProperty SERVINGS = IntProperty.of("servings", 0, 3);

    protected static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 2.0D, 8.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 8.0D),
            VoxelShapes.union(Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 8.0D), Block.createCuboidShape(0.0D, 0.0D, 8.0D, 8.0D, 2.0D, 16.0D)),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
    };

    public PizzaBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(SERVINGS, 3));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(SERVINGS)];
    }


	/*@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}*/

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            if (this.takeServing(world, pos, state, player, hand).isAccepted()) {
                return ActionResult.SUCCESS;
            }
        }
        return this.takeServing(world, pos, state, player, hand);
    }

    private ActionResult takeServing(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand) {
        int servings = state.get(SERVINGS);
        ItemStack serving = new ItemStack(BCObjects.PIZZA_SLICE, 1);
        ItemStack heldStack = player.getStackInHand(hand);
        if (!player.getInventory().insertStack(serving)) {
            player.dropItem(serving, false);
        } if (true) {
            if (world.getBlockState(pos).get(SERVINGS) == 0) {
                world.removeBlock(pos, false);
            } else if (world.getBlockState(pos).get(SERVINGS) > 0) {
                world.setBlockState(pos, state.with(SERVINGS, servings - 1), 3);
            }
        }
        world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SERVINGS);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(SERVINGS);
    }

    @Override
    public boolean canMobSpawnInside() {
        return false;
    }
}
