package za.co.nextgen.model.response;

import za.co.nextgen.model.Project;

public class CreateProjectResponse {

	private Project savedProject;
	private String taskId;
	private String taskName;
	private String taskDescription;
	private String processInstanceId;
	
	
	public Project getSavedProject() {
		return savedProject;
	}
	public void setSavedProject(Project savedProject) {
		this.savedProject = savedProject;
	}
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
	
	
	
}
