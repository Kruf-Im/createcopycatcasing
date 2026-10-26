package com.kruf.createcopycatcasing.blockentity;

import com.kruf.createcopycatcasing.client.CopycatModelProperties;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class CopycatEncasedKineticBlockEntity extends KineticBlockEntity {
    private BlockState material = Blocks.AIR.defaultBlockState();
    private ItemStack consumedItem = ItemStack.EMPTY;
    public CopycatEncasedKineticBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }
    public BlockState getMaterial(){
        return material;
    }

    public ItemStack getConsumedItem() {
        return consumedItem;
    }
    public boolean hasMaterial(){
        return !material.isAir();
    }
    public void setMaterial(BlockState state, ItemStack item){
        material = state;
        consumedItem = item.copyWithCount(1);
        notifyUpdate();
    }
    public void clearMaterial(){
        setMaterial(Blocks.AIR.defaultBlockState(), ItemStack.EMPTY);
    }
    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider regs, boolean clientPacket) {
        super.write(tag, regs, clientPacket);
        tag.put("Material", NbtUtils.writeBlockState(material));
        if (!consumedItem.isEmpty())
            tag.put("Item", consumedItem.save(regs));
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider regs, boolean clientPacket) {
        super.read(tag, regs, clientPacket);
        BlockState prev = material;
        material = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("Material"));
        consumedItem = tag.contains("Item")
                ? ItemStack.parseOptional(regs, tag.getCompound("Item"))
                : ItemStack.EMPTY;

        if (clientPacket && prev != material && level != null) {
            requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 8);
        }
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(CopycatModelProperties.MATERIAL, material).build();
    }
    public void setMaterial(BlockState state) {
        material = state;
        notifyUpdate();
    }

    public boolean canCycle() {
        return getCycledMaterial() != null;
    }

    public boolean cycleMaterial() {
        BlockState next = getCycledMaterial();
        if (next == null)
            return false;
        setMaterial(next);
        return true;
    }

    public void setConsumedItem(ItemStack item) {
        consumedItem = item.isEmpty() ? ItemStack.EMPTY : item.copyWithCount(1);
        setChanged();
    }

    @Nullable
    private BlockState getCycledMaterial() {
        BlockState m = material;

        if (m.hasProperty(TrapDoorBlock.HALF) && m.getOptionalValue(TrapDoorBlock.OPEN).orElse(false))
            return m.cycle(TrapDoorBlock.HALF);
        if (m.hasProperty(BlockStateProperties.FACING))
            return m.cycle(BlockStateProperties.FACING);
        if (m.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
            return m.setValue(BlockStateProperties.HORIZONTAL_FACING,
                    m.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
        if (m.hasProperty(BlockStateProperties.AXIS))
            return m.cycle(BlockStateProperties.AXIS);
        if (m.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
            return m.cycle(BlockStateProperties.HORIZONTAL_AXIS);
        if (m.hasProperty(BlockStateProperties.LIT))
            return m.cycle(BlockStateProperties.LIT);
        if (m.hasProperty(BlockStateProperties.ATTACH_FACE))
            return m.cycle(BlockStateProperties.ATTACH_FACE);

        return null;
    }
}
