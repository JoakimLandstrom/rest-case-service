package se.plushogskolan.restcaseservice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import se.plushogskolan.restcaseservice.resources.SaveResources;
import se.plushogskolan.restcaseservice.resources.UserResourceMock;

@RunWith(Suite.class)
@SuiteClasses({SaveResources.class, UserResourceMock.class})
public class RestCaseServiceApplicationTests {


}
