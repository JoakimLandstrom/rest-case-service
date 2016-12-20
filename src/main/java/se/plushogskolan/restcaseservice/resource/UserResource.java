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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.plushogskolan.casemanagement.model.User;
import se.plushogskolan.casemanagement.model.WorkItem;
import se.plushogskolan.restcaseservice.exception.ConflictException;
import se.plushogskolan.restcaseservice.model.DTOUser;
import se.plushogskolan.restcaseservice.model.PageRequestBean;
import se.plushogskolan.restcaseservice.model.UsersRequestBean;
import se.plushogskolan.restcaseservice.service.UserService;
import se.plushogskolan.restcaseservice.service.WorkItemService;

@Component
@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class UserResource {

	@Autowired
	private UserService userService;

	@Autowired
	private WorkItemService workItemService;

	@Context
	private UriInfo uriInfo;

	@POST
	public Response addUser(DTOUser dtoUser) throws ConflictException {

		User savedUser = userService.saveUser(dtoUser);

		URI location = uriInfo.getAbsolutePathBuilder().path(savedUser.getId().toString()).build();

		return Response.created(location).build();
	}

	@PUT
	@Path("{id}")
	public Response updateUserFirstNameLastNameUsername(@PathParam("id") Long id, DTOUser dtoUser,
			@QueryParam("workItemId") Long workItemId) {

		if (dtoUser != null && dtoUser.getUsername() != null)
			userService.updateUserUsername(id, dtoUser.getUsername());

		if (dtoUser != null && dtoUser.getFirstName() != null)
			userService.updateUserFirstName(id, dtoUser.getFirstName());

		if (dtoUser != null && dtoUser.getLastName() != null)
			userService.updateUserLastName(id, dtoUser.getLastName());

		if (dtoUser != null && dtoUser.getIsActive() != null)
			userService.updateUserIsActive(id, dtoUser.getIsActive());

		if (workItemId != null)
			workItemService.addWorkItemToUser(workItemId, id);

		return Response.status(Status.NO_CONTENT).build();
	}

	@GET
	@Path("{id}")
	public Response getUser(@PathParam("id") Long id) {

		return Response.ok(userService.getUser(id)).build();
	}

	@GET
	public Response searchUsers(@BeanParam UsersRequestBean bean) {

		List<DTOUser> list = userService.searchUsersByFirstNameLastNameUsername(bean.getFirstName(), bean.getLastName(),
				bean.getUsername(), bean.getPage(), bean.getSize());

		return Response.ok(list).build();
	}

	@GET
	@Path("{id}/workitems")
	public Response getWorkItemsByUserId(@PathParam("id") Long id, @BeanParam PageRequestBean pageRequest) {

		List<WorkItem> list = workItemService.getWorkItemsByUserId(id, pageRequest.getPage(), pageRequest.getSize());

		return Response.ok(list).build();
	}
}