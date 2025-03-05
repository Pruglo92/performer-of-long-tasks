package ru.tasks.performeroflongtasks.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tasks.performeroflongtasks.entity.TaskResult;

import java.util.UUID;

@Repository
public interface TaskResultRepository extends R2dbcRepository<TaskResult, Long> {
    Mono<TaskResult> findByTaskId(UUID taskId);

    Flux<TaskResult> findByProgressLessThan(int progress);
}