package com.kruf.createcopycatcasing.datagen;

import com.kruf.createcopycatcasing.CreateCopycatCasing;
import com.kruf.createcopycatcasing.registry.BlockRegistry;
import com.simibubi.create.api.data.recipe.ItemApplicationRecipeGen;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CCCItemApplicationRecipeGen extends ItemApplicationRecipeGen {
    GeneratedRecipe COPYCAT = woodCasingTag("copycat", ()-> CommonMetal.ZINC.ingots, BlockRegistry.COPYCAT_CASING::get);
    public CCCItemApplicationRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateCopycatCasing.MOD_ID);
    }
}
