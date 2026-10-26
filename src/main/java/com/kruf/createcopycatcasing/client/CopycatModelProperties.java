package com.kruf.createcopycatcasing.client;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class CopycatModelProperties {
    public static final ModelProperty<BlockState> MATERIAL = new ModelProperty<>();
    public static final ModelProperty<ModelData> WRAPPED_DATA = new ModelProperty<>();
    public static final ModelProperty<Integer> OCCLUDED_FACES = new ModelProperty<>();
}
