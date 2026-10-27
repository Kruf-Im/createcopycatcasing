package com.kruf.createcopycatcasing.copycat;

import com.kruf.createcopycatcasing.client.CopycatModelProperties;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nullable;

public class CopycatMaterial {
    private final SmartBlockEntity owner;
    private static final String MATERIAL_KEY = "CopycatMaterial";
    private static final String ITEM_KEY = "CopycatItem";
    private BlockState material = Blocks.AIR.defaultBlockState();
    private ItemStack consumedItem = ItemStack.EMPTY;
    public CopycatMaterial(SmartBlockEntity owner){
        this.owner = owner;
    }
    public BlockState get() {
        return material;
    }

    public ItemStack getConsumedItem() {
        return consumedItem;
    }

    public boolean has() {
        return !material.isAir();
    }
    public void set(BlockState state, ItemStack item) {
        material = state;
        consumedItem = item.isEmpty() ? ItemStack.EMPTY : item.copyWithCount(1);
        owner.notifyUpdate();
        updateLight();
    }
    public void set(BlockState state) {
        material = state;
        owner.notifyUpdate();
        updateLight();
    }
    public void clear() {
        set(Blocks.AIR.defaultBlockState(), ItemStack.EMPTY);
    }
    public void setConsumedItem(ItemStack item) {
        consumedItem = item.isEmpty() ? ItemStack.EMPTY : item.copyWithCount(1);
        owner.setChanged();
    }
    public boolean canCycle() {
        return getCycled() != null;
    }

    public boolean cycle() {
        BlockState next = getCycled();
        if (next == null)
            return false;
        set(next);
        return true;
    }

    @Nullable
    private BlockState getCycled() {
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
    public void write(CompoundTag tag, HolderLookup.Provider regs) {
        tag.put(MATERIAL_KEY, NbtUtils.writeBlockState(material));
        if (!consumedItem.isEmpty())
            tag.put(ITEM_KEY, consumedItem.save(regs));
    }

    public boolean read(CompoundTag tag, HolderLookup.Provider regs) {
        BlockState prev = material;
        material = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound(MATERIAL_KEY));
        consumedItem = tag.contains(ITEM_KEY)
                ? ItemStack.parseOptional(regs, tag.getCompound(ITEM_KEY))
                : ItemStack.EMPTY;
        return prev != material;
    }

    private void updateLight() {
        Level level = owner.getLevel();
        if (level != null)
            level.getLightEngine().checkBlock(owner.getBlockPos());
    }


    public ModelData modelData() {
        return ModelData.builder().with(CopycatModelProperties.MATERIAL, material).build();
    }

}
