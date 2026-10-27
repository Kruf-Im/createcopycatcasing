package com.kruf.createcopycatcasing.client;

import com.kruf.createcopycatcasing.blockentity.CopycatEncasedKineticBlockEntity;
import com.kruf.createcopycatcasing.copycat.ICopycatEncasedBlock;
import com.kruf.createcopycatcasing.copycat.ICopycatMaterialBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CopycatEncasedModel extends BakedModelWrapperWithData {
    private static final Set<ResourceLocation> CASING_TEXTURES = Set.of(
            ResourceLocation.fromNamespaceAndPath("createcopycatcasing", "block/copycat_casing"));

    public CopycatEncasedModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
                                    ModelData extraData, @Nullable RenderType renderType) {
        Integer occ = extraData.get(CopycatModelProperties.OCCLUDED_FACES);
        int occluded = occ == null ? 0 : occ;
        Integer mg = extraData.get(CopycatModelProperties.MERGED_FACES);
        int merged = mg == null ? 0 : mg;
        Integer ct = extraData.get(CopycatModelProperties.CONTINUED_FACES);
        int continued = ct == null ? 0 : ct;
        Direction.Axis shaftAxis = state != null && state.hasProperty(BlockStateProperties.AXIS)
                ? state.getValue(BlockStateProperties.AXIS) : null;

        BlockState material = extraData.get(CopycatModelProperties.MATERIAL);

        if (material == null || material.isAir()) {
            List<BakedQuad> out = new ArrayList<>();
            for (BakedQuad q : super.getQuads(state, side, rand, extraData, renderType))
                if (shaftAxis == null || occluded == 0 || !isOnOccludedEnd(q, shaftAxis, occluded))
                    out.add(q);
            addInnerQuads(out, state, side, rand, extraData);
            return out;
        }

        BakedModel materialModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(material);
        ModelData wrapped = extraData.get(CopycatModelProperties.WRAPPED_DATA);
        if (wrapped == null) wrapped = ModelData.EMPTY;

        ChunkRenderTypeSet materialTypes = materialModel.getRenderTypes(material, rand, wrapped);
        ChunkRenderTypeSet ownTypes = super.getRenderTypes(state, rand, extraData);
        boolean drawMaterial = renderType == null || materialTypes.contains(renderType);
        boolean drawOwn = renderType == null || ownTypes.contains(renderType);
        if (!drawMaterial && !drawOwn)
            return List.of();

        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, null);
        List<BakedQuad> result = new ArrayList<>(quads.size());
        boolean seeThrough = !material.canOcclude();
        boolean materialTranslucent = materialTypes.contains(RenderType.translucent());
        boolean hasInner = state != null && state.getBlock() instanceof ICopycatEncasedBlock b
                && b.getInnerRenderState(state) != null;

        for (BakedQuad quad : quads) {
            boolean boundary = isOnBoundary(quad);
            boolean isCasing = CASING_TEXTURES.contains(quad.getSprite().contents().name());

            if (boundary && (occluded & (1 << quad.getDirection().ordinal())) != 0) {
                boolean keepOverlay = seeThrough && !isCasing
                        && (continued & (1 << quad.getDirection().ordinal())) == 0;
                if (!keepOverlay)
                    continue;
            }

            boolean isEndFace = shaftAxis != null && quad.getDirection().getAxis() == shaftAxis;
            if (seeThrough && !isEndFace && !boundary)
                continue;

            if (seeThrough && isEndFace && isOnOccludedEnd(quad, shaftAxis, isCasing ? merged : continued))
                continue;

//            if (seeThrough && hasInner && !isCasing && boundary)
//                continue;

            if (isCasing) {
                if (!drawMaterial) continue;
                List<BakedQuad> srcs = findMaterialQuads(materialModel, material, quad.getDirection(), rand, wrapped);
                if (srcs.isEmpty())
                    result.add(quad);
                else
                    for (BakedQuad src : srcs)
                        result.add(retextureFrom(quad, src));
            } else {
                boolean inMaterialLayer = seeThrough && boundary && !materialTranslucent;
                if (inMaterialLayer ? drawMaterial : drawOwn)
                    result.add(quad);
            }
        }
        if (seeThrough && drawOwn)
            addInnerQuads(result, state, side, rand, extraData);
        return result;
    }

    private static List<BakedQuad> findMaterialQuads(BakedModel model, BlockState material, Direction dir,
                                                     RandomSource rand, ModelData wrapped) {
        List<BakedQuad> q = model.getQuads(material, dir, rand, wrapped, null);
        if (!q.isEmpty())
            return q;
        List<BakedQuad> out = new ArrayList<>();
        for (BakedQuad unculled : model.getQuads(material, null, rand, wrapped, null))
            if (unculled.getDirection() == dir)
                out.add(unculled);
        return out;
    }

    private static boolean isOnBoundary(BakedQuad quad) {
        Direction d = quad.getDirection();
        int[] v = quad.getVertices();
        int stride = v.length / 4;
        int axis = d.getAxis().ordinal(); // X=0, Y=1, Z=2
        float target = d.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1f : 0f;
        for (int i = 0; i < 4; i++)
            if (Math.abs(f(v, i, stride, axis) - target) > 1e-4f)
                return false;
        return true;
    }

    private static float centerAlong(BakedQuad quad, Direction.Axis axis) {
        int[] v = quad.getVertices();
        int stride = v.length / 4;
        int idx = axis.ordinal();
        float sum = 0;
        for (int i = 0; i < 4; i++)
            sum += f(v, i, stride, idx);
        return sum / 4;
    }

    private static boolean isOnOccludedEnd(BakedQuad quad, Direction.Axis axis, int occluded) {
        if (quad.getDirection().getAxis() != axis)
            return false;
        float c = centerAlong(quad, axis);
        Direction end = Direction.fromAxisAndDirection(axis,
                c > 0.5f ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
        return (occluded & (1 << end.ordinal())) != 0;
    }

    private static BakedQuad retextureFrom(BakedQuad quad, BakedQuad src) {
        int[] dst = quad.getVertices().clone();
        int[] s = src.getVertices();
        int stride = dst.length / 4;   // 8 ints per vertex (BLOCK format)
        int uvOff = 4;                 // pos(3) + color(1)

        Direction.Axis axis = quad.getDirection().getAxis();
        int ia = axis == Direction.Axis.X ? 1 : 0;           // X: (y,z)  Y: (x,z)  Z: (x,y)
        int ib = axis == Direction.Axis.Z ? 1 : 2;

        float a0 = f(s, 0, stride, ia), b0 = f(s, 0, stride, ib);
        float a1 = f(s, 1, stride, ia), b1 = f(s, 1, stride, ib);
        float a2 = f(s, 2, stride, ia), b2 = f(s, 2, stride, ib);
        float u0 = f(s, 0, stride, uvOff), v0 = f(s, 0, stride, uvOff + 1);
        float u1 = f(s, 1, stride, uvOff), v1 = f(s, 1, stride, uvOff + 1);
        float u2 = f(s, 2, stride, uvOff), v2 = f(s, 2, stride, uvOff + 1);

        float da1 = a1 - a0, db1 = b1 - b0, da2 = a2 - a0, db2 = b2 - b0;
        float det = da1 * db2 - da2 * db1;
        if (Math.abs(det) < 1e-6f)
            return quad;

        for (int i = 0; i < 4; i++) {
            float da = f(dst, i, stride, ia) - a0;
            float db = f(dst, i, stride, ib) - b0;
            float sP = (da * db2 - da2 * db) / det;
            float tP = (da1 * db - da * db1) / det;
            float u = u0 + sP * (u1 - u0) + tP * (u2 - u0);
            float v = v0 + sP * (v1 - v0) + tP * (v2 - v0);
            dst[i * stride + uvOff] = Float.floatToRawIntBits(u);
            dst[i * stride + uvOff + 1] = Float.floatToRawIntBits(v);
        }
        return new BakedQuad(dst, src.getTintIndex(), quad.getDirection(), src.getSprite(), quad.isShade());
    }

    private static float f(int[] data, int vertex, int stride, int offset) {
        return Float.intBitsToFloat(data[vertex * stride + offset]);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        BlockState material = data.get(CopycatModelProperties.MATERIAL);
        if (material != null && !material.isAir())
            return Minecraft.getInstance().getBlockRenderer().getBlockModel(material).getParticleIcon(ModelData.EMPTY);
        return super.getParticleIcon(data);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        ChunkRenderTypeSet own = super.getRenderTypes(state, rand, data);
        BlockState mat = data.get(CopycatModelProperties.MATERIAL);
        if (mat == null || mat.isAir())
            return own;
        ModelData wrapped = data.get(CopycatModelProperties.WRAPPED_DATA);
        if (wrapped == null) wrapped = ModelData.EMPTY;

        ChunkRenderTypeSet material = Minecraft.getInstance().getBlockRenderer()
                .getBlockModel(mat).getRenderTypes(mat, rand, wrapped);
        return ChunkRenderTypeSet.union(own, material);
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter level, BlockPos pos,
                                                BlockState state, ModelData blockEntityData) {
        BlockState inner = state.getBlock() instanceof ICopycatEncasedBlock b ? b.getInnerRenderState(state) : null;
        if (inner != null) {
            BakedModel innerModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(inner);
            builder.with(CopycatModelProperties.INNER_DATA, innerModel.getModelData(level, pos, inner, ModelData.EMPTY));
        }
        BlockState material = blockEntityData.get(CopycatModelProperties.MATERIAL);
        if (material == null && level.getBlockEntity(pos) instanceof ICopycatMaterialBlockEntity be)
            material = be.getCopycatMaterial().get();

        if (material == null || material.isAir())
            return builder.with(CopycatModelProperties.OCCLUDED_FACES, emptyOcclusion(level, pos, state));

        builder.with(CopycatModelProperties.MATERIAL, material);

        CopycatAppearanceGetter getter = new CopycatAppearanceGetter(level);

        int occluded = 0;
        int merged = 0;
        int continued = 0;
        for (Direction d : Direction.values()) {
            BlockPos n = pos.relative(d);
            int bit = 1 << d.ordinal();

            if (!Block.shouldRenderFace(material, getter, pos, d, n))
                occluded |= bit;

            boolean mergesWithMaterial = material.skipRendering(getter.getBlockState(n), d);
            if (mergesWithMaterial)
                merged |= bit;

            BlockState raw = level.getBlockState(n);
            if (mergesWithMaterial && raw != null && raw.getBlock() instanceof ICopycatEncasedBlock)
                continued |= bit;
        }
        BakedModel materialModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(material);
        ModelData wrapped = materialModel.getModelData(getter, pos, material, ModelData.EMPTY);

        return builder
                .with(CopycatModelProperties.WRAPPED_DATA, wrapped)
                .with(CopycatModelProperties.OCCLUDED_FACES, occluded)
                .with(CopycatModelProperties.MERGED_FACES, merged)
                .with(CopycatModelProperties.CONTINUED_FACES, continued);
    }

    private static int emptyOcclusion(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        if (!state.hasProperty(BlockStateProperties.AXIS))
            return 0;
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        int mask = 0;
        for (Direction d : Direction.values()) {
            if (d.getAxis() != axis)
                continue;
            BlockPos n = pos.relative(d);
            BlockState ns = level.getBlockState(n);
            boolean sameEmptyShaft = ns.getBlock() == state.getBlock()
                    && ns.getValue(BlockStateProperties.AXIS) == axis
                    && !(level.getBlockEntity(n) instanceof ICopycatMaterialBlockEntity be && be.getCopycatMaterial().has());
            if (sameEmptyShaft)
                mask |= 1 << d.ordinal();
        }
        return mask;
    }
    private static void addInnerQuads(List<BakedQuad> out, @Nullable BlockState state, @Nullable Direction side,
                                      RandomSource rand, ModelData extraData) {
        if (side != null)
            return;
        if (state == null || !(state.getBlock() instanceof ICopycatEncasedBlock block))
            return;
        BlockState inner = block.getInnerRenderState(state);
        if (inner == null)
            return;

        ModelData innerData = extraData.get(CopycatModelProperties.INNER_DATA);
        if (innerData == null)
            innerData = ModelData.EMPTY;

        BakedModel innerModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(inner);
        addInner(out, innerModel.getQuads(inner, null, rand, innerData, null));
        for (Direction d : Direction.values())
            addInner(out, innerModel.getQuads(inner, d, rand, innerData, null));
    }

    private static void addInner(List<BakedQuad> out, List<BakedQuad> quads) {
        for (BakedQuad q : quads)
            if (!isOnBoundary(q))
                out.add(q);
    }
}