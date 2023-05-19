package dev.sterner.brewinandchewin.common.block;

import com.nhoryzon.mc.farmersdelight.registry.ParticleTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class FieryFonduePotBlock extends Block {
    public static final VoxelShape INSIDE = Block.createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    public static final VoxelShape SHAPE;
    public static final IntProperty LEVEL;
    public static final DirectionProperty FACING;

    public FieryFonduePotBlock(Settings settings) {
        super(settings);
        this.setDefaultState(((this.getDefaultState()).with(LEVEL, 3)).with(FACING, Direction.NORTH));
    }

    protected double getContentHeight(BlockState state) {
        return (6.0 + (double) state.get(LEVEL) * 3.0) / 16.0;
    }

    public boolean isEntityInsideContent(BlockState state, BlockPos pos, Entity entity) {
        return entity.getY() < (double) pos.getY() + this.getContentHeight(state) && entity.getBoundingBox().maxY > (double) pos.getY() + 0.25;
    }

    @Override
    public void onEntityCollision(BlockState state, World level, BlockPos pos, Entity entity) {
        if (this.isEntityInsideContent(state, pos, entity)) {
            entity.setOnFireFromLava();
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int servings = state.get(LEVEL);
        ItemStack bowl = new ItemStack(Items.BOWL);
        ItemStack fondue = new ItemStack(BCObjects.FIERY_FONDUE);
        ItemStack heldStack = player.getStackInHand(hand);
        if (heldStack.isItemEqual(bowl)) {
            if (!player.getAbilities().creativeMode) {
                heldStack.decrement(1);
            }

            if (!player.getInventory().insertStack(fondue)) {
                player.dropItem(fondue, false);
            }

            BlockState newState = world.getBlockState(pos).get(LEVEL) > 1 ? state.with(LEVEL, servings - 1) : Blocks.CAULDRON.getDefaultState();
            world.setBlockState(pos, newState, 3);
            if (state.get(LEVEL) == 1) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.BONE));
            }

            world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView level, BlockPos pos) {
        return INSIDE;
    }

    @Override
    public int getComparatorOutput(BlockState state, World level, BlockPos pos) {
        return state.get(LEVEL);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (random.nextFloat() < 0.8F) {
            double x = (double) pos.getX() + 0.5 + (random.nextDouble() * 0.6 - 0.3);
            double y = (double) pos.getY() + this.getContentHeight(state);
            double z = (double) pos.getZ() + 0.5 + (random.nextDouble() * 0.6 - 0.3);
            world.addParticle(ParticleTypesRegistry.STEAM.get(), x, y, z, 0.0, 0.0, 0.0);
            double x1 = (double) pos.getX() + 0.5;
            double y1 = pos.getY();
            double z1 = (double) pos.getZ() + 0.5;
            if (random.nextInt(10) == 0) {
                world.playSound(x1, y1, z1, SoundsRegistry.BLOCK_COOKING_POT_BOIL_SOUP.get(), SoundCategory.BLOCKS, 0.5F, random.nextFloat() * 0.2F + 0.9F, false);
            }
        }
    }

    static {
        SHAPE = VoxelShapes.combineAndSimplify(
                VoxelShapes.fullCube(),
                VoxelShapes.union(
                        createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                        createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                        createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), INSIDE),
                BooleanBiFunction.ONLY_FIRST);

        LEVEL = Properties.LEVEL_3;
        FACING = HorizontalFacingBlock.FACING;
    }
}
