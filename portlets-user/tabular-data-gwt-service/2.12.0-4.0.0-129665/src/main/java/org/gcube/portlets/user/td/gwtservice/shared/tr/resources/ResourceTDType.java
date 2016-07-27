package org.gcube.portlets.user.td.gwtservice.shared.tr.resources;


/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum ResourceTDType {

	CHART("Chart"), GUESSER("Guesser"), MAP("Map"), CODELIST("Codelist"), CSV(
			"CSV"), SDMX("SDMX"), JSON("JSON"), GENERIC_FILE("Generic File"), GENERIC_TABLE(
			"Generic Table");

	/**
	 * @param text
	 */
	private ResourceTDType(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}
	
	
	

}
