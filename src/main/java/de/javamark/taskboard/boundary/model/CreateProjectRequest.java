package de.javamark.taskboard.boundary.model;

import de.javamark.taskboard.entity.model.Project.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateProjectRequest {

    @NotNull
    public Long teamId;

    @NotBlank
    public String name;

    public String description;

    public ProjectStatus status = ProjectStatus.ACTIVE;

    public CreateProjectRequest() {}

    public CreateProjectRequest(Long teamId, String name, String description) {
        this.teamId = teamId;
        this.name = name;
        this.description = description;
    }
}