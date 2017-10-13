/**
 * 
 */
package org.gcube.data.tr.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;


/**
 * A {@link Request} for the plugin to create a data source.
 * <p>
 * A source is created with a name and a {@link Mode} of access, {@link Mode#FULLACESSS} by default.
 * Optionally, it may have a description and one or more types that label the characterise the structure of the
 * trees that will be stored within it.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement
public class BindSource extends AbstractRequest {

	@XmlAttribute
	private String name;
	
	@XmlElement
	private String description;
	
	@XmlElement
	private Set<QName> types = new HashSet<QName>();
	
	//here for serialisation purposes
	BindSource() {
		super();
	}
	
	/**
	 * Creates an instance for a source with a given name.
	 * @param name the name
	 */
	public BindSource(String name) {
		this(name,Mode.FULLACESSS);
	}
	
	/**
	 * Creates an instance for a source with a given name and in a given mode.
	 * @param name the name
	 */
	public BindSource(String name,Mode mode) {
		super(mode);
		if (name==null || name.isEmpty())
			throw new IllegalArgumentException("source name is null or empty");
		this.name=name;
	}
	
	/**
	 * Returns the name of the source associated with the request.
	 * @return the name
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Returns the types of the source associated with the request.
	 * @return the types 
	 */
	public List<QName> types() {
		return new ArrayList<QName>(this.types);
	}
	
	/**
	 * Adds one or more types for the source associated with the request.
	 * @param types the types
	 * @throws IllegalArgumentException if the input is <code>null<code>
	 */
	public void addTypes(QName ... types) throws IllegalArgumentException {
		
		if (types==null)
			throw new IllegalArgumentException("types are null");
		
		this.types.addAll(Arrays.asList(types));
	}
	
	
	/**
	 * Sets a description for the source associated with the request.
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the description of the source associated with the request.
	 * @return the description
	 */
	public String description() {
		return description;
	}
}
