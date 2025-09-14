package de.javamark.taskboard.in.model;

import de.javamark.taskboard.out.model.Task.Priority;
import de.javamark.taskboard.out.model.Task.TaskStatus;

import java.time.LocalDateTime;

public class UpdateTaskRequest {

    public String title;
    public String description;
    public TaskStatus status;
    public Priority priority;
    public LocalDateTime dueDate;
    public Long assignedToId;

    public UpdateTaskRequest() {}
}