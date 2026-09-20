package com.example.addon.tasks;

public abstract class Task {

    public abstract void onStart();
    public abstract boolean shouldSearch();
    public abstract void search();
    public abstract Task onTick();
    public abstract int timeEstimate(); // estimated time required to accomplish the next part of a task
    public abstract int priority();
    public abstract boolean isFinished();
    public abstract void onStop();
    public abstract boolean isEqual(Task o);
    public abstract String debugInfo();
    

    private Task sub = null;
    private boolean first = true;
    private boolean stopped = false;
    private boolean active = false;
    public final String name;

    public Task(String _name) {
        name = _name;
    }

    public void trySearch() {if (shouldSearch()) search();}
    public boolean shouldContinue() {return !isFinished() && active;}
    public boolean isActive() {return active;}
    public Task getSub() {return sub;}

    public void tick() {
        if (first) {
            //Debug.logInternal("Task START: " + this);
            active = true;
            onStart();
            first = false;
            stopped = false;
        }
        if (stopped) return;

        final Task newSub = onTick();
        // We have a sub task
        if (newSub != null) {
            if (!newSub.equals(sub)) {
                // Our sub task is new
                if (sub != null) {
                    // Our previous sub must be interrupted.
                    sub.stop();
                }

                sub = newSub;
            }

            // Run our child
            sub.tick();
        } else {
            // We are null
            if (sub != null && sub.isFinished()) {
                // Our previous sub must be interrupted.
                sub.stop();
                sub = null;
            }
        }
    }
    
    public void reset() {
        first = true;
        active = false;
        stopped = false;
    }

    public void stop() {
        if (!active) return;
        //Debug.logInternal("Task STOP: " + this + ", interrupted by " + interruptTask);

        if (!first) {
            onStop();
        }
        

        if (sub != null && !sub.stopped) {
            sub.stop();
        }

        first = true;
        active = false;
        stopped = true;
    }

    public String getHierarchy() {
        return getHierarchy(false, false);
    }

    public double heuristic() {
        double a = timeEstimate();
        return 1 - a / (a + 1);
    }

    public String getHierarchy(boolean skipDebugInfo, boolean skipHeuristic) {
        final java.util.List<String> hierarchy = new java.util.ArrayList<>();
        double heuristic = Double.MAX_VALUE;
        Task task = this;

        while (task != null) {
            final String info = skipDebugInfo ? name : task.toString();
            hierarchy.add(info != null && !info.isEmpty() ? info : "");

            if (!skipHeuristic && task.sub == null) {
                heuristic = task.timeEstimate();
            }

            task = task.sub;
        }

        final String heuristicText = heuristic < Double.MAX_VALUE
                ? String.format(java.util.Locale.US, "%.3f", heuristic)
                : "Infinity";

        return (skipHeuristic ? "" : heuristicText.substring(0, Math.min(5, heuristicText.length()))
                + " | ") + String.join(" -> ", hierarchy);
    }

    public boolean equals(Object o) {
        return this == o || o instanceof Task task && this.isEqual(task);
    }

    public String toString() {
        return name + " " + debugInfo();
    }
    
}
