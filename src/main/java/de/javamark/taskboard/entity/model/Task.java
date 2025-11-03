package de.javamark.taskboard.entity.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    public Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to")
    public Member assignedTo;

    @Column(nullable = false, length = 200)
    public String title;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    public TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    public Priority priority = Priority.MEDIUM;

    @Column(name = "due_date")
    public LocalDateTime dueDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    // Custom Queries (Blocking)
    public static List<Task> findByProject(Long projectId) {
        return find("project.id = ?1 ORDER BY createdAt DESC", projectId).list();
    }

    public static List<Task> findOverdue() {
        return find("dueDate < ?1 AND status != ?2",
                LocalDateTime.now(), TaskStatus.DONE).list();
    }

    public static List<Task> findByAssignee(Long memberId) {
        return find("assignedTo.id = ?1 ORDER BY priority DESC, createdAt ASC", memberId).list();
    }

    public static long countByStatus(Long projectId, TaskStatus status) {
        return count("project.id = ?1 AND status = ?2", projectId, status);
    }

    public enum TaskStatus {
        TODO, IN_PROGRESS, REVIEW, DONE
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}