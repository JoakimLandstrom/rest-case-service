package se.plushogskolan.restcaseservice.resources;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static se.plushogskolan.restcaseservice.model.DTOTeam.toEntity;
import static se.plushogskolan.restcaseservice.model.DTOUser.toEntity;
import static se.plushogskolan.restcaseservice.model.DTOWorkItem.toDTO;
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
import se.plushogskolan.casemanagement.exception.NoSpaceException;
import se.plushogskolan.casemanagement.exception.NotPersistedException;
import se.plushogskolan.casemanagement.model.Team;
import se.plushogskolan.casemanagement.model.User;
import se.plushogskolan.casemanagement.model.WorkItem;
import se.plushogskolan.casemanagement.model.WorkItem.Status;
import se.plushogskolan.casemanagement.service.CaseService;
import se.plushogskolan.restcaseservice.model.DTOTeam;
import se.plushogskolan.restcaseservice.model.DTOUser;
import se.plushogskolan.restcaseservice.model.DTOWorkItem;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TeamResourceMock {

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
		targetUrl = String.format("http://localhost:%d/teams", randomPort);
		webTarget = client.target(targetUrl);
	}

	@Test
	public void saveTeamThrowsConflict() {

		DTOTeam team = DTOTeam.builder("tempteam", true).build();

		doThrow(new AlreadyPersistedException("")).when(caseService).save(toEntity(team));

		Response response = webTarget.request().header(header, token)
				.post(Entity.entity(team, MediaType.APPLICATION_JSON));

		assertEquals(CONFLICT, response.getStatusInfo());
	}

	@Test
	public void updateTeam() {

		DTOTeam team = DTOTeam.builder("tempteam", true).build();

		when(caseService.updateTeam(1l, toEntity(team))).thenReturn(toEntity(team));
		when(caseService.activateTeam(1l)).thenReturn(toEntity(team));

		Response response = webTarget.path("1").request().header(header, token)
				.put(Entity.entity(team, MediaType.APPLICATION_JSON));

		assertEquals(NO_CONTENT, response.getStatusInfo());
	}

	@Test
	public void updateTeamThrowsNotFound() {

		DTOTeam team = DTOTeam.builder("tempteam", true).build();

		doThrow(new NotPersistedException("")).when(caseService).updateTeam(1l, toEntity(team));

		Response response = webTarget.path("1").request().header(header, token)
				.put(Entity.entity(team, MediaType.APPLICATION_JSON));

		assertEquals(NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void getTeamById() {

		DTOTeam team = DTOTeam.builder("tempteam", false).build();

		when(caseService.getTeam(1l)).thenReturn(toEntity(team));

		Response response = webTarget.path("1").request().header(header, token).get();

		DTOTeam returnedTeam = response.readEntity(DTOTeam.class);

		assertEquals(OK, response.getStatusInfo());
		assertEquals(team, returnedTeam);
	}

	@Test
	public void getTeamByIdThrowsNotFound() {

		DTOTeam team = DTOTeam.builder("tempteam", true).build();

		doThrow(new NotPersistedException("")).when(caseService).getTeam(1l);

		Response response = webTarget.path("1").request().header(header, token).get();

		assertEquals(NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void getAllTeams() {

		DTOTeam team = DTOTeam.builder("tempteam", true).build();

		List<Team> teams = new ArrayList<>();

		teams.add(toEntity(team));

		when(caseService.getAllTeams(0, 5)).thenReturn(teams);

		Response response = webTarget.request().header(header, token).get();

		List<DTOTeam> dtoTeams = response.readEntity(new GenericType<List<DTOTeam>>() {
		});

		assertEquals(OK, response.getStatusInfo());
		assertEquals(team, dtoTeams.get(0));
	}

	@Test
	public void searchTeamByName() {

		DTOTeam team = DTOTeam.builder("tempteam", true).build();

		List<Team> teams = new ArrayList<>();

		teams.add(toEntity(team));

		when(caseService.searchTeamByName("temp", 0, 5)).thenReturn(teams);

		Response response = webTarget.queryParam("name", "temp").request().header(header, token).get();

		List<DTOTeam> dtoTeams = response.readEntity(new GenericType<List<DTOTeam>>() {
		});

		assertEquals(OK, response.getStatusInfo());
		assertEquals(team, dtoTeams.get(0));
	}

	@Test
	public void getUsersByTeam() {

		DTOUser user = DTOUser.builder().build("joakimlandstrom");

		List<User> users = new ArrayList<>();

		users.add(toEntity(user));

		when(caseService.getUsersByTeam(1l, 0, 10)).thenReturn(users);

		Response response = webTarget.path("1").path("users").request().header(header, token).get();

		List<DTOUser> dtoUsers = response.readEntity(new GenericType<List<DTOUser>>() {
		});

		assertEquals(OK, response.getStatusInfo());
		assertEquals(user, dtoUsers.get(0));
	}

	@Test
	public void getWorkItemsByTeam() {

		DTOWorkItem workItem1 = DTOWorkItem.builder("tempworkitem", Status.DONE).build();
		DTOWorkItem workItem2 = DTOWorkItem.builder("temp2workitem", Status.STARTED).build();

		List<WorkItem> workItems = new ArrayList<>();
		workItems.add(toEntity(workItem1));
		workItems.add(toEntity(workItem2));

		when(caseService.getWorkItemsByTeamId(1l, 0, 5)).thenReturn(workItems);

		Response response = webTarget.path("1").path("workitems").request().header(header, token).get();

		List<WorkItem> returnedWorkItems = response.readEntity(new GenericType<List<WorkItem>>() {
		});

		assertEquals(OK, response.getStatusInfo());
		assertEquals(workItem1, toDTO(returnedWorkItems.get(0)));
		assertEquals(workItem2, toDTO(returnedWorkItems.get(1)));
	}

	@Test
	public void addUserToTeam() {

		DTOUser user = DTOUser.builder().build("joakimlandstrom");

		Long userId = 1l;

		when(caseService.addUserToTeam(userId, 1l)).thenReturn(toEntity(user));

		Response response = webTarget.path("1").path("users").request().header(header, token)
				.put(Entity.entity(userId, MediaType.APPLICATION_JSON));

		assertEquals(NO_CONTENT, response.getStatusInfo());
	}

	@Test
	public void addUserToTeamThrowsConflict() {

		Long userId = 1l;

		doThrow(new NoSpaceException("")).when(caseService).addUserToTeam(1l, 1l);

		Response response = webTarget.path("1").path("users").request().header(header, token)
				.put(Entity.entity(userId, MediaType.APPLICATION_JSON));

		assertEquals(CONFLICT, response.getStatusInfo());
	}

}
