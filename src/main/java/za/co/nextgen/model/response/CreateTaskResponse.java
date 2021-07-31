package za.co.nextgen.model.response;

import za.co.nextgen.model.Project;
import za.co.nextgen.model.Task;

public class CreateTaskResponse {

	private Task savedTask;
	private String taskId;
	private String taskName;
	private String taskDescription;
	private String processInstanceId;
	
	
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskDescription() {
		return taskDescription;
	}
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public Task getSavedTask() {
		return savedTask;
	}
	public void setSavedTask(Task savedTask) {
		this.savedTask = savedTask;
	}
	
	
	
}
