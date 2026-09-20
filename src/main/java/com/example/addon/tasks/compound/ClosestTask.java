package com.example.addon.tasks.compound;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import com.example.addon.tasks.Task;

public interface ClosestTask {

    List<Task> getTasks();

    default Task getClosest() {

        Function<Boolean, Task> get = (b) -> {
            int lowestTime = Integer.MAX_VALUE;
            Task fastestTask = null;

            for (Task task : getTasks()) {
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
        }
        
        return ret;
    }
    
}
