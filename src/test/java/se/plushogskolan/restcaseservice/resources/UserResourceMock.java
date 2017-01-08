package se.plushogskolan.restcaseservice.resources;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static se.plushogskolan.restcaseservice.model.DTOUser.toEntity;
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
import se.plushogskolan.casemanagement.exception.IllegalArgumentException;
import se.plushogskolan.casemanagement.exception.InternalErrorException;
import se.plushogskolan.casemanagement.exception.NotPersistedException;
import se.plushogskolan.casemanagement.model.User;
import se.plushogskolan.casemanagement.model.WorkItem;
import se.plushogskolan.casemanagement.model.WorkItem.Status;
import se.plushogskolan.casemanagement.service.CaseService;
import se.plushogskolan.restcaseservice.model.DTOUser;
import se.plushogskolan.restcaseservice.model.DTOWorkItem;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserResourceMock {

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
		targetUrl = String.format("http://localhost:%d/users", randomPort);
		webTarget = client.target(targetUrl);

	}
	
	@Test
	public void saveUserThrowsConflict() {

		DTOUser user = DTOUser.builder().build("joakimlandstrom");

		doThrow(new AlreadyPersistedException("")).when(caseService).save(toEntity(user));

		Response response = webTarget.request().header(header, token)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON));

		assertEquals(CONFLICT, response.getStatusInfo());
	}

	@Test
	public void saveUserThrowsInternalError() {

		DTOUser user = DTOUser.builder().build("joakimlandstrom");

		doThrow(new InternalErrorException("")).when(caseService).save(toEntity(user));

		Response response = webTarget.request().header(header, token)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON));

		assertEquals(INTERNAL_SERVER_ERROR, response.getStatusInfo());
	}

	@Test
	public void updateUserFirstNameLastNameUserNameIsActive() {

		DTOUser user = DTOUser.builder().setFirstName("joakim").setLastName("landstrom").setIsActive(false)
				.build("joakimlandstrom");

		when(caseService.updateUserFirstName(1l, "joakim")).thenReturn(toEntity(user));
		when(caseService.updateUserLastName(1l, "landstrom")).thenReturn(toEntity(user));
		when(caseService.updateUserUsername(1l, "joakimlandstrom")).thenReturn(toEntity(user));
		when(caseService.inactivateUser(1l)).thenReturn(toEntity(user));
		when(caseService.addWorkItemToUser(1l, 1l))
				.thenReturn(toEntity(DTOWorkItem.builder("temp", Status.UNSTARTED).build()));

		Response response = webTarget.path("1").queryParam("workItemId", 1).request().header(header, token)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON));

		assertEquals(NO_CONTENT, response.getStatusInfo());
	}

	@Test
	public void updateUserFirstNameThrowsNotFound() {

		DTOUser user = DTOUser.builder().setFirstName("joakim").build(null);

		doThrow(new NotPersistedException("")).when(caseService).updateUserFirstName(1l, "joakim");

		Response response = webTarget.path("1").request().header(header, token)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON));

		assertEquals(NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void updateUserLastNameThrowsNotFound() {
		DTOUser user = DTOUser.builder().setLastName("landstrom").build(null);

		doThrow(new NotPersistedException("")).when(caseService).updateUserLastName(1l, "landstrom");

		Response response = webTarget.path("1").request().header(header, token)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON));

		assertEquals(NOT_FOUND, response.getStatusInfo());

	}

	@Test
	public void updateUserUsernameThrowsNotFound() {
		DTOUser user = DTOUser.builder().build("joakimlandstrom");

		doThrow(new NotPersistedException("")).when(caseService).updateUserUsername(1l, "joakimlandstrom");

		Response response = webTarget.path("1").request().header(header, token)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON));

		assertEquals(NOT_FOUND, response.getStatusInfo());

	}

	@Test
	public void updateUserUsernameThrowsBadRequest() {
		
		DTOUser user = DTOUser.builder().build("joakimlandstrom");

		doThrow(new IllegalArgumentException("")).when(caseService).updateUserUsername(1l, "joakimlandstrom");

		Response response = webTarget.path("1").request().header(header, token)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON));

		assertEquals(BAD_REQUEST, response.getStatusInfo());
	}
	
	@Test
	public void updateUsernameThrowsConflict(){
		
		DTOUser user = DTOUser.builder().build("joakimlandstrom");

		doThrow(new AlreadyPersistedException("")).when(caseService).updateUserUsername(1l, "joakimlandstrom");

		Response response = webTarget.path("1").request().header(header, token)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON));

		assertEquals(CONFLICT, response.getStatusInfo());
	}
	
	@Test
	public void updateUserIsActiveThrowsNotFound(){
		
		DTOUser user = DTOUser.builder().setIsActive(false).build(null);

		doThrow(new NotPersistedException("")).when(caseService).inactivateUser(1l);

		Response response = webTarget.path("1").request().header(header, token)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON));

		assertEquals(NOT_FOUND, response.getStatusInfo());
	}
	
	@Test
	public void searchUsers(){
		List<User> users = new ArrayList<User>();
		DTOUser user = DTOUser.builder().setIsActive(false).build("joakimlandstrom");
		users.add(toEntity(user));
		
		when(caseService.searchUsersByFirstNameLastNameUsername("", "",	"", 0, 10)).thenReturn(users);
		
		Response response = webTarget.request().header(header, token).get();
		
		List<DTOUser> dtoUsers = response.readEntity(new GenericType<List<DTOUser>>(){});
		
		assertEquals(OK, response.getStatusInfo());
		assertEquals(user, dtoUsers.get(0));
	}
	
	

	@Test
	public void getUser() {

		DTOUser user = DTOUser.builder().build("joakimlandstrom");

		when(caseService.getUser(1l)).thenReturn(toEntity(user));

		Response response = webTarget.path("1").request().header(header, token).get();

		DTOUser resultUser = response.readEntity(DTOUser.class);

		assertEquals(OK, response.getStatusInfo());
		assertEquals(user, resultUser);
	}
	

	@Test
	public void getUserThrowsNotFound() {

		doThrow(new NotPersistedException("")).when(caseService).getUser(1l);

		Response response = client.target(targetUrl).path("1").request().header(header, token).get();

		assertEquals(NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void getUserThrowsInternalError() {

		doThrow(new InternalErrorException("")).when(caseService).getUser(1l);

		Response response = client.target(targetUrl).path("1").request().header(header, token).get();

		assertEquals(INTERNAL_SERVER_ERROR, response.getStatusInfo());
	}
	
	@Test
	public void getWorkItemsByUserId(){
		
		DTOWorkItem workItem = DTOWorkItem.builder("temp", Status.UNSTARTED).build();
		
		List<WorkItem> workItems = new ArrayList<>();
		
		workItems.add(toEntity(workItem));
		
		when(caseService.getWorkItemsByUserId(1l, 0, 5)).thenReturn(workItems);
		
		Response response = client.target(targetUrl).path("1").path("workitems").request().header(header, token).get();
		
		List<WorkItem> dtoWorkItems = response.readEntity(new GenericType<List<WorkItem>>(){});
		
		assertEquals(OK, response.getStatusInfo());
		assertEquals(toEntity(workItem),dtoWorkItems.get(0));
	}

}
