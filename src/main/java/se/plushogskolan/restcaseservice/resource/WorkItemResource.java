package se.plushogskolan.restcaseservice.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import se.plushogskolan.casemanagement.model.Issue;
import se.plushogskolan.casemanagement.model.WorkItem;
import se.plushogskolan.restcaseservice.model.DTOWorkItem;
import se.plushogskolan.restcaseservice.model.PageRequestBean;
import se.plushogskolan.restcaseservice.model.WorkItemRequestBean;
import se.plushogskolan.restcaseservice.service.WorkItemService;
import se.plushogskolan.restcaseservice.service.IssueService;

@Path("workitems")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class WorkItemResource {

	@Context
	private UriInfo uriInfo;

	@Autowired
	private WorkItemService workItemService;

	@Autowired
	private IssueService issueService;

	@POST
	public Response saveWorkItem(DTOWorkItem dtoWorkItem) {
		WorkItem workItem = workItemService.save(dtoWorkItem);
		URI location = uriInfo.getAbsolutePathBuilder().path(workItem.getId().toString()).build();

		return Response.created(location).build();
	}

	@POST
	@Path("{id}/issues")
	public Response saveIssue(String description, @PathParam("id") Long workItemId) {
		Issue issue = issueService.save(description, workItemId);

		URI location = uriInfo.getAbsolutePathBuilder().path(issue.getId().toString()).build();

		return Response.created(location).build();
	}

	@PUT
	@Path("{id}")
	public Response updateStatus(@PathParam("id") Long id, String status) {
		workItemService.updateStatusById(id, status);

		return Response.status(Status.NO_CONTENT).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteWorkItem(@PathParam("id") Long id) {
		workItemService.deleteWorkItem(id);
		return Response.status(Status.NO_CONTENT).build();
	}

	@GET
	@Path("{id}")
	public Response getWorkItem(@PathParam("id") Long id) {
		DTOWorkItem dtoWorkItem = workItemService.getDTOWorkItemById(id);
		return Response.ok(dtoWorkItem).build();
	}

	@GET
	public Response getWorkItems(@BeanParam WorkItemRequestBean request, @BeanParam PageRequestBean pageRequest) {
		List<WorkItem> list = null;
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();

		if (request.getStatus() != null)
			list = workItemService.getWorkItemsByStatus(request.getStatus(), page, size);
		else if (request.getDescription() != null)
			list = workItemService.searchWorkItemByDescription(request.getDescription(), page, size);
		else if (request.isWithIssue())
			list = workItemService.getWorkItemsWithIssue(page, size);
		else
			list = workItemService.getAllWorkItems(page, size);

		return Response.ok(list).build();
	}

}
