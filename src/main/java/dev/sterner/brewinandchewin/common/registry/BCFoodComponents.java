package dev.sterner.brewinandchewin.common.registry;

import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class BCFoodComponents {
    public static final FoodComponent MEAD = (new FoodComponent.Builder())
            .hunger(6).saturationModifier(0.1F)
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SWEET_HEART, 6000, 0), 1.0F).build();
    public static final FoodComponent RICE_WINE = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SATISFACTION, 6000, 0), 1.0F).build();
    public static final FoodComponent EGG_GROG = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 3600, 0), 1.0F).build();
    public static final FoodComponent STRONGROOT_ALE = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0), 1.0F).build();
    public static final FoodComponent SACCHARINE_RUM = (new FoodComponent.Builder())
            .hunger(6).saturationModifier(0.1F)
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SWEET_HEART, 9600, 1), 1.0F).build();
    public static final FoodComponent PALE_JANE = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SATISFACTION, 9600, 1), 1.0F).build();
    public static final FoodComponent SALTY_FOLLY = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 9600, 0), 1.0F).build();
    public static final FoodComponent STEEL_TOE_STOUT = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 12000, 1), 1.0F).build();
    public static final FoodComponent GLITTERING_GRENADINE = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 12000, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 12000, 0), 1.0F).build();
    public static final FoodComponent BLOODY_MARY = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(EffectsRegistry.COMFORT.get(), 6000, 0), 1.0F).build();
    public static final FoodComponent RED_RUM = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(EffectsRegistry.COMFORT.get(), 9600, 1), 1.0F).build();
    public static final FoodComponent WITHERING_DROSS = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 12000, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 12000, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 12000, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.WITHER, 12000, 0), 1.0F).build();
    public static final FoodComponent KOMBUHCA = (new FoodComponent.Builder())
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SATISFACTION, 3600, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 6000, 0), 1.0F).build();


    public static final FoodComponent KIMCHI = (new FoodComponent.Builder())
            .hunger(2).saturationModifier(0.6F)
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SATISFACTION, 2400, 0), 1.0F).build();
    public static final FoodComponent JERKY = (new FoodComponent.Builder())
            .hunger(3).saturationModifier(0.7F).snack().build();
    public static final FoodComponent PICKLED_PICKLES = (new FoodComponent.Builder())
            .hunger(4).saturationModifier(0.3F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 2400, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.LUCK, 6000, 0), 1.0F).build();
    public static final FoodComponent KIPPERS = (new FoodComponent.Builder())
            .hunger(6).saturationModifier(0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 600, 0), 1.0F).build();
    public static final FoodComponent COCOA_FUDGE = (new FoodComponent.Builder())
            .hunger(4).saturationModifier(0.8F)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0), 1.0F).build();

    public static final FoodComponent FLAXEN_CHEESE = (new FoodComponent.Builder())
            .hunger(4).saturationModifier(1.0F).build();
    public static final FoodComponent SCARLET_CHEESE = (new FoodComponent.Builder())
            .hunger(4).saturationModifier(1.0F).build();

    public static final FoodComponent PIZZA_SLICE = (new FoodComponent.Builder())
            .hunger(3).saturationModifier(1.0F).build();
    public static final FoodComponent HAM_AND_CHEESE_SANDWICH = (new FoodComponent.Builder())
            .hunger(9).saturationModifier(1.0F).build();

    //Bowl Foods
    public static final FoodComponent FIERY_FONDUE = (new FoodComponent.Builder())
            .hunger(10).saturationModifier(1.0f)
            .statusEffect(new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), 3600, 0), 1.0F).build();
}
