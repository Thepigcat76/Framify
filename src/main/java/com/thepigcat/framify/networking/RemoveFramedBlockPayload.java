package com.thepigcat.framify.networking;

import com.thepigcat.framify.Framify;
import com.thepigcat.framify.client.renderer.BlockRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveFramedBlockPayload(BlockPos posToRemove) implements CustomPacketPayload {
    public static final Type<RemoveFramedBlockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Framify.MODID, "remove_framed_block"));
    public static final StreamCodec<ByteBuf, RemoveFramedBlockPayload> STREAM_CODEC = BlockPos.STREAM_CODEC.map(RemoveFramedBlockPayload::new, RemoveFramedBlockPayload::posToRemove);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void removeBlock(RemoveFramedBlockPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockRenderer.FRAMED_BLOCKS.remove(payload.posToRemove);
        });
    }
}
