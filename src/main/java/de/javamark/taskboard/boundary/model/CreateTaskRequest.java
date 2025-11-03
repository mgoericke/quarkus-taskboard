package de.javamark.taskboard.boundary.model;

import de.javamark.taskboard.entity.model.Task;
import de.javamark.taskboard.entity.model.Task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateTaskRequest {

    @NotNull
    public Long projectId;

    public Long assignedToId;

    @NotBlank
    public String title;

    public String description;

    public TaskStatus status = TaskStatus.TODO;

    public Task.Priority priority = Task.Priority.MEDIUM;

    public LocalDateTime dueDate;

    public CreateTaskRequest() {}

    public CreateTaskRequest(Long projectId, String title, String description, Long assignedToId) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.assignedToId = assignedToId;
    }
}