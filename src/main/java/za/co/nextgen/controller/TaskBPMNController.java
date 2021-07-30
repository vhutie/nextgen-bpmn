package za.co.nextgen.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import za.co.nextgen.model.Project;
import za.co.nextgen.model.Task;

@RestController
public class TaskBPMNController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskBPMNController.class);


	
	@RequestMapping(value = "/bpmn/task", method = RequestMethod.POST, produces = "application/json")
	 private @ResponseBody
	    ResponseEntity<Object> create(@RequestBody(required = true) Task task ) {

	        LOGGER.info("create task");

	        return null;
	}
	
	
	

	
	
	
}
