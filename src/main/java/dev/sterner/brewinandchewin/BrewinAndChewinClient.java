package dev.sterner.brewinandchewin;

import dev.sterner.brewinandchewin.client.renderer.FermentationControllerBlockEntityRenderer;
import dev.sterner.brewinandchewin.client.renderer.ItemCoasterRenderer;
import dev.sterner.brewinandchewin.client.renderer.TankardBlockEntityRenderer;
import dev.sterner.brewinandchewin.client.screen.KegScreen;
import dev.sterner.brewinandchewin.common.block.TankardBlock;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCScreenHandlerTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class BrewinAndChewinClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(BCBlockEntityTypes.COASTER, ItemCoasterRenderer::new);
        BlockEntityRendererFactories.register(BCBlockEntityTypes.FERMENTATION_CONTROLLER, FermentationControllerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BCBlockEntityTypes.TANKARD, TankardBlockEntityRenderer::new);
        HandledScreens.register(BCScreenHandlerTypes.KEG_SCREEN_HANDLER, KegScreen::new);
        EntityModelLayerRegistry.registerModelLayer(FermentationControllerBlockEntityRenderer.Indicator.LAYER, FermentationControllerBlockEntityRenderer.Indicator::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(FermentationControllerBlockEntityRenderer.Indicator.LAYER_SMALL, FermentationControllerBlockEntityRenderer.Indicator::getTexturedModelDataSmall);

        for (Block block : BCObjects.BLOCKS.keySet()) {
            if (block instanceof TankardBlock) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
            }
        }
    }
}
