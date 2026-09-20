package com.example.addon.tasks.world;

import java.util.List;
import java.util.function.Predicate;

import com.example.addon.mod;
import com.example.addon.item.ItemData;
import com.example.addon.tasks.Priority;
import com.example.addon.tasks.Task;
import com.example.addon.tasks.TaskSuppliers;
import com.example.addon.tasks.basic.DigPosTask;
import com.example.addon.tasks.basic.PathfindTask;
import com.example.addon.tasks.entity.CollectItemsTask;
import com.example.addon.tasks.item.ItemTask;
import com.example.addon.utils.Pair;
import com.example.addon.utils.PathUtils;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalGetToBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class CollectBlocksTask extends TargetBlockTask {

    public final PathfindTask pathfind;
    public final DigPosTask digPos;
    public final CollectItemsTask collectItems;
    private GoalGetToBlock goal;
    private final List<Item> items;

    protected boolean isRequestedItem(ItemStack stack) {
        return items.contains(stack.getItem());
    }

    public CollectBlocksTask(List<Item> _items) {
        super("collectBlocks");
        items = _items;

        pathfind = new PathfindTask() {

            @Override
            public Goal getGoal() {
                return goal;
            }
            
        };

        digPos = new DigPosTask() {

            @Override
            public BlockPos targetPos() {
                return targetPos;
            }

        };

        collectItems = new CollectItemsTask() {

            @Override
            public Predicate<ItemStack> getItemPredicate() {
                return stack -> isRequestedItem(stack);
            }
            
        };
    }

    
    @Override
    public Predicate<Block> getPredicate() {
        return (block) -> {
            for (Item item : items) {
                List<Item> dropsFrom = ItemData.allData.get(item).dropsFrom;
                if (dropsFrom.contains(block.asItem())) return true;
            }
            return false;
        };

    }

    @Override
    public void onStart() {}

    @Override
    public boolean shouldSearch() {return false;}

    @Override
    public void search() {}

    @Override
    public Task onTick() {
        if (!collectItems.isFinished()) return collectItems;
        if (targetPos == null) return null;
        if (mod.bot.isWithinRange(targetPos, 4) && !digPos.isFinished()) {
            ItemData.Data blockData = ItemData.allData.get(mod.mc.level.getBlockState(targetPos).getBlock().asItem());
            return digPos;
        }
        if (goal == null || !goal.getGoalPos().equals(targetPos)) goal = new GoalGetToBlock(targetPos);
        if (!pathfind.isFinished()) return pathfind;
        return null;
    }

    

    @Override
    public int timeEstimate() {
        if (!collectItems.isFinished()) return collectItems.timeEstimate();
        return pathfind.timeEstimate() + digPos.timeEstimate();
    }

    @Override
    public int priority() {
        return !collectItems.isFinished() ? collectItems.priority() : Priority.DEFAULT;
    }

    @Override
    public void onStop() {goal = null;}

    @Override
    public boolean isEqual(Task o) {
        return false;
    }

    @Override
    public String debugInfo() {
        return "";
    }

    
    @Override
    public boolean isFinished() {
        return collectItems.isFinished() && targetPos == null;
    }
}