package za.co.nextgen.model.response;

import java.util.List;

import za.co.nextgen.model.Project;
import za.co.nextgen.model.Task;

public class GetProjectByIdResponse {

	private Project project;
	
	private List<Task> tasks;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	
	
}
