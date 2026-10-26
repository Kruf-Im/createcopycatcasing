package com.kruf.createcopycatcasing.registry;

import com.kruf.createcopycatcasing.CreateCopycatCasingSpriteShifts;
import com.kruf.createcopycatcasing.block.CopycatCasingBlock;
/*import com.kruf.createcopycatcasing.block.CopycatEncasedCogwheelBlock;
import com.kruf.createcopycatcasing.block.CCCEncasedPipeBlock;*/
//import com.kruf.createcopycatcasing.block.CopycatEncasedCogwheelBlock;
import com.kruf.createcopycatcasing.block.CopycatEncasedShaftBlock;
import com.kruf.createcopycatcasing.client.CopycatEncasedModel;
import com.kruf.createcopycatcasing.util.CCCBuilderTransformers;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import static com.kruf.createcopycatcasing.CreateCopycatCasing.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

@SuppressWarnings("removal")
public class BlockRegistry {
    static {
        REGISTRATE.setCreativeTab(CreativeTabs.MAIN_TAB);
    }
    public static final BlockEntry<CopycatCasingBlock> COPYCAT_CASING = REGISTRATE
            .block("copycat_casing", CopycatCasingBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(axeOrPickaxe())
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(CCCBuilderTransformers.casing(() -> CreateCopycatCasingSpriteShifts.COPYCAT_CASING))
            .properties(p -> p.sound(SoundType.METAL))
            .tag(AllTags.AllBlockTags.COPYCAT_DENY.tag)
            .register();
    public static final BlockEntry<CopycatEncasedShaftBlock> COPYCAT_ENCASED_SHAFT = REGISTRATE
            .block("copycat_encased_shaft",
                    p -> new CopycatEncasedShaftBlock(p, BlockRegistry.COPYCAT_CASING::get))
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .sound(SoundType.METAL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(EncasingRegistry.addVariantTo(AllBlocks.SHAFT))
            .transform(axeOrPickaxe())
            .blockstate((c,p) -> BlockStateGen.axisBlock(c,p,
                    s -> p.models().getExistingFile(p.modLoc("block/copycat_encased_shaft/block_copycat")), true))
            .loot((lt,b) -> lt.dropOther(b, AllBlocks.SHAFT.get()))
            .onRegister(CreateRegistrate.blockModel(() -> CopycatEncasedModel::new))
            .tag(AllTags.AllBlockTags.COPYCAT_DENY.tag)
            .register();
//    public static final BlockEntry<CopycatEncasedCogwheelBlock> COPYCAT_ENCASED_COGWHEEL = REGISTRATE
//            .block("copycat_encased_cogwheel",
//                    p-> new CopycatEncasedCogwheelBlock(p, false, BlockRegistry.COPYCAT_CASING::get))
//            .initialProperties(SharedProperties::softMetal)
//            .properties(p->p.mapColor(MapColor.COLOR_GRAY))
//            .properties(BlockBehaviour.Properties::noOcclusion)
//            .addLayer(() -> RenderType::cutoutMipped)
//            .transform(CCCBuilderTransformers.encasedCogwheel("copycat", ()->CreateCopycatCasingSpriteShifts.COPYCAT_CASING))
//            .transform(EncasingRegistry.addVariantTo(AllBlocks.COGWHEEL))
//            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(CreateCopycatCasingSpriteShifts.COPYCAT_CASING,
//                    Couple.create(CreateCopycatCasingSpriteShifts.COPYCAT_ENCASED_COGWHEEL_SIDE,
//                            CreateCopycatCasingSpriteShifts.COPYCAT_ENCASED_COGWHEEL_OTHERSIDE))))
//            .properties(p -> p.sound(SoundType.METAL))
//            .transform(pickaxeOnly())
//            .register();
//    public static final BlockEntry<CopycatEncasedCogwheelBlock> COPYCAT_ENCASED_LARGE_COGWHEEL = REGISTRATE
//            .block("copycat_encased_large_cogwheel",
//                    p-> new CopycatEncasedCogwheelBlock(p, true, BlockRegistry.COPYCAT_CASING::get))
//            .initialProperties(SharedProperties::softMetal)
//            .properties(p->p.mapColor(MapColor.COLOR_GRAY))
//            .properties(BlockBehaviour.Properties::noOcclusion)
//            .addLayer(() -> RenderType::cutoutMipped)
//            .transform(CCCBuilderTransformers.encasedLargeCogwheel("copycat", ()->CreateCopycatCasingSpriteShifts.COPYCAT_CASING))
//            .transform(EncasingRegistry.addVariantTo(AllBlocks.LARGE_COGWHEEL))
//            .properties(p -> p.sound(SoundType.METAL))
//            .transform(pickaxeOnly())
//            .register();
//    public static final BlockEntry<CCCEncasedPipeBlock> COPYCAT_ENCASED_FLUID_PIPE = REGISTRATE
//            .block("copycat_encased_fluid_pipe",
//                    p -> new CCCEncasedPipeBlock(p, BlockRegistry.COPYCAT_CASING::get))
//            .initialProperties(SharedProperties::copperMetal)
//            .properties(p -> p.noOcclusion()
//                    .mapColor(MapColor.COLOR_GRAY))
//            .properties(BlockBehaviour.Properties::noOcclusion)
//            .addLayer(() -> RenderType::cutoutMipped)
//            .transform(pickaxeOnly())
//            .blockstate(BlockStateGen.encasedPipe())
//            .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(CreateCopycatCasingSpriteShifts.COPYCAT_CASING)))
//            .onRegister(CreateRegistrate.casingConnectivity(
//                    (block, casingConnectivity) -> casingConnectivity.make(block, CreateCopycatCasingSpriteShifts.COPYCAT_CASING,
//                            (state, direction) -> !state.getValue(CCCEncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(direction)))))
//            .onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::withAO))
//            .loot((lootTables, pipeBlock) -> lootTables.dropOther(pipeBlock, AllBlocks.FLUID_PIPE.get()))
//            .properties(p -> p.sound(SoundType.METAL))
//            .transform(EncasingRegistry.addVariantTo(AllBlocks.FLUID_PIPE))
//            .register();

    public static void register(){

    }
}
