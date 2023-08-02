package dev.sterner.brewinandchewin.mixin;

import com.nhoryzon.mc.farmersdelight.entity.block.HeatableBlockEntity;
import dev.sterner.brewinandchewin.common.block.entity.FermentationControllerBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HeatableBlockEntity.class)
public interface HeatableBlockEntityMixin {

    @Inject(method = "isHeated", at = @At(value = "RETURN"), cancellable = true)
    private void bc$isHeated(World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        if (!cir.getReturnValue()) {
            if (world.getBlockEntity(pos.down()) instanceof FermentationControllerBlockEntity blockEntity) {
                if (blockEntity.getTemperature() > 4) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
