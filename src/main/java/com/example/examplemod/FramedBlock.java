package com.example.examplemod;

import com.example.examplemod.client.BlockRenderer;
import com.example.examplemod.client.ClientEvents;
import com.example.examplemod.networking.RemoveFramedBlockPayload;
import com.example.examplemod.networking.SetFramedBlockPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class FramedBlock extends Block {
    public static final BooleanProperty HAS_BLOCK = BooleanProperty.create("has_block");

    public FramedBlock(Properties properties) {
        super(properties.noOcclusion());
        registerDefaultState(defaultBlockState().setValue(HAS_BLOCK, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(HAS_BLOCK));
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return state.getValue(HAS_BLOCK) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Does block have a stored block and is player not holding item
        if (isBlockStored(level, pos) && stack.isEmpty()) {
            removeFramedBlock(level, pos);
            level.setBlockAndUpdate(pos, state.setValue(HAS_BLOCK, false));
            return ItemInteractionResult.SUCCESS;
        } else if (!isBlockStored(level, pos) && stack.getItem() instanceof BlockItem blockItem && canInsert(blockItem, level, pos)) {
            setFramedBlock(level, pos, blockItem.getBlock());
            level.setBlockAndUpdate(pos, state.setValue(HAS_BLOCK, true));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }

    public static boolean isBlockStored(Level level, BlockPos blockPos) {
        if (level instanceof ServerLevel serverLevel) {
            return FramedBlockSavedData.get(serverLevel).contains(blockPos);
        } else if (level.isClientSide()) {
            return BlockRenderer.FRAMED_BLOCKS.containsKey(blockPos);
        }
        return false;
    }

    public static boolean canInsert(BlockItem blockItem, Level level, BlockPos pos) {
        return !(blockItem.getBlock() instanceof EntityBlock)
                && blockItem.getBlock().defaultBlockState().getRenderShape() == RenderShape.MODEL
                && !(blockItem.getBlock() instanceof FramedBlock)
                && shapeIsCubic(blockItem, level, pos);
    }

    private static boolean shapeIsCubic(BlockItem blockItem, Level level, BlockPos pos) {
        return blockItem.getBlock().defaultBlockState().getShape(level, pos).bounds().equals(new AABB(0, 0, 0, 1, 1, 1));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!state.is(newState.getBlock())) {
            removeFramedBlock(level, pos);
        }
    }

    public static void setFramedBlock(Level level, BlockPos blockPos, Block block) {
        if (level instanceof ServerLevel serverLevel) {
            FramedBlockSavedData framedBlockSavedData = FramedBlockSavedData.get(serverLevel);
            framedBlockSavedData.put(blockPos, block);
            PacketDistributor.sendToAllPlayers(new SetFramedBlockPayload(blockPos, block));
        }
    }

    public static void removeFramedBlock(Level level, BlockPos blockPos) {
        if (level instanceof ServerLevel serverLevel) {
            FramedBlockSavedData framedBlockSavedData = FramedBlockSavedData.get(serverLevel);
            framedBlockSavedData.remove(blockPos);
            PacketDistributor.sendToAllPlayers(new RemoveFramedBlockPayload(blockPos));
        }
    }
}
