package com.example.examplemod;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Map;

public class FramedBlockSavedData extends SavedData {
    public static final String ID = "framify_framed_block_saved_data";
    public static final Codec<Map<BlockPos, Block>> CODEC = Codec.unboundedMap(
            BlockPos.CODEC,
            ResourceLocation.CODEC.xmap(BuiltInRegistries.BLOCK::get, BuiltInRegistries.BLOCK::getKey)
    );

    private Map<BlockPos, Block> framedBlocks;

    public FramedBlockSavedData(Map<BlockPos, Block> framedBlocks) {
        this.framedBlocks = framedBlocks;
    }

    public FramedBlockSavedData() {
        this.framedBlocks = new Object2ObjectOpenHashMap<>();
    }

    public void put(BlockPos blockPos, Block block) {
        this.framedBlocks.put(blockPos, block);
        setDirty();
    }

    public void remove(BlockPos blockPos) {
        this.framedBlocks.remove(blockPos);
        setDirty();
    }

    public boolean contains(BlockPos blockPos) {
        return this.framedBlocks.containsKey(blockPos);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        DataResult<Tag> tagDataResult = CODEC.encodeStart(NbtOps.INSTANCE, framedBlocks);
        if (tagDataResult.isSuccess()) {
            compoundTag.put(ID+"map", tagDataResult.getOrThrow());
        } else {
            ExampleMod.LOGGER.error("ERROR: Failed to save framed blocks saved data");
        }
        return compoundTag;
    }

    public static FramedBlockSavedData load(CompoundTag tag) {
        DataResult<Pair<Map<BlockPos, Block>, Tag>> dataResult = CODEC.decode(NbtOps.INSTANCE, tag.get(ID + "map"));
        if (dataResult.isSuccess()) {
            return new FramedBlockSavedData(dataResult.getOrThrow().getFirst());
        } else {
            ExampleMod.LOGGER.error("ERROR: Failed to load framed blocks saved data");
            return new FramedBlockSavedData();
        }
    }

    public static SavedData.Factory<FramedBlockSavedData> factory() {
        return new SavedData.Factory<>(FramedBlockSavedData::new, (tag, provider) -> load(tag));
    }

    public static FramedBlockSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(factory(), ID);
    }
}
