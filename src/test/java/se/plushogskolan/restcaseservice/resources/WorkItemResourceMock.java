package se.plushogskolan.restcaseservice.resources;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static se.plushogskolan.restcaseservice.model.DTOIssue.toEntity;
import static se.plushogskolan.restcaseservice.model.DTOWorkItem.toEntity;

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

import se.plushogskolan.casemanagement.exception.AlreadyPersistedException;
import se.plushogskolan.casemanagement.exception.NotPersistedException;
import se.plushogskolan.casemanagement.model.WorkItem;
import se.plushogskolan.casemanagement.model.WorkItem.Status;
import se.plushogskolan.casemanagement.service.CaseService;
import se.plushogskolan.restcaseservice.model.DTOIssue;
import se.plushogskolan.restcaseservice.model.DTOWorkItem;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkItemResourceMock {

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
		targetUrl = String.format("http://localhost:%d/workitems", randomPort);
		webTarget = client.target(targetUrl);
	}

	@Test
	public void saveWorkItemThrowsConflict() {

		DTOWorkItem workItem = DTOWorkItem.builder("tempdescription", Status.DONE).build();

		doThrow(new AlreadyPersistedException("")).when(caseService).save(toEntity(workItem));

		Response response = webTarget.request().header(header, token)
				.post(Entity.entity(workItem, MediaType.APPLICATION_JSON));

		assertEquals(CONFLICT, response.getStatusInfo());
	}

	@Test
	public void saveIssueThrowsConflict() {

		DTOWorkItem workItem = DTOWorkItem.builder("tempdescription", Status.DONE).build();

		DTOIssue issue = DTOIssue.builder(workItem, "tempissuedescription").build();

		when(caseService.getWorkItemById(1l)).thenReturn(toEntity(workItem));
		doThrow(new AlreadyPersistedException("")).when(caseService).save(toEntity(issue));

		Response response = webTarget.path("1").path("issues").request().header(header, token)
				.post(Entity.entity(issue.getDescription(), MediaType.APPLICATION_JSON));

		assertEquals(CONFLICT, response.getStatusInfo());
	}

	@Test
	public void updateStatus() {

		String status = "DONE";

		DTOWorkItem workItem = DTOWorkItem.builder("descript", Status.DONE).build();

		when(caseService.updateStatusById(1l, Status.DONE)).thenReturn(toEntity(workItem));

		Response response = webTarget.path("1").request().header(header, token)
				.put(Entity.entity(status, MediaType.APPLICATION_JSON));

		assertEquals(NO_CONTENT, response.getStatusInfo());
	}

	@Test
	public void updateStatusThrowsNotFound() {

		String status = "DONE";

		DTOWorkItem workItem = DTOWorkItem.builder("descript", Status.DONE).build();

		doThrow(new NotPersistedException("")).when(caseService).updateStatusById(1l, Status.DONE);

		Response response = webTarget.path("1").request().header(header, token)
				.put(Entity.entity(status, MediaType.APPLICATION_JSON));

		assertEquals(NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void deleteWorkItem() {

		Response response = webTarget.path("1").request().header(header, token).delete();

		assertEquals(NO_CONTENT, response.getStatusInfo());
	}

	@Test
	public void deleteWorkItemThrowsNotFound() {

		doThrow(new NotPersistedException("")).when(caseService).deleteWorkItem(1l);

		Response response = webTarget.path("1").request().header(header, token).delete();

		assertEquals(NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void getWorkItem() {

		DTOWorkItem workItem = DTOWorkItem.builder("tempdescription", Status.DONE).build();

		when(caseService.getWorkItemById(1l)).thenReturn(toEntity(workItem));

		Response response = webTarget.path("1").request().header(header, token).get();

		DTOWorkItem returnedWorkItem = response.readEntity(DTOWorkItem.class);

		assertEquals(OK, response.getStatusInfo());
		assertEquals(workItem, returnedWorkItem);
	}

	@Test
	public void getAllWorkItems() {

		List<WorkItem> workItems = new ArrayList<>();

		DTOWorkItem dtoWorkItem = DTOWorkItem.builder("tempdescrip", Status.DONE).build();

		workItems.add(toEntity(dtoWorkItem));

		when(caseService.getAllWorkItems(0, 5)).thenReturn(workItems);

		Response response = webTarget.request().header(header, token).get();

		List<WorkItem> returnedList = response.readEntity(new GenericType<List<WorkItem>>() {
		});

		assertEquals(OK, response.getStatusInfo());
		assertEquals(workItems.get(0), returnedList.get(0));
	}

	@Test
	public void getWorkItemsWithIssue() {

		List<WorkItem> workItems = new ArrayList<>();

		DTOWorkItem dtoWorkItem = DTOWorkItem.builder("tempdescrip", Status.DONE).build();

		workItems.add(toEntity(dtoWorkItem));

		when(caseService.getWorkItemsWithIssue(0, 5)).thenReturn(workItems);

		Response response = webTarget.queryParam("withissue", true).request().header(header, token).get();

		List<WorkItem> returnedList = response.readEntity(new GenericType<List<WorkItem>>() {
		});

		assertEquals(OK, response.getStatusInfo());
		assertEquals(workItems.get(0), returnedList.get(0));
	}

	@Test
	public void searchWorkItemsByDescription() {

		List<WorkItem> workItems = new ArrayList<>();

		DTOWorkItem dtoWorkItem = DTOWorkItem.builder("tempdescrip", Status.DONE).build();

		workItems.add(toEntity(dtoWorkItem));

		when(caseService.searchWorkItemByDescription("blah", 0, 5)).thenReturn(workItems);

		Response response = webTarget.queryParam("description", "blah").request().header(header, token).get();

		List<WorkItem> returnedList = response.readEntity(new GenericType<List<WorkItem>>() {
		});

		assertEquals(OK, response.getStatusInfo());
		assertEquals(workItems.get(0), returnedList.get(0));
	}
	
	@Test
	public void getWorkItemsByStatus(){
		
		List<WorkItem> workItems = new ArrayList<>();

		DTOWorkItem dtoWorkItem = DTOWorkItem.builder("tempdescrip", Status.DONE).build();

		workItems.add(toEntity(dtoWorkItem));

		when(caseService.getWorkItemsByStatus(Status.DONE, 0, 5)).thenReturn(workItems);

		Response response = webTarget.queryParam("status", "DONE").request().header(header, token).get();

		List<WorkItem> returnedList = response.readEntity(new GenericType<List<WorkItem>>() {
		});

		assertEquals(OK, response.getStatusInfo());
		assertEquals(workItems.get(0), returnedList.get(0));
	}

}
