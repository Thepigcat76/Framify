package com.example.examplemod;

import com.example.examplemod.networking.SyncFramedBlocksPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class GameEvents {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel serverLevel = (ServerLevel) player.level();
            FramedBlockSavedData data = FramedBlockSavedData.get(serverLevel);
            ExampleMod.LOGGER.debug("data: {}", data.getFramedBlocks());
            PacketDistributor.sendToPlayer(player, new SyncFramedBlocksPayload(data.getFramedBlocks()));
        }
    }
}
