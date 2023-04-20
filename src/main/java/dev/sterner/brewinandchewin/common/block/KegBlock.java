package dev.sterner.brewinandchewin.common.block;

import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackHandler;
import com.nhoryzon.mc.farmersdelight.util.MathUtils;
import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KegBlock extends BlockWithEntity {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty VERTICAL = BooleanProperty.of("vertical");

    protected static final VoxelShape SHAPE_X = Block.createCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 16.0D, 16.0D);
    protected static final VoxelShape SHAPE_Z = Block.createCuboidShape(0.0D, 0.0D, 1.0D, 16.0D, 16.0D, 15.0D);
    protected static final VoxelShape SHAPE_VERTICAL = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public KegBlock() {
        super(FabricBlockSettings.of(Material.WOOD).strength(2, 3).sounds(BlockSoundGroup.WOOD));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(VERTICAL, false).with(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KegBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getStackInHand(hand);
        if (!world.isClient()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof KegBlockEntity kegBlockEntity) {
                kegBlockEntity.updateTemperature();
                ItemStack servingStack = kegBlockEntity.useHeldItemOnMeal(heldStack);
                if (servingStack != ItemStack.EMPTY) {
                    if (!player.getInventory().insertStack(servingStack)) {
                        player.dropItem(servingStack, false);
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                } else {
                    NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                    if (screenHandlerFactory != null) {
                        player.openHandledScreen(screenHandlerFactory);
                    }
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!state.get(VERTICAL)) {
            if ((state.get(FACING) == Direction.SOUTH || state.get(FACING) == Direction.NORTH)) {
                return SHAPE_X;
            }
            if ((state.get(FACING) == Direction.EAST || state.get(FACING) == Direction.WEST)) {
                return SHAPE_Z;
            }
        }
        return SHAPE_VERTICAL;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof KegBlockEntity kegBlockEntity) {
            kegBlockEntity.updateTemperature();
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerLookDirection();
        FluidState fluid = ctx.getWorld().getFluidState(ctx.getBlockPos());
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(VERTICAL, true).with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);
        }
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (world.getBlockEntity(pos) instanceof KegBlockEntity blockEntity) {
            blockEntity.updateTemperature();
        }
        return state;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        KegBlockEntity kegBlockEntity = (KegBlockEntity) world.getBlockEntity(pos);
        if (kegBlockEntity != null) {
            NbtCompound nbt = kegBlockEntity.writeDrink(new NbtCompound());
            if (!nbt.isEmpty()) {
                stack.setSubNbt("BlockEntityTag", nbt);
            }
            if (kegBlockEntity.hasCustomName()) {
                stack.setCustomName(kegBlockEntity.getCustomName());
            }
        }
        return stack;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof KegBlockEntity kegBlockEntity) {
                ItemScatterer.spawn(world, pos, kegBlockEntity.getDroppableInventory());
                kegBlockEntity.getUsedRecipesAndPopExperience(world, Vec3d.ofCenter(pos));
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        NbtCompound nbt = stack.getSubNbt("BlockEntityTag");
        if (nbt != null) {
            NbtCompound inventoryTag = nbt.getCompound("Inventory");
            if (inventoryTag.contains("Items", 9)) {
                ItemStackHandler handler = new ItemStackHandler();
                handler.readNbt(inventoryTag);
                ItemStack mealStack = handler.getStack(5);
                if (!mealStack.isEmpty()) {
                    MutableText textServingsOf = mealStack.getCount() == 1
                            ? BCTextUtils.getTranslation("tooltip.keg.single_serving")
                            : BCTextUtils.getTranslation("tooltip.keg.many_servings", mealStack.getCount());
                    tooltip.add(textServingsOf.formatted(Formatting.GRAY));
                    MutableText textMealName = mealStack.getName().copy();
                    tooltip.add(textMealName.formatted(mealStack.getRarity().formatting));
                }
            }
        } else {
            MutableText textEmpty = BCTextUtils.getTranslation("tooltip.keg.empty");

        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, VERTICAL, WATERLOGGED);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof KegBlockEntity kegBlockEntity) {
                kegBlockEntity.setCustomName(itemStack.getName());
                kegBlockEntity.updateTemperature();
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof KegBlockEntity) {
            ItemStackHandler inventory = ((KegBlockEntity) tileEntity).getInventory();
            return MathUtils.calcRedstoneFromItemHandler(inventory);
        }
        return 0;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return checkType(type, BCBlockEntityTypes.KEG, KegBlockEntity::animationTick);
        } else {
            return checkType(type, BCBlockEntityTypes.KEG, KegBlockEntity::fermentingTick);
        }
    }

}
