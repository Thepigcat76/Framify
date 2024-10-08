package com.thepigcat.framify.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.thepigcat.framify.Framify;
import com.thepigcat.framify.utils.CodecUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FramedBlockSavedData extends SavedData {
    public static final String ID = "framify_framed_block_saved_data";
    public static final Codec<Map<String, Block>> CODEC = Codec.unboundedMap(
            Codec.STRING,
            CodecUtils.BLOCK_CODEC
    );

    private final Object2ObjectOpenHashMap<BlockPos, Block> framedBlocks;

    public FramedBlockSavedData(Object2ObjectOpenHashMap<BlockPos, Block> framedBlocks) {
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
        return this.framedBlocks.containsKey(blockPos) && this.framedBlocks.get(blockPos) != null;
    }

    public Object2ObjectOpenHashMap<BlockPos, Block> getFramedBlocks() {
        return framedBlocks;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        DataResult<Tag> tagDataResult = CODEC.encodeStart(NbtOps.INSTANCE, blocksToString());
        tagDataResult
                .resultOrPartial(err -> Framify.LOGGER.error("Encoding error: {}", err))
                .ifPresent(tag -> compoundTag.put(ID, tag));
        return compoundTag;
    }

    public Map<String, Block> blocksToString() {
        Map<String, Block> map = new HashMap<>();
        for (Map.Entry<BlockPos, Block> entry : framedBlocks.entrySet()) {
            map.put(String.valueOf(entry.getKey().asLong()), entry.getValue());
        }
        return map;
    }

    public static FramedBlockSavedData load(CompoundTag tag) {
        Framify.LOGGER.debug("NBT: {}", tag.getAllKeys());
        DataResult<Pair<Map<String, Block>, Tag>> dataResult = CODEC.decode(NbtOps.INSTANCE, tag.get(ID));
        Optional<Pair<Map<String, Block>, Tag>> mapTagPair = dataResult
                .resultOrPartial(err -> Framify.LOGGER.error("Decoding error: {}", err));
        if (mapTagPair.isPresent()) {
            Map<String, Block> map = mapTagPair.get().getFirst();
            return new FramedBlockSavedData(blocksFromString(map));
        }
        return new FramedBlockSavedData();
    }

    public static Object2ObjectOpenHashMap<BlockPos, Block> blocksFromString(Map<String, Block> map) {
        Object2ObjectOpenHashMap<BlockPos, Block> blocks = new Object2ObjectOpenHashMap<>();
        for (Map.Entry<String, Block> entry : map.entrySet()) {
            blocks.put(BlockPos.of(Long.parseLong(entry.getKey())), entry.getValue());
        }
        return blocks;
    }

    public static SavedData.Factory<FramedBlockSavedData> factory() {
        return new SavedData.Factory<>(FramedBlockSavedData::new, (tag, provider) -> load(tag));
    }

    public static FramedBlockSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(factory(), ID);
    }
}
