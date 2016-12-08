package se.plushogskolan.restcaseservice.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.plughogskolan.restcaseservice.model.DTOIssue;
import se.plughogskolan.restcaseservice.model.WorkItem;
import se.plushogskolan.casemanagement.model.Issue;
import se.plushogskolan.casemanagement.service.CaseService;

@Component
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("issues")
public class IssueResource {

	@Autowired
	CaseService service;
	
	@POST
	public Response addIssue(DTOIssue issue){
		Issue newIssue = new Issue(issue.getWorkitem(), issue.getDescription(), issue.getId());
		service.save(newIssue);
		
		return Response.ok(newIssue).build();
	}
	
	@GET
	@Path("{id}")
	public DTOIssue getIssue(@PathParam("id") Long id){
		return service.getIssue(id);
	}
	
	
}