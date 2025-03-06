package ru.tasks.performeroflongtasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Компонент для инициализации задач после запуска приложения.
 * При старте приложения выполняет инициализацию задач через сервис {@link TaskService}.
 *
 * @author Pruglo92
 */
@Component
@RequiredArgsConstructor
public class TaskInitializer {

    private final TaskService taskService;

    /**
     * Инициализирует задачи после завершения запуска приложения.
     * Срабатывает при событии {@link ApplicationReadyEvent}.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void taskInit() {
        taskService.taskInitialization();
    }
}