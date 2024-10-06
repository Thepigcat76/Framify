package com.example.examplemod;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.Map;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class ClientEvents {
    public static Map<BlockPos, Block> FRAMED_BLOCKS = new Object2ObjectOpenHashMap<>();

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            Minecraft mc = Minecraft.getInstance();

            for (Map.Entry<BlockPos, Block> entry : FRAMED_BLOCKS.entrySet()) {
                BlockPos blockPos = entry.getKey();
                ClientLevel level = mc.level;
                BlockState blockState = level.getBlockState(blockPos);
                if (blockState.is(ExampleMod.EXAMPLE_BLOCK)) {
                    Block storedBlock = entry.getValue();
                    PoseStack poseStack = event.getPoseStack();
                    Vec3 vec3 = event.getCamera().getPosition();
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
}
