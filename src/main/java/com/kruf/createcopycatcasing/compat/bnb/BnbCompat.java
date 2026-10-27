package com.kruf.createcopycatcasing.compat.bnb;

import com.kipti.bnb.content.decoration.dyeable.pipes.DyeablePipeBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.neoforged.fml.ModList;

import java.util.List;

public class BnbCompat {
    public static final String MOD_ID = "bits_n_bobs";

    private BnbCompat() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }

    public static void addCogwheelMaterial(SmartBlockEntity be, List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new CopycatCogwheelMaterialBehaviour(be));
    }
    public static void addPipeDye(SmartBlockEntity be, List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DyeablePipeBehaviour(be));
    }

}
