package com.example.addon.tasks.entity;

import java.util.function.Predicate;

import com.example.addon.mod;
import com.example.addon.tasks.Priority;
import com.example.addon.tasks.Task;
import com.example.addon.tasks.basic.LookAtTask;
import com.example.addon.tasks.basic.PathfindTask;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.utils.input.Input;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

public abstract class CollectItemsTask extends TargetEntityTask {

    public final PathfindTask pathfind;
    public final LookAtTask lookAt;
    private GoalGetToBlock goal;

    public CollectItemsTask() {
        super("collectItems");
        
        pathfind = new PathfindTask() {

            @Override
            public Goal getGoal() {
                return goal;
            }
            
        };

        lookAt = new LookAtTask() {

            @Override
            public Vec2 getRot() {
                return LookAtTask.getRotation(targetEntity.getEyePosition());
            }

            @Override
            public double getSpeed() {
                return 0.8;
            }
            
        };
    }

    abstract public Predicate<ItemStack> getItemPredicate();

    public Predicate<Entity> getPredicate() {return (e) -> e instanceof ItemEntity i && getItemPredicate().test(i.getItem());}

    @Override
    public void onStart() {}

    @Override
    public boolean shouldSearch() {return false;}

    @Override
    public void search() {}

    @Override
    public Task onTick() {
        if (targetEntity == null) return null;
        if (mod.bot.isWithinRange(targetEntity.blockPosition(), 2)) {
            mod.b.getInputOverrideHandler().setInputForceState(Input.MOVE_FORWARD, true);
            if (targetEntity.getBlockY() > mod.bot.iy())
                mod.b.getInputOverrideHandler().setInputForceState(Input.JUMP, true);
            return lookAt;
        };
        if (goal == null || !goal.getGoalPos().equals(targetEntity.blockPosition())) goal = new GoalGetToBlock(targetEntity.blockPosition());
        if (!pathfind.isFinished()) return pathfind;
        return null;
    }

    @Override
    public int timeEstimate() {
        return pathfind.timeEstimate();
    }

    @Override
    public int priority() {
        return Priority.COLLECT_ITEMS;
    }

    @Override
    public boolean isFinished() {
        return targetEntity == null;
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isEqual(Task o) {
        return false;
    }

    @Override
    public String debugInfo() {
        return "";
    }
    
}
