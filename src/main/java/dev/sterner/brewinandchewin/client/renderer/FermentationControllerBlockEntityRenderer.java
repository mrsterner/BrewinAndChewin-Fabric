package dev.sterner.brewinandchewin.client.renderer;

import dev.sterner.brewinandchewin.BrewinAndChewin;
import dev.sterner.brewinandchewin.common.block.FermentationControllerBlock;
import dev.sterner.brewinandchewin.common.block.entity.FermentationControllerBlockEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;


public class FermentationControllerBlockEntityRenderer implements BlockEntityRenderer<FermentationControllerBlockEntity> {

    private final TextRenderer textRenderer;
    private final Indicator MODEL;
    private final Indicator MODEL_SMALL;
    private final Identifier TEXTURE = new Identifier(BrewinAndChewin.MODID, "textures/block/indicator.png");
    private final int MAGIC_OFFSET_NUMBER = 56;

    public FermentationControllerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
        this.MODEL = new Indicator(ctx.getLayerModelPart(Indicator.LAYER));
        this.MODEL_SMALL = new Indicator(ctx.getLayerModelPart(Indicator.LAYER_SMALL));
    }

    @Override
    public void render(FermentationControllerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (BrewinAndChewin.DEBUG_MODE) {
            renderDebugText(entity, matrices, vertexConsumers, light);
        }

        float xOffset = entity.getTemperature();
        float xOffsetTarget = entity.getTargetTemperature();

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getCachedState().get(HorizontalFacingBlock.FACING).asRotation()));


        if (!entity.getCachedState().get(FermentationControllerBlock.VERTICAL)) {
            matrices.push();

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

            renderIndicator(matrices, vertexConsumers, xOffset, xOffsetTarget, overlay);
            matrices.pop();
        } else {
            matrices.push();
            renderIndicator(matrices, vertexConsumers, xOffset, xOffsetTarget, overlay);
            matrices.pop();
        }

        matrices.pop();
    }

    private void renderIndicator(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float xOffset, float xOffsetTarget, int overlay) {
        matrices.translate(xOffset / MAGIC_OFFSET_NUMBER, -((double) (7 + 16) / 16), 0.501);
        MODEL.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE)), 15728880, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.translate((xOffsetTarget / MAGIC_OFFSET_NUMBER) - xOffset / MAGIC_OFFSET_NUMBER, -((double) 5 / 16), 0);
        MODEL_SMALL.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE)), 15728880, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderDebugText(FermentationControllerBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        setTextAngles(matrices, new Vec3d(0, 2, 0));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getCachedState().get(HorizontalFacingBlock.FACING).asRotation()));

        var orderedText = Text.translatable(entity.getTargetTemperature() + " : " + entity.getTemperature());
        float f = (float) (-this.textRenderer.getWidth(orderedText) / 2);

        this.textRenderer.draw(orderedText, f, 1, 0, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.POLYGON_OFFSET, 0, light);
        matrices.pop();
    }

    private void setTextAngles(MatrixStack matrices, Vec3d translation) {
        float f = 0.05F;
        matrices.translate(translation.x, translation.y, translation.z);
        matrices.scale(f, -f, f);
    }

    public static class Indicator extends EntityModel<Entity> {
        public static final EntityModelLayer LAYER = new EntityModelLayer(new Identifier(BrewinAndChewin.MODID, "indicator"), "main");
        public static final EntityModelLayer LAYER_SMALL = new EntityModelLayer(new Identifier(BrewinAndChewin.MODID, "indicator_small"), "main");
        private final ModelPart main;

        public Indicator(ModelPart root) {
            this.main = root.getChild("main");
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-0.4F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
            return TexturedModelData.of(modelData, 16, 16);
        }

        public static TexturedModelData getTexturedModelDataSmall() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-0.4F, -2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
            return TexturedModelData.of(modelData, 16, 16);
        }

        @Override
        public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }
}
