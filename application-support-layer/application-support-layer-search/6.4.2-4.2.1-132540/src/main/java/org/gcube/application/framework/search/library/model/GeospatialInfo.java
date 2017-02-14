package org.gcube.application.framework.search.library.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gcube.application.framework.search.library.util.Point;

public class GeospatialInfo implements Cloneable{
	
	String relation;


	protected Date startingDate;
	protected Date endingDate;
	protected Point[] bounds;
	protected String startingDateString;
	protected String endingDateString;


	/**
	 *  Geospatial info constructor
	 *
	 */
	public GeospatialInfo() {
		this.relation = null;
		this.bounds = null;
		this.endingDate = null;
		this.startingDate = null;
		startingDateString = new String();
		endingDateString = new String();
	}

	/**
	 * @param relation
	 * @param bounds
	 * @param timeInterval
	 */
	public GeospatialInfo(String relation, Point[] bounds,
			Date startTime, Date endTime) {
		this.relation = relation;
		this.bounds = bounds;
		this.startingDate = startTime;
		this.endingDate = endTime;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@hh:mm:ss");
		String tempDate = dateFormat.format(startTime);
		String[] tempMatrix = tempDate.split("@");
		startingDateString = tempMatrix[0] + "T" + tempMatrix[1];
		
		tempDate = dateFormat.format(endTime);
		tempMatrix = tempDate.split("@");
		endingDateString = tempMatrix[0] + "T" + tempMatrix[1];
	}

	/**
	 * @return bounds
	 */
	public Point[] getBounds() {
		return bounds;
	}

	/**
	 * @return relation
	 */
	public String getRelation() {
		return relation;
	}
	
	/**
	 * @param bounds
	 */
	public void setBounds(Point[] bounds) {
		this.bounds = bounds;
	}

	/**
	 * @param relation
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}

	/**
	 * @return the startingDate
	 */
	public Date getStartingDate() {
		return startingDate;
	}

	/**
	 * @param startingDate the startingDate to set
	 */
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@hh:mm:ss");
		String tempDate = dateFormat.format(startingDate);
		String[] tempMatrix = tempDate.split("@");
		startingDateString = tempMatrix[0] + "T" + tempMatrix[1];
	}

	/**
	 * @return the endingDate
	 */
	public Date getEndingDate() {
		return endingDate;
	}

	/**
	 * @param endingDate the endingDate to set
	 */
	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@hh:mm:ss");
		String tempDate = dateFormat.format(endingDate);
		String[] tempMatrix = tempDate.split("@");
		endingDateString = tempMatrix[0] + "T" + tempMatrix[1];
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public GeospatialInfo clone() {
		// TODO Auto-generated method stub
		GeospatialInfo g = new GeospatialInfo();
		g.bounds = new Point[this.bounds.length];
		for(int i=0; i< g.bounds.length; i++)
		{
			g.bounds[i] = this.bounds[i];
		}
		
		if(this.endingDate != null)
			g.endingDate = (Date) this.endingDate.clone();
		else
			g.endingDate = null;
		if(this.startingDate != null)
			g.startingDate = (Date) this.startingDate.clone();
		else
			g.startingDate = null;
		if(this.relation != null)
			g.relation = new String(this.relation);
		else
			g.relation = null;
		
		this.startingDateString = new String(startingDateString);
		this.endingDateString = new String(endingDateString);
		return g;
	}
	
	
	public String getStartingDateString() {
		return startingDateString;
	}
	
	public String getEndingDateString() {
		return endingDateString;
	}

}
