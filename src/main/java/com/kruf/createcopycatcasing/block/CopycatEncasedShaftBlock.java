package com.kruf.createcopycatcasing.block;

import com.kruf.createcopycatcasing.blockentity.CopycatEncasedKineticBlockEntity;
import com.kruf.createcopycatcasing.registry.BlockEntityTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CopycatEncasedShaftBlock extends EncasedShaftBlock {

    public CopycatEncasedShaftBlock(Properties properties, Supplier<Block> casing) {
        super(properties, casing);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return BlockEntityTypes.COPYCAT_ENCASED_SHAFT.get();
    }
    public static BlockState getMaterial(BlockGetter getter, BlockPos pos){
        if(getter.getBlockEntity(pos) instanceof CopycatEncasedKineticBlockEntity cesbe)
            return cesbe.getMaterial();
        return Blocks.AIR.defaultBlockState();
    }

    protected boolean isAcceptedMaterial(BlockState material, Level level, BlockPos pos){
        Block b = material.getBlock();

        if (AllTags.AllBlockTags.COPYCAT_DENY.matches(material)) return false;

        if (b instanceof EntityBlock) return false;
        if (material.getRenderShape() != RenderShape.MODEL) return false;
        return material.isCollisionShapeFullBlock(level, pos);
    }
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
        if (!(stack.getItem() instanceof BlockItem blockItem))
            return super.useItemOn(stack, state, level, pos, player, hand, hit);

        BlockState material = blockItem.getBlock().defaultBlockState();
        if (!isAcceptedMaterial(material, level, pos))
            return super.useItemOn(stack, state, level, pos, player, hand, hit);

        if (player == null || !player.mayBuild())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!(level.getBlockEntity(pos) instanceof CopycatEncasedKineticBlockEntity be))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (be.getMaterial().is(material.getBlock())) {
            if (!be.canCycle())
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (level.isClientSide)
                return ItemInteractionResult.SUCCESS;
            be.cycleMaterial();
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, .75f, .95f);
            return ItemInteractionResult.SUCCESS;
        }

        if (be.hasMaterial())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        be.setMaterial(material, stack);
        if (!player.isCreative())
            stack.shrink(1);

        level.playSound(null, pos, material.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1, 0.75f);
        return ItemInteractionResult.SUCCESS;
    }
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext ctx){
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        if(level.getBlockEntity(pos) instanceof CopycatEncasedKineticBlockEntity be && be.hasMaterial()){
            if(!level.isClientSide){
                Player player = ctx.getPlayer();
                if (player != null && !player.isCreative())
                    player.getInventory().placeItemBackInInventory(be.getConsumedItem().copy());
                level.levelEvent(2001, pos, Block.getId(getMaterial(ctx.getLevel(), ctx.getClickedPos())));
                be.clearMaterial();
            }
            return InteractionResult.SUCCESS;
        }
        return super.onWrenched(state,ctx);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved){
        if(!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof CopycatEncasedKineticBlockEntity be && !be.getConsumedItem().isEmpty())
            Block.popResource(level,pos,be.getConsumedItem().copy());
        super.onRemove(state,level,pos,newState,moved);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (player.isCreative() && level.getBlockEntity(pos) instanceof CopycatEncasedKineticBlockEntity be)
            be.setConsumedItem(ItemStack.EMPTY);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        if (player != null && player.isShiftKeyDown()){
            return new ItemStack(getCasing());
        }
        if (level.getBlockEntity(pos) instanceof CopycatEncasedKineticBlockEntity be && be.hasMaterial()){
            ItemStack consumed = be.getConsumedItem();
            return consumed.isEmpty() ? new ItemStack(be.getMaterial().getBlock()) : consumed.copyWithCount(1);
        }
        return new ItemStack(getCasing());
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side,
                                    @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        BlockState result = state;
        if (level.getBlockEntity(pos) instanceof CopycatEncasedKineticBlockEntity be && be.hasMaterial())
            result = be.getMaterial();
        return result;
    }
    @Override
    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state,
                                     BlockState neighborState, Direction dir) {
        BlockState mat = getMaterial(level, pos);
        return !mat.isAir() && mat.skipRendering(neighborState, dir);
    }
}
