package dev.sterner.brewinandchewin.common.item;

import dev.sterner.brewinandchewin.common.registry.BCStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DreadNogItem extends BoozeItem{
    public DreadNogItem(int potency, int duration, Settings settings) {
        super(potency, duration, settings);
    }

    @Override
    public void affectConsumer(ItemStack stack, World level, LivingEntity consumer) {
        StatusEffectInstance badOmenEffect = consumer.getStatusEffect(StatusEffects.BAD_OMEN);
        if (!consumer.hasStatusEffect(StatusEffects.BAD_OMEN)) {
            consumer.addStatusEffect(new StatusEffectInstance(StatusEffects.BAD_OMEN, 12000, 0), consumer);
        }
        else if (badOmenEffect != null && badOmenEffect.getAmplifier() < 2) {
            consumer.addStatusEffect(new StatusEffectInstance(StatusEffects.BAD_OMEN, 12000, badOmenEffect.getAmplifier() + 1), consumer);
        }
        if (consumer.hasStatusEffect(BCStatusEffects.TIPSY)) {
            StatusEffectInstance tipsyEffect = consumer.getStatusEffect(BCStatusEffects.TIPSY);
            if (tipsyEffect != null) {
                consumer.addStatusEffect(new StatusEffectInstance(BCStatusEffects.TIPSY, tipsyEffect.getDuration() + (duration * 600), tipsyEffect.getAmplifier() + potency), consumer);
            }
        } else if (!consumer.hasStatusEffect(BCStatusEffects.TIPSY)) {
            consumer.addStatusEffect(new StatusEffectInstance(BCStatusEffects.TIPSY, duration * 1200, potency - 1), consumer);
        }
    }
}
