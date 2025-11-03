package de.javamark.taskboard.boundary.rest;

import de.javamark.taskboard.boundary.model.CreateProjectRequest;
import de.javamark.taskboard.entity.model.Project;
import de.javamark.taskboard.entity.model.Team;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    @GET
    public List<Project> getAllProjects() {
        return Project.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getProject(@PathParam("id") Long id) {
        Project project = Project.findById(id);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(project).build();
    }

    @GET
    @Path("/team/{teamId}")
    public List<Project> getTeamProjects(@PathParam("teamId") Long teamId) {
        return Project.find("team.id = ?1", teamId).list();
    }

    @POST
    @Transactional
    public Response createProject(CreateProjectRequest request) {
        Team team = Team.findById(request.teamId);
        if (team == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Team not found")
                    .build();
        }

        Project project = new Project();
        project.team = team;
        project.name = request.name;
        project.description = request.description;
        project.status = request.status;
        project.persist();

        return Response.status(Response.Status.CREATED).entity(project).build();
    }
}