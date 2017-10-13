package org.gcube.portlets.user.reportgenerator.client.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.user.reportgenerator.client.events.AddCommentEvent;

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
	 * @param components .
	 * @param metadata .
	 */
	public TemplateSection(List<TemplateComponent> components, List<Metadata> metadata) {
		this.components = components;
		this.metadata = metadata;
	}

	///*** GETTERS N SETTERS
	/**
	 * enqueue a component
	 */
	public void addComponent(TemplateComponent tc) {
		if (tc != null)
			components.add(tc);
		else {
			throw new NullPointerException();
		}
	}

	public boolean removeComponent(TemplateComponent tc) {
		if (tc != null)
			return components.remove(tc);
		else {
			throw new NullPointerException();
		}
	}

	public ArrayList<String> getSectionComments() {
		ArrayList<String> toRet = new ArrayList<String>();
		for (int i = 0; i < components.size(); i++) {
			TemplateComponent tc = components.get(i);
			if (tc.getUserComments() != null) {
				//toRet.
			}
		}
		return toRet;
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

			if (tc.getContent() != null && tc.getContent().equals(toResize)) {
				GWT.log("FOUND CORRESPONDANCE");
				tc.setWidth(newWidth);
				tc.setHeight(newHeight);
				tc.setContent(toResize);
				break;
			}
		}
	}

	public void discardComments(Widget component) {
		for (int i = 0; i < components.size(); i++) {
			TemplateComponent tc = components.get(i);
			if (tc.getContent() != null && tc.getContent().equals(component)) {
				//removes previous comment is exists
				for (Metadata metadata : tc.getAllMetadata()) {
					if (metadata.getAttribute().equals(TemplateModel.USER_COMMENT)) {
						tc.getAllMetadata().remove(metadata); 
						GWT.log("FOUND and REMOVED");
						break;
					}					
				}
				for (Metadata metadata : tc.getAllMetadata()) {
					if (metadata.getAttribute().equals(TemplateModel.USER_COMMENT_HEIGHT)) {
						tc.getAllMetadata().remove(metadata); 
						break;
					}
				}
			}
		}
	}
	/**
	 * look for the model in the current page and edits its size
	 */
	public void addCommentToComponent(Widget component, String comment2Add, int visibleHeight) {		

		for (int i = 0; i < components.size(); i++) {
			TemplateComponent tc = components.get(i);
			if (tc.getContent() != null && tc.getContent().equals(component)) {
				//removes previous comment is exists
				for (Metadata metadata : tc.getAllMetadata()) {
					if (metadata.getAttribute().equals(TemplateModel.USER_COMMENT)) {
						tc.getAllMetadata().remove(metadata); 
						break;
					}					
				}
				for (Metadata metadata : tc.getAllMetadata()) {
					if (metadata.getAttribute().equals(TemplateModel.USER_COMMENT_HEIGHT)) {
						tc.getAllMetadata().remove(metadata); 
						break;
					}
				}
				//add the comment
				tc.addMetadata(TemplateModel.USER_COMMENT, comment2Add);
				tc.addMetadata(TemplateModel.USER_COMMENT_HEIGHT, ""+visibleHeight);
				GWT.log("Comment Added: " + comment2Add);
				break;
			}
		}
	}
	/**
	 * look if a comment with a specific metadata (that indicate sit is a comment)
	 * exists in the current report model: 		
	 * @return true if comment is present yet false otherwise
	 */
	public boolean hasComments(Widget component) {
		for (int i = 0; i < components.size(); i++) {
			TemplateComponent tc = components.get(i);
			if (tc.getContent() != null && tc.getContent().equals(component)) {
				for (Metadata metadata : tc.getAllMetadata()) 
					if (metadata.getAttribute().equals(TemplateModel.USER_COMMENT)) return true;
			}
		}		
		return false;
	}

	/**
	 * look if a comment with a specific metadata (that indicate sit is a comment)
	 * exists in the current report model: 		
	 * @return true if comment is present yet false otherwise
	 */
	public AddCommentEvent getComponentComments(Widget component) {
		String commentText = "";
		int commentHeight = -1;
		for (int i = 0; i < components.size(); i++) {
			TemplateComponent tc = components.get(i);
			if (tc.getContent() != null && tc.getContent().equals(component)) {
				for (Metadata metadata : tc.getAllMetadata()) {
					if (metadata.getAttribute().equals(TemplateModel.USER_COMMENT)) 
						commentText = metadata.getValue();
					if (metadata.getAttribute().equals(TemplateModel.USER_COMMENT_HEIGHT)) 
						commentHeight = Integer.parseInt(metadata.getValue());
				}
			}
		}		
		return new AddCommentEvent(null, commentText, commentHeight);
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
	/**
	 * 
	 * @param index . 
	 * @return .
	 */
	public TemplateComponent getComponent(int index) {
		return components.get(index);
	}

	/**
	 * 
	 * @param attr .
	 * @param value .
	 */
	public void addMetadata(String attr, String value) {
		if (attr != null && value != null) {
			metadata.add(new Metadata(attr, value));
		}
		else 
			throw new NullPointerException();
	}

	/**
	 * 
	 * @return .
	 */
	public List<Metadata> getAllMetadata() {
		if (metadata == null) {
			new LinkedList<Metadata>();
		}	
		return metadata;
	}

	/**
	 * 
	 * @return .
	 */
	public List<TemplateComponent> getAllComponents() {
		if (components == null) {
			new TemplateSection();
		}			
		return components;
	}


}
