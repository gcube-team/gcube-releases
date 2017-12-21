/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * The Class GisLayerJob.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2017
 */
@Entity
public class GisLayerJob extends DefaultJob{

	/**
	 *
	 */
	private static final long serialVersionUID = 2604265579184366453L;
	private long totalPoints;
	private String layerUUID;
	private String gisViewerAppLink;
	private long completedEntries;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int internalId;



	/**
	 * Instantiates a new gis layer job.
	 */
	public GisLayerJob() {
	}


	/**
	 * Instantiates a new gis layer job.
	 *
	 * @param id the id
	 * @param name the name
	 * @param startTime the start time
	 * @param submitTime the submit time
	 * @param endTime the end time
	 * @param elapsedTime the elapsed time
	 * @param description the description
	 * @param state the state
	 * @param gisViewerAppLink the gis viewer app link
	 * @param totalPoints the total points
	 * @param completedEntries the completed entries
	 */
	public GisLayerJob(String id, String name, long startTime, long submitTime, long endTime,long elapsedTime, String description, String state, String gisViewerAppLink, long totalPoints, long completedEntries) {
		super(id, name, startTime, submitTime, endTime, description, state, elapsedTime);
		this.totalPoints = totalPoints;
		this.gisViewerAppLink = gisViewerAppLink;
		this.completedEntries = completedEntries;
	}


	/**
	 * Gets the completed entries.
	 *
	 * @return the completedEntries
	 */
	public long getCompletedEntries() {

		return completedEntries;
	}


	/**
	 * Sets the completed entries.
	 *
	 * @param completedEntries the completedEntries to set
	 */
	public void setCompletedEntries(long completedEntries) {

		this.completedEntries = completedEntries;
	}

	/**
	 * Gets the total points.
	 *
	 * @return the totalPoints
	 */
	public long getTotalPoints() {

		return totalPoints;
	}

	/**
	 * Gets the layer uuid.
	 *
	 * @return the layerUUID
	 */
	public String getLayerUUID() {

		return layerUUID;
	}


	/**
	 * Sets the layer uuid.
	 *
	 * @param layerUUID the layerUUID to set
	 */
	public void setLayerUUID(String layerUUID) {

		this.layerUUID = layerUUID;
	}

	/**
	 * Sets the total points.
	 *
	 * @param totalPoints the totalPoints to set
	 */
	public void setTotalPoints(long totalPoints) {

		this.totalPoints = totalPoints;
	}

	/**
	 * Gets the gis viewer app link.
	 *
	 * @return the gisViewerAppLink
	 */
	public String getGisViewerAppLink() {

		return gisViewerAppLink;
	}


	/**
	 * Sets the gis viewer app link.
	 *
	 * @param gisViewerAppLink the gisViewerAppLink to set
	 */
	public void setGisViewerAppLink(String gisViewerAppLink) {

		this.gisViewerAppLink = gisViewerAppLink;
	}

	/**
	 * Gets the internal id.
	 *
	 * @return the internalId
	 */
	public int getInternalId() {

		return internalId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GisLayerJob [totalPoints=");
		builder.append(totalPoints);
		builder.append(", layerUUID=");
		builder.append(layerUUID);
		builder.append(", gisViewerAppLink=");
		builder.append(gisViewerAppLink);
		builder.append(", completedEntries=");
		builder.append(completedEntries);
		builder.append(", internalId=");
		builder.append(internalId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", submitTime=");
		builder.append(submitTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", description=");
		builder.append(description);
		builder.append(", state=");
		builder.append(state);
		builder.append(", elapsedTime=");
		builder.append(elapsedTime);
		builder.append("]");
		return builder.toString();
	}
}
