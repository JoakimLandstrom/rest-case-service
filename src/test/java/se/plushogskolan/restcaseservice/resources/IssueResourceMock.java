package se.plushogskolan.restcaseservice.resources;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import se.plushogskolan.casemanagement.exception.NotPersistedException;
import se.plushogskolan.casemanagement.model.Issue;
import se.plushogskolan.casemanagement.model.WorkItem.Status;
import se.plushogskolan.casemanagement.service.CaseService;
import se.plushogskolan.restcaseservice.model.DTOIssue;
import se.plushogskolan.restcaseservice.model.DTOWorkItem;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IssueResourceMock {

	@MockBean
	private CaseService caseService;

	@LocalServerPort
	private int randomPort;

	private static Client client;

	private WebTarget webTarget;

	private String targetUrl;

	private final String header = "Authorization";
	private final String token = "auth";

	@BeforeClass
	public static void initialize() {
		client = ClientBuilder.newClient();
	}

	@Before
	public void setup() {
		targetUrl = String.format("http://localhost:%d/issues", randomPort);
		webTarget = client.target(targetUrl);
	}

	@Test
	public void updateIssue() {

		String description = "newdescription";

		DTOIssue dtoIssue = DTOIssue.builder(DTOWorkItem.builder("text", Status.DONE).build(), description).build();

		when(caseService.updateIssueDescription(1l, description)).thenReturn(DTOIssue.toEntity(dtoIssue));

		Response response = webTarget.path("1").queryParam("description", description).request().header(header, token)
				.put(Entity.entity("", MediaType.APPLICATION_JSON));

		assertEquals(NO_CONTENT, response.getStatusInfo());
	}

	@Test
	public void updateIssueThrowsNotFound() {

		String description = "newdescription";

		doThrow(new NotPersistedException("")).when(caseService).updateIssueDescription(1l, description);

		Response response = webTarget.path("1").queryParam("description", description).request().header(header, token)
				.put(Entity.entity("", MediaType.APPLICATION_JSON));

		assertEquals(NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void getIssue() {

		DTOIssue dtoIssue = DTOIssue.builder(DTOWorkItem.builder("text", Status.DONE).build(), "temp").build();

		when(caseService.getIssue(1l)).thenReturn(DTOIssue.toEntity(dtoIssue));

		Response response = webTarget.path("1").request().header(header, token).get();

		DTOIssue returnedIssue = response.readEntity(DTOIssue.class);

		assertEquals(OK, response.getStatusInfo());
		assertEquals(dtoIssue, returnedIssue);
	}

	@Test
	public void getIssueThrowsNotFound() {

		doThrow(new NotPersistedException("")).when(caseService).getIssue(1l);
		
		Response response = webTarget.path("1").request().header(header, token).get();
		
		assertEquals(NOT_FOUND, response.getStatusInfo());
	}
	
	@Test
	public void getAllIssues(){
		
		List<Issue> issues = new ArrayList<>();
		
		DTOIssue dtoIssue = DTOIssue.builder(DTOWorkItem.builder("text", Status.DONE).build(), "temp").build();

		issues.add(DTOIssue.toEntity(dtoIssue));
		
		when(caseService.getAllIssues(0, 5)).thenReturn(issues);
		
		Response response = webTarget.request().header(header, token).get();
		
		List<DTOIssue> returnedList = response.readEntity(new GenericType<List<DTOIssue>>(){});
		
		assertEquals(OK, response.getStatusInfo());
		assertEquals(dtoIssue, returnedList.get(0));
	}

}
