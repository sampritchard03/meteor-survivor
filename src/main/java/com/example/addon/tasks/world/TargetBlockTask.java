package com.example.addon.tasks.world;

import java.util.function.Predicate;

import com.example.addon.mod;
import com.example.addon.tasks.Task;
import com.example.addon.utils.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public abstract class TargetBlockTask extends Task {

    public BlockPos targetPos;

    public TargetBlockTask(String _name) {
        super(_name);
        mod.bot.targetBlockFilter.add(new Pair<Predicate<Block>, TargetBlockTask>(getPredicate(), this));
    }
    
    public abstract Predicate<Block> getPredicate();
}
