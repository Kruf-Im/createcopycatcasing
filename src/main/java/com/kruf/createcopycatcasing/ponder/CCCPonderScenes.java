package com.kruf.createcopycatcasing.ponder;

import com.kruf.createcopycatcasing.registry.BlockRegistry;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.simibubi.create.infrastructure.ponder.scenes.KineticsScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CCCPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(BlockRegistry.COPYCAT_CASING, AllBlocks.SHAFT)
                .addStoryBoard("shaft/shaft_encasing", CCCKineticsScenes::shaftsCanBeCopycatEncased, AllCreatePonderTags.KINETIC_RELAYS);
        HELPER.forComponents(BlockRegistry.COPYCAT_CASING, AllBlocks.COGWHEEL, AllBlocks.LARGE_COGWHEEL)
                .addStoryBoard("cog/cog_encasing", CCCKineticsScenes::cogwheelsCanBeCopycatEncased, AllCreatePonderTags.KINETIC_RELAYS);

        HELPER.forComponents(BlockRegistry.COPYCAT_CASING, AllBlocks.FLUID_PIPE)
                .addStoryBoard("pipe/pipe_encasing", CCCPipeScenes::pipesCanBeCopycatEncased, AllCreatePonderTags.FLUIDS);
    }
}
