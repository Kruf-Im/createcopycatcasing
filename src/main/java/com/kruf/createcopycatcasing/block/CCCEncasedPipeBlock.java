package com.kruf.createcopycatcasing.block;

import com.kruf.createcopycatcasing.registry.BlockEntityTypes;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class CCCEncasedPipeBlock extends EncasedPipeBlock {
    public CCCEncasedPipeBlock(Properties properties, Supplier<Block> casing) {
        super(properties, casing);
    }

    @Override
    public BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return BlockEntityTypes.ENCASED_FLUID_PIPE.get();
    }
}
