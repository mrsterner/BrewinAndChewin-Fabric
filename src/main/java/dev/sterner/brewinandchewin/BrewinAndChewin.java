package dev.sterner.brewinandchewin;

import com.nhoryzon.mc.farmersdelight.item.ConsumableItem;
import dev.sterner.brewinandchewin.common.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BrewinAndChewin implements ModInitializer {
    public static final String MODID = "brewinandchewin";
    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MODID, "main"));

    public static final Logger LOGGER = LogManager.getLogger();
    public static final boolean DEBUG_MODE = false;

    @Override
    public void onInitialize() {
        BCObjects.init();
        BCBlockEntityTypes.init();
        BCStatusEffects.init();
        BCRecipeTypes.init();
        BCScreenHandlerTypes.init();
        BCLootFunctionsRegistry.init();

        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(BCObjects.BEER))
                .displayName(Text.translatable(MODID + ".group.main"))
                .build());
    }
}
