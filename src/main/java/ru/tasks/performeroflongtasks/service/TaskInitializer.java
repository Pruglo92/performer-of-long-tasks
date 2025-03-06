package ru.tasks.performeroflongtasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskInitializer {

    private final TaskService taskService;

    @EventListener(ApplicationReadyEvent.class)
    public void taskInit() {
        taskService.taskInitialization();
    }
}