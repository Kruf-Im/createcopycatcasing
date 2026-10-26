package com.kruf.createcopycatcasing.client;

import com.kruf.createcopycatcasing.block.CopycatEncasedShaftBlock;
import com.kruf.createcopycatcasing.copycat.ICopycatEncasedBlock;
import com.kruf.createcopycatcasing.registry.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = "createcopycatcasing", value = Dist.CLIENT)
public class CopycatColors {

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null)
                return -1;
            BlockState mat = ICopycatEncasedBlock.getMaterial(level, pos);
            if (mat.isAir())
                return -1;
            return Minecraft.getInstance().getBlockColors().getColor(mat, level, pos, tintIndex);
        }, BlockRegistry.COPYCAT_ENCASED_SHAFT.get());
    }
}