package dev.sterner.brewinandchewin;

import dev.sterner.brewinandchewin.client.renderer.ItemCoasterRenderer;
import dev.sterner.brewinandchewin.client.screen.KegScreen;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import dev.sterner.brewinandchewin.common.registry.BCScreenHandlerTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.Identifier;

import static net.minecraft.client.texture.SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;

public class BrewinAndChewinClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(BCBlockEntityTypes.COASTER, ItemCoasterRenderer::new);
        HandledScreens.register(BCScreenHandlerTypes.KEG_SCREEN_HANDLER, KegScreen::new);

        ClientSpriteRegistryCallback.event(BLOCK_ATLAS_TEXTURE).register((atlas, registry) -> {
            registry.register(new Identifier("brewinandchewin", "item/empty_container_slot_mug"));
        });
    }
}
