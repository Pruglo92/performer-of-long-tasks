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

@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/start")
    public Mono<UUID> startTask(@RequestParam Integer min,
                                @RequestParam Integer max,
                                @RequestParam Integer count) {
        return taskService.startTask(min, max, count);
    }

    @GetMapping("/result/{taskId}")
    public Mono<TaskResult> getResult(@PathVariable UUID taskId) {
        return taskService.getResult(taskId);
    }
}