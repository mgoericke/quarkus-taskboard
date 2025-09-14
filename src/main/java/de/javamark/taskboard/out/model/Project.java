package de.javamark.taskboard.out.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    public Team team;

    @Column(nullable = false, length = 200)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Enumerated(EnumType.STRING)
    public ProjectStatus status = ProjectStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    public List<Task> tasks;

    public enum ProjectStatus {
        ACTIVE, COMPLETED, ARCHIVED
    }
}