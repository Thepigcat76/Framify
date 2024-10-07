package com.example.examplemod.networking;

import com.example.examplemod.ClientEvents;
import com.example.examplemod.ExampleMod;
import com.example.examplemod.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Set;

public record SetFramedBlockPayload(BlockPos blockPos, Block newBlock) implements CustomPacketPayload {
    public static final Type<SetFramedBlockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "set_framed_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetFramedBlockPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SetFramedBlockPayload::blockPos,
            Utils.BLOCK_STREAM_CODEC,
            SetFramedBlockPayload::newBlock,
            SetFramedBlockPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void setBlock(SetFramedBlockPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientEvents.FRAMED_BLOCKS.put(payload.blockPos, payload.newBlock);
        });
    }
}
