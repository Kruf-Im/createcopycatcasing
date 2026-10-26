package com.kruf.createcopycatcasing.block;

import com.kruf.createcopycatcasing.copycat.ICopycatEncasedBlock;
import com.kruf.createcopycatcasing.registry.BlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CopycatEncasedShaftBlock extends EncasedShaftBlock implements ICopycatEncasedBlock {

    public CopycatEncasedShaftBlock(Properties properties, Supplier<Block> casing) {
        super(properties, casing);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return BlockEntityTypes.COPYCAT_ENCASED_SHAFT.get();
    }
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        ItemInteractionResult r = copycatUseItemOn(stack, level, pos, player);
        return r != null ? r : super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext ctx) {
        InteractionResult r = copycatOnWrenched(ctx);
        return r != null ? r : super.onWrenched(state, ctx);
    }
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        copycatOnRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        copycatPlayerWillDestroy(level, pos, player);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public ItemStack getInnerCloneItem(BlockState state, HitResult target) {
        if (target instanceof BlockHitResult hit && hit.getDirection().getAxis() == getRotationAxis(state))
            return AllBlocks.SHAFT.asStack();
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return copycatCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side,
                                    @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        return copycatAppearance(state, level, pos);
    }

    @Override
    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state,
                                     BlockState neighborState, Direction dir) {
        return copycatHidesNeighborFace(level, pos, neighborState, dir);
    }
}
