package dev.sterner.brewinandchewin.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.screen.KegBlockScreenHandler;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KegScreen extends HandledScreen<KegBlockScreenHandler> {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(BrewinAndChewin.MODID, "textures/gui/keg.png");
    private static final Rectangle PROGRESS_ARROW = new Rectangle(72, 44, 0, 9);
    private static final Rectangle FRIGID_BAR = new Rectangle(72, 39, 6, 4);
    private static final Rectangle COLD_BAR = new Rectangle(78, 39, 7, 4);
    private static final Rectangle WARM_BAR = new Rectangle(91, 39, 7, 4);
    private static final Rectangle HOT_BAR = new Rectangle(98, 39, 7, 4);

    private static final Rectangle BUBBLE_1 = new Rectangle(69, 14, 9, 24);
    private static final Rectangle BUBBLE_2 = new Rectangle(98, 14, 9, 24);
    private static final int[] BUBBLELENGTHS = new int[]{24, 20, 16, 12, 8, 4, 0};
    private boolean mouseDown;

    public KegScreen(KegBlockScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = 27;
        this.titleY = 17;
    }

    @Override
    public void render(MatrixStack context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.renderTemperatureTooltip(context, mouseX, mouseY);
        this.renderMealDisplayTooltip(context, mouseX, mouseY);
    }

    private void renderTemperatureTooltip(MatrixStack ctx, int mouseX, int mouseY) {
        if (this.isPointWithinBounds(77, 39, 33, 4, mouseX, mouseY)) {
            List<Text> tooltip = new ArrayList<>();
            MutableText key = null;
            int i = this.handler.getTemperature();
            if (i < -8) {
                key = BCTextUtils.getTranslation("container.keg.frigid");
            }
            if (i < -4 && i > -9) {
                key = BCTextUtils.getTranslation("container.keg.cold");
            }
            if (i < 5 && i > -5) {
                key = BCTextUtils.getTranslation("container.keg.normal");
            }
            if (i > 4 && i < 9) {
                key = BCTextUtils.getTranslation("container.keg.warm");
            }
            if (i > 8) {
                key = BCTextUtils.getTranslation("container.keg.hot");
            }
            tooltip.add(key);

            this.renderTooltip(ctx, tooltip, mouseX, mouseY);
        }
    }

    protected void renderMealDisplayTooltip(MatrixStack ctx, int mouseX, int mouseY) {
        if (this.client != null && this.client.player != null && this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (this.focusedSlot.id == 5) {
                List<Text> tooltip = new ArrayList<>();

                ItemStack mealStack = this.focusedSlot.getStack();
                tooltip.add(((MutableText) mealStack.getName()).formatted(mealStack.getRarity().formatting));

                ItemStack containerStack = this.handler.blockEntity.getContainer();
                String container = !containerStack.isEmpty() ? containerStack.getItem().getName().getString() : "";

                tooltip.add(BCTextUtils.getTranslation("container.keg.served_in", container).formatted(Formatting.GRAY));

                this.renderTooltip(ctx, tooltip, mouseX, mouseY);
            } else {
                this.renderTooltip(ctx, this.focusedSlot.getStack(), mouseX, mouseY);

            }
        }
    }

    @Override
    protected void drawForeground(MatrixStack context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        this.textRenderer.draw(context, this.playerInventoryTitle, 8.0f, (float) (this.backgroundHeight - 96 + 2), 4210752);
    }


    @Override
    protected void drawBackground(MatrixStack context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.client == null) {
            return;
        }
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        this.drawTexture(context, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        // Render progress arrow
        int l = this.handler.getFermentProgressionScaled();
        this.drawTexture(context, this.x + PROGRESS_ARROW.x, this.y + PROGRESS_ARROW.y, 176, 28, l + 1, PROGRESS_ARROW.height);

        int temp = this.handler.getTemperature();
        if (temp < -4 && temp > -9) {
            this.drawTexture(context, this.x + COLD_BAR.x, this.y + COLD_BAR.y, 182, 0, COLD_BAR.width, COLD_BAR.height);
        }
        if (temp < -8) {
            this.drawTexture(context, this.x + COLD_BAR.x, this.y + COLD_BAR.y, 182, 0, COLD_BAR.width, COLD_BAR.height);
            this.drawTexture(context, this.x + FRIGID_BAR.x, this.y + FRIGID_BAR.y, 176, 0, FRIGID_BAR.width, FRIGID_BAR.height);
        }
        if (temp > 4 && temp < 9) {
            this.drawTexture(context, this.x + WARM_BAR.x, this.y + WARM_BAR.y, 195, 0, WARM_BAR.width, WARM_BAR.height);
        }
        if (temp > 8) {
            this.drawTexture(context, this.x + WARM_BAR.x, this.y + WARM_BAR.y, 195, 0, WARM_BAR.width, WARM_BAR.height);
            this.drawTexture(context, this.x + HOT_BAR.x, this.y + HOT_BAR.y, 202, 0, HOT_BAR.width, HOT_BAR.height);
        }

        int i = this.handler.getFermentingTicks();
        if (i > 0) {
            int j;
            j = BUBBLELENGTHS[i / 5 % 7];
            this.drawTexture(context, this.x + BUBBLE_1.x, this.y + BUBBLE_1.y, 176, 4, BUBBLE_1.width, BUBBLE_1.height - j);
            this.drawTexture(context, this.x + BUBBLE_2.x, this.y + BUBBLE_2.y, 186, 4, BUBBLE_2.width, BUBBLE_2.height - j);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.mouseDown) {
            this.mouseDown = false;
            return true;
        } else {
            return super.mouseReleased(mouseX, mouseY, button);
        }
    }
}
