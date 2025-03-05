package ru.tasks.performeroflongtasks.junit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.tasks.performeroflongtasks.entity.TaskResult;
import ru.tasks.performeroflongtasks.repository.TaskResultRepository;
import ru.tasks.performeroflongtasks.service.TaskService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.tasks.performeroflongtasks.utils.TaskUtil.generateTaskId;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskResultRepository taskResultRepository;

    @Spy
    @InjectMocks
    private TaskService taskService;

    private UUID taskId;
    private TaskResult taskResult;

    @BeforeEach
    void setUp() {
        taskId = generateTaskId(1, 100, 10);
        taskResult = new TaskResult(taskId);
    }

    @Test
    @DisplayName("Проверка на корректный запуск задачи")
    void givenValidParameters_whenStartTask_thenReturnTaskId() {
        when(taskResultRepository.findByTaskId(any(UUID.class))).thenReturn(Mono.empty());
        when(taskResultRepository.save(any(TaskResult.class))).thenReturn(Mono.just(taskResult));

        Mono<UUID> result = taskService.startTask(1, 100, 10);

        StepVerifier.create(result)
                .expectNext(taskId)
                .verifyComplete();

        verify(taskResultRepository, times(1)).findByTaskId(any(UUID.class));
        verify(taskResultRepository, atLeastOnce()).save(any(TaskResult.class));
    }

    @Test
    @DisplayName("Проверка на создание задачи, когда такая задача уже существует")
    void givenExistingTaskId_whenStartTask_thenThrowTaskAlreadyExistsException() {
        when(taskResultRepository.findByTaskId(any(UUID.class))).thenReturn(Mono.just(taskResult));

        Mono<UUID> result = taskService.startTask(1, 100, 10);

        StepVerifier.create(result)
                .expectError(ResponseStatusException.class)
                .verify();

        verify(taskResultRepository, times(1)).findByTaskId(any(UUID.class));
        verify(taskResultRepository, never()).save(any(TaskResult.class));
    }

    @Test
    @DisplayName("Проверка на получение задачи по ИД")
    void givenTaskId_whenGetResult_thenReturnTaskResult() {
        when(taskResultRepository.findByTaskId(taskId)).thenReturn(Mono.just(taskResult));

        Mono<TaskResult> result = taskService.getResult(taskId);

        StepVerifier.create(result)
                .expectNext(taskResult)
                .verifyComplete();

        verify(taskResultRepository, times(1)).findByTaskId(taskId);
    }

    @Test
    @DisplayName("Проверка на получение несуществующей задачи")
    void givenNonExistentTaskId_whenGetResult_thenReturnEmptyMono() {
        when(taskResultRepository.findByTaskId(taskId)).thenReturn(Mono.empty());

        Mono<TaskResult> result = taskService.getResult(taskId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(taskResultRepository, times(1)).findByTaskId(taskId);
    }

    @Test
    @DisplayName("Проверка на инициализацию задач, когда задач нет.")
    void givenNoIncompleteTasks_whenTaskInitialization_thenDoNothing() {
        when(taskResultRepository.findByProgressLessThan(100)).thenReturn(Flux.empty());

        taskService.taskInitialization();

        verify(taskResultRepository, times(1)).findByProgressLessThan(100);
        verifyNoMoreInteractions(taskResultRepository);
    }

    @Test
    @DisplayName("Проверка на инициализацию существующих задач.")
    void givenIncompleteTasks_whenTaskInitialization_thenTasksAreRestarted() {
        TaskResult taskResult = new TaskResult(UUID.randomUUID());
        taskResult.setProgress(50);

        when(taskResultRepository.findByProgressLessThan(100)).thenReturn(Flux.just(taskResult));
        doNothing().when(taskService).runTask(any(TaskResult.class), anyInt(), anyInt(), anyInt(), anyInt());

        taskService.taskInitialization();

        verify(taskResultRepository, times(1)).findByProgressLessThan(100);
        verify(taskService, times(1)).runTask(eq(taskResult), anyInt(), anyInt(), anyInt(), anyInt());
    }
}