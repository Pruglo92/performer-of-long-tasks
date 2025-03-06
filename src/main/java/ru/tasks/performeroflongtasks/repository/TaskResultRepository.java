package ru.tasks.performeroflongtasks.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tasks.performeroflongtasks.entity.TaskResult;

import java.util.UUID;

/**
 * Репозиторий для работы с результатами задач.
 * Предоставляет методы для получения результатов по идентификатору задачи
 * и по прогрессу выполнения задачи.
 *
 * @author Pruglo92
 */
@Repository
public interface TaskResultRepository extends R2dbcRepository<TaskResult, Long> {

    /**
     * Находит результат задачи по уникальному идентификатору задачи.
     *
     * @param taskId уникальный идентификатор задачи
     * @return результат выполнения задачи
     */
    Mono<TaskResult> findByTaskId(UUID taskId);

    /**
     * Находит все результаты задач с прогрессом выполнения меньше указанного.
     *
     * @param progress прогресс задачи
     * @return поток результатов задач с прогрессом меньше заданного
     */
    Flux<TaskResult> findByProgressLessThan(int progress);
}