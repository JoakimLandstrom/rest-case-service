package se.plushogskolan.restcaseservice.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import se.plushogskolan.restcaseservice.model.DTOUser;
import se.plushogskolan.restcaseservice.service.UserService;

@RunWith(SpringRunner.class )
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserResource {

	@MockBean
	private UserService userService;

	@LocalServerPort
	private int randomPort;

	private static Client client;

	private String targetUrl;

	@BeforeClass
	public static void initialize() {
		client = ClientBuilder.newClient();
	}

	@Before
	public void setup() {
		targetUrl = String.format("http://localhost:%d/users", randomPort);
		
	}

	@Test
	public void contextLoads() {

		DTOUser user = DTOUser.builder().build("joakimlandstrom");

		when(userService.getUser(1l)).thenReturn(user);

		Response response = client.target(targetUrl).path("1").request().header("Authorization", "auth").get();

		DTOUser resultUser = response.readEntity(DTOUser.class);
		
		assertEquals(200, response.getStatus());
		assertEquals(user, resultUser);

	}
}
