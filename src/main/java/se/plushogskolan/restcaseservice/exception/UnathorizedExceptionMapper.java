package se.plushogskolan.restcaseservice.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class UnathorizedExceptionMapper implements ExceptionMapper<UnathorizedException> {

	@Override
	public Response toResponse(UnathorizedException exception) {
		
		return Response.status(Status.UNAUTHORIZED).entity(exception.getMessage()).build();
	}

}
