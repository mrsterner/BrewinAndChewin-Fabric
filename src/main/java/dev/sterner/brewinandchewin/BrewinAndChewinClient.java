package dev.sterner.brewinandchewin;

import dev.sterner.brewinandchewin.client.renderer.ItemCoasterRenderer;
import dev.sterner.brewinandchewin.client.screen.KegScreen;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import dev.sterner.brewinandchewin.common.registry.BCScreenHandlerTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class BrewinAndChewinClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(BCBlockEntityTypes.COASTER, ItemCoasterRenderer::new);
        HandledScreens.register(BCScreenHandlerTypes.KEG_SCREEN_HANDLER, KegScreen::new);
    }
}
