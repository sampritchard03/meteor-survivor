package com.example.addon.utils;

import java.util.List;

import com.example.addon.mod;
import com.example.addon.modules.Bot;

import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.Goal;
import baritone.api.utils.BetterBlockPos;

public class PathUtils {
    public static double pathLength(Goal goal) {
        Goal currentGoal = mod.b.getCustomGoalProcess().getGoal();
        boolean isPathing = mod.b.getPathingBehavior().isPathing();
        mod.b.getCustomGoalProcess().setGoal(goal);
        double ret = mod.b.getPathingBehavior().getPath()
                .map(path -> pathLength(path))
                .orElse(-1.0);
        if (isPathing)
            mod.b.getCustomGoalProcess().setGoalAndPath(currentGoal);
        else
            mod.b.getCustomGoalProcess().setGoal(currentGoal);
        return ret;
    }

    public static double pathLength(IPath path) {
        double ret = 0;
        List<BetterBlockPos> positions = path.positions();
        int closestPosIndex = 0;
        double closestSqDist = Double.MAX_VALUE;

        for (int i = 1; i < positions.size(); i++) {
            double sqDist = positions.get(i).distToCenterSqr(mod.bot.pos());
            if (sqDist < closestSqDist) {
                closestSqDist = sqDist;
                closestPosIndex = Math.min(i+1, positions.size()-1);
            }
        }

        ret += closestSqDist;

        for (int j = closestPosIndex; j < positions.size()-1; j++) {
            BetterBlockPos current = positions.get(j);
            BetterBlockPos next = positions.get(j+1);

            ret += current.distanceSq(next);
        }
        return Math.sqrt(ret);
    }
}
