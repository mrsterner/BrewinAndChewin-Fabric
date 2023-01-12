package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.statuseffect.EmptyStatusEffect;
import dev.sterner.brewinandchewin.common.statuseffect.SatisfactionEffect;
import dev.sterner.brewinandchewin.common.statuseffect.TipsyEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class BCStatusEffects {
    public static final Map<StatusEffect, Identifier> STATUS_EFFECTS = new LinkedHashMap<>();

    public static final StatusEffect TIPSY = create("tipsy", new TipsyEffect(StatusEffectCategory.BENEFICIAL, 0x605448));
    public static final StatusEffect SWEET_HEART = create("sweet_heart", new EmptyStatusEffect(StatusEffectCategory.BENEFICIAL, 0x605448));
    public static final StatusEffect SATISFACTION = create("satisfaction", new SatisfactionEffect(StatusEffectCategory.BENEFICIAL, 0x605448));

    private static <T extends StatusEffect> T create(String name, T effect) {
        STATUS_EFFECTS.put(effect, new Identifier(BrewinAndChewin.MODID, name));
        return effect;
    }

    public static void init() {
        STATUS_EFFECTS.keySet().forEach(effect -> Registry.register(Registry.STATUS_EFFECT, STATUS_EFFECTS.get(effect), effect));
    }
}
