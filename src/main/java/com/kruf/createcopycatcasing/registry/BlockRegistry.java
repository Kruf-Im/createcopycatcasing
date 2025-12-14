package com.kruf.createcopycatcasing.registry;

import com.kruf.createcopycatcasing.CreateCopycatCasingSpriteShifts;
import com.kruf.createcopycatcasing.block.CCCCopycatCasingBlock;
import com.kruf.createcopycatcasing.block.CCCEncasedCogwheelBlock;
import com.kruf.createcopycatcasing.block.CCCEncasedPipeBlock;
import com.kruf.createcopycatcasing.block.CCCEncasedShaftBlock;
import com.kruf.createcopycatcasing.util.CCCBuilderTransformers;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.fluids.PipeAttachmentModel;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.kruf.createcopycatcasing.CreateCopycatCasing.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

@SuppressWarnings("removal")
public class BlockRegistry {
    static {
        REGISTRATE.setCreativeTab(CreativeTabs.MAIN_TAB);
    }
    public static final BlockEntry<CCCCopycatCasingBlock> COPYCAT_CASING = REGISTRATE
            .block("copycat_casing", CCCCopycatCasingBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(pickaxeOnly())
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
//            .transform(CCCBuilderTransformers.copycat())
            .transform(CCCBuilderTransformers.casing(() -> CreateCopycatCasingSpriteShifts.COPYCAT_CASING))
            .properties(p -> p.sound(SoundType.METAL))
            .register();
    public static final BlockEntry<CCCEncasedShaftBlock> COPYCAT_ENCASED_SHAFT = REGISTRATE
            .block("copycat_encased_shaft",
                    p -> new CCCEncasedShaftBlock(p, BlockRegistry.COPYCAT_CASING::get))
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(CCCBuilderTransformers.encasedShaft("copycat", () -> CreateCopycatCasingSpriteShifts.COPYCAT_CASING))
            .transform(EncasingRegistry.addVariantTo(AllBlocks.SHAFT))
            .properties(p -> p.sound(SoundType.METAL))
            .transform(pickaxeOnly())
            .register();
    public static final BlockEntry<CCCEncasedCogwheelBlock> COPYCAT_ENCASED_COGWHEEL = REGISTRATE
            .block("copycat_encased_cogwheel",
                    p-> new CCCEncasedCogwheelBlock(p, false, BlockRegistry.COPYCAT_CASING::get))
            .initialProperties(SharedProperties::softMetal)
            .properties(p->p.mapColor(MapColor.COLOR_GRAY))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(CCCBuilderTransformers.encasedCogwheel("copycat", ()->CreateCopycatCasingSpriteShifts.COPYCAT_CASING))
            .transform(EncasingRegistry.addVariantTo(AllBlocks.COGWHEEL))
            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(CreateCopycatCasingSpriteShifts.COPYCAT_CASING,
                    Couple.create(CreateCopycatCasingSpriteShifts.COPYCAT_ENCASED_COGWHEEL_SIDE,
                            CreateCopycatCasingSpriteShifts.COPYCAT_ENCASED_COGWHEEL_OTHERSIDE))))
            .properties(p -> p.sound(SoundType.METAL))
            .transform(pickaxeOnly())
            .register();
    public static final BlockEntry<CCCEncasedCogwheelBlock> COPYCAT_ENCASED_LARGE_COGWHEEL = REGISTRATE
            .block("copycat_encased_large_cogwheel",
                    p-> new CCCEncasedCogwheelBlock(p, true, BlockRegistry.COPYCAT_CASING::get))
            .initialProperties(SharedProperties::softMetal)
            .properties(p->p.mapColor(MapColor.COLOR_GRAY))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(CCCBuilderTransformers.encasedLargeCogwheel("copycat", ()->CreateCopycatCasingSpriteShifts.COPYCAT_CASING))
            .transform(EncasingRegistry.addVariantTo(AllBlocks.LARGE_COGWHEEL))
            .properties(p -> p.sound(SoundType.METAL))
            .transform(pickaxeOnly())
            .register();
    public static final BlockEntry<CCCEncasedPipeBlock> COPYCAT_ENCASED_FLUID_PIPE = REGISTRATE
            .block("copycat_encased_fluid_pipe",
                    p -> new CCCEncasedPipeBlock(p, BlockRegistry.COPYCAT_CASING::get))
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion()
                    .mapColor(MapColor.COLOR_GRAY))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.encasedPipe())
            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(CreateCopycatCasingSpriteShifts.COPYCAT_CASING)))
            .onRegister(CreateRegistrate.casingConnectivity(
                    (block, casingConnectivity) -> casingConnectivity.make(block, CreateCopycatCasingSpriteShifts.COPYCAT_CASING,
                            (state, direction) -> !state.getValue(CCCEncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(direction)))))
            .onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::withAO))
            .loot((lootTables, pipeBlock) -> lootTables.dropOther(pipeBlock, AllBlocks.FLUID_PIPE.get()))
            .properties(p -> p.sound(SoundType.METAL))
            .transform(EncasingRegistry.addVariantTo(AllBlocks.FLUID_PIPE))
            .register();

    public static void register(){

    }
}
