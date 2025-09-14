package de.javamark.taskboard.out.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "members")
public class Member extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    public Team team;

    @Column(nullable = false, length = 100)
    public String name;

    @Column(length = 150, unique = true)
    public String email;

    @Enumerated(EnumType.STRING)
    public Role role = Role.DEVELOPER;

    @Column(name = "avatar_url")
    public String avatarUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
    @JsonBackReference
    public List<Task> assignedTasks;

    public enum Role {
        DEVELOPER, LEAD, MANAGER, DESIGNER
    }
}