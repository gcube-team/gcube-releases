package org.gcube.data.spd.catalogueoflife.capabilities;

import java.util.List;
import java.util.Collections;

public class SpeciesIdentifier {

	private String name;
	
	private List<String> children = Collections.emptyList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getChildren() {
		return children;
	}

	public void setChildren(List<String> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "SpeciesIdentifier [name=" + name + ", children=" + children
				+ "]";
	}

	
}
