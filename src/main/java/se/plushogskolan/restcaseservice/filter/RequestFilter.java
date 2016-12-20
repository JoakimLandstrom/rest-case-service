package se.plushogskolan.restcaseservice.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public final class RequestFilter implements ContainerRequestFilter {
	
	public static final String AUTH_TOKEN = "auth";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		String authHeader = requestContext.getHeaderString("Authorization");
		
		if(!authHeader.equalsIgnoreCase(AUTH_TOKEN)){			
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
		
	}

}
