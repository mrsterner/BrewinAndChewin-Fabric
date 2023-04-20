package dev.sterner.brewinandchewin.common.block;

import com.mojang.datafixers.util.Pair;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;


public class QuicheBlock extends Block {
    public static final IntProperty BITES = IntProperty.of("bites", 0, 3);
    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    public QuicheBlock(Settings settings) {
        super(settings);
        this.setDefaultState((this.getDefaultState()).with(BITES, 3));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getStackInHand(hand);
        if (world.isClient) {//TODO this dont make sense, shouldnt it be serverside?
            if (heldStack.isIn(TagsRegistry.KNIVES)) {
                return this.cutSlice(world, pos, state);
            }

            if (this.consumeBite(world, pos, state, player) == ActionResult.SUCCESS) {
                return ActionResult.SUCCESS;
            }

            if (heldStack.isEmpty()) {
                return ActionResult.CONSUME;
            }
        }

        return heldStack.isIn(TagsRegistry.KNIVES) ? this.cutSlice(world, pos, state) : this.consumeBite(world, pos, state, player);
    }

    protected ActionResult consumeBite(World level, BlockPos pos, BlockState state, PlayerEntity playerIn) {
        if (!playerIn.canConsume(false)) {
            return ActionResult.PASS;
        } else {
            ItemStack sliceStack = new ItemStack((ItemConvertible) BCObjects.QUICHE_SLICE);
            FoodComponent sliceFood = sliceStack.getItem().getFoodComponent();
            playerIn.getHungerManager().eat(sliceStack.getItem(), sliceStack);
            if (sliceStack.getItem().isFood() && sliceFood != null) {

                for (Pair<StatusEffectInstance, Float> pair : sliceFood.getStatusEffects()) {
                    if (!level.isClient && pair.getFirst() != null && level.random.nextFloat() < pair.getSecond()) {
                        playerIn.addStatusEffect(new StatusEffectInstance(pair.getFirst()));
                    }
                }
            }

            int bites = (Integer)state.get(BITES);
            if (bites > 0) {
                level.setBlockState(pos, (BlockState)state.with(BITES, bites - 1), 3);
            } else {
                level.removeBlock(pos, false);
            }

            level.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.8F, 0.8F);
            return ActionResult.SUCCESS;
        }
    }

    protected ActionResult cutSlice(World level, BlockPos pos, BlockState state) {
        int bites = (Integer)state.get(BITES);
        if (bites > 0) {
            level.setBlockState(pos, (BlockState)state.with(BITES, bites - 1), 3);
        } else {
            level.removeBlock(pos, false);
        }

        ItemScatterer.spawn(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack((ItemConvertible) BCObjects.QUICHE_SLICE));
        level.playSound(null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.PLAYERS, 0.8F, 0.8F);
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN && !this.canPlaceAt(state, world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BITES);
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return (Integer)state.get(BITES);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView worldIn, BlockPos pos, NavigationType type) {
        return false;
    }
}
