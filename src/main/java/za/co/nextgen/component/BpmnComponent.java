package za.co.nextgen.component;

import org.camunda.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.RestAssuredResponseImpl;
import com.jayway.restassured.response.ResponseBody;
import static com.jayway.restassured.RestAssured.given;

import java.util.List;
import java.util.Objects;

import za.co.nextgen.controller.ProjectBPMNController;
import za.co.nextgen.exception.BadRequestException;
import za.co.nextgen.model.Project;
import za.co.nextgen.model.Task;

@Component
public class BpmnComponent {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectBPMNController.class);

	@Value("${project_microservice_url}")
	private String project_url;
	
	@Value("${task_microservice_url}")
	private String task_url;
	
	@Autowired
	private RuntimeService runtimeService;
	
	public void processInit(String executionId, String processInstanceId){
		LOGGER.info("Start processInit");
		runtimeService.setVariable(executionId, "process_continue", true);
		LOGGER.info("End processInit");
	}
	
	public void createProject(String executionId, String processInstanceId){
		try {
		LOGGER.info("Start createProject");
		//Retrieve Project From Process
		Project project = (Project) runtimeService.getVariable(executionId, "project");
		
		String body = new Gson().toJson(project);
		//Create Project On Project MicroService
		ResponseBody responseBody = given()
                .contentType(ContentType.JSON)
                .body(body)
	            .when()
                .post(project_url)
                .body();
		if (((RestAssuredResponseImpl) responseBody).getStatusCode() == HttpStatus.OK.value()) {
			Project savedProject = responseBody.as(Project.class);
			//Store Saved Project Back into The Process
			if(Objects.isNull(savedProject))
				throw new BadRequestException("Project Failed To Create Due To Your Funny Request");
			
			runtimeService.setVariable(executionId, "savedProject", savedProject);
		}else {
			throw new BadRequestException("Project Failed To Create Due To Your Funny Request");
		}
		
		
		
		LOGGER.info("End createProject");
		
		}catch (BadRequestException e) {
			runtimeService.setVariable(executionId, "process_continue", false);
			throw e;
		}
	}
	
	public void createTask(String executionId, String processInstanceId){
		try {
		LOGGER.info("Start createTask");
		//Retrieve Task From Process
		Task task = (Task) runtimeService.getVariable(executionId, "task");
		
		
		String body = new Gson().toJson(task);
		//Create Project On Project MicroService
		ResponseBody responseBody = given()
                .contentType(ContentType.JSON)
                .body(body)
	            .when()
                .post(task_url)
                .body();
		if (((RestAssuredResponseImpl) responseBody).getStatusCode() == HttpStatus.OK.value()) {
			Task savedTask = responseBody.as(Task.class);
			//Store Saved Project Back into The Process
			if(Objects.isNull(savedTask))
				throw new BadRequestException("Task Failed To Create Due To Your Funny Request");
			
			runtimeService.setVariable(executionId, "savedTask", savedTask);
		}else {
			throw new BadRequestException("Task Failed To Create Due To Your Funny Request");
		}
		
		
		
		LOGGER.info("End createTask");
		
		}catch (BadRequestException e) {
			runtimeService.setVariable(executionId, "process_continue", false);
			throw e;
		}
	}
	
	public void getAllProject(String executionId, String processInstanceId){
		try {
		LOGGER.info("Start Get All Project");
		//Retrieve Projects From MicroService
		ResponseBody responseBody = given()
                .contentType(ContentType.JSON)
	            .when()
                .get(project_url)
                .body();
		if (((RestAssuredResponseImpl) responseBody).getStatusCode() == HttpStatus.OK.value()) {
			List<Project> projects = responseBody.as(List.class);
			//Store Saved Project Back into The Process
			if(Objects.isNull(projects) || projects.isEmpty())
				throw new BadRequestException("Failed To Retrive Projects");
			
			runtimeService.setVariable(executionId, "project_list", projects);
		}else {
			throw new BadRequestException("Failed To Retrive Projects");
		}
		
		
		
		LOGGER.info("End Get All Project");
		
		}catch (BadRequestException e) {
			runtimeService.setVariable(executionId, "process_continue", false);
			throw e;
		}
	}
	
	
	public void getProjectById(String executionId, String processInstanceId){
		try {
		LOGGER.info("Start Get Project By Id");
		
		Long projectId = (Long) runtimeService.getVariable(executionId, "project_id");
		//Retrieve Project By Id From MicroService
		ResponseBody responseBody = given()
                .contentType(ContentType.JSON)
	            .when()
                .get(String.format("%s%s%s", project_url,"/",projectId))
                .body();
		if (((RestAssuredResponseImpl) responseBody).getStatusCode() == HttpStatus.OK.value()) {
			Project project = responseBody.as(Project.class);
			//Store Saved Project Back into The Process
			if(Objects.isNull(project))
				throw new BadRequestException("Project Failed To Retrieve Due To Your Funny Request");
			
			runtimeService.setVariable(executionId, "project", project);
		}else {
			throw new BadRequestException("Failed To Retrieve Project");
		}
		
		
		
		LOGGER.info("End Get Project By Id");
		
		}catch (BadRequestException e) {
			runtimeService.setVariable(executionId, "process_continue", false);
			throw e;
		}
	}
	
	
	public void getTasksByProjectId(String executionId, String processInstanceId){
		try {
		LOGGER.info("Start getTasksByProjectId");
		//Retrieve Project ID From Process
		Long projectId = (Long) runtimeService.getVariable(executionId, "project_id");
		
		
		//Get Tasks By Project ID On Task MicroService
		ResponseBody responseBody = given()
                .contentType(ContentType.JSON)
	            .when()
                .get(String.format("%s%s%s", task_url,"/extended/by-project/",projectId))
                .body();
		if (((RestAssuredResponseImpl) responseBody).getStatusCode() == HttpStatus.OK.value()) {
			List<Task> tasks = responseBody.as(List.class);
			//Store Saved Project Back into The Process
			if(Objects.isNull(tasks) || tasks.isEmpty())
				throw new BadRequestException("Failed To Retrieve Tasks Due To Your Funny Request");
			
			runtimeService.setVariable(executionId, "task_list", tasks);
		}else {
			throw new BadRequestException("Failed To Retrieve Tasks Due To Your Funny Request");
		}
		
		
		
		LOGGER.info("End getTasksByProjectId");
		
		}catch (BadRequestException e) {
			runtimeService.setVariable(executionId, "process_continue", false);
			throw e;
		}
	}
	
}
