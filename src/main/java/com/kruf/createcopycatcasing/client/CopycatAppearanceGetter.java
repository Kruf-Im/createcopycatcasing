package com.kruf.createcopycatcasing.client;

import com.kruf.createcopycatcasing.block.CopycatEncasedShaftBlock;
import com.kruf.createcopycatcasing.blockentity.CopycatEncasedKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class CopycatAppearanceGetter implements BlockAndTintGetter {
    private final BlockAndTintGetter level;
    public CopycatAppearanceGetter(BlockAndTintGetter level){
        this.level= level;
    }

    @Override
    public float getShade(Direction direction, boolean b) {
        return level.getShade(direction, b);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return level.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        return level.getBlockTint(blockPos, colorResolver);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
        return level.getBlockEntity(blockPos);
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        BlockState state = level.getBlockState(blockPos);
        if(state.getBlock() instanceof CopycatEncasedShaftBlock){
            if(level.getBlockEntity(blockPos) instanceof CopycatEncasedKineticBlockEntity cesbe && cesbe.hasMaterial())
                return cesbe.getMaterial();
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return level.getFluidState(blockPos);
    }

    @Override
    public int getHeight() {
        return level.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return level.getMinBuildHeight();
    }
}
