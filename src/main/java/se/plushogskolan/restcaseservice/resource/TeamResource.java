package se.plushogskolan.restcaseservice.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.plushogskolan.casemanagement.model.Team;
import se.plushogskolan.restcaseservice.model.DTOTeam;
import se.plushogskolan.restcaseservice.model.DTOUser;
import se.plushogskolan.restcaseservice.model.PageRequestBean;
import se.plushogskolan.restcaseservice.model.UsersRequestBean;
import se.plushogskolan.restcaseservice.service.TeamService;
import se.plushogskolan.restcaseservice.service.UserService;
import se.plushogskolan.restcaseservice.service.WorkItemService;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("teams")
public final class TeamResource {

	@Context
	private UriInfo uriInfo;

	@Autowired
	private TeamService service;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkItemService workItemService;

	@POST
	public Response addTeam(DTOTeam dtoTeam) {

		Team team = service.save(dtoTeam);

		URI location = uriInfo.getAbsolutePathBuilder().path(team.getId().toString()).build();
		return Response.created(location).build();
	}

	@PUT
	@Path("{id}")
	public Response updateTeam(@PathParam("id") Long teamId, DTOTeam dtoTeam) {

		if (dtoTeam.getIsActive() != null) {
			service.activateTeam(teamId, dtoTeam.getIsActive());
		}
		if (dtoTeam.getName() != null) {
			service.update(teamId, dtoTeam);
		}
		return Response.status(Status.NO_CONTENT).build();
	}

	@GET
	@Path("{id}")
	public Response getTeamById(@PathParam("id") Long teamId) {
		DTOTeam team = service.getTeam(teamId);
		return Response.ok(team).build();
	}

	@GET
	public Response searchTeamByName(@QueryParam("name") String name, @BeanParam PageRequestBean pageRequestBean) {
		if(name != null){
			List<DTOTeam> teams = service.searchTeamByName(name, pageRequestBean.getPage(), pageRequestBean.getSize());
			return Response.ok(teams).build();
		} else{
			List<DTOTeam> teams = service.getAllTeams(pageRequestBean.getPage(), pageRequestBean.getSize());
			return Response.ok(teams).build();
		}
	}

	@GET
	@Path("{id}/users")
	public Response getUsersByTeam(@BeanParam UsersRequestBean bean, @PathParam("id") Long id) {
		List<DTOUser> dtoList = userService.getUsersByTeam(id, bean.getPage(), bean.getSize());
		return Response.ok(dtoList).build();
	}

	@GET
	@Path("{id}/workitems")
	public Response getWorkItemsByTeamId(@PathParam("id") Long id, @BeanParam PageRequestBean pageRequest) {
		return Response.ok(workItemService.getWorkItemsByTeamId(id, pageRequest.getPage(), pageRequest.getSize()))
				.build();
	}

	@PUT
	@Path("{id}/users")
	public Response addUserToTeam(Long userId, @PathParam("id") Long teamId) {
		userService.addUserToTeam(userId, teamId);
		return Response.status(Status.NO_CONTENT).build();
	}

}
