package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * Groups a list of Components, useful for repetitive elements
 *
 */
public class RepeatableSequence implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4162621086489046629L;
	
	private ArrayList<BasicComponent> groupedComponents;
	private String key;
	private int height;

	public RepeatableSequence() {
		super();
	}
	
	
	public RepeatableSequence(ArrayList<BasicComponent> groupedComponents, String key, int height) {
		super();
		this.groupedComponents = groupedComponents;
		this.key = key;
		this.height = height;
	}


	public RepeatableSequence(ArrayList<BasicComponent> groupedComponents, int height) {
		super();
		this.height = height;
		this.groupedComponents = groupedComponents;
		this.key = "-1";
	}

	public ArrayList<BasicComponent> getGroupedComponents() {
		return groupedComponents;
	}

	public void setGroupedComponents(
			ArrayList<BasicComponent> groupedComponents) {
		this.groupedComponents = groupedComponents;
	}
	
	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((groupedComponents == null) ? 0 : groupedComponents
						.hashCode());
		return result;
	}

	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepeatableSequence other = (RepeatableSequence) obj;
		if (groupedComponents == null) {
			if (other.groupedComponents != null)
				return false;
		} else if (!groupedComponents.equals(other.groupedComponents))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String toReturn = "RepeatableSequence: GroupedComponents=" + getGroupedComponents() + "]";
		return toReturn;
	}	
}
