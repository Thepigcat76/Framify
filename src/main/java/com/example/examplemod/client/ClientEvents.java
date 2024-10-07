package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public final class ClientEvents {

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            BlockRenderer.renderBlock(event.getPoseStack(), event.getCamera());
        }
    }
}
