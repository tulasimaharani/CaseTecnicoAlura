CREATE TABLE _Option(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    createdAt datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    _option varchar(80) NOT NULL,
    isCorrect boolean NOT NULL,
    task_id bigint(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Task FOREIGN KEY (task_id) REFERENCES Task(id) ON DELETE CASCADE,
    CONSTRAINT UC_Task_Option UNIQUE (_option, task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;