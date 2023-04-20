package dev.sterner.brewinandchewin.mixin;

import dev.sterner.brewinandchewin.common.registry.BCStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    @Unique
    private PlayerEntity player;

    @Inject(method = "update", at = @At(value = "HEAD"))
    private void findPlayer(PlayerEntity player, CallbackInfo ci) {
        this.player = player;
    }

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"))
    private float disableNaturalRegen(float amount) {
        if (player.hasStatusEffect(BCStatusEffects.TIPSY)) {
            StatusEffectInstance effect = player.getStatusEffect(BCStatusEffects.TIPSY);
            if (effect != null) {
                return effect.getAmplifier() > 0 ? 0.0F : amount;
            }
        }
        return amount;
    }
}
