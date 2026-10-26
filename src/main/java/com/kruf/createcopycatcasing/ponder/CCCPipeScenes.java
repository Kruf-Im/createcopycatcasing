package com.kruf.createcopycatcasing.ponder;

import com.kruf.createcopycatcasing.blockentity.CopycatEncasedPipeBlockEntity;
import com.kruf.createcopycatcasing.registry.BlockRegistry;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;

public class CCCPipeScenes {
    public static void pipesCanBeCopycatEncased(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("fluid_pipe_copycat_casing", "Encasing Fluid Pipes with Copycat Casing");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().layer(0), Direction.UP);

        BlockPos intoWall = util.grid().at(2, 1, 4);
        BlockPos frontRight = util.grid().at(3, 1, 1);
        BlockPos frontMiddle = util.grid().at(2, 1, 1);

        Selection pipes = util.select().fromTo(0, 1, 0, 4, 1, 3).add(util.select().fromTo(2, 1, 4, 2, 1, 5));
        Selection wall = util.select().everywhere()
                .substract(util.select().layer(0))
                .substract(pipes);

        ItemStack casingItem = BlockRegistry.COPYCAT_CASING.asStack();

        scene.world().showSection(pipes, Direction.DOWN);
        scene.idle(20);

        scene.overlay().showControls(util.vector().topOf(intoWall), Pointing.DOWN, 40).rightClick()
                .withItem(casingItem);
        scene.idle(7);
        scene.world().modifyBlock(intoWall, s -> EncasedPipeBlock.transferSixWayProperties(s,
                BlockRegistry.COPYCAT_ENCASED_FLUID_PIPE.getDefaultState()), true);
        scene.idle(15);

        scene.overlay().showControls(util.vector().topOf(frontRight), Pointing.DOWN, 40).rightClick()
                .withItem(casingItem);
        scene.idle(7);
        scene.world().modifyBlock(frontRight, s -> EncasedPipeBlock.transferSixWayProperties(s,
                BlockRegistry.COPYCAT_ENCASED_FLUID_PIPE.getDefaultState()), true);
        scene.idle(15);

        scene.overlay().showText(70)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Copycat Casing can be used to encase Fluid Pipes")
                .pointAt(util.vector().topOf(frontRight));
        scene.idle(80);

        scene.overlay().showControls(util.vector().topOf(frontMiddle), Pointing.DOWN, 30).leftClick();
        scene.idle(7);
        scene.world().destroyBlock(frontMiddle);
        scene.world().modifyBlock(util.grid().at(1, 1, 1), s -> {
            s = s.setValue(PipeBlock.EAST, false);
            Direction only = null;
            int count = 0;
            for (Direction d : Direction.values())
                if (s.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(d))) {
                    only = d;
                    count++;
                }
            if (count == 1)
                s = s.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(only.getOpposite()), true);
            return s;
        }, false);
        scene.idle(15);

        scene.overlay().showText(80)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Encased pipes keep their connections, even when a neighbouring pipe is removed")
                .pointAt(util.vector().blockSurface(frontRight, Direction.WEST));
        scene.idle(90);

        scene.overlay().showControls(util.vector().topOf(intoWall), Pointing.DOWN, 40).rightClick()
                .withItem(new ItemStack(Blocks.BRICKS));
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(util.select().position(intoWall), CopycatEncasedPipeBlockEntity.class,
                nbt -> nbt.put("Material", NbtUtils.writeBlockState(Blocks.BRICKS.defaultBlockState())),
                true);
        scene.idle(15);

        scene.overlay().showControls(util.vector().topOf(frontRight), Pointing.DOWN, 40).rightClick()
                .withItem(new ItemStack(Blocks.BRICKS));
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(util.select().position(frontRight), CopycatEncasedPipeBlockEntity.class,
                nbt -> nbt.put("Material", NbtUtils.writeBlockState(Blocks.BRICKS.defaultBlockState())),
                true);
        scene.idle(15);

        scene.overlay().showText(80)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Right-click them with a block to give the casing that block's appearance")
                .pointAt(util.vector().topOf(intoWall));
        scene.idle(90);

        scene.world().showSection(wall, Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(80)
                .placeNearTarget()
                .attachKeyFrame()
                .text("This lets pipes run through your builds without breaking their look")
                .pointAt(util.vector().topOf(intoWall));
        scene.idle(90);
    }
}
