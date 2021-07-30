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

import java.util.Objects;

import za.co.nextgen.controller.ProjectBPMNController;
import za.co.nextgen.exception.BadRequestException;
import za.co.nextgen.model.Project;

@Component
public class BpmnComponent {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectBPMNController.class);

	@Value("${project_microservice_url}")
	private String url;
	
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
                .post(url)
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
}
