package dev.sterner.brewinandchewin.client.renderer;

import dev.sterner.brewinandchewin.common.block.entity.ItemCoasterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ItemCoasterRenderer implements BlockEntityRenderer<ItemCoasterBlockEntity> {
    public ItemCoasterRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(ItemCoasterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack boardStack = entity.getStoredItem();
        int posLong = (int) entity.getPos().asLong();
        if (!boardStack.isEmpty() && entity.getWorld() != null) {
            matrices.push();
            matrices.translate(0.5, 0.3 + (double) (MathHelper.sin((float) entity.getWorld().getTime() / 50.0F) / 40.0F), 0.5);
            float f3 = 3.2F * ((float) entity.getWorld().getTime() + 1.0F) / 5.0F;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f3));
            matrices.scale(0.5F, 0.5F, 0.5F);
            MinecraftClient.getInstance().getItemRenderer().renderItem(boardStack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, entity.getWorld(), posLong);
            matrices.pop();
        }
    }
}
