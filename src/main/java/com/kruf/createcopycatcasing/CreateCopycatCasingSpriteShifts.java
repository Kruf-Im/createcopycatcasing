package com.kruf.createcopycatcasing;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;
import net.minecraft.resources.ResourceLocation;

public class CreateCopycatCasingSpriteShifts {
    public static final CTSpriteShiftEntry COPYCAT_CASING = CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL,
            ResourceLocation.fromNamespaceAndPath(CreateCopycatCasing.MOD_ID, "block/copycat_casing"),
            ResourceLocation.fromNamespaceAndPath(CreateCopycatCasing.MOD_ID, "block/copycat_casing_connected")),
//            omnidirectional("copycat_casing"),
        COPYCAT_ENCASED_COGWHEEL_SIDE = CTSpriteShifter.getCT(AllCTTypes.VERTICAL,
        ResourceLocation.fromNamespaceAndPath(CreateCopycatCasing.MOD_ID, "block/copycat_encased_cogwheel_side"),
        ResourceLocation.fromNamespaceAndPath(CreateCopycatCasing.MOD_ID, "block/copycat_encased_cogwheel_side_connected")),
//        vertical("copycat_encased_cogwheel_side"),
        COPYCAT_ENCASED_COGWHEEL_OTHERSIDE = CTSpriteShifter.getCT(AllCTTypes.HORIZONTAL,
        ResourceLocation.fromNamespaceAndPath(CreateCopycatCasing.MOD_ID, "block/copycat_encased_cogwheel_side"),
        ResourceLocation.fromNamespaceAndPath(CreateCopycatCasing.MOD_ID, "block/copycat_encased_cogwheel_side_connected"));
//    horizontal("copycat_encased_cogwheel_side");



    private static SpriteShiftEntry get(String location, String targetLocation){
        return SpriteShifter.get(CreateCopycatCasing.asResource(location), CreateCopycatCasing.asResource(targetLocation));
    }
    private static SpriteShiftEntry getCreate(String location, String targetLocation){
        return SpriteShifter.get(Create.asResource(location), Create.asResource(targetLocation));
    }
    private static SpriteShiftEntry getCreate(String location){
        return SpriteShifter.get(Create.asResource(location), CreateCopycatCasing.asResource(location));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTexture) {
        return getCT(type, blockTexture, blockTexture);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTexture, String connectedBlockTexture){
        return CTSpriteShifter.getCT(type,
                CreateCopycatCasing.asResource("block/" + blockTexture),
                CreateCopycatCasing.asResource("block/" + connectedBlockTexture + "_connected"));
    }
    private static CTSpriteShiftEntry horizontal(String name){
        return getCT(AllCTTypes.HORIZONTAL, name);
    }
    private static CTSpriteShiftEntry vertical(String name){
        return getCT(AllCTTypes.VERTICAL, name);
    }
    private static CTSpriteShiftEntry omnidirectional(String name){
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }
}
