package dev.sterner.brewinandchewin.common.registry;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.screen.KegBlockScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BCScreenHandlerTypes {
    public static final ExtendedScreenHandlerType<KegBlockScreenHandler> KEG_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(KegBlockScreenHandler::new);


    public static void init() {
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(BrewinAndChewin.MODID, "keg_screen"), KEG_SCREEN_HANDLER);
    }
}
