package org.jack.common.orm;

import java.util.List;

public class Project {
	private String name;
	private List<String> modules;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getModules() {
		return modules;
	}
	public void setModules(List<String> modules) {
		this.modules = modules;
	}
}
