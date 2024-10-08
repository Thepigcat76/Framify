package com.thepigcat.framify.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thepigcat.framify.Framify;
import com.thepigcat.framify.client.models.FramedBlockModel;
import com.thepigcat.framify.content.blocks.IFramedBlock;
import com.thepigcat.framify.registries.FYBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BlockRenderer {
    public static Map<BlockPos, @Nullable Block> FRAMED_BLOCKS = new Object2ObjectOpenHashMap<>();
    public static final ModelProperty<Block> PROPERTY = new ModelProperty<>();

    public static void renderBlock(PoseStack poseStack, Camera camera) {
        Minecraft mc = Minecraft.getInstance();

        for (Map.Entry<BlockPos, Block> entry : FRAMED_BLOCKS.entrySet()) {
            BlockPos blockPos = entry.getKey();
            ClientLevel level = mc.level;
            BlockState framedBlock = level.getBlockState(blockPos);
            Vec3 vec3 = camera.getPosition();
            double d0 = vec3.x();
            double d1 = vec3.y();
            double d2 = vec3.z();

            poseStack.pushPose();
            {
                poseStack.translate((double) blockPos.getX() - d0, (double) blockPos.getY() - d1, (double) blockPos.getZ() - d2);
                if (framedBlock.getBlock() instanceof IFramedBlock) {
                    renderFramedCube(blockPos, framedBlock, entry.getValue(), poseStack);
                }
            }
            poseStack.popPose();
        }
    }

    public static void renderFramedCube(BlockPos blockPos, BlockState framedBlock, Block block, PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();
        BlockState state = getState(block, framedBlock);
        for (RenderType type : mc.getBlockRenderer().getBlockModel(state).getRenderTypes(state, mc.level.random, ModelData.EMPTY)) {
            renderBatched(block, framedBlock, blockPos, mc.level, poseStack, mc.renderBuffers().bufferSource().getBuffer(type), true, mc.level.random, ModelData.EMPTY, type);
        }
    }

    public static void renderFramedSlabCube(BlockPos blockPos, Block block, PoseStack poseStack, Camera camera) {

    }

    public static void renderBatched(Block state, BlockState framedBlock, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, RandomSource random) {
        renderBatched(state, framedBlock, pos, level, poseStack, consumer, checkSides, random, ModelData.EMPTY, null);
    }

    public static void renderBatched(Block block, BlockState framedBlock, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, RandomSource random, ModelData modelData, RenderType renderType) {
        Minecraft mc = Minecraft.getInstance();
        BlockState state = getState(block, framedBlock);
        try {
            BakedModel blockModel = mc.getBlockRenderer().getBlockModel(framedBlock);
            mc.getBlockRenderer().getModelRenderer().tesselateBlock(level, blockModel, framedBlock, pos, poseStack, consumer, checkSides, random, framedBlock.getSeed(pos), OverlayTexture.NO_OVERLAY, block != null ? ModelData.of(PROPERTY, block) : modelData, blockModel.getRenderTypes(state, random, ModelData.EMPTY).asList().getFirst());
        } catch (Throwable var13) {
            CrashReport crashreport = CrashReport.forThrowable(var13, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, level, pos, block != null ? state : framedBlock);
            throw new ReportedException(crashreport);
        }
    }

    private static BlockState getState(Block block, BlockState framedBlock) {
        return block != null ? block.defaultBlockState() : framedBlock;
    }
}
