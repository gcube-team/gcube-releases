package org.gcube.portlets.user.trainingcourse.client.view;


// TODO: Auto-generated Javadoc
/**
 * The Enum CourseStatus.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 23, 2018
 */
public enum CourseStatus {
	
	/** The active. */
	ACTIVE("Enable", "Enabling", "Enabled"),
	
	/** The idle. */
	IDLE("Disable", "Disabling", "Disabled");

	private String infinitive;

	private String presentParticiple;

	private String pastParticiple;
	
	/**
	 * Instantiates a new course status.
	 *
	 * @param infinitive the id
	 * @param pastPariciple the label
	 */
	CourseStatus(String infinitive, String presentParticiple, String pastParticiple){
		this.infinitive = infinitive;
		this.presentParticiple = presentParticiple;
		this.pastParticiple = pastParticiple;
	}
	
	public String getInfinitive() {
		return infinitive;
	}

	public String getPresentParticiple() {
		return presentParticiple;
	}

	public String getPastParticiple() {
		return pastParticiple;
	}
	
	

}
