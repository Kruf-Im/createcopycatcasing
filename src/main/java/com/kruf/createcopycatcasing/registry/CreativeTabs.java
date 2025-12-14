package com.kruf.createcopycatcasing.registry;

import com.kruf.createcopycatcasing.CreateCopycatCasing;
import com.simibubi.create.AllCreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            net.neoforged.neoforge.registries.DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateCopycatCasing.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            TAB_REGISTER.register("main",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("creativetab.create_copycat_casing_tab"))
                            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                            .icon(BlockRegistry.COPYCAT_CASING::asStack)
                            .displayItems((pParameters, pOutput) -> {
                                pOutput.accept(BlockRegistry.COPYCAT_CASING.get());
                            })
                            .build());

    public static void register(IEventBus eventBus){
        TAB_REGISTER.register(eventBus);
    }
}
