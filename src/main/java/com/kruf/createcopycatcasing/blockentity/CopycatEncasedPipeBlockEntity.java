package com.kruf.createcopycatcasing.blockentity;

import com.kruf.createcopycatcasing.copycat.CopycatMaterial;
import com.kruf.createcopycatcasing.copycat.ICopycatMaterialBlockEntity;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CopycatEncasedPipeBlockEntity extends FluidPipeBlockEntity implements ICopycatMaterialBlockEntity {
    private final CopycatMaterial copycat = new CopycatMaterial(this);
    public CopycatEncasedPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public CopycatMaterial getCopycatMaterial() {
        return copycat;
    }
    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider regs, boolean clientPacket) {
        super.write(tag, regs, clientPacket);
        copycat.write(tag, regs);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider regs, boolean clientPacket) {
        super.read(tag, regs, clientPacket);
        if (copycat.read(tag, regs) && clientPacket && level != null) {
            requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 8);
            level.getLightEngine().checkBlock(worldPosition);
        }
        if (copycat.read(tag, regs) && level != null && level.isClientSide)
            redraw();
    }
    private void redraw() {
        requestModelDataUpdate();
        if (!isVirtual()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 8);
            level.getLightEngine().checkBlock(worldPosition);
        }
    }
    @Override
    public ModelData getModelData() {
        return copycat.modelData();
    }
}
