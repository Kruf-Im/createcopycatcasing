package com.kruf.createcopycatcasing.mixin.compat.bnb;

import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialRenderer;
import com.kruf.createcopycatcasing.block.CopycatEncasedCogwheelBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialRenderer", remap = false)
public abstract class CogwheelMaterialRendererMixin {

    @Inject(method = "getVariant", at = @At("HEAD"), cancellable = true)
    private static void createcopycatcasing$copycatCogVariant(BlockState state, CallbackInfoReturnable<Object> cir) {
        if (!(state.getBlock() instanceof CopycatEncasedCogwheelBlock cog))
            return;
        BlockState create = (cog.isLargeCog() ? AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL : AllBlocks.ANDESITE_ENCASED_COGWHEEL)
                .getDefaultState()
                .setValue(EncasedCogwheelBlock.AXIS, state.getValue(EncasedCogwheelBlock.AXIS))
                .setValue(EncasedCogwheelBlock.TOP_SHAFT, state.getValue(EncasedCogwheelBlock.TOP_SHAFT))
                .setValue(EncasedCogwheelBlock.BOTTOM_SHAFT, state.getValue(EncasedCogwheelBlock.BOTTOM_SHAFT));
        cir.setReturnValue(CogwheelMaterialRenderer.getVariant(create));
    }
}