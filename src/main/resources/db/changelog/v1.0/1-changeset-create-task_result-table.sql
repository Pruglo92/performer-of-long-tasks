--liquibase formatted sql

--changeset pruglo-ve:20250301 failOnError:true
--comment: Create task_result table.
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_name = 'task_result';
CREATE TABLE IF NOT EXISTS task_result
(
    id       BIGSERIAL PRIMARY KEY,
    task_id  UUID,
    result   INTEGER[],
    progress INTEGER
);