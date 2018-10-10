/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.shared;


/**
 * The Enum AccoutingReference.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 11, 2016
 */
public enum AccoutingReference {


	DOWNLOAD("dwn", "Download"),
	JAVADOC("jdc", "Javadoc"),
	MAVEN_REPO("mvn", "Maven Repository"),
	WIKI("wki", "Wiki Doc"),
	GITHUB("ghb", "GitHub");

	private String id;
	private String name;

	/**
	 * Instantiates a new accouting reference.
	 *
	 * @param id the id
	 * @param name the name
	 */
	AccoutingReference(String id, String name){
		this.id = id;
		this.name = name;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
