package ru.tasks.performeroflongtasks.jobs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class TestJob {
    public static Stream<Integer> run(int min, int max, int count) {
        AtomicInteger counter = new AtomicInteger(0);
        return Stream
                .generate(() -> {
                    counter.incrementAndGet();
                    int random = (int) (Math.random() * max + min);
                    return random;
                })
                .takeWhile(n -> counter.get() < count);
    }
}