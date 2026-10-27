package com.kruf.createcopycatcasing.blockentity;

import com.kruf.createcopycatcasing.copycat.CopycatMaterial;
import com.kruf.createcopycatcasing.copycat.ICopycatMaterialBlockEntity;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

public class CopycatEncasedPipeBlockEntity extends FluidPipeBlockEntity implements ICopycatMaterialBlockEntity {
    private final CopycatMaterial copycat = new CopycatMaterial(this);
    public CopycatEncasedPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public BlockState getMaterial()                           { return copycat.get(); }
    public ItemStack getConsumedItem()                        { return copycat.getConsumedItem(); }
    public boolean hasMaterial()                              { return copycat.has(); }
    public void setMaterial(BlockState state, ItemStack item) { copycat.set(state, item); }
    public void setMaterial(BlockState state)                 { copycat.set(state); }
    public void clearMaterial()                               { copycat.clear(); }
    public void setConsumedItem(ItemStack item)               { copycat.setConsumedItem(item); }
    public boolean canCycle()                                 { return copycat.canCycle(); }
    public boolean cycleMaterial()                            { return copycat.cycle(); }

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
        boolean changed = copycat.read(tag, regs);
        if (changed && level != null && level.isClientSide)
            redraw();
    }
    @Override
    public void onLoad() {
        super.onLoad();
        updateLighting();
    }
    private void redraw() {
        requestModelDataUpdate();
        if (!isVirtual()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 8);
//            level.getLightEngine().checkBlock(worldPosition);
            updateLighting();
        }
    }
    private void updateLighting(){
        BlockPos pos = getBlockPos();
        AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
        if(lightManager != null)
            lightManager.setLightAt(pos, getMaterial().getLightEmission(level, pos));
    }
    @Override
    public ModelData getModelData() {
        return copycat.modelData();
    }
}
