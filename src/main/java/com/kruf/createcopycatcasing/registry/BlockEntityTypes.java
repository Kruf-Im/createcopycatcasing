package com.kruf.createcopycatcasing.registry;

import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.kruf.createcopycatcasing.CreateCopycatCasing.REGISTRATE;

public class BlockEntityTypes {
    public static final BlockEntityEntry<KineticBlockEntity> ENCASED_SHAFT = REGISTRATE
            .blockEntity("encased_shaft", KineticBlockEntity::new)
            .visual(() -> ShaftVisual::new, false)
            .validBlocks(BlockRegistry.COPYCAT_ENCASED_SHAFT)
            .renderer(()-> ShaftRenderer::new)
            .register();
    public static final BlockEntityEntry<SimpleKineticBlockEntity> ENCASED_COGWHEEL = REGISTRATE
            .blockEntity("encased_cogwheel", SimpleKineticBlockEntity::new)
            .visual(() -> EncasedCogVisual::small, false)
            .validBlocks(BlockRegistry.COPYCAT_ENCASED_COGWHEEL)
            .renderer(()-> EncasedCogRenderer::small)
            .register();
    public static final BlockEntityEntry<SimpleKineticBlockEntity> ENCASED_LARGE_COGWHEEL = REGISTRATE
            .blockEntity("encased_large_cogwheel", SimpleKineticBlockEntity::new)
            .visual(() -> EncasedCogVisual::large, false)
            .validBlocks(BlockRegistry.COPYCAT_ENCASED_LARGE_COGWHEEL)
            .renderer(()-> EncasedCogRenderer::large)
            .register();
    public static final BlockEntityEntry<FluidPipeBlockEntity> ENCASED_FLUID_PIPE = REGISTRATE
            .blockEntity("encased_fluid_pipe", FluidPipeBlockEntity::new)
            .validBlocks(BlockRegistry.COPYCAT_ENCASED_FLUID_PIPE)
            .register();
    public static void register(){
    }
}
