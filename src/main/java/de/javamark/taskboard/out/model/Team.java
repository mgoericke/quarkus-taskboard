package de.javamark.taskboard.out.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 100)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    public List<Member> members;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    public List<Project> projects;
}