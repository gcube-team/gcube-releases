package org.gcube.vremanagement.vremodel.cl.stubs.types;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static  org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class VREDescription {

	@XmlElement(namespace=TYPES_NAMESPACE)
	private String name;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String description;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String designer;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String manager;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private Calendar startTime;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private Calendar endTime;
	
	protected VREDescription(){}
	
	public VREDescription(String name, String description, String designer,
			String manager, Calendar startTime, Calendar endTime) {
		super();
		this.name = name;
		this.description = description;
		this.designer = designer;
		this.manager = manager;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * @return the name
	 */
	public String name() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void name(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String description() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void description(String description) {
		this.description = description;
	}

	/**
	 * @return the designer
	 */
	public String designer() {
		return designer;
	}

	/**
	 * @param designer the designer to set
	 */
	public void designer(String designer) {
		this.designer = designer;
	}

	/**
	 * @return the manager
	 */
	public String manager() {
		return manager;
	}

	/**
	 * @param manager the manager to set
	 */
	public void manager(String manager) {
		this.manager = manager;
	}

	/**
	 * @return the startTime
	 */
	public Calendar startTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void startTime(Calendar startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public Calendar endTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void endTime(Calendar endTime) {
		this.endTime = endTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VREDescription [name=" + name + ", description=" + description
				+ ", designer=" + designer + ", manager=" + manager
				+ ", startTime=" + startTime.getTimeInMillis() + ", endTime=" + endTime.getTimeInMillis() + "]";
	}

	
	
}
