/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder.items.ts;

import java.io.Serializable;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class TimeSeriesInfo implements Serializable, Cloneable {

	private static final long serialVersionUID = -2391248161206397329L;
	
	protected String id;
	protected String title;
	protected String creator;
	protected String description;
	protected String creationDate;
	protected String publisher;
	protected String sourceId;
	protected String sourceName;
	protected String rights;
	protected long dimension;
	
	/**
	 * Constructs a new TimeSeriesInfo class.
	 * @param id the Time Series id.
	 * @param title the Time Series title.
	 * @param creator the Time Series creator.
	 * @param description the Time Series description. 
	 * @param creationDate the Time Series creation date.
	 * @param publisher the Time Series publisher.
	 * @param sourceId the Time Series source id.
	 * @param sourceName the Time Series source name.
	 * @param rights the Time Series associated rights.
	 * @param dimension the Time Series dimension.
	 */
	public TimeSeriesInfo(String id, String title, String creator,
			String description, String creationDate,
			String publisher, String sourceId, String sourceName,
			String rights, long dimension) {
		this.id = id;
		this.title = title;
		this.creator = creator;
		this.description = description;
		this.creationDate = creationDate;
		this.publisher = publisher;
		this.sourceId = sourceId;
		this.sourceName = sourceName;
		this.rights = rights;
		this.dimension = dimension;
	}

	/**
	 * Return the Time Series id.
	 * @return the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Return the Time Series title.
	 * @return the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Return the Time Series creator.
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * Return the Time Series description.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Return the Time Series 
	 * @return the CreationDate
	 */
	public String getCreationDate() {
		return creationDate;
	}

	/**
	 * Return the Time Series 
	 * @return the publisher
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * Return the Time Series 
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * Return the Time Series 
	 * @return the sourceName
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * Return the Time Series 
	 * @return the rights
	 */
	public String getRights() {
		return rights;
	}

	/**
	 * Return the Time Series 
	 * @return the dimension
	 */
	public long getDimension() {
		return dimension;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (int) (dimension ^ (dimension >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((publisher == null) ? 0 : publisher.hashCode());
		result = prime * result + ((rights == null) ? 0 : rights.hashCode());
		result = prime * result
				+ ((sourceId == null) ? 0 : sourceId.hashCode());
		result = prime * result
				+ ((sourceName == null) ? 0 : sourceName.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)	
			

			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeSeriesInfo other = (TimeSeriesInfo) obj;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (dimension != other.dimension)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (publisher == null) {
			if (other.publisher != null)
				return false;
		} else if (!publisher.equals(other.publisher))
			return false;
		if (rights == null) {
			if (other.rights != null)
				return false;
		} else if (!rights.equals(other.rights))
			return false;
		if (sourceId == null) {
			if (other.sourceId != null)
				return false;
		} else if (!sourceId.equals(other.sourceId))
			return false;
		if (sourceName == null) {
			if (other.sourceName != null)
				return false;
		} else if (!sourceName.equals(other.sourceName))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeSeriesInfo [creator=");
		builder.append(creator);
		builder.append(", dimension=");
		builder.append(dimension);
		builder.append(", publisher=");
		builder.append(publisher);
		builder.append(", rights=");
		builder.append(rights);
		builder.append(", sourceId=");
		builder.append(sourceId);
		builder.append(", sourceName=");
		builder.append(sourceName);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append(", description=");
		builder.append(description);
		builder.append(", timeSeriesId=");
		builder.append(id);
		builder.append(", title=");
		builder.append(title);
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TimeSeriesInfo clone()
	{
		return new TimeSeriesInfo(id, title, creator, description, creationDate, publisher, sourceId, sourceName, rights, dimension);
	}

}
