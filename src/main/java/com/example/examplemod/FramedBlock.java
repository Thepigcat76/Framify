package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class FramedBlock extends Block {
    public static final BooleanProperty HAS_BLOCK = BooleanProperty.create("has_block");

    public Block storedBlock;

    public FramedBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(HAS_BLOCK, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(HAS_BLOCK));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(HAS_BLOCK) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level instanceof ServerLevel serverLevel) {
            FramedBlockSavedData framedBlockSavedData = FramedBlockSavedData.get(serverLevel);
            // Does block have a stored block and is player not holding item
            if (framedBlockSavedData.contains(pos) && stack.isEmpty()) {
                level.setBlockAndUpdate(pos, state.setValue(HAS_BLOCK, false));
                framedBlockSavedData.remove(pos);
            } else if (!framedBlockSavedData.contains(pos) && stack.getItem() instanceof BlockItem blockItem) {
                serverLevel.setBlockAndUpdate(pos, state.setValue(HAS_BLOCK, true));
                framedBlockSavedData.put(pos, blockItem.getBlock());
            }
            return ItemInteractionResult.SUCCESS;
        } else if (level.isClientSide()) {
            // Does block have a stored block and is player not holding item
            if (ClientEvents.FRAMED_BLOCKS.containsKey(pos) && stack.isEmpty()) {
                ClientEvents.FRAMED_BLOCKS.remove(pos);
            } else if (!ClientEvents.FRAMED_BLOCKS.containsKey(pos) && stack.getItem() instanceof BlockItem blockItem) {
                ClientEvents.FRAMED_BLOCKS.put(pos, blockItem.getBlock());
            }
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.FAIL;
    }
}
