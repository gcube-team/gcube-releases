/**
 * 
 */
package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * Groups a list of Components, and specify which one of the is the key, useful for dropdown list items, controlled terms, etc
 *
 */
public class Tuple extends RepeatableSequence implements Serializable {

	private static final long serialVersionUID = 3689751957403789201L;
	
	private String key;

	public Tuple() {
		super();
	}
	/**
	 * 
	 * @param key the key to show
	 * @param groupedComponents
	 */
	public Tuple(String key, ArrayList<BasicComponent> groupedComponents) {
		super(groupedComponents, 0);
		this.key= key;
	}
	
	/**
	 * just for GWT serialization purpose
	 * @param key
	 */
	public Tuple(String key) {
		super();
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	@Override
	public String toString() {
		return "Tuple [key=" + key +
				", GroupedComponents=" + getGroupedComponents() + "]";
	}
	
	/**
	 * return a deep copy instance of the object
	 */
	public Tuple clone() {
		ArrayList<BasicComponent> clonedComponents = new ArrayList<BasicComponent>();
		//clone the Model components
		for (BasicComponent bc : getGroupedComponents()) {
			BasicComponent cloned = new BasicComponent(bc.getX(), bc.getY(), bc.getWidth(), bc.getHeight(),
					bc.getTemplatePage(), bc.getType(), bc.getId(), bc.getParamName(), bc.getPossibleContent(), bc.isDoubleColLayout(), bc.isLocked(), bc.getMetadata());
			clonedComponents.add(cloned);
		}
		return new Tuple(new String(key), clonedComponents);
	}
}
