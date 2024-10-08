package com.thepigcat.framify.content.blocks;

import com.thepigcat.framify.client.renderer.BlockRenderer;
import com.thepigcat.framify.data.FramedBlockSavedData;
import com.thepigcat.framify.networking.SetFramedBlockPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IFramedBlock {
    BooleanProperty HAS_BLOCK = BooleanProperty.create("has_block");

    default BooleanProperty getHasBlockProperty() {
        return HAS_BLOCK;
    }

    default @NotNull ItemInteractionResult use(ItemStack stack, BlockState state, Level level, BlockPos pos) {
        // Does block have a stored block and is player not holding item
        if (isBlockStored(level, pos) && stack.isEmpty()) {
            removeFramedBlock(level, pos);
            level.setBlockAndUpdate(pos, state.setValue(getHasBlockProperty(), false));
            return ItemInteractionResult.SUCCESS;
        } else if (!isBlockStored(level, pos) && stack.getItem() instanceof BlockItem blockItem && canInsert(blockItem, level, pos)) {
            setFramedBlock(level, pos, blockItem.getBlock());
            level.setBlockAndUpdate(pos, state.setValue(getHasBlockProperty(), true));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }

    default boolean isBlockStored(Level level, BlockPos blockPos) {
        if (level instanceof ServerLevel serverLevel) {
            return FramedBlockSavedData.get(serverLevel).contains(blockPos);
        } else if (level.isClientSide()) {
            return BlockRenderer.FRAMED_BLOCKS.containsKey(blockPos) && BlockRenderer.FRAMED_BLOCKS.get(blockPos) != null;
        }
        return false;
    }

    default void setFramedBlock(Level level, BlockPos blockPos, @Nullable Block block) {
        if (level instanceof ServerLevel serverLevel) {
            FramedBlockSavedData framedBlockSavedData = FramedBlockSavedData.get(serverLevel);
            framedBlockSavedData.put(blockPos, block);
            PacketDistributor.sendToAllPlayers(new SetFramedBlockPayload(blockPos, Optional.ofNullable(block)));
        }
    }

    default void removeFramedBlock(Level level, BlockPos blockPos) {
        if (level instanceof ServerLevel serverLevel) {
            FramedBlockSavedData framedBlockSavedData = FramedBlockSavedData.get(serverLevel);
            framedBlockSavedData.put(blockPos, null);
            PacketDistributor.sendToAllPlayers(new SetFramedBlockPayload(blockPos, Optional.empty()));
        }
    }

    default boolean canInsert(BlockItem blockItem, Level level, BlockPos pos) {
        return !(blockItem.getBlock() instanceof EntityBlock)
                && blockItem.getBlock().defaultBlockState().getRenderShape() == RenderShape.MODEL
                && !(blockItem.getBlock() instanceof IFramedBlock)
                && shapeIsCubic(blockItem, level, pos);
    }

    static boolean shapeIsCubic(BlockItem blockItem, Level level, BlockPos pos) {
        return blockItem.getBlock().defaultBlockState().getShape(level, pos).bounds().equals(new AABB(0, 0, 0, 1, 1, 1));
    }

}
