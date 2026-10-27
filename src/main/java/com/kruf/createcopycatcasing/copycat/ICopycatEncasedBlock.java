package com.kruf.createcopycatcasing.copycat;

import com.simibubi.create.AllTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.jetbrains.annotations.Nullable;

public interface ICopycatEncasedBlock {
    Block getCasing();

    @Nullable
    static CopycatMaterial copycatAt(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ICopycatMaterialBlockEntity be
                ? be.getCopycatMaterial() : null;
    }

    static BlockState getMaterial(BlockGetter level, BlockPos pos) {
        CopycatMaterial cm = copycatAt(level, pos);
        return cm != null ? cm.get() : Blocks.AIR.defaultBlockState();
    }

    default boolean isAcceptedMaterial(BlockState material, Level level, BlockPos pos) {
        Block b = material.getBlock();
        if (AllTags.AllBlockTags.COPYCAT_DENY.matches(material)) return false;
        if (b instanceof ICopycatEncasedBlock) return false;
        if (b instanceof EntityBlock) return false;
        if (material.getRenderShape() != RenderShape.MODEL) return false;
        return material.isCollisionShapeFullBlock(level, pos);
    }

    @Nullable
    default ItemInteractionResult copycatUseItemOn(ItemStack stack, Level level, BlockPos pos, Player player) {
        if (!(stack.getItem() instanceof BlockItem blockItem))
            return null;

        BlockState material = blockItem.getBlock().defaultBlockState();
        if (!isAcceptedMaterial(material, level, pos))
            return null;

        if (player == null || !player.mayBuild())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        CopycatMaterial cm = copycatAt(level, pos);
        if (cm == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (cm.get().is(material.getBlock())) {
            if (!cm.canCycle())
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (level.isClientSide)
                return ItemInteractionResult.SUCCESS;
            cm.cycle();
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, .75f, .95f);
            return ItemInteractionResult.SUCCESS;
        }

        if (cm.has())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        cm.set(material, stack);
        if (!player.isCreative())
            stack.shrink(1);
        level.playSound(null, pos, material.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1, .75f);
        return ItemInteractionResult.SUCCESS;
    }

    @Nullable
    default InteractionResult copycatOnWrenched(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        CopycatMaterial cm = copycatAt(level, pos);
        if (cm == null || !cm.has())
            return null;

        if (!level.isClientSide) {
            Player player = ctx.getPlayer();
            if (player != null && !player.isCreative())
                player.getInventory().placeItemBackInInventory(cm.getConsumedItem().copy());
            level.levelEvent(2001, pos, Block.getId(cm.get())); // break particles
            cm.clear();
        }
        return InteractionResult.SUCCESS;
    }

    default void copycatOnRemove(BlockState state, Level level, BlockPos pos, BlockState newState) {
        if (state.is(newState.getBlock()))
            return;
        CopycatMaterial cm = copycatAt(level, pos);
        if (cm != null && !cm.getConsumedItem().isEmpty())
            Block.popResource(level, pos, cm.getConsumedItem().copy());
        AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
        if(lightManager != null)
            lightManager.setLightAt(pos, 0);
    }

    default void copycatPlayerWillDestroy(Level level, BlockPos pos, Player player) {
        CopycatMaterial cm = copycatAt(level, pos);
        if (player.isCreative() && cm != null)
            cm.setConsumedItem(ItemStack.EMPTY);
    }

    default ItemStack getInnerCloneItem(BlockState state, HitResult target) {
        return ItemStack.EMPTY;
    }

    default ItemStack copycatCloneItemStack(BlockState state, HitResult target, BlockGetter level,
                                            BlockPos pos, @Nullable Player player) {
        if (player != null && player.isShiftKeyDown())
            return new ItemStack(getCasing());

        ItemStack inner = getInnerCloneItem(state, target);
        if (!inner.isEmpty())
            return inner;

        CopycatMaterial cm = copycatAt(level, pos);
        if (cm != null && cm.has()) {
            ItemStack consumed = cm.getConsumedItem();
            return consumed.isEmpty() ? new ItemStack(cm.get().getBlock()) : consumed.copyWithCount(1);
        }

        return new ItemStack(getCasing());
    }
    default BlockState copycatAppearance(BlockState state, BlockGetter level, BlockPos pos) {
        CopycatMaterial cm = copycatAt(level, pos);
        return cm != null && cm.has() ? cm.get() : state;
    }

    default boolean copycatHidesNeighborFace(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
        BlockState mat = getMaterial(level, pos);
        return !mat.isAir() && mat.skipRendering(neighborState, dir);
    }
    @Nullable
    default BlockState getInnerRenderState(BlockState state) {
        return null;
    }
    default int copycatLightBlock(BlockGetter level, BlockPos pos) {
        CopycatMaterial cm = copycatAt(level, pos);
        if (cm == null || !cm.has())
            return 0;
        return Math.min(cm.get().getLightBlock(level, pos), 1);
    }

    default boolean copycatPropagatesSkylightDown(BlockGetter level, BlockPos pos) {
        CopycatMaterial cm = copycatAt(level, pos);
        return cm == null || !cm.has() || cm.get().propagatesSkylightDown(level, pos);
    }

    default float copycatShadeBrightness(BlockGetter level, BlockPos pos) {
        CopycatMaterial cm = copycatAt(level, pos);
        return cm != null && cm.has() ? cm.get().getShadeBrightness(level, pos) : 1.0F;
    }
    default boolean copycatHasDynamicLightEmission(BlockState state) {
        return true;
    }

    default int copycatGetLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
        return lightManager != null ? lightManager.getLightAt(pos) : 0;
    }
}