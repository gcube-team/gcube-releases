package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
/**
 * The <code> SerialazableSection </code> class represents a Template Section that can be associated to any Template
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version April 20011 (1.5) 
 */
public class BasicSection implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5868517234042597438L;
	/**
	 * says if the section is locked
	 */
	private boolean locked = false;
	/**
	 * holds the metadata(s) for the sections
	 */
	private List<Metadata> metadata;
	/**
	 * holds the TemplateComponents for the sections
	 */
	private List<BasicComponent> components;
	
	/**
	 * 
	 * @param components
	 * @param metadata
	 */
	public BasicSection() {
		this.components = new LinkedList<BasicComponent>();
		this.metadata = new LinkedList<Metadata>();
	}
	
	/**
	 * 
	 * @param components .
	 * @param metadata .
	 */
	public BasicSection(List<BasicComponent> components, List<Metadata> metadata) {
		this.components = components;
		this.metadata = metadata;
	}

	
	
///*** GETTERS N SETTERS
	
	/**
	 * @return .
	 */
	public List<Metadata> getMetadata() {
		if (metadata == null)
			return new LinkedList<Metadata>();
		else
			return metadata;
	}

	/**
	 * @param metadata .
	 */
	public void setMetadata(List<Metadata> metadata) {
		this.metadata = metadata;
	}

	/**
	 * 
	 * @return .
	 */
	public List<BasicComponent> getComponents() {
		if (components == null)
			return new LinkedList<BasicComponent>();
		else 
			return components;
	}

	/**
	 *  
	 * @param components .
	 */
	public void setComponents(List<BasicComponent> components) {
		this.components = components;
	}
	/**
	 * 
	 * @return .
	 */
	public boolean isLocked() {
		return locked;
	}
	/**
	 * 
	 * @param locked .
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

}
