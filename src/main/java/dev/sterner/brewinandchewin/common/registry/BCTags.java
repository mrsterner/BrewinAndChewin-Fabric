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

    public static final TagKey<Block> HOT_BLOCK = TagKey.of(Registry.BLOCK_KEY, new Identifier(BrewinAndChewin.MODID, "hot_blocks"));
    public static final TagKey<Block> COLD_BLOCK = TagKey.of(Registry.BLOCK_KEY, new Identifier(BrewinAndChewin.MODID, "cold_blocks"));

    public static final TagKey<Item> RAW_FISHES = TagKey.of(Registry.ITEM_KEY, new Identifier("c", "raw_fishes"));
    public static final TagKey<Item> VEGETABLES = TagKey.of(Registry.ITEM_KEY, new Identifier("c", "vegetables"));
}
