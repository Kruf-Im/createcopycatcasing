package com.kruf.createcopycatcasing.compat.bnb;

import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialBehaviour;
import com.kruf.createcopycatcasing.copycat.CopycatMaterial;
import com.kruf.createcopycatcasing.copycat.ICopycatEncasedBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

class CopycatCogwheelMaterialBehaviour extends CogwheelMaterialBehaviour {
    CopycatCogwheelMaterialBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public void onItemUse(UseItemOnBlockEvent event) {
        CopycatMaterial cm = ICopycatEncasedBlock.copycatAt(getLevel(), getPos());
        if (cm != null && !cm.has())
            return; // empty casing: let the planks become the casing's material
        super.onItemUse(event);
    }
}