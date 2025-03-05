package ru.tasks.performeroflongtasks.integration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.tasks.performeroflongtasks.controller.TaskController;
import ru.tasks.performeroflongtasks.entity.TaskResult;
import ru.tasks.performeroflongtasks.service.TaskService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.tasks.performeroflongtasks.utils.TaskUtil.generateTaskId;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private WebTestClient webTestClient;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        taskId = generateTaskId(1, 100, 10);
        webTestClient = WebTestClient.bindToController(taskController).build();
    }

    @Test
    @DisplayName("Проверка на корректный запуск задачи.")
    void givenValidParameters_whenStartTask_thenReturnTaskId() {
        when(taskService.startTask(anyInt(), anyInt(), anyInt())).thenReturn(Mono.just(taskId));

        webTestClient.post()
                .uri("/task/start?min=1&max=100&count=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class)
                .isEqualTo(taskId);

        verify(taskService, times(1)).startTask(1, 100, 10);
    }

    @Test
    @DisplayName("Проверка на запуск существующей задачи.")
    void givenExistingTaskId_whenStartTask_thenReturnBadRequest() {
        when(taskService.startTask(anyInt(), anyInt(), anyInt()))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)));

        webTestClient.post()
                .uri("/task/start?min=1&max=100&count=10")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("Проверка на получение результата задачи по ИД.")
    void givenExistingTaskId_whenGetResult_thenReturnTaskResult() {
        TaskResult taskResult = new TaskResult(taskId);
        when(taskService.getResult(taskId)).thenReturn(Mono.just(taskResult));

        webTestClient.get()
                .uri("/task/result/" + taskId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.taskId").isEqualTo(taskId.toString())
                .jsonPath("$.result").isArray()
                .jsonPath("$.progress").isEqualTo(0);

        verify(taskService, times(1)).getResult(taskId);
    }

    @Test
    @DisplayName("Проверка на получение несуществующего результата задачи.")
    void givenNonExistentTaskId_whenGetResult_thenReturnNotFound() {
        when(taskService.getResult(taskId)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/task/result/" + taskId)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        verify(taskService, times(1)).getResult(taskId);
    }
}