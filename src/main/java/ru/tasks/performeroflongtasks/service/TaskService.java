package ru.tasks.performeroflongtasks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.tasks.performeroflongtasks.entity.TaskResult;
import ru.tasks.performeroflongtasks.jobs.TestJob;
import ru.tasks.performeroflongtasks.repository.TaskResultRepository;

import java.util.UUID;

import static java.util.Objects.nonNull;
import static ru.tasks.performeroflongtasks.utils.TaskUtil.decodeTaskId;
import static ru.tasks.performeroflongtasks.utils.TaskUtil.generateTaskId;

/**
 * Сервис для управления задачами.
 * Обеспечивает запуск задач, их сохранение, получение результатов и восстановление незавершённых задач.
 *
 * @author Pruglo92
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskResultRepository taskResultRepository;


    /**
     * Запускает задачу с заданными параметрами.
     * Если задача с таким ID уже существует, возвращается ошибка.
     *
     * @param min   минимальное значение для генерации случайных чисел
     * @param max   максимальное значение для генерации случайных чисел
     * @param count количество случайных чисел для генерации
     * @return уникальный идентификатор задачи
     */
    public Mono<UUID> startTask(Integer min, Integer max, Integer count) {
        UUID taskId = generateTaskId(min, max, count);

        return taskResultRepository.findByTaskId(taskId)
                .flatMap(existingTask -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .switchIfEmpty(Mono.defer(() -> saveTaskResult(taskId)
                        .doOnSuccess(taskResult -> runTask(taskResult, min, max, count, null))
                        .thenReturn(taskId)))
                .ofType(UUID.class);
    }

    /**
     * Запускает выполнение задачи.
     * Генерирует случайные числа и сохраняет их в результат задачи, обновляя прогресс.
     *
     * @param taskResult объект с результатом задачи
     * @param min        минимальное значение для генерации случайных чисел
     * @param max        максимальное значение для генерации случайных чисел
     * @param count      количество случайных чисел для генерации
     * @param remainder  оставшийся прогресс выполнения задачи
     */
    public void runTask(TaskResult taskResult, Integer min, Integer max, Integer count, Integer remainder) {
        Flux.fromStream(TestJob.run(min, max, nonNull(remainder) ? remainder : count))
                .publishOn(Schedulers.boundedElastic())
                .concatMap(i -> {
                    int trueCount = count - 1;
                    taskResult.addValue(i, trueCount);
                    return taskResultRepository.save(taskResult);
                })
                .doOnError(e -> log.error("Ошибка в задаче {}: {}", taskResult.getTaskId(), e.getMessage()))
                .doOnTerminate(() -> log.info("Задача с ID {} завершена.", taskResult.getTaskId()))
                .doOnCancel(() -> log.warn("Задача с ID {} была отменена.", taskResult.getTaskId()))
                .subscribe();
    }

    /**
     * Сохраняет результат задачи в базе данных.
     *
     * @param taskId уникальный идентификатор задачи
     * @return результат задачи
     */
    public Mono<TaskResult> saveTaskResult(UUID taskId) {
        return taskResultRepository.save(new TaskResult(taskId));
    }

    /**
     * Получает результат выполнения задачи по её уникальному идентификатору.
     *
     * @param taskId уникальный идентификатор задачи
     * @return результат выполнения задачи
     */
    public Mono<TaskResult> getResult(UUID taskId) {
        return taskResultRepository.findByTaskId(taskId);
    }


    /**
     * Инициализирует незавершённые задачи после перезапуска приложения.
     * Восстанавливает прогресс задач и продолжает их выполнение.
     */
    public void taskInitialization() {
        taskResultRepository.findByProgressLessThan(100)
                .flatMap(taskResult -> {
                    UUID taskId = taskResult.getTaskId();
                    int[] values = decodeTaskId(taskId);
                    int min = values[0];
                    int max = values[1];
                    int count = values[2];
                    int remainder = count - taskResult.getResult().size();
                    runTask(taskResult, min, max, count, remainder);
                    return Mono.empty();
                })
                .doOnTerminate(() -> log.info("Все незавершённые задачи запущены."))
                .doOnError(e -> log.error("Ошибка в процессе перезапуска: {}", e.getMessage()))
                .doOnCancel(() -> log.warn("Процесс перезапуска был отменён."))
                .subscribe();
    }
}