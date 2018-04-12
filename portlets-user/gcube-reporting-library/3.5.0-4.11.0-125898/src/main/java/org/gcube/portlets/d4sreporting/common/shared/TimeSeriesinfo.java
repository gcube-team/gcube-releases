package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author massi
 *
 */
public class TimeSeriesinfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3119389353983571356L;
	
	protected String id;
	protected String name;
	protected String timeseriesId;
	protected String title;
	protected String owner;
	protected String creator;
	protected String timeseriesDescription;
	protected String timeseriesCreationDate;
	protected String publisher;
	protected String sourceId;
	protected String sourceName;
	protected String rights;
	protected long dimension;
	protected Date creationTime;
	protected Date lastModificationTime;
	protected List<String> headerLabels;
	
	/**
	 * 
	 */
	public TimeSeriesinfo() {}

	/**
	 * 
	 * @param id id in basket
	 * @param name name 
	 * @param description .
	 * @param owner .
	 * @param creationTime .
	 * @param lastModificationTime .
	 * @param timeseriesId .
	 * @param title .
	 * @param creator .
	 * @param timeseriesDescription .
	 * @param timeseriesCreationDate .
	 * @param publisher .
	 * @param sourceId .
	 * @param sourceName .
	 * @param rights .
	 * @param dimension .
	 * @param headerLabels .
	 */
	public TimeSeriesinfo(String id, String name, String description, String owner, Date creationTime, Date lastModificationTime, 
	 String timeseriesId, String title, String creator, String timeseriesDescription, String timeseriesCreationDate, String publisher, String sourceId,
			String sourceName, String rights, long dimension, List<String> headerLabels) {

		this.id = id;
		this.name = name;
		this.timeseriesId = timeseriesId;
		this.title = title;
		this.creator = creator;
		this.timeseriesDescription = timeseriesDescription;
		this.timeseriesCreationDate = timeseriesCreationDate;
		this.publisher = publisher;
		this.sourceId = sourceId;
		this.sourceName = sourceName;
		this.rights = rights;
		this.dimension = dimension;
		this.headerLabels = headerLabels;
		this.owner = owner;
		this.creationTime = creationTime;
		this.lastModificationTime = lastModificationTime;
	}
	
	/**
	 * 
	 * @return .
	 */
	public Date getLastModificationTime() {
		return lastModificationTime;
	}

	/**
	 * 
	 * @param lastModificationTime .
	 */
	public void setLastModificationTime(Date lastModificationTime) {
		this.lastModificationTime = lastModificationTime;
	}
	/**
	 * 
	 * @return .
	 */
	public Date getCreationTime() {
		return creationTime;
	}
	/**
	 * 
	 * @param creationTime .
	 */
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * 
	 * @return .
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * 
	 * @param owner .
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * 
	 * @return .
	 */

	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id .
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 
	 * @return .
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param name .
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCreator() {
		return creator;
	}


	/**
	 * {@inheritDoc}
	 */
	public long getDimension() {
		return  dimension;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getPublisher() {
		return publisher;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getRights() {
		return rights;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getSourceId() {
		return sourceId;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getSourceName() {
		return sourceName;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getTimeSeriesCreationDate() {
		return timeseriesCreationDate;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getTimeSeriesDescription() {
		return timeseriesDescription;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getTimeSeriesId() {
		return timeseriesId;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @return the headerLabels
	 */
	public List<String> getHeaderLabels() {
		return headerLabels;
	}
	
	
}
