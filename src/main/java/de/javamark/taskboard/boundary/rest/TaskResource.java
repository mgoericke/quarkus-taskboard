package de.javamark.taskboard.boundary.rest;

import de.javamark.taskboard.boundary.model.CreateTaskRequest;
import de.javamark.taskboard.boundary.model.UpdateTaskRequest;
import de.javamark.taskboard.control.TaskService;
import de.javamark.taskboard.entity.model.Task;

import de.javamark.taskboard.entity.model.Task.TaskStatus;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    TaskService taskService;

    @GET
    public List<Task> getAllTasks(@QueryParam("projectId") Long projectId) {
        if (projectId != null) {
            return Task.findByProject(projectId);
        }
        return Task.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getTask(@PathParam("id") Long id) {
        Task task = Task.findById(id);
        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(task).build();
    }

    @POST
    @Transactional
    public Response createTask(CreateTaskRequest request) {
        try {
            Task task = taskService.createTask(request);
            return Response.status(Response.Status.CREATED).entity(task).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateTask(@PathParam("id") Long id, UpdateTaskRequest request) {
        Task task = taskService.updateTask(id, request);
        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(task).build();
    }

    @PUT
    @Path("/{id}/status")
    @Transactional
    public Response updateTaskStatus(@PathParam("id") Long id, @QueryParam("status") TaskStatus status) {
        Task task = taskService.updateTaskStatus(id, status);
        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(task).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteTask(@PathParam("id") Long id) {
        boolean deleted = Task.deleteById(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/overdue")
    public List<Task> getOverdueTasks() {
        return Task.findOverdue();
    }

    @GET
    @Path("/member/{memberId}")
    public List<Task> getMemberTasks(@PathParam("memberId") Long memberId) {
        return Task.findByAssignee(memberId);
    }

    @GET
    @Path("/stats/{projectId}")
    public Response getProjectStats(@PathParam("projectId") Long projectId) {
        return Response.ok(taskService.getProjectStatistics(projectId)).build();
    }
}