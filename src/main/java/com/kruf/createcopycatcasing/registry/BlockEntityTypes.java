package com.kruf.createcopycatcasing.registry;

import com.kruf.createcopycatcasing.blockentity.CopycatEncasedKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.kruf.createcopycatcasing.CreateCopycatCasing.REGISTRATE;

public class BlockEntityTypes {
    public static final BlockEntityEntry<CopycatEncasedKineticBlockEntity> COPYCAT_ENCASED_SHAFT = REGISTRATE
            .blockEntity("copycat_encased_shaft", CopycatEncasedKineticBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual::shaft, false)
            .validBlocks(BlockRegistry.COPYCAT_ENCASED_SHAFT)
            .renderer(() -> ShaftRenderer::new)
            .register();
//    public static final BlockEntityEntry<CopycatEncasedKineticBlockEntity> COPYCAT_ENCASED_COGWHEEL = REGISTRATE
//            .blockEntity("copycat_encased_cogwheel", CopycatEncasedKineticBlockEntity::new)
//            .visual(() -> EncasedCogVisual::small, false)
//            .validBlocks(BlockRegistry.COPYCAT_ENCASED_COGWHEEL)
//            .renderer(()-> EncasedCogRenderer::small)
//            .register();
//    public static final BlockEntityEntry<CopycatEncasedKineticBlockEntity> COPYCAT_ENCASED_LARGE_COGWHEEL = REGISTRATE
//            .blockEntity("encased_large_cogwheel", CopycatEncasedKineticBlockEntity::new)
//            .visual(() -> EncasedCogVisual::large, false)
//            .validBlocks(BlockRegistry.COPYCAT_ENCASED_LARGE_COGWHEEL)
//            .renderer(()-> EncasedCogRenderer::large)
//            .register();
//    public static final BlockEntityEntry<FluidPipeBlockEntity> ENCASED_FLUID_PIPE = REGISTRATE
//            .blockEntity("encased_fluid_pipe", FluidPipeBlockEntity::new)
//            .validBlocks(BlockRegistry.COPYCAT_ENCASED_FLUID_PIPE)
//            .register();
    public static void register(){
    }
}
