/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class CreateGisLayerJobEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2017
 */
public class CreateGisLayerJobEvent extends GwtEvent<CreateGisLayerJobEventHandler> {

	public static final GwtEvent.Type<CreateGisLayerJobEventHandler> TYPE = new Type<CreateGisLayerJobEventHandler>();
	private String jobName;

	/**
	 * @return the jobName
	 */
	public String getJobName() {

		return jobName;
	}


	/**
	 * @return the jobDescription
	 */
	public String getJobDescription() {

		return jobDescription;
	}

	private String jobDescription;
	private long totalPoints;

	/**
	 * @param jobName
	 * @param string
	 */
	public CreateGisLayerJobEvent(String jobName, String descr, long totalPoints) {

		this.jobName = jobName;
		this.jobDescription = descr;
		this.totalPoints = totalPoints;
	}


	/**
	 * @return the totalPoints
	 */
	public long getTotalPoints() {

		return totalPoints;
	}



	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<CreateGisLayerJobEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(CreateGisLayerJobEventHandler handler) {
		handler.onCreateGisLayerJob(this);
	}
}
