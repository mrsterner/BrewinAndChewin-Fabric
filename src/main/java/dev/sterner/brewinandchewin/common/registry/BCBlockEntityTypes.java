package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.entity.ItemCoasterBlockEntity;
import dev.sterner.brewinandchewin.common.block.entity.KegBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class BCBlockEntityTypes {
    private static final Map<BlockEntityType<?>, Identifier> BLOCK_ENTITY_TYPES = new LinkedHashMap<>();

    public static final BlockEntityType<KegBlockEntity> KEG = register("keg", FabricBlockEntityTypeBuilder.create(KegBlockEntity::new, BCObjects.KEG).build());

    public static final BlockEntityType<ItemCoasterBlockEntity> COASTER = register("coaster", FabricBlockEntityTypeBuilder.create(ItemCoasterBlockEntity::new, BCObjects.COASTER).build());


    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        BLOCK_ENTITY_TYPES.put(type, new Identifier(BrewinAndChewin.MODID, name));
        return type;
    }

    public static void init() {
        BLOCK_ENTITY_TYPES.keySet().forEach(blockEntityType -> Registry.register(Registry.BLOCK_ENTITY_TYPE, BLOCK_ENTITY_TYPES.get(blockEntityType), blockEntityType));
    }
}
