/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: GHNPersistenceBindings.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.resources;

/**
 * Keeps the association between parameters of GHNs that
 * are stored inside their XML representation.
 * Provides the path to access them.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public enum GHNProfileBindings {

	// These other parameters are automatically retrieved from
	// the xml profile associated to GHNDescriptor instances.
	LOAD1MIN("/GHNDescription/Load/@Last1Min"),
	LOAD5MIN("/GHNDescription/Load/@Last5Min"),
	LOAD15MIN("/GHNDescription/Load/@Last15Min");

	private final static String pathPrefix = "/RIONGHN/ProfileXML";

	/**
	 * If the method is null the value must be retrieved
	 * from the xpath element associated to the profile.
	 */
	private String profileXPath = null;

	GHNProfileBindings(final String profileXPath) {
		this.profileXPath = profileXPath;
	}

	public String getValue() {
		return pathPrefix + this.profileXPath;
	}

	@Override
	public String toString() {
		return this.getValue();
	}

}
