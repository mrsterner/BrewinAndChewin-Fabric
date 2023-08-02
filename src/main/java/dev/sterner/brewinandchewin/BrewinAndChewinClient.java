package dev.sterner.brewinandchewin;

import dev.sterner.brewinandchewin.client.renderer.FermentationControllerBlockEntityRenderer;
import dev.sterner.brewinandchewin.client.renderer.ItemCoasterRenderer;
import dev.sterner.brewinandchewin.client.screen.KegScreen;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import dev.sterner.brewinandchewin.common.registry.BCScreenHandlerTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

import static net.minecraft.client.texture.SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;

public class BrewinAndChewinClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(BCBlockEntityTypes.COASTER, ItemCoasterRenderer::new);
        BlockEntityRendererFactories.register(BCBlockEntityTypes.FERMENTATION_CONTROLLER, FermentationControllerBlockEntityRenderer::new);
        HandledScreens.register(BCScreenHandlerTypes.KEG_SCREEN_HANDLER, KegScreen::new);

        ClientSpriteRegistryCallback.event(BLOCK_ATLAS_TEXTURE).register((atlas, registry) -> {
            registry.register(new Identifier("brewinandchewin", "item/empty_container_slot_mug"));
        });
        EntityModelLayerRegistry.registerModelLayer(FermentationControllerBlockEntityRenderer.Indicator.LAYER, FermentationControllerBlockEntityRenderer.Indicator::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(FermentationControllerBlockEntityRenderer.Indicator.LAYER_SMALL, FermentationControllerBlockEntityRenderer.Indicator::getTexturedModelDataSmall);
    }
}
