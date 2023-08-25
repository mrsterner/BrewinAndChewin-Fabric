package dev.sterner.brewinandchewin.common.block;

import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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

public class RipeCheeseWheelBlock extends Block {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    public static final IntProperty SERVINGS = IntProperty.of("servings", 0, 3);
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.createCuboidShape(2.0D, 0.0D, 2.0D, 8.0D, 6.0D, 8.0D),
            Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 8.0D),
            VoxelShapes.union(Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 8.0D), Block.createCuboidShape(2.0D, 0.0D, 8.0D, 8.0D, 6.0D, 14.0D)),
            Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D),
    };
    public final Item cheeseType;

    public RipeCheeseWheelBlock(Item cheeseType, Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(SERVINGS, 3));
        this.cheeseType = cheeseType;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return !world.isAir(pos.down());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(SERVINGS)];
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int servings = state.get(SERVINGS);
        ItemStack heldStack = player.getStackInHand(hand);

        if (servings > 0) {
            if (heldStack.isIn(TagsRegistry.KNIVES)) {
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                dropStack(world, pos, new ItemStack(cheeseType, 1));
                world.setBlockState(pos, state.with(SERVINGS, servings - 1), 3);
            } else {
                player.sendMessage(BCTextUtils.getTranslation("block.cheese.use_knife"), true);
            }
        }
        if (servings == 0) {
            if (heldStack.isIn(TagsRegistry.KNIVES)) {
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                dropStack(world, pos, new ItemStack(cheeseType, 1));
                world.breakBlock(pos, false);
            } else {
                player.sendMessage(BCTextUtils.getTranslation("block.cheese.use_knife"), true);
            }
        }
        return ActionResult.SUCCESS;
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
