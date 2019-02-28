/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2017
 */
@MappedSuperclass
public class DefaultJob implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 5677419614560436596L;

	public final static String ID_FIELD = "id";
	public final static String NAME = "name";
	public final static String DESCRIPTION = "description";
	public final static String STARTTIME = "startTime";
	public final static String SUBMITTIME = "submitTime";
	public final static String ENDTIME = "endTime";
	public static final String STATE = "state";
	public static final String ELAPSEDTIME = "Elapsed Time";

	protected String id;
	protected String name;
	protected long startTime;
	protected long submitTime;
	protected long endTime;
	protected String description;
	protected String state;
	protected long elapsedTime;


	/**
	 *
	 */
	public DefaultJob() {

	}

	/**
	 * @param id
	 * @param name
	 * @param startTime
	 * @param submitTime
	 * @param endTime
	 * @param description
	 * @param state
	 * @param elapsedTime
	 */
	public DefaultJob(String id, String name, long startTime, long submitTime, long endTime, String description, String state, long elapsedTime) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.submitTime = submitTime;
		this.endTime = endTime;
		this.description = description;
		this.state = state;
		this.elapsedTime = elapsedTime;
	}




	/**
	 * @return the id
	 */
	public String getId() {

		return id;
	}




	/**
	 * @return the name
	 */
	public String getName() {

		return name;
	}




	/**
	 * @return the startTime
	 */
	public long getStartTime() {

		return startTime;
	}




	/**
	 * @return the submitTime
	 */
	public long getSubmitTime() {

		return submitTime;
	}




	/**
	 * @return the endTime
	 */
	public long getEndTime() {

		return endTime;
	}




	/**
	 * @return the description
	 */
	public String getDescription() {

		return description;
	}




	/**
	 * @return the state
	 */
	public String getState() {

		return state;
	}




	/**
	 * @return the elapsedTime
	 */
	public long getElapsedTime() {

		return elapsedTime;
	}




	/**
	 * @param id the id to set
	 */
	public void setId(String id) {

		this.id = id;
	}




	/**
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}




	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {

		this.startTime = startTime;
	}




	/**
	 * @param submitTime the submitTime to set
	 */
	public void setSubmitTime(long submitTime) {

		this.submitTime = submitTime;
	}




	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {

		this.endTime = endTime;
	}




	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {

		this.description = description;
	}




	/**
	 * @param state the state to set
	 */
	public void setState(String state) {

		this.state = state;
	}




	/**
	 * @param elapsedTime the elapsedTime to set
	 */
	public void setElapsedTime(long elapsedTime) {

		this.elapsedTime = elapsedTime;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("DefaultJob [id=");
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
