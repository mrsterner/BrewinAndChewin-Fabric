package dev.sterner.brewinandchewin.common.statuseffect;

import dev.sterner.brewinandchewin.common.registry.BCStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.random.Random;

public class TipsyEffect extends StatusEffect {
    public TipsyEffect(StatusEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        StatusEffectInstance effect = entity.getStatusEffect(BCStatusEffects.TIPSY);
        if(effect != null ){
            if (effect.getAmplifier() > 1) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, effect.getDuration(), 0));
            }
            if (effect.getAmplifier() > 8) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, effect.getDuration(), 0));
            }

            if (effect.getAmplifier() > 3) {
                Random rand = entity.getWorld().getRandom();
                float amount = rand.nextFloat() * (0.06F + (0.01F * effect.getAmplifier()));
                float x = 0.0F;
                float z = 0.0F;
                if (rand.nextBoolean())
                    amount *= -1;
                if (rand.nextBoolean())
                    x = amount;
                else
                    z = amount;
                entity.setVelocity(entity.getVelocity().add(x, 0.0F, z ));
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
