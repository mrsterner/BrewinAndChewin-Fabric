package dev.sterner.brewinandchewin.client.renderer;

import com.google.common.collect.Maps;
import dev.sterner.brewinandchewin.common.block.TankardBlock;
import dev.sterner.brewinandchewin.common.block.entity.TankardBlockEntity;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.util.RotationPropertyHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;

import java.util.Map;

public class TankardBlockEntityRenderer implements BlockEntityRenderer<TankardBlockEntity> {
    private final BlockRenderManager blockRenderer;

    private Map<Item, BlockState> itemBlockStateMap() {
        return Util.make(Maps.newHashMap(), map -> {
            map.put(BCObjects.BEER, BCObjects.BEER_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.BLOODY_MARY, BCObjects.BLOODY_MARY_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.EGG_GROG, BCObjects.EGG_GROG_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.GLITTERING_GRENADINE, BCObjects.GLITTERING_GRENADINE_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.MEAD, BCObjects.MEAD_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.PALE_JANE, BCObjects.PALE_JANE_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.RED_RUM, BCObjects.RED_RUM_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.RICE_WINE, BCObjects.RICE_WINE_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.SACCHARINE_RUM, BCObjects.SACCHARINE_RUM_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.SALTY_FOLLY, BCObjects.SALTY_FOLLY_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.STEEL_TOE_STOUT, BCObjects.STEEL_TOE_STOUT_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.STRONGROOT_ALE, BCObjects.STRONGROT_ALE_TANKARD_BLOCK.getDefaultState());

            map.put(BCObjects.WITHERING_DROSS, BCObjects.WITHERING_DROSS_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.KOMBUCHA, BCObjects.KOMBUCHA_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.VODKA, BCObjects.VODKA_TANKARD_BLOCK.getDefaultState());
            map.put(BCObjects.DREAD_NOG, BCObjects.DREAD_NOG_TANKARD_BLOCK.getDefaultState());
        });
    }

    public TankardBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.blockRenderer = ctx.getRenderManager();
    }

    @Override
    public void render(TankardBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) {
            return;
        }
        float[] x = {0};
        float[] z = {0};
        int[] rot = {0};

        int count = (int) entity.getItems().stream().filter(i -> !i.isEmpty()).count();
        /*
        if (count == 2) {
            x = new float[]{2f / 16, -12.5f / 16};
            z = new float[]{4f / 16, -12f / 16};
            rot = new int[]{0, 180};
        } else if (count == 3) {
            x = new float[]{2f / 16, -13.5f / 16, -20f / 16};
            z = new float[]{4f / 16, -3f / 16, -12f / 16};
            rot = new int[]{0, 90, 180};
        }

         */
        if (count == 2) {
            x = new float[]{2f / 16, 7.5f / 16};
            z = new float[]{8.5f / 16, 4f / 16};
            rot = new int[]{0, 180};
        } else if (count == 3) {
            x = new float[]{2f / 16, 6.5f / 16, 9.5f / 16};
            z = new float[]{7f / 16, 9.5f / 16, 4.5f / 16};
            rot = new int[]{0, 90, 180};
        }

        matrices.push();

        for (int i = 0; i < entity.getItems().size(); i++) {
            ItemStack itemStack = entity.getItems().get(i);
            if (itemBlockStateMap().containsKey(itemStack.getItem())) {
                matrices.push();
                matrices.translate(0.5, 0, 0.5);

                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rot[i]));
                float rotation = RotationPropertyHelper.toDegrees(entity.getCachedState().get(TankardBlock.ROTATION));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-rotation));
                matrices.translate(-0.5, 0, -0.5);

                matrices.translate(x[i] / 2, 0, z[i] / 2);
                blockRenderer.renderBlock(itemBlockStateMap().get(itemStack.getItem()), entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), true, entity.getWorld().getRandom());
                matrices.pop();
            }
        }

        matrices.pop();
    }
}
