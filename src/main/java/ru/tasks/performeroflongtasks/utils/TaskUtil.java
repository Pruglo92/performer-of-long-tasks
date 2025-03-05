package ru.tasks.performeroflongtasks.utils;

import java.util.UUID;

public class TaskUtil {

    private TaskUtil() {
    }

    public static UUID generateTaskId(int min, int max, int count) {
        long mostSigBits = ((long) min << 32) | (max & 0xFFFFFFFFL);
        long leastSigBits = count & 0xFFFFFFFFL;
        return new UUID(mostSigBits, leastSigBits);
    }

    public static int[] decodeTaskId(UUID taskId) {
        long mostSigBits = taskId.getMostSignificantBits();
        long leastSigBits = taskId.getLeastSignificantBits();

        int num1 = (int) (mostSigBits >>> 32);
        int num2 = (int) mostSigBits;
        int num3 = (int) leastSigBits;

        return new int[]{num1, num2, num3};
    }
}