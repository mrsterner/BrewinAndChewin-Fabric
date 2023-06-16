package dev.sterner.brewinandchewin.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.screen.KegBlockScreenHandler;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KegScreen extends HandledScreen<KegBlockScreenHandler> implements RecipeBookProvider {
    private static final Identifier RECIPE_BUTTON_LOCATION = new Identifier("textures/gui/recipe_button.png");

    private static final Identifier BACKGROUND_TEXTURE = new Identifier(BrewinAndChewin.MODID, "textures/gui/keg.png");
    private static final Rectangle PROGRESS_ARROW = new Rectangle(72, 44, 0, 9);
    private static final Rectangle FRIGID_BAR = new Rectangle(72, 39, 6, 4);
    private static final Rectangle COLD_BAR = new Rectangle(78, 39, 7, 4);
    private static final Rectangle WARM_BAR = new Rectangle(91, 39, 7, 4);
    private static final Rectangle HOT_BAR = new Rectangle(98, 39, 7, 4);

    private static final Rectangle BUBBLE_1 = new Rectangle(69, 14, 9, 24);
    private static final Rectangle BUBBLE_2 = new Rectangle(98, 14, 9, 24);
    private static final int[] BUBBLELENGTHS = new int[]{24, 20, 16, 12, 8, 4, 0};
    public final KegRecipeBookComponent recipeBookComponent = new KegRecipeBookComponent();
    private boolean widthTooNarrow;
    private boolean open;
    private boolean mouseDown;

    public KegScreen(KegBlockScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = 27;
        this.titleY = 17;

        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.initialize(this.width, this.height, this.client, this.widthTooNarrow, this.handler);
        this.open = true;
        this.x = this.recipeBookComponent.findLeftEdge(this.width, this.backgroundWidth);
        this.addDrawableChild(new TexturedButtonWidget(this.x + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, new Identifier("textures/gui/recipe_button.png"), button -> {
            this.recipeBookComponent.toggleOpen();
            this.x = this.recipeBookComponent.findLeftEdge(this.width, this.backgroundWidth);
            ((TexturedButtonWidget) button).setPos(this.x + 5, this.height / 2 - 49);
            this.mouseDown = true;
        }));
        this.addSelectableChild(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        this.recipeBookComponent.update();
    }


    @Override
    public void render(MatrixStack ms, final int mouseX, final int mouseY, float partialTicks) {
        this.renderBackground(ms);
        if (this.recipeBookComponent.isOpen() && this.widthTooNarrow) {
            this.drawBackground(ms, partialTicks, mouseX, mouseY);
            this.recipeBookComponent.render(ms, mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookComponent.render(ms, mouseX, mouseY, partialTicks);
            super.render(ms, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.drawGhostSlots(ms, this.x, this.y, false, partialTicks);
        }
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTemperatureTooltip(ms, mouseX, mouseY);
        this.renderMealDisplayTooltip(ms, mouseX, mouseY);
    }

    private void renderTemperatureTooltip(MatrixStack ms, int mouseX, int mouseY) {
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
            this.renderTooltip(ms, tooltip, mouseX, mouseY);
        }
    }

    protected void renderMealDisplayTooltip(MatrixStack ms, int mouseX, int mouseY) {
        if (this.client != null && this.client.player != null && this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (this.focusedSlot.id == 5) {
                List<Text> tooltip = new ArrayList<>();

                ItemStack mealStack = this.focusedSlot.getStack();
                tooltip.add(((MutableText) mealStack.getName()).formatted(mealStack.getRarity().formatting));

                ItemStack containerStack = this.handler.blockEntity.getContainer();
                String container = !containerStack.isEmpty() ? containerStack.getItem().getName().getString() : "";

                tooltip.add(BCTextUtils.getTranslation("container.keg.served_in", container).formatted(Formatting.GRAY));

                this.renderTooltip(ms, tooltip, mouseX, mouseY);
            } else {
                this.renderTooltip(ms, this.focusedSlot.getStack(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawForeground(MatrixStack ms, int mouseX, int mouseY) {
        super.drawForeground(ms, mouseX, mouseY);
        this.textRenderer.draw(ms, this.playerInventoryTitle, 8.0f, (float) (this.backgroundHeight - 96 + 2), 4210752);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.client == null) {
            return;
        }

        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);


        // Render progress arrow
        int l = this.handler.getFermentProgressionScaled();
        this.drawTexture(matrices, this.x + PROGRESS_ARROW.x, this.y + PROGRESS_ARROW.y, 176, 28, l + 1, PROGRESS_ARROW.height);

        int temp = this.handler.getTemperature();
        if (temp < -4 && temp > -9) {
            this.drawTexture(matrices, this.x + COLD_BAR.x, this.y + COLD_BAR.y, 182, 0, COLD_BAR.width, COLD_BAR.height);
        }
        if (temp < -8) {
            this.drawTexture(matrices, this.x + COLD_BAR.x, this.y + COLD_BAR.y, 182, 0, COLD_BAR.width, COLD_BAR.height);
            this.drawTexture(matrices, this.x + FRIGID_BAR.x, this.y + FRIGID_BAR.y, 176, 0, FRIGID_BAR.width, FRIGID_BAR.height);
        }
        if (temp > 4 && temp < 9) {
            this.drawTexture(matrices, this.x + WARM_BAR.x, this.y + WARM_BAR.y, 195, 0, WARM_BAR.width, WARM_BAR.height);
        }
        if (temp > 8) {
            this.drawTexture(matrices, this.x + WARM_BAR.x, this.y + WARM_BAR.y, 195, 0, WARM_BAR.width, WARM_BAR.height);
            this.drawTexture(matrices, this.x + HOT_BAR.x, this.y + HOT_BAR.y, 202, 0, HOT_BAR.width, HOT_BAR.height);
        }

        int i = this.handler.getFermentingTicks();
        if (i > 0) {
            int j;
            j = BUBBLELENGTHS[i / 5 % 7];
            this.drawTexture(matrices, this.x + BUBBLE_1.x, this.y + BUBBLE_1.y, 176, 4, BUBBLE_1.width, BUBBLE_1.height - j);
            this.drawTexture(matrices, this.x + BUBBLE_2.x, this.y + BUBBLE_2.y, 186, 4, BUBBLE_2.width, BUBBLE_2.height - j);
        }
    }

    @Override
    protected boolean isPointWithinBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isOpen()) && super.isPointWithinBounds(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        if (this.recipeBookComponent.mouseClicked(mouseX, mouseY, buttonId)) {
            this.setFocused(this.recipeBookComponent);
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookComponent.isOpen() || super.mouseClicked(mouseX, mouseY, buttonId);
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int buttonIdx) {
        boolean flag = mouseX < (double) x || mouseY < (double) y || mouseX >= (double) (x + this.backgroundWidth) || mouseY >= (double) (y + this.backgroundHeight);
        return flag && this.recipeBookComponent.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, buttonIdx);
    }

    @Override
    protected void onMouseClick(Slot slot, int mouseX, int mouseY, SlotActionType clickType) {
        super.onMouseClick(slot, mouseX, mouseY, clickType);
        this.recipeBookComponent.slotClicked(slot);
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

    @Override
    public void refreshRecipeBook() {
        this.recipeBookComponent.refresh();
    }

    @Override
    public void removed() {
        if (this.open) {
            this.recipeBookComponent.close();
        }

        super.removed();
    }

    @Override
    public void close() {
        this.recipeBookComponent.close();
        super.close();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBookComponent;
    }
}
