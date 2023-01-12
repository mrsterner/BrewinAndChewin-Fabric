package dev.sterner.brewinandchewin.common.statuseffect;

import dev.sterner.brewinandchewin.common.registry.BCStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;

public class SatisfactionEffect extends StatusEffect {
    public SatisfactionEffect(StatusEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.hasStatusEffect(BCStatusEffects.SATISFACTION) && entity instanceof PlayerEntity player) {
            HungerManager foodData = player.getHungerManager();
            if (foodData.getFoodLevel() < 20) {
                foodData.setFoodLevel(foodData.getFoodLevel() + 1);
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int k = 400 >> amplifier;
        if (k > 0) {
            return duration % k == 0;
        } else {
            return true;
        }
    }
}
