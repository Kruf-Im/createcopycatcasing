package com.kruf.createcopycatcasing.ponder;

import com.kruf.createcopycatcasing.block.CopycatEncasedShaftBlock;
import com.kruf.createcopycatcasing.blockentity.CopycatEncasedKineticBlockEntity;
import com.kruf.createcopycatcasing.registry.BlockRegistry;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CCCKineticsScenes {
    public static void shaftsCanBeCopycatEncased(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("shaft_copycat_casing", "Encasing Shafts with Copycat Casing");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        Selection cog = util.select().position(5,0,1);
        Selection shaft = util.select().cuboid(new BlockPos(0, 1, 2), new Vec3i(5, 0, 2)).add(cog);
        Selection wall = util.select().everywhere()
                .substract(util.select().layer(0))
                .substract(shaft);
        BlockPos first = util.grid().at(3, 1, 2);
        BlockPos second = util.grid().at(1, 1, 2);

        BlockState encased = BlockRegistry.COPYCAT_ENCASED_SHAFT.getDefaultState()
                .setValue(CopycatEncasedShaftBlock.AXIS, Direction.Axis.X);
        ItemStack casingItem = BlockRegistry.COPYCAT_CASING.asStack();

        scene.world().showSection(shaft, Direction.DOWN);
        scene.idle(20);

        // Step 1: encase two shafts
        scene.overlay().showControls(util.vector().topOf(first), Pointing.DOWN, 40).rightClick()
                .withItem(casingItem);
        scene.idle(7);
        scene.world().setBlocks(util.select().position(first), encased, true);
        scene.world().setKineticSpeed(shaft, 32);
        scene.idle(15);

        scene.overlay().showControls(util.vector().topOf(second), Pointing.DOWN, 40).rightClick()
                .withItem(casingItem);
        scene.idle(7);
        scene.world().setBlocks(util.select().position(second), encased, true);
        scene.world().setKineticSpeed(shaft, 32);
        scene.idle(15);

        scene.overlay().showText(70)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Copycat Casing can be used to encase Shafts")
                .pointAt(util.vector().topOf(second));
        scene.idle(80);

        // Step 2: apply a material
        scene.overlay().showControls(util.vector().topOf(first), Pointing.DOWN, 40).rightClick()
                .withItem(new ItemStack(Blocks.BRICKS));
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(util.select().position(first), CopycatEncasedKineticBlockEntity.class,
                nbt -> nbt.put("CopycatMaterial", NbtUtils.writeBlockState(Blocks.BRICKS.defaultBlockState())),
                true);
        scene.idle(10);

        scene.world().showSection(wall, Direction.DOWN);
        scene.idle(15);

        scene.overlay().showText(90)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Right-click it with a block to give the casing that block's appearance, so it can blend into your builds")
                .pointAt(util.vector().topOf(first));
        scene.idle(100);
    }
    public static void cogwheelsCanBeCopycatEncased(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("cogwheel_copycat_casing", "Encasing Cogwheels with Copycat Casing");

        Selection cogFloor = util.select().position(5, 0, 3);
        Selection floor = util.select().layer(0).substract(cogFloor);
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(floor, Direction.UP);


        Selection large1 = util.select().position(4, 1, 3).add(cogFloor);
        Selection small1 = util.select().fromTo(3, 1, 2, 3, 2, 2);
        Selection small2 = util.select().position(2, 1, 2);
        Selection large2 = util.select().fromTo(1, 1, 3, 1, 1, 4);
        Selection shaft2 = util.select().position(2, 2, 2);

        scene.world().setKineticSpeed(small1, -32);
        scene.world().setKineticSpeed(small2, 32);
        scene.world().setKineticSpeed(util.select().position(4, 1, 3), 16);
        scene.world().setKineticSpeed(util.select().position(1, 1, 3), -16);
        scene.world().setKineticSpeed(util.select().position(1, 1, 4), 16);
        scene.world().setKineticSpeed(cogFloor, -16);
        scene.idle(10);

        BlockPos smallWithShaft = util.grid().at(3, 1, 2);
        BlockPos smallPlain = util.grid().at(2, 1, 2);
        BlockPos large = util.grid().at(1, 1, 3);

        BlockState copycatSmall = BlockRegistry.COPYCAT_ENCASED_COGWHEEL.getDefaultState()
                .setValue(EncasedCogwheelBlock.AXIS, Direction.Axis.Y);
        BlockState copycatLarge = BlockRegistry.COPYCAT_ENCASED_LARGE_COGWHEEL.getDefaultState()
                .setValue(EncasedCogwheelBlock.AXIS, Direction.Axis.Y);
        ItemStack casingItem = BlockRegistry.COPYCAT_CASING.asStack();

        scene.world().setKineticSpeed(shaft2, 0);
        scene.idle(10);

        scene.world().showSection(large1, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(small1, Direction.DOWN);
        scene.world().showSection(small2, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(large2, Direction.EAST);
        scene.idle(20);



        // Step 1: encase the cogwheels with copycat casing
        scene.overlay().showControls(util.vector().topOf(3, 0, 2), Pointing.UP, 60).rightClick()
                .withItem(casingItem);
        scene.idle(7);
        scene.world().setBlocks(util.select().position(smallWithShaft),
                copycatSmall.setValue(EncasedCogwheelBlock.TOP_SHAFT, true), true);
        scene.world().setKineticSpeed(util.select().position(smallWithShaft), -32);
        scene.idle(15);

        scene.overlay().showControls(util.vector().topOf(smallPlain), Pointing.DOWN, 30).rightClick()
                .withItem(casingItem);
        scene.idle(7);
        scene.world().setBlocks(small2, copycatSmall, true);
        scene.world().setKineticSpeed(small2, 32);
        scene.idle(15);

        scene.overlay().showControls(util.vector().topOf(1, 0, 3), Pointing.UP, 60).rightClick()
                .withItem(casingItem);
        scene.idle(7);
        scene.world().setBlocks(util.select().position(large), copycatLarge, true);
        scene.world().setKineticSpeed(util.select().position(large), -16);
        scene.idle(10);

        scene.overlay().showText(70)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Copycat Casing can be used to encase Cogwheels")
                .pointAt(util.vector().topOf(large));
        scene.idle(80);

        // Step 2: apply materials
        scene.overlay().showControls(util.vector().topOf(large), Pointing.DOWN, 40).rightClick()
                .withItem(new ItemStack(Blocks.OAK_PLANKS));
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(util.select().position(large), CopycatEncasedKineticBlockEntity.class,
                nbt -> nbt.put("CopycatMaterial", NbtUtils.writeBlockState(Blocks.OAK_WOOD.defaultBlockState())),
                true);
        scene.idle(15);

        scene.overlay().showControls(util.vector().topOf(smallPlain), Pointing.DOWN, 40).rightClick()
                .withItem(new ItemStack(Blocks.BRICKS));
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(util.select().position(smallPlain), CopycatEncasedKineticBlockEntity.class,
                nbt -> nbt.put("CopycatMaterial", NbtUtils.writeBlockState(Blocks.BRICKS.defaultBlockState())),
                true);
        scene.idle(15);

        scene.overlay().showText(80)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Right-click them with a block to give the casing that block's appearance")
                .pointAt(util.vector().topOf(smallPlain));
        scene.idle(90);

        // Step 3: shaft connections (same as Create's scene)
        ElementLink<WorldSectionElement> shaftLink = scene.world().showIndependentSection(shaft2, Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(90)
                .placeNearTarget()
                .colored(PonderPalette.RED)
                .attachKeyFrame()
                .text("Components added after encasing will not connect to the shaft outputs")
                .pointAt(util.vector().centerOf(2, 2, 2));
        scene.idle(90);

        scene.world().moveSection(shaftLink, new Vec3(0, .5f, 0), 10);
        scene.idle(10);

        scene.addKeyframe();
        Vec3 wrenchHere = util.vector().topOf(smallPlain).add(.25, 0, -.25);
        scene.overlay().showControls(wrenchHere, Pointing.RIGHT, 25).rightClick()
                .withItem(AllItems.WRENCH.asStack());
        scene.idle(7);
        scene.world().cycleBlockProperty(smallPlain, EncasedCogwheelBlock.TOP_SHAFT);
        scene.idle(15);
        scene.world().moveSection(shaftLink, new Vec3(0, -.5f, 0), 10);
        scene.idle(10);
        scene.world().setKineticSpeed(shaft2, 32);
        scene.effects().rotationDirectionIndicator(util.grid().at(2, 2, 2));
        scene.idle(20);

        scene.overlay().showText(90)
                .placeNearTarget()
                .colored(PonderPalette.GREEN)
                .text("The Wrench can be used to toggle connections")
                .pointAt(wrenchHere.add(-.5, 0, .5));
        scene.idle(40);

        scene.overlay().showControls(wrenchHere, Pointing.RIGHT, 25).rightClick()
                .withItem(AllItems.WRENCH.asStack());
        scene.idle(7);
        scene.world().cycleBlockProperty(smallPlain, EncasedCogwheelBlock.TOP_SHAFT);
        scene.world().setKineticSpeed(shaft2, 0);
    }
}
