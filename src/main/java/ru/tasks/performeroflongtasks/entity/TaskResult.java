package ru.tasks.performeroflongtasks.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сущность для хранения результатов выполнения задачи.
 * Хранит данные о задаче, её прогрессе и результатах.
 *
 * @author Pruglo92
 */
@Getter
@Setter
@Table("task_result")
public class TaskResult {

    @Id
    @Column("id")
    private Long id;
    @Column("task_id")
    private UUID taskId;
    @Column("result")
    private List<Integer> result;
    @Column("progress")
    private Integer progress;

    /**
     * Конструктор для инициализации объекта с уникальным идентификатором задачи.
     *
     * @param taskId уникальный идентификатор задачи
     */
    public TaskResult(UUID taskId) {
        this.taskId = taskId;
        this.result = new ArrayList<>();
        this.progress = 0;
    }

    /**
     * Добавляет новое значение в результат задачи и обновляет прогресс выполнения.
     *
     * @param newValue   новое значение для добавления в результат
     * @param totalCount общее количество элементов, которое нужно достичь
     */
    public void addValue(int newValue, int totalCount) {
        this.result.add(newValue);
        this.progress = (int) (((double) this.result.size() / totalCount) * 100);
    }
}