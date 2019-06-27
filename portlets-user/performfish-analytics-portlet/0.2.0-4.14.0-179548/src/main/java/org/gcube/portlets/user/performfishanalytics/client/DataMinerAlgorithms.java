/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client;


/**
 * The Enum DataMinerAlgorithms.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 8, 2019
 */
public enum DataMinerAlgorithms {

	DEA_CHART("DEA_CHART", "DEA_CHART"),
	SCATTER("SCATTER", "SCATTER"),
	SPEEDOMETER("SPEEDOMETER","SPEEDOMETER"),
	BOXPLOT("BOXPLOT","BOXPLOT"),
	DEA_ANALYSIS("DEA_ANALYSIS", "DEA_ANALYSIS"),
	CORRELATION("CORRELATION", "CORRELATION");

	String id;
	String name;

	/**
	 * Instantiates a new data miner algorithms.
	 *
	 * @param id the id
	 * @param name the name
	 */
	DataMinerAlgorithms(String id, String name){
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
