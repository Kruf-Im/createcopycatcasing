package com.kruf.createcopycatcasing.datagen;

import com.kruf.createcopycatcasing.CreateCopycatCasing;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = CreateCopycatCasing.MOD_ID)
public class CCCDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        gen.addProvider(event.includeServer(), new CCCItemApplicationRecipeGen(output,event.getLookupProvider()));
    }
}
