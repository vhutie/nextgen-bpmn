package za.co.nextgen.model.request;

import java.io.Serializable;

import za.co.nextgen.model.Task;

public class CompleteCreateMoreTasksTaskRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String taskId;
	private String processInstanceId;
	private boolean createMoreTasks;
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public boolean isCreateMoreTasks() {
		return createMoreTasks;
	}
	public void setCreateMoreTasks(boolean createMoreTasks) {
		this.createMoreTasks = createMoreTasks;
	}
	
	
	
}
