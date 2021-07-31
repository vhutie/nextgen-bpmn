package za.co.nextgen.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
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

import za.co.nextgen.exception.BadRequestException;
import za.co.nextgen.model.Project;
import za.co.nextgen.model.Task;
import za.co.nextgen.model.request.CompleteCreateMoreTasksTaskRequest;
import za.co.nextgen.model.request.CompleteTaskTaskRequest;
import za.co.nextgen.model.response.CreateMoreTasksResponse;
import za.co.nextgen.model.response.CreateTaskResponse;

@RestController
public class TaskBPMNController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskBPMNController.class);

	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	
	@RequestMapping(value = "/bpmn/task/complete-create-task", method = RequestMethod.POST, produces = "application/json")
	 private @ResponseBody
	    ResponseEntity<Object> create(@RequestBody(required = true) CompleteTaskTaskRequest request ) {

		CreateTaskResponse response = new CreateTaskResponse();
		
		// Get Task
		org.camunda.bpm.engine.task.Task task = this.getTask(request.getProcessInstanceId(), "capture-task-details");
		if(Objects.nonNull(task)) {
			LOGGER.info("create task");
	    	Map<String, Object> taskVariables = new HashMap<String, Object>();
	    	taskVariables.put("task", request.getTask());
	    	
	    	//Complete Task
	    	this.taskService.complete(task.getId(), taskVariables);
	    	
	    	
        }else {
        	throw new BadRequestException("Task Failed To Create Due To Your Funny Request");
        }
		
		
		//Get Created Task With DB Table ID
        HistoricVariableInstance savedTaskFromHistory = historyService.createHistoricVariableInstanceQuery().processInstanceId(request.getProcessInstanceId()).variableName("savedTask").singleResult();
        Task savedTask = null;
        if(Objects.nonNull(savedTaskFromHistory) 
        		&& Objects.nonNull(savedTaskFromHistory.getValue())) {
        	savedTask = (Task)savedTaskFromHistory.getValue();
        	//Add Saved Project To The Response
        	response.setSavedTask(savedTask);
        }else {
        	throw new BadRequestException("Task Failed To Create Due To Your Funny Request");
        }
        
        /**
         * Get The Human Task This Process Is Stuck On
         * Task Description
         *Task Name
         *Task ID
         */
        org.camunda.bpm.engine.task.Task camundaTask = taskService.createTaskQuery().processInstanceId(request.getProcessInstanceId()).taskDefinitionKey("want-to-create-more-tasks").singleResult();
        if(Objects.nonNull(camundaTask)) {
	        response.setTaskId(camundaTask.getId());
	        response.setTaskDescription(camundaTask.getDescription());
	        response.setTaskName(camundaTask.getName());
        }else {
        	throw new BadRequestException("Failed To Fetch Task Due To Your Funny Request");
        }
		
        // we already have processInstanceId
        response.setProcessInstanceId(request.getProcessInstanceId());
	        
    	return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/bpmn/task/complete-create-more-tasks-task", method = RequestMethod.POST, produces = "application/json")
	 private @ResponseBody
	    ResponseEntity<Object> decideToCreateMore(@RequestBody(required = true) CompleteCreateMoreTasksTaskRequest request ) {

		CreateMoreTasksResponse response = new CreateMoreTasksResponse();
		
		// Get Task
		org.camunda.bpm.engine.task.Task task = this.getTask(request.getProcessInstanceId(), "want-to-create-more-tasks");
		if(Objects.nonNull(task)) {
			LOGGER.info("decide to create more tasks");
	    	Map<String, Object> taskVariables = new HashMap<String, Object>();
	    	taskVariables.put("create_other_task", request.isCreateMoreTasks());
	    	
	    	//Complete Task
	    	this.taskService.complete(task.getId(), taskVariables);
	    	
	    	
       }else {
       	throw new BadRequestException("Task Failed To Create Due To Your Funny Request");
       }
		
		if(request.isCreateMoreTasks()) {
	       /**
	        * Get The Human Task This Process Is Stuck On
	        * Task Description
	        *Task Name
	        *Task ID
	        */
	       org.camunda.bpm.engine.task.Task camundaTask = taskService.createTaskQuery().processInstanceId(request.getProcessInstanceId()).taskDefinitionKey("capture-task-details").singleResult();
	       if(Objects.nonNull(camundaTask)) {
		        response.setTaskId(camundaTask.getId());
		        response.setTaskDescription(camundaTask.getDescription());
		        response.setTaskName(camundaTask.getName());
	       }else {
	       	throw new BadRequestException("Failed To Fetch Task Due To Your Funny Request");
	       }
		}
		
       // we already have processInstanceId
       response.setProcessInstanceId(request.getProcessInstanceId());
	        
   	return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	private org.camunda.bpm.engine.task.Task getTask(String processId, String taskName) {
    	org.camunda.bpm.engine.task.Task task = this.taskService
				.createTaskQuery()
				.processInstanceId(processId)
				.taskDefinitionKey(taskName)
				.singleResult();
    	   			   	
    	if(Objects.isNull(task)) {
    		task = this.taskService
    				.createTaskQuery()
                    .processInstanceId(processId)
                    .singleResult();
    	}    	
    	return task;
    }
	
	
	
}
