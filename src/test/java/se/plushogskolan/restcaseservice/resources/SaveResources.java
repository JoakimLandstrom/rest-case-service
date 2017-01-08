package se.plushogskolan.restcaseservice.resources;

import static javax.ws.rs.core.Response.Status.CREATED;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import se.plushogskolan.casemanagement.model.WorkItem.Status;
import se.plushogskolan.restcaseservice.model.DTOIssue;
import se.plushogskolan.restcaseservice.model.DTOTeam;
import se.plushogskolan.restcaseservice.model.DTOUser;
import se.plushogskolan.restcaseservice.model.DTOWorkItem;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SaveResources {
	
	@LocalServerPort
	private int randomPort;

	private static Client client;

	private WebTarget webTarget;

	private String userUrl;
	private String teamUrl;
	private String workItemUrl;
	
	private final String header = "Authorization";
	private final String token = "auth";

	@BeforeClass
	public static void initialize() {
		client = ClientBuilder.newClient();
	}

	@Before
	public void setup() {
		userUrl = String.format("http://localhost:%d/users", randomPort);
		teamUrl = String.format("http://localhost:%d/teams", randomPort);
		workItemUrl = String.format("http://localhost:%d/workitems", randomPort);
	}

	@Test
	public void saveUser(){
		webTarget = client.target(userUrl);
		
		DTOUser user = DTOUser.builder().build("joakimlandstrom");
		
		Response response = webTarget.request().header(header, token).post(Entity.entity(user, MediaType.APPLICATION_JSON));
		
		assertEquals(CREATED, response.getStatusInfo());
	}
	
	@Test
	public void saveTeam(){
		webTarget = client.target(teamUrl);
		
		DTOTeam team = DTOTeam.builder("tempteam", true).build();
		
		Response response = webTarget.request().header(header, token).post(Entity.entity(team, MediaType.APPLICATION_JSON));
		
		assertEquals(CREATED, response.getStatusInfo());
	}
	
	@Test
	public void saveWorkItem(){
		webTarget = client.target(workItemUrl);
		
		DTOWorkItem workItem = DTOWorkItem.builder("tempdescription", Status.STARTED).build();
		
		Response response = webTarget.request().header(header, token).post(Entity.entity(workItem, MediaType.APPLICATION_JSON));
		
		assertEquals(CREATED, response.getStatusInfo());
	}
	
	@Test
	public void saveIssue(){
		webTarget = client.target(workItemUrl);
		
		
		DTOWorkItem workItem = DTOWorkItem.builder("tempdescription", Status.DONE).build();
		
		String description = "tempdescription";
		
		Response workItemResponse = webTarget.request().header(header, token).post(Entity.entity(workItem, MediaType.APPLICATION_JSON));

		String workItemLocation = workItemResponse.getLocation().toString();
		int length = workItemLocation.length();
		workItemLocation = workItemLocation.substring(length-1, length);
		
		Response response = webTarget.path(workItemLocation).path("issues").request().header(header, token).post(Entity.entity(description, MediaType.APPLICATION_JSON));
		
		assertEquals(CREATED, response.getStatusInfo());
	}
}
