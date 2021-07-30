package za.co.nextgen.test;

import static com.jayway.restassured.RestAssured.given;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.google.gson.Gson;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RestAssuredResponseImpl;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ResponseBody;

import za.co.nextgen.NextgenServiceApplication;
import za.co.nextgen.model.Project;
import za.co.nextgen.model.response.CreateProjectResponse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.Expectation;
import org.mockserver.model.Header;
import org.mockserver.model.RequestDefinition;
import org.mockserver.verify.VerificationTimes;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.Cookie.cookie;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.Parameter.param;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT,
		classes = NextgenServiceApplication.class)
public class APITest {

	
    private static ClientAndServer mockServer;

    @BeforeAll
    public static void startMockServer() {
        mockServer = startClientAndServer(5100);

        Expectation[] expectations = new MockServerClient("localhost", 5100)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/project/api/project")
//                                .withCookies(
//                                        cookie("session", "4930456C-C718-476F-971F-CB8E047AB349")
//                                )
                                .withBody(
                                        new Gson().toJson(APITest.createProject())
                                )
                )
                .respond(
                		
                        response().withStatusCode(200)
                        .withHeaders(
                                new Header("Content-Type", "application/json; charset=utf-8"),
                                new Header("Cache-Control", "public, max-age=86400")
                        )
                                .withBody(
                                		new Gson().toJson(createProjectResponse())
                                			)
                );
        System.out.println(expectations);

        RequestDefinition[] requestDefinitions = new MockServerClient("localhost", 5100)
                .retrieveRecordedRequests(
                        request()
                                .withPath("/project/api/project")
                                .withBody(
                                        new Gson().toJson(APITest.createProject())
                                ).withMethod("POST")
                );
        System.out.println(requestDefinitions);
    }

    @AfterAll
    public static void stopMockServer() {
       mockServer.stop();
    }
//	//fail fetch all projects
//	
//	@Test
//	@Order(1)
//	public void test_fetch_all_fail() {
//		
//		Response response = APITest.doGetRequest("/bpmn/project");
//		System.out.println(response.getStatusCode());
//		Assert.assertEquals(response.getStatusCode(), 404);
//		
//	}
//	
//	/**
//	 * fail fetch get project by Id
//	 * its suppose to try get project and all its tasks
//	 */
//	
//	@Test
//	@Order(2)
//	public void test_fetch_by_id_fail() {
//		
//		Response response = APITest.doGetRequest("/bpmn/project/1");
//		
//		Assert.assertEquals(response.getStatusCode(), 404);
//		
//	}
	
	//create a project - screen number 1
	@Test
	@Order(3)
	public void test_create_resource_success() {
		String projectJSON = new Gson().toJson(createProject());
		
		ResponseBody responseBody = APITest.doPostRequestWithBodyResponse("/bpmn/project", projectJSON);
		
		Assert.assertEquals(200, ((RestAssuredResponseImpl) responseBody).getStatusCode());
		Assert.assertEquals(true, Objects.nonNull(responseBody.as(CreateProjectResponse.class)));
		CreateProjectResponse response = responseBody.as(CreateProjectResponse.class);
		
		Assert.assertEquals(true, Objects.nonNull(response.getTaskId()));
		Assert.assertEquals(true, Objects.nonNull(response.getProcessInstanceId()));
		Assert.assertEquals(true, Objects.nonNull(response.getSavedProject()));
		
		System.out.println(new Gson().toJson(response));
		
	}
	//create a task to the above project - screen 2
	
	//create a second task to the above project
	
	
//	//Fetch all projects
//	@Test
//	@Order(4)
//	public void test_fetch_all_success() {
//		Response response = APITest.doGetRequest("/bpmn/project");
//		
//		Assert.assertEquals(response.getStatusCode(), 200);
//		
//		
//	}
//	
//	
//	/**
//	 * success fetch get project by Id
//	 * it will project and all its tasks
//	 */
//	@Test
//	@Order(4)
//	public void test_fetch_by_id_success() {
//		Response response = APITest.doGetRequest("/bpmn/project/1");
//		
//		Assert.assertEquals(response.getStatusCode(), 200);
//		
//		
//	}
	
	public static Project createProject() {
		Project project = new Project();
		project.setName("Project From Test");
		project.setDescription("Project From Test, Spring-Boot Demo");
		return project;
	}
	
	public static Project createProjectResponse() {
		Project project = new Project();
		project.setId(1L);
		project.setName("Project From Test");
		project.setDescription("Project From Test, Spring-Boot Demo");
		project.setHref("http://nextgen-crm-project/api/project/1");
//		project.setCreateDate(new Date());
		return project;
	}
	
	public static Response doGetRequest(String endpoint){
		
		return
	            given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
	            when().get(endpoint).
	            then().contentType(ContentType.JSON).
	            extract().response();
	}
	
	public static Response doPostRequest(String endpoint, String body){
		
		return
	            given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
	            body(body).
	            when().post(endpoint).
	            then().contentType(ContentType.JSON).
	            extract().response();
	}
	
	
	public static ResponseBody doPostRequestWithBodyResponse(String endpoint, String body){
		
		return
	            given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
	            body(body).
	            when().post(endpoint).
	            body();
	}
}
