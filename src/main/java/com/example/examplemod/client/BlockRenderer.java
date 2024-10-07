package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class BlockRenderer {
    public static Map<BlockPos, Block> FRAMED_BLOCKS = new Object2ObjectOpenHashMap<>();

    public static void renderBlock(PoseStack poseStack, Camera camera) {
        Minecraft mc = Minecraft.getInstance();

        for (Map.Entry<BlockPos, Block> entry : FRAMED_BLOCKS.entrySet()) {
            BlockPos blockPos = entry.getKey();
            ClientLevel level = mc.level;
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.is(ExampleMod.EXAMPLE_BLOCK)) {
                Block storedBlock = entry.getValue();
                Vec3 vec3 = camera.getPosition();
                double d0 = vec3.x();
                double d1 = vec3.y();
                double d2 = vec3.z();

                poseStack.pushPose();
                {
                    poseStack.translate((double) blockPos.getX() - d0, (double) blockPos.getY() - d1, (double) blockPos.getZ() - d2);
                    mc.getBlockRenderer().renderBatched(storedBlock.defaultBlockState(), blockPos, level, poseStack, mc.renderBuffers().bufferSource().getBuffer(RenderType.solid()), true, level.random);
                }
                poseStack.popPose();
            }
        }
    }
}
