/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder.item;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItem;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItemType;


/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public class GWTTimeSeries extends GWTFolderItem implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3119389353983571356L;
	
	protected String timeseriesId;
	protected String title;
	protected String creator;
	protected String timeseriesDescription;
	protected String timeseriesCreationDate;
	protected String publisher;
	protected String sourceId;
	protected String sourceName;
	protected String rights;
	protected long dimension;
	protected List<String> headerLabels;
	

	public GWTTimeSeries() {}


	public GWTTimeSeries(String id, String name, String description, String owner, Date creationTime, 
			GWTProperties properties, Date lastModificationTime, GWTWorkspaceItemAction lastAction,
			GWTWorkspaceFolder parent, long length, String timeseriesId, String title, 
			String creator, String timeseriesDescription,
			String timeseriesCreationDate, String publisher, String sourceId,
			String sourceName, String rights, long dimension, List<String> headerLabels) {
		super(id, name, description, owner, creationTime, properties, lastModificationTime, lastAction, parent, length);
		
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.TIME_SERIES;
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
