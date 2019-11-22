package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public enum CodeContentType {

	Main("Main"), Binary("Binary");

	private String id;

	private CodeContentType(String id) {
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
	
	public static CodeContentType valueFromLabel(String label) {
		for(CodeContentType type: values()){
			if(type.getId().compareTo(label)==0){
				return type;
			}
		}
		return null;
	}

	public static List<CodeContentType> asList() {
		List<CodeContentType> list = Arrays.asList(values());
		return list;
	}

}
