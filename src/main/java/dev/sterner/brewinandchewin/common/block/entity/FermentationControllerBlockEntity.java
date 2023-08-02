package dev.sterner.brewinandchewin.common.block.entity;

import com.nhoryzon.mc.farmersdelight.entity.block.SyncedBlockEntity;
import dev.sterner.brewinandchewin.common.block.FermentationControllerBlock;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static dev.sterner.brewinandchewin.common.block.FermentationControllerBlock.STATE;

public class FermentationControllerBlockEntity extends SyncedBlockEntity {

    private int targetTemp = 0;
    private int temperature = 0;
    private final int MAX_TEMP = 16;
    private final int MIN_TEMP = - MAX_TEMP;

    private int ticker = 0;

    public FermentationControllerBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.FERMENTATION_CONTROLLER, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world != null && !world.isClient) {
            if (getTemperature() != getTargetTemperature()) {
                ticker++;
                if (ticker > 20 * 5) {
                    if (getTemperature() > getTargetTemperature()) {
                        decreaseTemp();
                    } else {
                        increaseTemp();
                    }
                    updateState(world, pos, state);
                    ticker = 0;
                }
            } else {
                ticker = 0;
            }
        }
    }

    private void updateState(World world, BlockPos pos, BlockState state) {
        int temperature = getTemperature();
        if (temperature == 0 && state.get(STATE) != FermentationControllerBlock.State.NONE) {
            world.setBlockState(pos, state.with(STATE, FermentationControllerBlock.State.NONE), Block.field_31022);

        } else if (temperature > 0 && state.get(STATE) != FermentationControllerBlock.State.HOT) {
            world.setBlockState(pos, state.with(STATE, FermentationControllerBlock.State.HOT), Block.field_31022);

        } else if (temperature < 0 && state.get(STATE) != FermentationControllerBlock.State.COLD) {
            world.setBlockState(pos, state.with(STATE, FermentationControllerBlock.State.COLD), Block.field_31022);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        temperature = nbt.getInt("Temperature");
        targetTemp = nbt.getInt("TargetTemperature");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Temperature", getTemperature());
        nbt.putInt("TargetTemperature", getTargetTemperature());
    }

    private void sync(){
        this.markDirty();
        if (this.world != null) {
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
        }
    }

    public int getTargetTemperature() {
        return targetTemp;
    }

    public void setTargetTemperature(int targetTemp) {
        this.targetTemp = targetTemp;
        sync();
    }

    public int getTemperature() {
        return temperature;
    }

    private void setTemperature(int temperature) {
        this.temperature = temperature;
        sync();
    }

    public void increaseTemp(){
        if (getTemperature() < MAX_TEMP) {
            setTemperature(getTemperature() + 1);
        }
    }

    public void decreaseTemp(){
        if (getTemperature() > MIN_TEMP) {
            setTemperature(getTemperature() - 1);
        }
    }
}
