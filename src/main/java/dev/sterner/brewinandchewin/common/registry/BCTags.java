package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface BCTags {
    TagKey<Item> RAW_MEATS = TagKey.of(RegistryKeys.ITEM, new Identifier(BrewinAndChewin.MODID, "raw_meats"));
    TagKey<Item> HORROR_LASAGNA_MEATS = TagKey.of(RegistryKeys.ITEM, new Identifier(BrewinAndChewin.MODID, "horror_lasagna_meats"));
    TagKey<Item> OFFHAND_EQUIPMENT = TagKey.of(RegistryKeys.ITEM, new Identifier(BrewinAndChewin.MODID, "offhand_equipment"));
    TagKey<Block> FREEZE_SOURCES = TagKey.of(RegistryKeys.BLOCK, new Identifier(BrewinAndChewin.MODID, "freeze_sources"));

    TagKey<Item> RAW_FISHES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "raw_fishes"));
    TagKey<Item> VEGETABLES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "vegetables"));
}
