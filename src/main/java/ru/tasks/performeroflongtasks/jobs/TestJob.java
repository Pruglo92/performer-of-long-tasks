package ru.tasks.performeroflongtasks.jobs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Класс для генерации случайных чисел в заданном диапазоне.
 */
public class TestJob {

    /**
     * Генерирует поток случайных чисел в заданном диапазоне и количестве.
     *
     * @param min   минимальное значение диапазона
     * @param max   максимальное значение диапазона
     * @param count количество случайных чисел для генерации
     * @return поток случайных чисел
     */
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