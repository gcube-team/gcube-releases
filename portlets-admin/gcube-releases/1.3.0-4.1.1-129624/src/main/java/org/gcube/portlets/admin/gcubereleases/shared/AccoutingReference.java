/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.shared;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 3, 2015
 */
public enum AccoutingReference {

	
	DOWNLOAD("dwn", "Download"),
	JAVADOC("jdc", "Javadoc"),
	MAVEN_REPO("mvn", "Maven Repository"),
	WIKI("wki", "Wiki Doc");
	
	private String id;
	private String name;
	
	AccoutingReference(String id, String name){
		this.id = id;
		this.name = name;
	}

	/**
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
