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

import se.plushogskolan.restcaseservice.model.DTOUser;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserResource {
	
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
	public void saveUser(){
		
		DTOUser user = DTOUser.builder().build("joakimlandstrom");
		
		Response response = webTarget.request().header(header, token).post(Entity.entity(user, MediaType.APPLICATION_JSON));
		
		assertEquals(CREATED, response.getStatusInfo());
		
	}
}
