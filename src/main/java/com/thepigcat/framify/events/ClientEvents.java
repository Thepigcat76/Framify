package com.thepigcat.framify.events;

import com.thepigcat.framify.Framify;
import com.thepigcat.framify.client.models.FramedBlockModel;
import com.thepigcat.framify.client.models.FramedSlabBlockModel;
import com.thepigcat.framify.client.renderer.BlockRenderer;
import com.thepigcat.framify.content.blocks.IFramedBlock;
import com.thepigcat.framify.registries.FYBlocks;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = Framify.MODID, value = Dist.CLIENT)
public final class ClientEvents {

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            BlockRenderer.renderBlock(event.getPoseStack(), event.getCamera());
        }
    }

    @EventBusSubscriber(modid = Framify.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class Mod {
        @SubscribeEvent
        public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
            event.getModels().computeIfPresent(BlockModelShaper.stateToModelLocation(FYBlocks.FRAMED_BLOCK.get().defaultBlockState().setValue(IFramedBlock.HAS_BLOCK, true)),
                    (loc, model) -> new FramedBlockModel(model));
            event.getModels().computeIfPresent(BlockModelShaper.stateToModelLocation(FYBlocks.FRAMED_SLAB_BLOCK.get().defaultBlockState().setValue(IFramedBlock.HAS_BLOCK, true)),
                    (loc, model) -> new FramedSlabBlockModel(model));
        }
    }
}
