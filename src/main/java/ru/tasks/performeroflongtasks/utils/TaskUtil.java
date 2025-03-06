package ru.tasks.performeroflongtasks.utils;

import java.util.UUID;

/**
 * Утилитный класс для работы с задачами.
 * Включает методы для генерации уникального идентификатора задачи
 * и декодирования идентификатора обратно в исходные параметры задачи.
 * <p>
 * Этот класс не имеет экземпляров, так как все его методы статичны.
 *
 * @author Pruglo92
 */
public class TaskUtil {

    private TaskUtil() {
    }

    /**
     * Генерирует уникальный идентификатор задачи на основе заданных параметров.
     * Этот метод использует параметры задачи (min, max, count) для создания
     * уникального идентификатора типа {@link UUID}.
     *
     * @param min   минимальное значение для генерации задачи
     * @param max   максимальное значение для генерации задачи
     * @param count количество элементов для задачи
     * @return уникальный идентификатор задачи в формате {@link UUID}
     */
    public static UUID generateTaskId(int min, int max, int count) {
        long mostSigBits = ((long) min << 32) | (max & 0xFFFFFFFFL);
        long leastSigBits = count & 0xFFFFFFFFL;
        return new UUID(mostSigBits, leastSigBits);
    }


    /**
     * Декодирует уникальный идентификатор задачи обратно в исходные параметры.
     * Извлекает значения для min, max и count из переданного {@link UUID}.
     *
     * @param taskId уникальный идентификатор задачи в формате {@link UUID}
     * @return массив с тремя значениями: min, max и count
     */
    public static int[] decodeTaskId(UUID taskId) {
        long mostSigBits = taskId.getMostSignificantBits();
        long leastSigBits = taskId.getLeastSignificantBits();

        int num1 = (int) (mostSigBits >>> 32);
        int num2 = (int) mostSigBits;
        int num3 = (int) leastSigBits;

        return new int[]{num1, num2, num3};
    }
}