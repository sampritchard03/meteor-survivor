package com.example.addon.tasks.entity;

import java.util.function.Predicate;

import com.example.addon.mod;
import com.example.addon.tasks.Task;
import com.example.addon.tasks.world.TargetBlockTask;
import com.example.addon.utils.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public abstract class TargetEntityTask extends Task {

    public Entity targetEntity;

    public TargetEntityTask(String _name) {
        super(_name);
        mod.bot.targetEntityFilter.add(new Pair<Predicate<Entity>, TargetEntityTask>(getPredicate(), this));
    }
    
    public abstract Predicate<Entity> getPredicate();
}
