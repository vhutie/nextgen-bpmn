package za.co.nextgen.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

import za.co.nextgen.exception.BadRequestException;
import za.co.nextgen.model.Project;
import za.co.nextgen.model.response.CreateProjectResponse;
import za.co.nextgen.model.response.GetAllProjectsResponse;
import za.co.nextgen.model.response.GetProjectByIdResponse;

@RestController
public class ProjectBPMNController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectBPMNController.class);

	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private TaskService taskService;
	
	
	@RequestMapping(value = "/bpmn/project", method = RequestMethod.POST, produces = "application/json")
	 private @ResponseBody
	    ResponseEntity<Object> create(@RequestBody(required = true) Project project ) {

		CreateProjectResponse response = new CreateProjectResponse();
	        LOGGER.info("create project");
	        
	        //Map process variables to start the process with
	        Map<String, Object> processVariables = new HashMap<String, Object>();
	        processVariables.put("project", project);
	        processVariables.put("process_continue", true);
	        
	        //Start Camunda Process
	        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-create-project-task", processVariables);
	        String processInstanceId = processInstance.getProcessInstanceId();
	        
	        //Get Created Project With DB Table ID
	        HistoricVariableInstance savedProjectFromHistory = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableName("savedProject").singleResult();
	        Project savedProject = null;
	        if(Objects.nonNull(savedProjectFromHistory) 
	        		&& Objects.nonNull(savedProjectFromHistory.getValue())) {
	        	savedProject = (Project)savedProjectFromHistory.getValue();
	        	//Add Saved Project To The Response
	        	response.setSavedProject(savedProject);
	        }else {
	        	throw new BadRequestException("Project Failed To Create Due To Your Funny Request");
	        }
	        
	        /**
	         * Get The Human Task This Process Is Stuck On
	         * Task Description
	         *Task Name
	         *Task ID
	         */
	        Task camundaTask = taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey("capture-task-details").singleResult();
	        if(Objects.nonNull(camundaTask)) {
		        response.setTaskId(camundaTask.getId());
		        response.setTaskDescription(camundaTask.getDescription());
		        response.setTaskName(camundaTask.getName());
	        }else {
	        	throw new BadRequestException("Failed To Fetch Task Due To Your Funny Request");
	        }
	        
	        
	        //Get Process Instance ID
	        // we already have processInstanceId
	        response.setProcessInstanceId(processInstanceId);
	        
	        
	        return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value = "/bpmn/project/{projectId}", method = RequestMethod.GET, produces = "application/json")
	 private @ResponseBody
	    ResponseEntity<Object> getById(
	            @PathVariable(name = "projectId", required = false) Long id) {

		
	        LOGGER.info("obtaining project by id {} ", id);
	        GetProjectByIdResponse response = new GetProjectByIdResponse();
			
	      //Map process variables to start the process with
	        Map<String, Object> processVariables = new HashMap<String, Object>();
	        processVariables.put("project_id", id);

	        //Start Camunda Process
	        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-get-project-by-id", processVariables);
	        String processInstanceId = processInstance.getProcessInstanceId();
	        
	        
	        //Retrieve Project From Process
	        HistoricVariableInstance projectFromHistory = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableName("project").singleResult();
	        Project project = null;
	        if(Objects.nonNull(projectFromHistory) 
	        		&& Objects.nonNull(projectFromHistory.getValue())) {
	        	project = (Project)projectFromHistory.getValue();
	        	//Add Saved Project To The Response
	        	response.setProject(project);
	        }else {
	        	throw new BadRequestException("Failed To Retrieve Project Due To Your Funny Request");
	        }
	        
	      //Retrieve Tasks From Process
	        HistoricVariableInstance projectsFromHistory = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableName("task_list").singleResult();
	        List<za.co.nextgen.model.Task> tasks = null;
	        if(Objects.nonNull(projectsFromHistory) 
	        		&& Objects.nonNull(projectsFromHistory.getValue())) {
	        	tasks = (List)projectsFromHistory.getValue();
	        	//Add Tasks To The Response
	        	response.setTasks(tasks);
	        }else {
	        	throw new BadRequestException("Failed To Retrieve Tasks Due To Your Funny Request");
	        }
			
		        LOGGER.info("obtaining all projects ");

		        return new ResponseEntity<Object>(response, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/bpmn/project", method = RequestMethod.GET, produces = "application/json")
	 private @ResponseBody
	    ResponseEntity<Object> getAll() {

		GetAllProjectsResponse response = new GetAllProjectsResponse();
		
		
        //Start Camunda Process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-get-all-projects");
        String processInstanceId = processInstance.getProcessInstanceId();
        
        //Get All Projects 
        HistoricVariableInstance projectsFromHistory = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableName("project_list").singleResult();
        List<Project> projects = null;
        if(Objects.nonNull(projectsFromHistory) 
        		&& Objects.nonNull(projectsFromHistory.getValue())) {
        	projects = (List)projectsFromHistory.getValue();
        	//Add Projects To The Response
        	response.setProjects(projects);
        }else {
        	throw new BadRequestException("Project Failed To Create Due To Your Funny Request");
        }
		
	        LOGGER.info("obtaining all projects ");

	        return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	
	
}
