package com.thepigcat.framify.client.models;

import com.google.common.base.Preconditions;
import com.thepigcat.framify.Framify;
import com.thepigcat.framify.client.renderer.BlockRenderer;
import com.thepigcat.framify.utils.quad.QuadData;
import com.thepigcat.framify.utils.quad.QuadModifier;
import com.thepigcat.framify.utils.quad.QuadTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FramedSlabBlockModel extends BakedModelWrapper<BakedModel> {
    private static final QuadModifier.Modifier NOOP_MODIFIER = data -> true;

    public FramedSlabBlockModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        Block block = extraData.get(BlockRenderer.PROPERTY);
        QuadTable quadTable = new QuadTable();
        if (block != null) {
            quadTable.initializeForLayer(renderType);
            List<BakedQuad> quads = Minecraft.getInstance().getModelManager().getModel(BlockModelShaper.stateToModelLocation(block.defaultBlockState())).getQuads(state, side, rand, extraData, renderType);
            for (BakedQuad quad : quads) {
                transformQuad(quadTable, state, quad);
            }
            return quadTable.getAllQuads(side);
        }
        return originalModel.getQuads(state, side, rand, extraData, renderType);
    }

    public void transformQuad(QuadTable quadTable, BlockState state, final BakedQuad quad)
    {
        if (state == null) return;

        boolean b = state.getValue(SlabBlock.TYPE) == SlabType.TOP;
        if ((b && quad.getDirection() == Direction.DOWN) || (state.getValue(SlabBlock.TYPE) != SlabType.TOP && quad.getDirection() == Direction.UP))
        {
            QuadModifier.of(quad)
                    .apply(setPosition(.5F))
                    .export(quadTable.get(null));
        }
        else if (!isY(quad.getDirection()))
        {
            QuadModifier.of(quad)
                    .apply(cutSideUpDown(b, .5F))
                    .export(quadTable.get(quad.getDirection()));
        }
    }

    public static QuadModifier.Modifier cutSideUpDown(boolean downwards, float length)
    {
        if (Mth.equal(length, 1F))
        {
            return NOOP_MODIFIER;
        }
        return cutSideUpDown(downwards, length, length);
    }

    public static QuadModifier.Modifier cutSideUpDown(boolean downwards, float lengthRight, float lengthLeft)
    {
        return data -> cutSideUpDown(data, downwards, lengthRight, lengthLeft);
    }

    private static boolean cutSideUpDown(QuadData data, boolean downwards, float lengthRight, float lengthLeft)
    {
        Direction quadDir = data.quad().getDirection();
        Preconditions.checkState(!isY(quadDir), "Quad direction must be horizontal");

        Direction quadDirRot = quadDir.getCounterClockWise();
        boolean x = isX(quadDirRot);
        boolean positive = isPositive(quadDirRot);

        float factorR = positive ? data.pos(0, x ? 0 : 2) : (1F - data.pos(0, x ? 0 : 2));
        float factorL = positive ? data.pos(3, x ? 0 : 2) : (1F - data.pos(3, x ? 0 : 2));

        float targetR = Mth.lerp(factorR, downwards ? 1F - lengthRight : lengthRight, downwards ? 1F - lengthLeft : lengthLeft);
        float targetL = Mth.lerp(factorL, downwards ? 1F - lengthRight : lengthRight, downwards ? 1F - lengthLeft : lengthLeft);

        if (downwards && (isLower(data.pos(0, 1), targetR) || isLower(data.pos(3, 1), targetL)))
        {
            return false;
        }
        if (!downwards && (isHigher(data.pos(1, 1), targetR) || isHigher(data.pos(2, 1), targetL)))
        {
            return false;
        }

        int idx1 = downwards ? 1 : 0;
        int idx2 = downwards ? 2 : 3;

        float y1 = data.pos(idx1, 1);
        float y2 = data.pos(idx2, 1);

        float toY1 = downwards ? Math.max(y1, targetR) : Math.min(y1, targetR);
        float toY2 = downwards ? Math.max(y2, targetL) : Math.min(y2, targetL);

        boolean rotated = data.uvRotated();
        boolean mirrored = data.uvMirrored();
        TextureAtlasSprite sprite = data.quad().getSprite();
        remapUV(quadDir, sprite, data.pos(1, 1), data.pos(0, 1), toY1, data, 0, 1, idx1, true, !mirrored, rotated, mirrored);
        remapUV(quadDir, sprite, data.pos(2, 1), data.pos(3, 1), toY2, data, 3, 2, idx2, true, !mirrored, rotated, mirrored);

        data.pos(idx1, 1, toY1);
        data.pos(idx2, 1, toY2);

        return true;
    }

    public static QuadModifier.Modifier setPosition(float posTarget)
    {
        if (Mth.equal(posTarget, 1F))
        {
            return NOOP_MODIFIER;
        }

        return data ->
        {
            int idx = data.quad().getDirection().getAxis().ordinal();
            float value = isPositive(data.quad().getDirection()) ? posTarget : 1F - posTarget;

            for (int i = 0; i < 4; i++)
            {
                data.pos(i, idx, value);
            }

            return true;
        };
    }

    public static boolean isPositive(Direction dir)
    {
        return dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
    }

    public static boolean isY(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.Y;
    }

    public static boolean isX(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.X;
    }

    /**
     * Check if the left hand value is lower than the right hand value.
     * If the difference between the two values is smaller than {@code 1.0E-5F},
     * the result will be {@code false}
     * @return Returns true when the left hand value is lower than the right hand value,
     *         accounting for floating point precision issues
     */
    public static boolean isLower(float lhs, float rhs)
    {
        if (Mth.equal(lhs, rhs))
        {
            return false;
        }
        return lhs < rhs;
    }

    /**
     * Check if the left hand value is higher than the right hand value.
     * If the difference between the two values is smaller than {@code 1.0E-5F},
     * the result will be {@code false}
     * @return Returns true when the left hand value is higher than the right hand value,
     *         accounting for floating point precision issues
     */
    public static boolean isHigher(float lhs, float rhs)
    {
        if (Mth.equal(lhs, rhs))
        {
            return false;
        }
        return lhs > rhs;
    }

    public static void remapUV(
            Direction quadDir,
            TextureAtlasSprite sprite,
            float coord1,
            float coord2,
            float coordTo,
            QuadData data,
            int uv1,
            int uv2,
            int uvTo,
            boolean vAxis,
            boolean invert,
            boolean rotated,
            boolean mirrored
    )
    {
        if (rotated)
        {
            if (quadDir == Direction.UP)
            {
                invert = vAxis == mirrored;
            }
            else if (quadDir == Direction.DOWN)
            {
                invert = !mirrored;
            }
            else if (!vAxis)
            {
                invert = invert == mirrored;
            }
        }
        else if (mirrored)
        {
            if (quadDir == Direction.UP)
            {
                invert = !vAxis || (data.uv(0, 1) > data.uv(1, 1)) || (data.uv(3, 1) > data.uv(2, 1));
            }
            else if (quadDir == Direction.DOWN)
            {
                invert = !vAxis || (data.uv(0, 1) < data.uv(1, 1)) || (data.uv(3, 1) < data.uv(2, 1));
            }
            else if (!vAxis)
            {
                invert = !invert;
            }
        }

        float coordMin = Math.min(coord1, coord2);
        float coordMax = Math.max(coord1, coord2);

        int uvIdx = rotated != vAxis ? 1 : 0;

        float uvMin = Math.min(data.uv(uv1, uvIdx), data.uv(uv2, uvIdx));
        float uvMax = Math.max(data.uv(uv1, uvIdx), data.uv(uv2, uvIdx));

        if (coordTo == coordMin)
        {
            data.uv(uvTo, uvIdx,  (invert) ? uvMax : uvMin);
        }
        else if (coordTo == coordMax)
        {
            data.uv(uvTo, uvIdx,  (invert) ? uvMin : uvMax);
        }
    }
}
