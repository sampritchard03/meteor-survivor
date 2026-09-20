package com.example.addon.tasks.compound;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import com.example.addon.tasks.Task;

public interface IClosestTask {

    List<Task> getTasks();

    default Task getClosest() {

        List<Task> tasks = getTasks();

        if (tasks.size() == 0) return null;
        if (tasks.size() == 1) return tasks.get(0);

        Function<Boolean, Task> get = (b) -> {
            int lowestTime = Integer.MAX_VALUE;
            Task fastestTask = null;

            for (Task task : tasks) {
                if (b ? !task.shouldContinue() : task.isFinished()) continue;
                int time = task.timeEstimate();
                if (time < lowestTime) {
                    lowestTime = time;
                    fastestTask = task;
                }
            }
            return fastestTask;
        };

        Task ret = get.apply(false);
        if (ret == null) {
            ret = get.apply(true);
            if (ret == null) {
                List<Task> unfinishedTasks = tasks.stream().filter((t) -> !t.isFinished()).toList();
                if (!unfinishedTasks.isEmpty()) ret = unfinishedTasks.getFirst();
            }
        }
        
        return ret;
    }
    
}
