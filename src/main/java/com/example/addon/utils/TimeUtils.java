package com.example.addon.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static void setTimeout(Runnable runnable, int time) {
        Executors
            .newSingleThreadScheduledExecutor()
            .schedule(runnable, time, TimeUnit.MILLISECONDS);
    }
}
