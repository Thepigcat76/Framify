package com.thepigcat.framify.events;

import com.thepigcat.framify.Framify;
import com.thepigcat.framify.data.FramedBlockSavedData;
import com.thepigcat.framify.networking.SyncFramedBlocksPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Framify.MODID)
public final class GameEvents {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel serverLevel = (ServerLevel) player.level();
            FramedBlockSavedData data = FramedBlockSavedData.get(serverLevel);
            PacketDistributor.sendToPlayer(player, new SyncFramedBlocksPayload(data.getFramedBlocks()));
        }
    }
}
