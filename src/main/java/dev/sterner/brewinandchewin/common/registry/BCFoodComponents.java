package dev.sterner.brewinandchewin.common.registry;

import com.nhoryzon.mc.farmersdelight.registry.EffectsRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public interface BCFoodComponents {
    FoodComponent MEAD = new FoodComponent.Builder()
            .hunger(6).saturationModifier(0.1F)
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SWEET_HEART, 6000, 0), 1.0F).build();
    FoodComponent RICE_WINE = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SATISFACTION, 6000, 0), 1.0F).build();
    FoodComponent EGG_GROG = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 3600, 0), 1.0F).build();
    FoodComponent STRONGROOT_ALE = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0), 1.0F).build();
    FoodComponent SACCHARINE_RUM = new FoodComponent.Builder()
            .hunger(6).saturationModifier(0.1F)
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SWEET_HEART, 9600, 1), 1.0F).build();
    FoodComponent PALE_JANE = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SATISFACTION, 9600, 1), 1.0F).build();
    FoodComponent SALTY_FOLLY = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 9600, 0), 1.0F).build();
    FoodComponent STEEL_TOE_STOUT = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 12000, 1), 1.0F).build();
    FoodComponent GLITTERING_GRENADINE = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 12000, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 12000, 0), 1.0F).build();
    FoodComponent BLOODY_MARY = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(EffectsRegistry.COMFORT.get(), 6000, 0), 1.0F).build();
    FoodComponent RED_RUM = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(EffectsRegistry.COMFORT.get(), 9600, 1), 1.0F).build();
    FoodComponent WITHERING_DROSS = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 12000, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 12000, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 12000, 0), 0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.WITHER, 12000, 0), 1.0F).build();
    FoodComponent KOMBUHCA = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SATISFACTION, 3600, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 6000, 0), 1.0F).build();


    FoodComponent KIMCHI = new FoodComponent.Builder()
            .hunger(2).saturationModifier(0.6F)
            .statusEffect(new StatusEffectInstance(BCStatusEffects.SATISFACTION, 2400, 0), 1.0F).build();
    FoodComponent JERKY = new FoodComponent.Builder()
            .hunger(3).saturationModifier(0.7F).snack().build();
    FoodComponent PICKLED_PICKLES = new FoodComponent.Builder()
            .hunger(4).saturationModifier(0.3F)
            .statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 2400, 0), 1.0F)
            .statusEffect(new StatusEffectInstance(StatusEffects.LUCK, 6000, 0), 1.0F).build();
    FoodComponent KIPPERS = new FoodComponent.Builder()
            .hunger(6).saturationModifier(0.5F)
            .statusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 600, 0), 1.0F).build();
    FoodComponent COCOA_FUDGE = new FoodComponent.Builder()
            .hunger(4).saturationModifier(0.8F)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 0), 1.0F).build();

    FoodComponent FLAXEN_CHEESE = new FoodComponent.Builder()
            .hunger(4).saturationModifier(1.0F).build();
    FoodComponent SCARLET_CHEESE = new FoodComponent.Builder()
            .hunger(4).saturationModifier(1.0F).build();

    FoodComponent PIZZA_SLICE = new FoodComponent.Builder()
            .hunger(3).saturationModifier(1.0F).build();
    FoodComponent HAM_AND_CHEESE_SANDWICH = new FoodComponent.Builder()
            .hunger(9).saturationModifier(1.0F).build();

    //Bowl Foods

    FoodComponent QUICHE_SLICE = new FoodComponent.Builder()
            .hunger(3).saturationModifier(0.8F).snack().build();
    FoodComponent FIERY_FONDUE = new FoodComponent.Builder()
            .hunger(16).saturationModifier(1.0F)
            .statusEffect(new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), 7200, 0), 1.0F).build();

    FoodComponent VEGETABLE_OMELET = new FoodComponent.Builder()
            .hunger(12).saturationModifier(0.75F)
            .statusEffect(new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), 4800, 0), 1.0F).build();
    FoodComponent CHEESY_PASTA = new FoodComponent.Builder()
            .hunger(12).saturationModifier(0.8F)
            .statusEffect(new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), 4800, 0), 1.0F).build();

    FoodComponent CREAMY_ONION_SOUP = new FoodComponent.Builder()
            .hunger(12).saturationModifier(0.75F)
            .statusEffect(new StatusEffectInstance(EffectsRegistry.COMFORT.get(), 4800, 0), 1.0F).build();

    FoodComponent SCARLET_PIEROGIES = new FoodComponent.Builder()
            .hunger(14).saturationModifier(0.75F)
            .statusEffect(new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), 4800, 0), 1.0F).build();
    FoodComponent HORROR_LASAGNA = new FoodComponent.Builder()
            .hunger(14).saturationModifier(0.75F)
            .statusEffect(new StatusEffectInstance(EffectsRegistry.NOURISHMENT.get(), 7200, 0), 1.0F).build();
}
