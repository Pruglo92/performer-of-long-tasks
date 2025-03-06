package ru.tasks.performeroflongtasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.tasks.performeroflongtasks.entity.TaskResult;
import ru.tasks.performeroflongtasks.service.TaskService;

import java.util.UUID;

/**
 * Контроллер для управления задачами.
 * Предоставляет REST API для запуска задач и получения их результатов.
 *
 * @author Pruglo92
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    /**
     * Запускает задачу с заданными параметрами.
     *
     * @param min   минимальное значение для генерации случайных чисел
     * @param max   максимальное значение для генерации случайных чисел
     * @param count количество случайных чисел для генерации - 1
     * @return уникальный идентификатор задачи
     */
    @PostMapping("/start")
    public Mono<UUID> startTask(@RequestParam Integer min,
                                @RequestParam Integer max,
                                @RequestParam Integer count) {
        return taskService.startTask(min, max, count);
    }

    /**
     * Получает результат выполнения задачи по её уникальному идентификатору.
     *
     * @param taskId уникальный идентификатор задачи
     * @return результат выполнения задачи
     */
    @GetMapping("/result/{taskId}")
    public Mono<TaskResult> getResult(@PathVariable UUID taskId) {
        return taskService.getResult(taskId);
    }
}