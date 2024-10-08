package com.thepigcat.framify.registries;

import com.thepigcat.framify.content.blocks.FramedBlock;
import com.thepigcat.framify.Framify;
import com.thepigcat.framify.content.blocks.FramedSlabBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class FYBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Framify.MODID);

    public static final DeferredBlock<FramedBlock> FRAMED_BLOCK = registerBlock("framed_block", FramedBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(1.0f));
    public static final DeferredBlock<FramedSlabBlock> FRAMED_SLAB_BLOCK = registerBlock("framed_slab_block", FramedSlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(1.0f));

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockConstructor, BlockBehaviour.Properties properties) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockConstructor, properties);
        FYItems.ITEMS.registerSimpleBlockItem(toReturn);
        return toReturn;
    }
}
