package com.thepigcat.framify.networking;

import com.thepigcat.framify.Framify;
import com.thepigcat.framify.client.renderer.BlockRenderer;
import com.thepigcat.framify.utils.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record SetFramedBlockPayload(BlockPos blockPos, Optional<Block> newBlock) implements CustomPacketPayload {
    public static final Type<SetFramedBlockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Framify.MODID, "set_framed_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetFramedBlockPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SetFramedBlockPayload::blockPos,
            ByteBufCodecs.optional(CodecUtils.BLOCK_STREAM_CODEC),
            SetFramedBlockPayload::newBlock,
            SetFramedBlockPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void setBlock(SetFramedBlockPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockRenderer.FRAMED_BLOCKS.put(payload.blockPos, payload.newBlock.orElse(null));
        });
    }
}
