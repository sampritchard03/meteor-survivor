package com.example.addon.tasks.compound;

import java.util.List;

import com.example.addon.mod;
import com.example.addon.tasks.Task;

public class ClosestTask extends Task implements IClosestTask {
    public final List<Task> tasks;
    private Task closest;

    public ClosestTask(String _name, List<Task> _tasks) {
        super(_name);
        tasks = _tasks;
    }

    @Override
    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public void onStart() {closest = getClosest();}
    @Override
    public boolean shouldSearch() {return false;}

    @Override
    public void search() {
        if (closest == null) return;
        if (closest.shouldSearch()) closest.search();
    }

    @Override
    public Task onTick() {
        closest = getClosest();
        mod.log(""+closest.toString());
        return closest;
    }

    @Override
    public int timeEstimate() {
        if (closest == null) return Integer.MAX_VALUE;
        return closest.timeEstimate();
    }

    @Override
    public int priority() {
        if (closest == null) return Integer.MIN_VALUE;
        return closest.priority();
    }

    @Override
    public boolean isFinished() {
        closest = getClosest();
        if (closest == null) return true;
        return closest.isFinished();
    }

    @Override
    public void onStop() {
        closest = null;
    }

    @Override
    public boolean isEqual(Task o) {
        return false;
    }

    @Override
    public String debugInfo() {
        return "";
    }
    
}
