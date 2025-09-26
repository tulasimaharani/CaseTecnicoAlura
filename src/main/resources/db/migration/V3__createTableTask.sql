CREATE TABLE Task (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    createdAt datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statement varchar(255) NOT NULL,
    _order bigint(20) NOT NULL,
    _type enum('OPEN_TEXT', 'MULTIPLE_CHOICE', 'SINGLE_CHOICE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'OPEN_TEXT',
    course_id bigint(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Course FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE,
    CONSTRAINT UC_Statement UNIQUE (statement)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;