package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public enum ProjectSupportType {

	REDIT("REdit"), BASHEDIT("BashEdit"), BLACKBOX("BlackBox");

	private String id;

	private ProjectSupportType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return id;
	}

	public String getLabel() {
		return id;
	}
	
	public static ProjectSupportType valueFromLabel(String label) {
		for(ProjectSupportType type: values()){
			if(type.getId().compareTo(label)==0){
				return type;
			}
		}
		return null;
	}

	public static List<ProjectSupportType> asList() {
		List<ProjectSupportType> list = Arrays.asList(values());
		return list;
	}

}
