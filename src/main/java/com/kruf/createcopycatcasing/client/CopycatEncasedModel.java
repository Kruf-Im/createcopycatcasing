package com.kruf.createcopycatcasing.client;

import com.kruf.createcopycatcasing.blockentity.CopycatEncasedKineticBlockEntity;
import com.kruf.createcopycatcasing.copycat.ICopycatMaterialBlockEntity;
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

public class CopycatEncasedModel extends BakedModelWrapper<BakedModel> {
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
        Direction.Axis shaftAxis = state != null && state.hasProperty(BlockStateProperties.AXIS)
                ? state.getValue(BlockStateProperties.AXIS) : null;

        BlockState material = extraData.get(CopycatModelProperties.MATERIAL);

        if (material == null || material.isAir()) {
            List<BakedQuad> base = super.getQuads(state, side, rand, extraData, renderType);
            if (occluded == 0 || shaftAxis == null)
                return base;
            List<BakedQuad> out = new ArrayList<>(base.size());
            for (BakedQuad q : base)
                if (!isOnOccludedEnd(q, shaftAxis, occluded))
                    out.add(q);
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

        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        List<BakedQuad> result = new ArrayList<>(quads.size());
        boolean seeThrough = !material.canOcclude();

        for (BakedQuad quad : quads) {
            boolean boundary = isOnBoundary(quad);
            boolean isCasing = CASING_TEXTURES.contains(quad.getSprite().contents().name());

            if (boundary && (occluded & (1 << quad.getDirection().ordinal())) != 0)
                continue;

            boolean isEndFace = shaftAxis != null && quad.getDirection().getAxis() == shaftAxis;
            if (seeThrough && !isEndFace && (!isCasing || !boundary))
                continue;

            if (seeThrough && isEndFace && isOnOccludedEnd(quad, shaftAxis, occluded))
                continue;

            if (isCasing) {
                if (!drawMaterial) continue;
                List<BakedQuad> srcs = findMaterialQuads(materialModel, material, quad.getDirection(), rand, wrapped);
                if (srcs.isEmpty())
                    result.add(quad);
                else
                    for (BakedQuad src : srcs)
                        result.add(retextureFrom(quad, src));
            } else if (drawOwn) {
                result.add(quad);
            }
        }
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

    // Copy UVs from the material quad by vertex position on the face plane
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
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        BlockState material = modelData.get(CopycatModelProperties.MATERIAL);

        if (material == null || material.isAir())
            return super.getModelData(level, pos, state, modelData).derive()
                    .with(CopycatModelProperties.OCCLUDED_FACES, emptyOcclusion(level, pos, state))
                    .build();

        CopycatAppearanceGetter getter = new CopycatAppearanceGetter(level);

        int occluded = 0;
        for (Direction d : Direction.values())
            if (!Block.shouldRenderFace(material, getter, pos, d, pos.relative(d)))
                occluded |= 1 << d.ordinal();

        BakedModel materialModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(material);
        ModelData wrapped = materialModel.getModelData(getter, pos, material, ModelData.EMPTY);

        return modelData.derive()
                .with(CopycatModelProperties.WRAPPED_DATA, wrapped)
                .with(CopycatModelProperties.OCCLUDED_FACES, occluded)
                .build();
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
}