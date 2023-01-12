package dev.sterner.brewinandchewin.common.block;

import com.nhoryzon.mc.farmersdelight.registry.ParticleTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.SoundsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FonduePotBlock extends Block {
    public static final VoxelShape INSIDE = Block.createCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    public static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.union(
            Block.createCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D),
                    Block.createCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D),
                    Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE),
            BooleanBiFunction.ONLY_FIRST);
    public static final int MIN_FILL_LEVEL = 1;
    public static final int MAX_FILL_LEVEL = 3;
    public static final IntProperty LEVEL = Properties.LEVEL_3;

    public FonduePotBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(LEVEL, 3));
    }

    public boolean isFull(BlockState state) {
        return state.get(LEVEL) == 3;
    }

    protected double getContentHeight(BlockState state) {
        return (6.0D + (double) state.get(LEVEL) * 3.0D) / 16.0D;
    }

    public boolean isEntityInsideContent(BlockState state, BlockPos pos, Entity entity) {
        return entity.getY() < (double)pos.getY() + this.getContentHeight(state) && entity.getBoundingBox().maxY > (double)pos.getY() + 0.25D;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (this.isEntityInsideContent(state, pos, entity)) {
            entity.setOnFireFromLava();
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(LEVEL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return INSIDE;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (random.nextFloat() < 0.8F) {
            double x = (double) pos.getX() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
            double y = (double) pos.getY() + this.getContentHeight(state);
            double z = (double) pos.getZ() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
            world.addParticle(ParticleTypesRegistry.STEAM.get(), x, y, z, 0.0D, 0.0D, 0.0D);

            double x1 = (double) pos.getX() + 0.5D;
            double y1 = pos.getY();
            double z1 = (double) pos.getZ() + 0.5D;
            if (random.nextInt(10) == 0) {
                world.playSound(x1, y1, z1, SoundsRegistry.BLOCK_COOKING_POT_BOIL_SOUP.get(), SoundCategory.BLOCKS, 0.5F, random.nextFloat() * 0.2F + 0.9F, false);
            }
        }
    }
}
