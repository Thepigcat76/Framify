package com.thepigcat.framify.networking;

import com.thepigcat.framify.Framify;
import com.thepigcat.framify.client.renderer.BlockRenderer;
import com.thepigcat.framify.utils.CodecUtils;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncFramedBlocksPayload(Object2ObjectOpenHashMap<BlockPos, Block> framedBlocks) implements CustomPacketPayload {
    public static final Type<SyncFramedBlocksPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Framify.MODID, "sync_framed_blocks"));
    public static final StreamCodec<ByteBuf, SyncFramedBlocksPayload> STREAM_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, BlockPos.STREAM_CODEC, CodecUtils.BLOCK_STREAM_CODEC)
            .map(SyncFramedBlocksPayload::new, SyncFramedBlocksPayload::framedBlocks);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void syncFramedBlock(SyncFramedBlocksPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockRenderer.FRAMED_BLOCKS = payload.framedBlocks();
        });
    }
}
