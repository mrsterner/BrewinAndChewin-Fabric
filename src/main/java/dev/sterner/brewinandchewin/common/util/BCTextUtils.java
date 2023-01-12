package dev.sterner.brewinandchewin.common.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;

public class BCTextUtils
{
    private static final MutableText NO_EFFECTS = (new TranslatableText("effect.none")).formatted(Formatting.GRAY);

    /**
     * Syntactic sugar for custom translation keys. Always prefixed with the mod's ID in lang files (e.g. farmersdelight.your.key.here).
     */
    public static MutableText getTranslation(String key, Object... args) {
        return new TranslatableText(BrewinAndChewin.MODID + "." + key, args);
    }

    /**
     * An alternate version of PotionUtils.addPotionTooltip, that obtains the item's food-property potion effects instead.
     */
    @Environment(EnvType.CLIENT)
    public static void addFoodEffectTooltip(ItemStack itemIn, List<Text> lores, float durationFactor) {
        FoodComponent foodStats = itemIn.getItem().getFoodComponent();
        if (foodStats == null) {
            return;
        }
        List<Pair<StatusEffectInstance, Float>> effectList = foodStats.getStatusEffects();
        List<Pair<EntityAttribute, EntityAttributeModifier>> attributeList = Lists.newArrayList();
        if (effectList.isEmpty()) {
            lores.add(NO_EFFECTS);
        } else {
            for (Pair<StatusEffectInstance, Float> effectPair : effectList) {
                StatusEffectInstance instance = effectPair.getFirst();
                MutableText iformattabletextcomponent = new TranslatableText(instance.getTranslationKey());
                StatusEffect effect = instance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> attributeMap = effect.getAttributeModifiers();
                if (!attributeMap.isEmpty()) {
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : attributeMap.entrySet()) {
                        EntityAttributeModifier rawModifier = entry.getValue();
                        EntityAttributeModifier modifier = new EntityAttributeModifier(rawModifier.getName(), effect.adjustModifierAmount(instance.getAmplifier(), rawModifier), rawModifier.getOperation());
                        attributeList.add(new Pair<>(entry.getKey(), modifier));
                    }
                }

                if (instance.getAmplifier() > 0) {
                    iformattabletextcomponent = new TranslatableText("potion.withAmplifier", iformattabletextcomponent, new TranslatableText("potion.potency." + instance.getAmplifier()));
                }

                if (instance.getDuration() > 20) {
                    iformattabletextcomponent = new TranslatableText("potion.withDuration", iformattabletextcomponent, StatusEffectUtil.durationToString(instance, durationFactor));
                }

                lores.add(iformattabletextcomponent.formatted(effect.getCategory().getFormatting()));
            }
        }

        if (!attributeList.isEmpty()) {
            lores.add(Text.of(""));
            lores.add((new TranslatableText("potion.whenDrank")).formatted(Formatting.DARK_PURPLE));

            for (Pair<EntityAttribute, EntityAttributeModifier> pair : attributeList) {
                EntityAttributeModifier modifier = pair.getSecond();
                double amount = modifier.getValue();
                double formattedAmount;
                if (modifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && modifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                    formattedAmount = modifier.getValue();
                } else {
                    formattedAmount = modifier.getValue() * 100.0D;
                }

                if (amount > 0.0D) {
                    lores.add((new TranslatableText("attribute.modifier.plus." + modifier.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(formattedAmount), new TranslatableText(pair.getFirst().getTranslationKey()))).formatted(Formatting.BLUE));
                } else if (amount < 0.0D) {
                    formattedAmount = formattedAmount * -1.0D;
                    lores.add((new TranslatableText("attribute.modifier.take." + modifier.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(formattedAmount), new TranslatableText(pair.getFirst().getTranslationKey()))).formatted(Formatting.RED));
                }
            }
        }
    }
}
