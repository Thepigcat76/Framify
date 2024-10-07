package com.example.examplemod;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class Utils {
    public static final Codec<Block> BLOCK_CODEC = ResourceLocation.CODEC.xmap(BuiltInRegistries.BLOCK::get, BuiltInRegistries.BLOCK::getKey);
    public static final StreamCodec<ByteBuf, Block> BLOCK_STREAM_CODEC = ByteBufCodecs.INT.map(BuiltInRegistries.BLOCK::byId, BuiltInRegistries.BLOCK::getId);

    private Utils() {
    }
}
