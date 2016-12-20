package se.plushogskolan.restcaseservice.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import se.plushogskolan.restcaseservice.exception.BadRequestExceptionMapper;
import se.plushogskolan.restcaseservice.exception.ConflictExceptionMapper;
import se.plushogskolan.restcaseservice.exception.NotFoundExceptionMapper;
import se.plushogskolan.restcaseservice.exception.UnathorizedExceptionMapper;
import se.plushogskolan.restcaseservice.filter.RequestFilter;
import se.plushogskolan.restcaseservice.resource.IssueResource;
import se.plushogskolan.restcaseservice.resource.TeamResource;
import se.plushogskolan.restcaseservice.resource.UserResource;
import se.plushogskolan.restcaseservice.resource.WorkItemResource;

@Component
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(UserResource.class);
		register(WorkItemResource.class);
		register(TeamResource.class);
		register(IssueResource.class);
		register(ConflictExceptionMapper.class);
		register(NotFoundExceptionMapper.class);
		register(BadRequestExceptionMapper.class);
		register(UnathorizedExceptionMapper.class);
		register(RequestFilter.class);
	}

}
