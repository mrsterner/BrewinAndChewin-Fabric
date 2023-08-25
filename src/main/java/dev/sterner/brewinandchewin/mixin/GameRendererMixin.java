package dev.sterner.brewinandchewin.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @ModifyArg(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3f;getDegreesQuaternion(F)Lnet/minecraft/util/math/Quaternion;", ordinal = 0))
    private float adjust1(float amount) {
        return BC$modifyAmplifier(amount);
    }

    @ModifyArg(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3f;getDegreesQuaternion(F)Lnet/minecraft/util/math/Quaternion;", ordinal = 1))
    private float adjust2(float amount) {
        return BC$modifyAmplifier(amount);
    }

    private float BC$modifyAmplifier(float amount) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        PlayerEntity player = minecraft.player;
        if (player != null && player.hasStatusEffect(StatusEffects.NAUSEA) && player.getStatusEffect(StatusEffects.NAUSEA).getAmplifier() > 0) {
            int amplifier = player.getStatusEffect(StatusEffects.NAUSEA).getAmplifier() + 1;
            return amount / (float) amplifier;
        } else {
            return amount;
        }
    }
}