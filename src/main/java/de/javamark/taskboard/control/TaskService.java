package de.javamark.taskboard.control;

import de.javamark.taskboard.boundary.model.CreateTaskRequest;
import de.javamark.taskboard.boundary.model.ProjectStatsResponse;
import de.javamark.taskboard.boundary.model.UpdateTaskRequest;
import de.javamark.taskboard.entity.model.Member;
import de.javamark.taskboard.entity.model.Project;
import de.javamark.taskboard.entity.model.Task;
import de.javamark.taskboard.entity.model.Task.TaskStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class TaskService {

    @Inject
    ConnectionManager connectionManager;

    public Task createTask(CreateTaskRequest request) {
        Project project = Project.findById(request.projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }

        Member assignedTo = null;
        if (request.assignedToId != null) {
            assignedTo = Member.findById(request.assignedToId);
        }

        Task task = new Task();
        task.project = project;
        task.assignedTo = assignedTo;
        task.title = request.title;
        task.description = request.description;
        task.status = request.status;
        task.priority = request.priority;
        task.dueDate = request.dueDate;
        task.persist();

        // WebSocket Benachrichtigung
        String message = String.format("Neue Task '%s' wurde erstellt", task.title);
        connectionManager.broadcastToProject(project.id,
                createUpdateMessage("task_created", task, message));

        return task;
    }

    public Task updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = Task.findById(taskId);
        if (task == null) {
            return null;
        }

        // Update fields if provided
        if (request.title != null) task.title = request.title;
        if (request.description != null) task.description = request.description;
        if (request.status != null) task.status = request.status;
        if (request.priority != null) task.priority = request.priority;
        if (request.dueDate != null) task.dueDate = request.dueDate;

        // Update assignee if provided
        if (request.assignedToId != null) {
            task.assignedTo = Member.findById(request.assignedToId);
        }

        task.updatedAt = LocalDateTime.now();
        task.persist();

        broadcastTaskUpdate(task, "task_updated");
        return task;
    }

    public Task updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = Task.findById(taskId);
        if (task == null) {
            return null;
        }

        TaskStatus oldStatus = task.status;
        task.status = newStatus;
        task.updatedAt = LocalDateTime.now();
        task.persist();

        String message = String.format("Task '%s' wurde von %s zu %s verschoben",
                task.title, oldStatus, newStatus);
        connectionManager.broadcastToProject(task.project.id,
                createStatusUpdateMessage(task, oldStatus, newStatus, message));

        return task;
    }

    public ProjectStatsResponse getProjectStatistics(Long projectId) {
        long todoCount = Task.countByStatus(projectId, TaskStatus.TODO);
        long inProgressCount = Task.countByStatus(projectId, TaskStatus.IN_PROGRESS);
        long reviewCount = Task.countByStatus(projectId, TaskStatus.REVIEW);
        long doneCount = Task.countByStatus(projectId, TaskStatus.DONE);
        long overdueCount = Task.count("project.id = ?1 AND dueDate < ?2 AND status != ?3",
                projectId, LocalDateTime.now(), TaskStatus.DONE);

        return new ProjectStatsResponse(todoCount, inProgressCount, reviewCount, doneCount, overdueCount);
    }

    private void broadcastTaskUpdate(Task task, String eventType) {
        String message = String.format("Task '%s' wurde aktualisiert", task.title);
        connectionManager.broadcastToProject(task.project.id,
                createUpdateMessage(eventType, task, message));
    }

    private String createUpdateMessage(String eventType, Task task, String message) {
        return String.format("""
            {
                "type": "%s",
                "taskId": %d,
                "taskTitle": "%s",
                "status": "%s",
                "assignee": "%s",
                "message": "%s",
                "timestamp": "%s"
            }
            """,
                eventType,
                task.id,
                task.title,
                task.status,
                task.assignedTo != null ? task.assignedTo.name : "Unassigned",
                message,
                LocalDateTime.now()
        );
    }

    private String createStatusUpdateMessage(Task task, TaskStatus oldStatus, TaskStatus newStatus, String message) {
        return String.format("""
            {
                "type": "status_changed",
                "taskId": %d,
                "taskTitle": "%s",
                "oldStatus": "%s",
                "newStatus": "%s",
                "assignee": "%s",
                "message": "%s",
                "timestamp": "%s"
            }
            """,
                task.id,
                task.title,
                oldStatus,
                newStatus,
                task.assignedTo != null ? task.assignedTo.name : "Unassigned",
                message,
                LocalDateTime.now()
        );
    }
}