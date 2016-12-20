package se.plushogskolan.restcaseservice.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import se.plushogskolan.restcaseservice.exception.UnathorizedException;

@Provider
public final class RequestFilter implements ContainerRequestFilter {
	
	public static final String AUTH_TOKEN = "auth";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		String authHeader = requestContext.getHeaderString("Authorization");
		
		if(authHeader == null || !authHeader.equalsIgnoreCase(AUTH_TOKEN)){			
			throw new UnathorizedException("Unathorized");
		}
		
	}

}
