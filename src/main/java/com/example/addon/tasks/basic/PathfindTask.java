package com.example.addon.tasks.basic;

import com.example.addon.mod;
import com.example.addon.tasks.Priority;
import com.example.addon.tasks.Task;
import com.example.addon.utils.PathUtils;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalGetToBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class PathfindTask extends Task {
    public PathfindTask() {
        super("pathfind");
    }

    abstract public Goal getGoal();

    @Override
    public void onStart() {
        mod.b.getCustomGoalProcess().setGoalAndPath(getGoal());
    }



    @Override
    public boolean shouldSearch() {return false;}



    @Override
    public void search() {}



    @Override
    public Task onTick() {
        Goal goal = getGoal();
        if (!goal.equals(mod.b.getPathingBehavior().getGoal()))
            mod.b.getCustomGoalProcess().setGoalAndPath(goal);
        return null;
    }



    @Override
    public int timeEstimate() {
        double len = PathUtils.pathLength(getGoal());
        if (len >= Double.MAX_VALUE) return Integer.MAX_VALUE;
        return (int)(Math.max(0, len) / 0.07);
    }



    @Override
    public int priority() {
        return Priority.DEFAULT;
    }



    @Override
    public boolean isFinished() {
        Goal goal = getGoal();
        
        return goal == null ? false : goal.isInGoal(mod.bot.blockPos());
    }



    @Override
    public void onStop() {
        mod.b.getPathingBehavior().cancelEverything();
    }



    @Override
    public boolean isEqual(Task o) {
        return o instanceof PathfindTask p && p.getGoal().equals(getGoal());
    }



    @Override
    public String debugInfo() {
        return "";
    }
}

