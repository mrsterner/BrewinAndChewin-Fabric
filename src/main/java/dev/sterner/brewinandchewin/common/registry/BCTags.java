package dev.sterner.brewinandchewin.common.registry;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BCTags {
    public static final TagKey<Item> RAW_MEATS = TagKey.of(Registry.ITEM_KEY, new Identifier(BrewinAndChewin.MODID, "raw_meats"));
    public static final TagKey<Item> HORROR_LASAGNA_MEATS = TagKey.of(Registry.ITEM_KEY, new Identifier(BrewinAndChewin.MODID,"horror_lasagna_meats"));
    public static final TagKey<Block> FREEZE_SOURCES = TagKey.of(Registry.BLOCK_KEY, new Identifier(BrewinAndChewin.MODID,"freeze_sources"));

    public static final TagKey<Item> RAW_FISHES = TagKey.of(Registry.ITEM_KEY, new Identifier("c", "raw_fishes"));
    public static final TagKey<Item> VEGETABLES = TagKey.of(Registry.ITEM_KEY, new Identifier("c", "vegetables"));
}
