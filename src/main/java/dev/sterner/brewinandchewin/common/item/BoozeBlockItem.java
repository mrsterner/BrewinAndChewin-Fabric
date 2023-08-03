package dev.sterner.brewinandchewin.common.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoozeBlockItem extends BoozeItem {
    private final Block block;

    public BoozeBlockItem(Block block, int potency, int duration, Settings settings) {
        super(potency, duration, settings);
        this.block = block;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult actionResult = this.place(new ItemPlacementContext(context));
        if (!actionResult.isAccepted() && this.isFood()) {
            ActionResult actionResult2 = this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
            return actionResult2 == ActionResult.CONSUME ? ActionResult.CONSUME_PARTIAL : actionResult2;
        } else {
            return actionResult;
        }
    }

    public ActionResult place(ItemPlacementContext itemPlacementContext) {
        if (!this.getBlock().isEnabled(itemPlacementContext.getWorld().getEnabledFeatures())) {
            return ActionResult.FAIL;
        } else if (!itemPlacementContext.canPlace()) {
            return ActionResult.FAIL;
        } else {
            BlockState blockState = this.getPlacementState(itemPlacementContext);
            if (blockState == null) {
                return ActionResult.FAIL;
            } else if (!this.place(itemPlacementContext, blockState)) {
                return ActionResult.FAIL;
            } else {
                BlockPos blockPos = itemPlacementContext.getBlockPos();
                World world = itemPlacementContext.getWorld();
                PlayerEntity playerEntity = itemPlacementContext.getPlayer();
                ItemStack itemStack = itemPlacementContext.getStack();
                BlockState blockState2 = world.getBlockState(blockPos);
                if (blockState2.isOf(blockState.getBlock())) {
                    blockState2 = this.placeFromNbt(blockPos, world, itemStack, blockState2);
                    writeNbtToBlockEntity(world, playerEntity, blockPos, itemStack);
                    blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
                    if (playerEntity instanceof ServerPlayerEntity) {
                        Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
                    }
                }

                BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
                world.playSound(
                        playerEntity,
                        blockPos,
                        this.getPlaceSound(blockState2),
                        SoundCategory.BLOCKS,
                        (blockSoundGroup.getVolume() + 1.0F) / 2.0F,
                        blockSoundGroup.getPitch() * 0.8F
                );
                world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(playerEntity, blockState2));
                if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                return ActionResult.success(world.isClient);
            }
        }
    }

    protected SoundEvent getPlaceSound(BlockState state) {
        return state.getSoundGroup().getPlaceSound();
    }

    @Nullable
    protected BlockState getPlacementState(ItemPlacementContext context) {
        BlockState blockState = this.getBlock().getPlacementState(context);
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }

    private BlockState placeFromNbt(BlockPos pos, World world, ItemStack stack, BlockState state) {
        BlockState blockState = state;
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound("BlockStateTag");
            StateManager<Block, BlockState> stateManager = state.getBlock().getStateManager();

            for(String string : nbtCompound2.getKeys()) {
                Property<?> property = stateManager.getProperty(string);
                if (property != null) {
                    String string2 = nbtCompound2.get(string).asString();
                    blockState = with(blockState, property, string2);
                }
            }
        }

        if (blockState != state) {
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        }

        return blockState;
    }

    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String name) {
        return property.parse(name).map(value -> state.with(property, value)).orElse(state);
    }

    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        PlayerEntity playerEntity = context.getPlayer();
        ShapeContext shapeContext = playerEntity == null ? ShapeContext.absent() : ShapeContext.of(playerEntity);
        return (!this.checkStatePlacement() || state.canPlaceAt(context.getWorld(), context.getBlockPos()))
                && context.getWorld().canPlace(state, context.getBlockPos(), shapeContext);
    }

    protected boolean checkStatePlacement() {
        return true;
    }

    protected boolean place(ItemPlacementContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getBlockPos(), state, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
    }

    public static boolean writeNbtToBlockEntity(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack) {
        MinecraftServer minecraftServer = world.getServer();
        if (minecraftServer == null) {
            return false;
        } else {
            NbtCompound nbtCompound = getBlockEntityNbt(stack);
            if (nbtCompound != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null) {
                    if (!world.isClient && blockEntity.copyItemDataRequiresOperator() && (player == null || !player.isCreativeLevelTwoOp())) {
                        return false;
                    }

                    NbtCompound nbtCompound2 = blockEntity.createNbt();
                    NbtCompound nbtCompound3 = nbtCompound2.copy();
                    nbtCompound2.copyFrom(nbtCompound);
                    if (!nbtCompound2.equals(nbtCompound3)) {
                        blockEntity.readNbt(nbtCompound2);
                        blockEntity.markDirty();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    @Override
    public String getTranslationKey() {
        return this.getBlock().getTranslationKey();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        this.getBlock().appendTooltip(stack, world, tooltip, context);
    }

    public Block getBlock() {
        return this.block;
    }

    @Nullable
    public static NbtCompound getBlockEntityNbt(ItemStack stack) {
        return stack.getSubNbt("BlockEntityTag");
    }

    @Override
    public FeatureSet getRequiredFeatures() {
        return this.getBlock().getRequiredFeatures();
    }
}
