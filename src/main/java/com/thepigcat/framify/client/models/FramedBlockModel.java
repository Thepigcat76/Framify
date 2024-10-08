package com.thepigcat.framify.client.models;

import com.thepigcat.framify.Framify;
import com.thepigcat.framify.client.renderer.BlockRenderer;
import com.thepigcat.framify.content.blocks.FramedBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FramedBlockModel extends BakedModelWrapper<BakedModel> {
    public FramedBlockModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        Block block = extraData.get(BlockRenderer.PROPERTY);
        if (block != null) {
            return Minecraft.getInstance().getModelManager().getModel(BlockModelShaper.stateToModelLocation(block.defaultBlockState())).getQuads(state, side, rand, extraData, renderType);
        }
        return originalModel.getQuads(state, side, rand, extraData, renderType);
    }
}
