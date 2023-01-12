package dev.sterner.brewinandchewin.common.item;

import com.nhoryzon.mc.farmersdelight.item.DrinkableItem;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCStatusEffects;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoozeItem extends DrinkableItem {
    protected final int potency;
    protected final int duration;

    public BoozeItem(int potency, int duration, Settings settings) {
        super(settings);
        this.potency = potency;
        this.duration = duration;
    }

    @Override
    public void affectConsumer(ItemStack stack, World world, LivingEntity user) {
        if (user.hasStatusEffect(BCStatusEffects.TIPSY)) {
            StatusEffectInstance effect = user.getStatusEffect(BCStatusEffects.TIPSY);
            if(effect != null){
                if (effect.getAmplifier() == 8) {
                    user.addStatusEffect(new StatusEffectInstance(BCStatusEffects.TIPSY, effect.getDuration() + (duration * 600), effect.getAmplifier() + 1), user);
                }
                if (effect.getAmplifier() == 7 && potency > 2) {
                    user.addStatusEffect(new StatusEffectInstance(BCStatusEffects.TIPSY, effect.getDuration() + (duration * 600), effect.getAmplifier() + 2), user);
                }
                if (effect.getAmplifier() < 9) {
                    user.addStatusEffect(new StatusEffectInstance(BCStatusEffects.TIPSY, effect.getDuration() + (duration * 600), effect.getAmplifier() + potency), user);
                }
                if (effect.getAmplifier() == 9) {
                    user.addStatusEffect(new StatusEffectInstance(BCStatusEffects.TIPSY, effect.getDuration() + (duration * 600), effect.getAmplifier()), user);
                }
            }
        } else if (!user.hasStatusEffect(BCStatusEffects.TIPSY)) {
            user.addStatusEffect(new StatusEffectInstance(BCStatusEffects.TIPSY, duration * 1200, potency - 1), user);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (potency == 1) {
            MutableText textTipsy = BCTextUtils.getTranslation("tooltip.tipsy1", duration);
            tooltip.add(textTipsy.formatted(Formatting.RED));
        }
        if (potency == 2) {
            MutableText textTipsy = BCTextUtils.getTranslation("tooltip.tipsy2", duration);
            tooltip.add(textTipsy.formatted(Formatting.RED));
        }
        if (potency == 3) {
            MutableText textTipsy = BCTextUtils.getTranslation("tooltip.tipsy3", duration);
            tooltip.add(textTipsy.formatted(Formatting.RED));
        }
        BCTextUtils.addFoodEffectTooltip(stack, tooltip, 1.0F);
        if (stack.isItemEqual(new ItemStack(BCObjects.DREAD_NOG))) {
            MutableText textEmpty = BCTextUtils.getTranslation("tooltip." + this);
            tooltip.add(textEmpty.formatted(Formatting.RED));
        }
    }
}
