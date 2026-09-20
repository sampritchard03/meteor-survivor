package com.example.addon.tasks.basic;

import com.example.addon.mod;
import com.example.addon.tasks.Task;

import baritone.api.utils.input.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public abstract class PlaceBlockTask extends DigPosTask {

    public PlaceBlockTask() {
        super("placeBlock");

    }

    abstract public Item toPlace();

    @Override 
    protected void setHoldingMouse(boolean holding) {
        if (mod.mc.level.getBlockState(targetPos()).isAir())
            mod.b.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, holding);
        else super.setHoldingMouse(holding);
    }

    @Override
    public boolean isFinished() {
        BlockPos targetPos = targetPos();
        if (targetPos == null) return false;
        return mod.mc.level.getBlockState(targetPos()).getBlock().asItem() == toPlace();
    }

    @Override
    public Task onTick() {
        mod.bot.inventory.equip(toPlace());
        if (
            mod.mc.level.getBlockState(targetPos()).isAir() &&
            targetPos().getY() <= mod.bot.iy() && targetPos().getX() == mod.bot.ix() && targetPos().getZ() == mod.bot.iz()
        )
            mod.b.getInputOverrideHandler().setInputForceState(Input.JUMP, true);
        return super.onTick();
    }

}
