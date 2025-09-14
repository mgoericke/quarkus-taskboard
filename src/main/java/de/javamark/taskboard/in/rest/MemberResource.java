package de.javamark.taskboard.in.rest;

import de.javamark.taskboard.out.model.Member;
import de.javamark.taskboard.out.model.Task;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MemberResource {

    @GET
    public List<Member> getAllMembers() {
        return Member.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getMember(@PathParam("id") Long id) {
        Member member = Member.findById(id);
        if (member == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(member).build();
    }

    @GET
    @Path("/{id}/tasks")
    public List<Task> getMemberTasks(@PathParam("id") Long memberId) {
        return Task.findByAssignee(memberId);
    }

    @GET
    @Path("/team/{teamId}")
    public List<Member> getTeamMembers(@PathParam("teamId") Long teamId) {
        return Member.find("team.id = ?1", teamId).list();
    }

    @GET
    @Path("/project/{projectId}")
    public List<Member> getProjectMembers(@PathParam("projectId") Long projectId) {
        return Member.find("team.projects.id = ?1", projectId).list();
    }
}