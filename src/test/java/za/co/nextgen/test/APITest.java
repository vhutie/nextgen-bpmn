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
import com.google.gson.GsonBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RestAssuredResponseImpl;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ResponseBody;

import za.co.nextgen.NextgenServiceApplication;
import za.co.nextgen.model.Project;
import za.co.nextgen.model.ProjectRef;
import za.co.nextgen.model.Task;
import za.co.nextgen.model.request.CompleteCreateMoreTasksTaskRequest;
import za.co.nextgen.model.request.CompleteTaskTaskRequest;
import za.co.nextgen.model.response.CreateMoreTasksResponse;
import za.co.nextgen.model.response.CreateProjectResponse;
import za.co.nextgen.model.response.CreateTaskResponse;

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
    
    private static String processInstanceId;
    private static String taskInstanceId;
    private static Long projectId;
    private static String projectHref;
    
    private static String secondTaskInstanceId;

    @BeforeAll
    public static void startMockServer() {
        mockServer = startClientAndServer(5100);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        MockServerClient mockServerClient = new MockServerClient("localhost", 5100);
        
        // Fake Project MicroService Request And Response
        mockServerClient
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/project/api/project")
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
                                		gson.toJson(createProjectResponse())
                                			)
                );
        
        
        
        
     // Fake Task MicroService Response
        
        mockServerClient
        .when(
                request()
                        .withMethod("POST")
                        .withPath("/task/api/task")
        )
        .respond(
        		
                response().withStatusCode(200)
                .withHeaders(
                        new Header("Content-Type", "application/json; charset=utf-8"),
                        new Header("Cache-Control", "public, max-age=86400")
                )
                        .withBody(
                        		gson.toJson(createTaskResponse())
                        			)
        );
//        System.out.println(expectations);

        RequestDefinition[] requestDefinitions = new MockServerClient("localhost", 5100)
                .retrieveRecordedRequests(
                        request()
                                .withPath("/project/api/project")
                                .withBody(
                                        new Gson().toJson(APITest.createProject())
                                ).withMethod("POST")
//                        .request().withPath("/task/api/task")
//                        .withBody(
//                                new Gson().toJson(APITest.createCompleteTaskTaskRequest())
//                        ).withMethod("POST")
                );
        System.out.println(requestDefinitions);
    }

    @AfterAll
    public static void stopMockServer() {
       mockServer.stop();
    }

	
	//create a project - screen number 1
	@Test
	@Order(1)
	public void test_create_project_resource_success() {
		String projectJSON = new Gson().toJson(createProject());
		
		ResponseBody responseBody = APITest.doPostRequestWithBodyResponse("/bpmn/project", projectJSON);
		
		Assert.assertEquals(200, ((RestAssuredResponseImpl) responseBody).getStatusCode());
		Assert.assertEquals(true, Objects.nonNull(responseBody.as(CreateProjectResponse.class)));
		CreateProjectResponse response = responseBody.as(CreateProjectResponse.class);
		
		Assert.assertEquals(true, Objects.nonNull(response.getTaskId()));
		Assert.assertEquals(true, Objects.nonNull(response.getProcessInstanceId()));
		Assert.assertEquals(true, Objects.nonNull(response.getSavedProject()));
		
		System.out.println(new Gson().toJson(response));
		APITest.processInstanceId = response.getProcessInstanceId();
		APITest.taskInstanceId = response.getTaskId();
		APITest.projectId = response.getSavedProject().getId();
		APITest.projectHref = response.getSavedProject().getHref();
	}
	//create a task to the above project - screen 2
	@Test
	@Order(2)
	public void test_complete_create_task_success() {
		String taskJSON = new Gson().toJson(createCompleteTaskTaskRequest());
		
		System.out.println(taskJSON);
		ResponseBody responseBody = APITest.doPostRequestWithBodyResponse("/bpmn/task/complete-create-task", taskJSON);
		
		Assert.assertEquals(200, ((RestAssuredResponseImpl) responseBody).getStatusCode());
		Assert.assertEquals(true, Objects.nonNull(responseBody.as(CreateTaskResponse.class)));
		CreateTaskResponse response = responseBody.as(CreateTaskResponse.class);
		
		Assert.assertEquals(true, Objects.nonNull(response.getTaskId()));
		Assert.assertEquals(true, Objects.nonNull(response.getProcessInstanceId()));
		Assert.assertEquals(true, Objects.nonNull(response.getSavedTask()));
		
		System.out.println(new Gson().toJson(response));
		
		
		
	}
	
	@Test
	@Order(3)
	public void test_complete_create_more_tasks_yes_success() {
		String moreTasksJSON = new Gson().toJson(createCompleteCreateMoreTasksTaskYesRequest());
		
		System.out.println(moreTasksJSON);
		ResponseBody responseBody = APITest.doPostRequestWithBodyResponse("/bpmn/task/complete-create-more-tasks-task", moreTasksJSON);
		
		Assert.assertEquals(200, ((RestAssuredResponseImpl) responseBody).getStatusCode());
		Assert.assertEquals(true, Objects.nonNull(responseBody.as(CreateMoreTasksResponse.class)));
		CreateMoreTasksResponse response = responseBody.as(CreateMoreTasksResponse.class);
		
		Assert.assertEquals(true, Objects.nonNull(response.getTaskId()));
		Assert.assertEquals(true, Objects.nonNull(response.getProcessInstanceId()));
		APITest.secondTaskInstanceId = response.getTaskId();
		System.out.println(new Gson().toJson(response));
		
	}
	//create a second task to the above project
	@Test
	@Order(4)
	public void test_complete_create_second_task_success() {
		String taskJSON = new Gson().toJson(createCompleteSecondTaskTaskRequest());
		
		System.out.println(taskJSON);
		ResponseBody responseBody = APITest.doPostRequestWithBodyResponse("/bpmn/task/complete-create-task", taskJSON);
		
		Assert.assertEquals(200, ((RestAssuredResponseImpl) responseBody).getStatusCode());
		Assert.assertEquals(true, Objects.nonNull(responseBody.as(CreateTaskResponse.class)));
		CreateTaskResponse response = responseBody.as(CreateTaskResponse.class);
		
		Assert.assertEquals(true, Objects.nonNull(response.getTaskId()));
		Assert.assertEquals(true, Objects.nonNull(response.getProcessInstanceId()));
		Assert.assertEquals(true, Objects.nonNull(response.getSavedTask()));
		
		System.out.println(new Gson().toJson(response));
		
		
		
	}
	
	@Test
	@Order(5)
	public void test_complete_create_more_tasks_no_success() {
		String moreTasksJSON = new Gson().toJson(createCompleteCreateMoreTasksTaskNoRequest());
		
		System.out.println(moreTasksJSON);
		ResponseBody responseBody = APITest.doPostRequestWithBodyResponse("/bpmn/task/complete-create-more-tasks-task", moreTasksJSON);
		
		Assert.assertEquals(200, ((RestAssuredResponseImpl) responseBody).getStatusCode());
		Assert.assertEquals(true, Objects.nonNull(responseBody.as(CreateMoreTasksResponse.class)));
		CreateMoreTasksResponse response = responseBody.as(CreateMoreTasksResponse.class);
		
		Assert.assertEquals(true, Objects.nonNull(response.getProcessInstanceId()));
		System.out.println(new Gson().toJson(response));
		
	}
	
	
	//Get all projects via Camunda
	
	
	//Get Project By Id Via Camunda and also return its tasks
	

	
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
		project.setCreateDate(new Date());
		return project;
	}
	
	public static Task createTaskResponse() {
		Task task =new Task();
		task.setName("Task One From Test");
		task.setDescription("Description From Task One From Test");
		ProjectRef projectRef = new ProjectRef();
		projectRef.setId(APITest.projectId);
		projectRef.setHref(APITest.projectHref);
		task.setProjectRef(projectRef);
		task.setHref("http://nextgen-crm-task/api/task/1");
		task.setCreateDate(new Date());
		return task;
	}
	
	public static CompleteTaskTaskRequest createCompleteTaskTaskRequest(){
		CompleteTaskTaskRequest request = new CompleteTaskTaskRequest();
		request.setProcessInstanceId(APITest.processInstanceId);
		request.setTaskId(APITest.taskInstanceId);
		Task task =new Task();
		task.setName("Task One From Test");
		task.setDescription("Description From Task One From Test");
		ProjectRef projectRef = new ProjectRef();
		projectRef.setId(APITest.projectId);
		projectRef.setHref(APITest.projectHref);
		task.setProjectRef(projectRef);
		request.setTask(task);
		return request;
	}
	
	public static CompleteTaskTaskRequest createCompleteSecondTaskTaskRequest(){
		CompleteTaskTaskRequest request = new CompleteTaskTaskRequest();
		request.setProcessInstanceId(APITest.processInstanceId);
		request.setTaskId(APITest.secondTaskInstanceId);
		Task task =new Task();
		task.setName("Task Two From Test");
		task.setDescription("Description From Task Two From Test");
		ProjectRef projectRef = new ProjectRef();
		projectRef.setId(APITest.projectId);
		projectRef.setHref(APITest.projectHref);
		task.setProjectRef(projectRef);
		request.setTask(task);
		return request;
	}
	
	public static CompleteCreateMoreTasksTaskRequest createCompleteCreateMoreTasksTaskYesRequest(){
		CompleteCreateMoreTasksTaskRequest request = new CompleteCreateMoreTasksTaskRequest();
		request.setProcessInstanceId(APITest.processInstanceId);
		request.setTaskId(APITest.taskInstanceId);
		request.setCreateMoreTasks(true);
		return request;
	}
	
	public static CompleteCreateMoreTasksTaskRequest createCompleteCreateMoreTasksTaskNoRequest(){
		CompleteCreateMoreTasksTaskRequest request = new CompleteCreateMoreTasksTaskRequest();
		request.setProcessInstanceId(APITest.processInstanceId);
		request.setTaskId(APITest.taskInstanceId);
		request.setCreateMoreTasks(false);
		return request;
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
