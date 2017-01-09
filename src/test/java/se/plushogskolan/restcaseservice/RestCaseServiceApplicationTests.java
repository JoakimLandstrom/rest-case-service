package se.plushogskolan.restcaseservice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import se.plushogskolan.restcaseservice.resources.IssueResourceMock;
import se.plushogskolan.restcaseservice.resources.SaveResources;
import se.plushogskolan.restcaseservice.resources.TeamResourceMock;
import se.plushogskolan.restcaseservice.resources.UserResourceMock;
import se.plushogskolan.restcaseservice.resources.WorkItemResourceMock;

@RunWith(Suite.class)
@SuiteClasses({SaveResources.class, UserResourceMock.class, WorkItemResourceMock.class, IssueResourceMock.class, TeamResourceMock.class})
public class RestCaseServiceApplicationTests {


}
