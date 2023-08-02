package dev.sterner.brewinandchewin.common.block;

import com.nhoryzon.mc.farmersdelight.block.StoveBlock;
import dev.sterner.brewinandchewin.common.block.entity.FermentationControllerBlockEntity;
import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stat;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class FermentationControllerBlock extends BlockWithEntity {

    public static final EnumProperty<State> STATE = EnumProperty.of("state", State.class);

    public FermentationControllerBlock() {
        super(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).strength(2, 4).sounds(BlockSoundGroup.METAL).nonOpaque());
        setDefaultState(this.stateManager.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH).with(STATE, State.NONE));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FermentationControllerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (tickerWorld, pos, tickerState, blockEntity) -> {
            if (blockEntity instanceof FermentationControllerBlockEntity be) {
                be.tick(world, pos, state);
            }
        };
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        updateTemp(world, pos, state);
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        updateTemp(world, pos, state);
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    private void updateTemp(World world, BlockPos pos, BlockState state){
        if (world.getBlockEntity(pos) instanceof FermentationControllerBlockEntity blockEntity) {
            Direction facing = state.get(HorizontalFacingBlock.FACING);
            Direction right = facing.rotateYClockwise();
            Direction left = facing.rotateYCounterclockwise();
            int coldPower = getReceivedRedstonePower(world, pos, right);
            int hotPower = getReceivedRedstonePower(world, pos, left);
            int totalPower = hotPower - coldPower;
            blockEntity.setTargetTemperature(totalPower);
        }
    }

    public int getReceivedRedstonePower(World world, BlockPos pos, Direction direction) {
        int i = 0;

        int j = world.getEmittedRedstonePower(pos.offset(direction), direction);
        if (j >= 15) {
            return 15;
        }

        if (j > i) {
            i = j;
        }
        return i;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HorizontalFacingBlock.FACING, STATE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getPlayerFacing().getOpposite()).with(STATE, State.NONE);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(HorizontalFacingBlock.FACING, rotation.rotate(state.get(HorizontalFacingBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(HorizontalFacingBlock.FACING)));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public enum State implements StringIdentifiable {
        NONE("none"),
        HOT("hot"),
        COLD("cold");

        private final String name;

        State(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
