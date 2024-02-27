package com.achievementtracker.util;

import java.util.concurrent.TimeUnit;

public final class TimeUtility {
    private TimeUtility() {}

    public static void waitSeconds(int duration) {
        try {
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void waitMilliseconds(int duration) {
        try {
            TimeUnit.MILLISECONDS.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
