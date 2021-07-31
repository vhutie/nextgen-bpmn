package za.co.nextgen.model.response;

import java.util.List;

import za.co.nextgen.model.Project;

public class GetAllProjectsResponse {

	private List<Project> projects;

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
	
}
