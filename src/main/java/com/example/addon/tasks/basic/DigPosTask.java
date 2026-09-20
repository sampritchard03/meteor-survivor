package com.example.addon.tasks.basic;

import com.example.addon.mod;
import com.example.addon.tasks.Priority;
import com.example.addon.tasks.Task;
import com.example.addon.tasks.TaskSuppliers;

import baritone.api.utils.input.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public abstract class DigPosTask extends Task {

    public final LookAtTask lookAtTask;

    public DigPosTask() {
        super("digPos");
        lookAtTask = new LookAtTask() {

            @Override
            public Vec2 getRot() {
                return LookAtTask.getRotation(Vec3.atCenterOf(targetPos()));
            }

            @Override
            public double getSpeed() {
                return 0.8;
            }
            
        };
    }

    private void setHoldingMouseLeft(boolean holding) {mod.b.getInputOverrideHandler().setInputForceState(Input.CLICK_LEFT, holding);}

    abstract public BlockPos targetPos();

    @Override
    public void onStart() {}

    @Override
    public boolean shouldSearch() {return false;}

    @Override
    public void search() {}

    @Override
    public Task onTick() {
        if (!lookAtTask.isFinished()) {
            return lookAtTask;
        }

        setHoldingMouseLeft(true);
        return null;
    }

    @Override
    public int timeEstimate() {
        BlockPos pos = targetPos();
        if (pos == null) return Integer.MAX_VALUE;
        BlockState state = mod.mc.level.getBlockState(pos);
        float hardness = state.getDestroySpeed(mod.mc.level, pos);
        if (hardness < 0) return Integer.MAX_VALUE; // unbreakable

        ItemStack tool = mod.bot.inventory.getBestTool(state);
        boolean correctTool = !tool.isEmpty() && tool.isCorrectToolForDrops(state);
        float digSpeed = tool.isEmpty() ? 1.0F : tool.getDestroySpeed(state);

        float progress = hardness == 0 ? 1.0F : digSpeed / hardness / (correctTool ? 30F : 100F);
        if (progress > 0)
            return -(int)(-(1.0F / progress));

        return 0;
    }

    @Override
    public int priority() {
        return Priority.DEFAULT;
    }

    @Override
    public boolean isFinished() {
        BlockPos targetPos = targetPos();
        if (targetPos == null) return true;
        return mod.mc.level.getBlockState(targetPos()).isAir();
    }

    @Override
    public void onStop() {
        setHoldingMouseLeft(false);
    }

    @Override
    public boolean isEqual(Task o) {
        return o instanceof DigPosTask;
    }

    @Override
    public String debugInfo() {
        return "";
    }

}
