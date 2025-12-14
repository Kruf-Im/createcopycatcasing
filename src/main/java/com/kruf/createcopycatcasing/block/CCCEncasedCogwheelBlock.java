package com.kruf.createcopycatcasing.block;

import com.kruf.createcopycatcasing.registry.BlockEntityTypes;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class CCCEncasedCogwheelBlock extends EncasedCogwheelBlock {

    public CCCEncasedCogwheelBlock(Properties properties, boolean large, Supplier<Block> casing) {
        super(properties, large, casing);
    }

    @Override
    public BlockEntityType<? extends SimpleKineticBlockEntity> getBlockEntityType() {
        return this.isSmallCog() ? BlockEntityTypes.ENCASED_COGWHEEL.get() : BlockEntityTypes.ENCASED_LARGE_COGWHEEL.get();
    }
}
