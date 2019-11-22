/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client;


/**
 * The Enum DataMinerAlgorithms.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 22, 2019
 */
public enum DataMinerAlgorithms {

	DEA_CHART("DEA_CHART", "DEA_CHART", "DEA_CHART"),
	SCATTER("SCATTER", "SCATTER", "SCATTER"),
	SPEEDOMETER("SPEEDOMETER","SPEEDOMETER", "Performeter"),
	BOXPLOT("BOXPLOT","BOXPLOT","BOXPLOT"),
	DEA_ANALYSIS("DEA_ANALYSIS", "DEA_ANALYSIS", "DEA_ANALYSIS"),
	CORRELATION("CORRELATION", "CORRELATION","CORRELATION"),
	PERFORMFISH_SYNOPTICTABLE_BATCH("PERFORMFISH_SYNOPTICTABLE_BATCH", "PERFORMFISH_SYNOPTICTABLE_BATCH","PERFORMFISH_SYNOPTICTABLE_BATCH"),
	PERFORMFISH_SYNOPTIC_TABLE_FARM("PERFORMFISH_SYNOPTIC_TABLE_FARM","PERFORMFISH_SYNOPTIC_TABLE_FARM","PERFORMFISH_SYNOPTIC_TABLE_FARM"),
	PERFORMFISH_SYNOPTICTABLE_BATCH_HATCHERY("PERFORMFISH_SYNOPTICTABLE_BATCH_HATCHERY", "PERFORMFISH_SYNOPTICTABLE_BATCH_HATCHERY", "PERFORMFISH_SYNOPTICTABLE_BATCH_HATCHERY"),
	PERFORMFISH_SYNOPTICTABLE_BATCH_PREGROW("PERFORMFISH_SYNOPTICTABLE_BATCH_PREGROW","PERFORMFISH_SYNOPTICTABLE_BATCH_PREGROW","PERFORMFISH_SYNOPTICTABLE_BATCH_PREGROW");

	String id;
	String name;
	String title;

	/**
	 * Instantiates a new data miner algorithms.
	 *
	 * @param id the id
	 * @param name the name
	 * @param title the title
	 */
	DataMinerAlgorithms(String id, String name, String title){
		this.id = id;
		this.name = name;
		this.title = title;
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
	
	
	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


}
