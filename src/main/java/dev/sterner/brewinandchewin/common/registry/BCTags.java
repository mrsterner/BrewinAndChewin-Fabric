package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BCTags {
    public static final TagKey<Item> RAW_MEATS = TagKey.of(RegistryKeys.ITEM, new Identifier(BrewinAndChewin.MODID, "raw_meats"));
    public static final TagKey<Item> HORROR_LASAGNA_MEATS = TagKey.of(RegistryKeys.ITEM, new Identifier(BrewinAndChewin.MODID, "horror_lasagna_meats"));
    public static final TagKey<Item> OFFHAND_EQUIPMENT = TagKey.of(RegistryKeys.ITEM, new Identifier(BrewinAndChewin.MODID, "offhand_equipment"));
    public static final TagKey<Block> FREEZE_SOURCES = TagKey.of(RegistryKeys.BLOCK, new Identifier(BrewinAndChewin.MODID, "freeze_sources"));

    public static final TagKey<Item> RAW_FISHES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "raw_fishes"));
    public static final TagKey<Item> VEGETABLES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "vegetables"));
}
