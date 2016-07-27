package org.gcube.portlets.user.templates.client.model;

import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.user.templates.client.components.ClientRepeatableSequence;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

/**
 * The <code> TemplateSection </code> class represents a Template Section that can be associated to any Template
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2009 (1.4) 
 */

public class TemplateSection {

	/**
	 * holds the metadata(s) for the sections
	 */
	private List<Metadata> metadata;
	/**
	 * holds the TemplateComponents for the sections
	 */
	private List<TemplateComponent> components;

	/**
	 * 
	 * @param components
	 * @param metadata
	 */
	public TemplateSection() {
		this.components = new LinkedList<TemplateComponent>();
		this.metadata = new LinkedList<Metadata>();
	}

	/**
	 * 
	 * @param components
	 * @param metadata
	 */
	public TemplateSection(List<TemplateComponent> components, List<Metadata> metadata) {
		this.components = components;
		this.metadata = metadata;
	}

	///*** GETTERS N SETTERS

	/**
	 * add a component
	 */
	public void addComponent(TemplateComponent tc, int index) {
		if (tc != null)
			components.add(index, tc);
		else {
			throw new NullPointerException();
		}
	}

	/**
	 * remoce a component 
	 * @param toRemove
	 * @return the component or null if there is not
	 */
	public TemplateComponent removeComponent(Widget toRemove) {
		TemplateComponent toReturn = null;
		for (int i = 0; i < components.size(); i++)  {
			TemplateComponent tc = components.get(i);
			if (tc.getContent().equals(toRemove)) {
				toReturn = components.remove(i);
				break;
			}
		}
		return toReturn;
	}

	/**
	 * look for the model in the current page and edits its size
	 * @param toResize .
	 * @param newWidth .
	 * @param newHeight .
	 */
	public void resizeModelComponent(Widget toResize, int newWidth, int newHeight) {		

		for (int i = 0; i < components.size(); i++) {
			TemplateComponent tc = components.get(i);
			if (tc.getContent().equals(toResize)) {
				GWT.log("FOUND CORRESPONDANCE", null);
				tc.setWidth(newWidth);
				tc.setHeight(newHeight);
				tc.setContent(toResize);
				break;
			}
		}
	}

	/**
	 * look for the model in the current page and edits its index, the y variable is used for the index
	 * @param toUpdate
	 * @param newIndex
	 */
	public void updateModelComponentIndex(Widget toUpdate, int newIndex) {		

		for (int i = 0; i < components.size(); i++) {
			TemplateComponent tc = components.get(i);
			if (tc.getContent().equals(toUpdate)) {
				GWT.log("FOUND INDEX TO UPDATE, FOUND", null);
				tc.setY(i);
				break;
			}
		}
	}

	/**
	 * look for the model in the current page and edits its size
	 * @param toLock l
	 * @param locked .  
	 */
	public void lockComponent(Widget toLock, boolean locked) {

		for (int i = 0; i < components.size(); i++) {
			TemplateComponent tc = components.get(i);
			if (tc.getType() == ComponentType.REPEAT_SEQUENCE) {
				ClientRepeatableSequence rs = (ClientRepeatableSequence) tc.getContent();
				for (TemplateComponent rsItem : rs.getGroupedComponents()) {
					if (rsItem.getContent().equals(toLock)) {
						GWT.log("Locking, FOUND CORRESPONDANCE IN SEQUENCE: setting lock to " + locked, null);
						rsItem.setLocked(locked);
						break;
					}
				}
			}else
				if (tc.getContent().equals(toLock)) {
					GWT.log("Locking, FOUND CORRESPONDANCE: setting lock to " + locked, null);
					tc.setLocked(locked);
					break;
				}
		}
	}

	/**
	 * look for the model in the current section and edits its position
	 * @param toRepos .
	 * @param newX .
	 * @param newY .
	 */
	public void repositionModelComponent(Widget toRepos, int newX, int newY) {	

		for (int i = 0; i < components.size(); i++) {
			TemplateComponent tc = components.get(i);
			if (tc.getContent().equals(toRepos)) {
				GWT.log("FOUND CORRESPONDANCE", null);
				tc.setX(newX);
				tc.setY(newY);
				tc.setContent(toRepos);
				break;
			}
		}
	}

	public TemplateComponent getComponent(int index) {
		return components.get(index);
	}


	public void addMetadata(String attr, String value) {
		if (attr != null && value != null) {
			metadata.add(new Metadata(attr, value));
		}
		else 
			throw new NullPointerException();
	}

	public List<Metadata> getAllMetadata() {
		if (metadata == null) {
			new TemplateSection();
		}	
		return metadata;
	}

	public List<TemplateComponent> getAllComponents() {
		if (components == null) {
			new TemplateSection();
		}			
		return components;
	}


}
