package com.thepigcat.framify.events;

import com.thepigcat.framify.Framify;
import com.thepigcat.framify.networking.RemoveFramedBlockPayload;
import com.thepigcat.framify.networking.SetFramedBlockPayload;
import com.thepigcat.framify.networking.SyncFramedBlocksPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Framify.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class NetworkingEvents {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Framify.MODID);
        registrar.playToClient(SetFramedBlockPayload.TYPE, SetFramedBlockPayload.STREAM_CODEC, SetFramedBlockPayload::setBlock);
        registrar.playToClient(RemoveFramedBlockPayload.TYPE, RemoveFramedBlockPayload.STREAM_CODEC, RemoveFramedBlockPayload::removeBlock);
        registrar.playToClient(SyncFramedBlocksPayload.TYPE, SyncFramedBlocksPayload.STREAM_CODEC, SyncFramedBlocksPayload::syncFramedBlock);
    }
}
