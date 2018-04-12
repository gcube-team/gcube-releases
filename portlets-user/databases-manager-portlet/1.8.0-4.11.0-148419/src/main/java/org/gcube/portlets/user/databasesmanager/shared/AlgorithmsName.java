package org.gcube.portlets.user.databasesmanager.shared;

import java.util.Arrays;
import java.util.List;

/**
 * 
 *
 */
public enum AlgorithmsName {
	LISTDBNAMES("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTDBNAMES"),
	LISTDBINFO("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTDBINFO"),
	LISTDBSCHEMA("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTDBSCHEMA"),
	LISTTABLES("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.LISTTABLES"),
	GETTABLEDETAILS("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.GETTABLEDETAILS"),
	SUBMITQUERY("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SUBMITQUERY"),
	SAMPLEONTABLE("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SAMPLEONTABLE"),
	SMARTSAMPLEONTABLE("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SMARTSAMPLEONTABLE"),
	RANDOMSAMPLEONTABLE("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.RANDOMSAMPLEONTABLE");
	
	
	/**
	 * @param text
	 */
	private AlgorithmsName(final String fullPackageName) {
		this.fullPackageName = fullPackageName;
	}

	private final String fullPackageName;

	@Override
	public String toString() {
		return fullPackageName;
	}

	public String getFullPackageName() {
		return fullPackageName;
	}

	public String getId() {
		return name();
		
	}

	/**
	 * 
	 * @param fullPackageName full package name
	 * @return AlgorithmsName
	 */
	public static AlgorithmsName getFromFullPackageName(String fullPackageName) {
		if (fullPackageName == null || fullPackageName.isEmpty())
			return null;

		for (AlgorithmsName type : values()) {
			if (type.fullPackageName.compareToIgnoreCase(fullPackageName) == 0) {
				return type;
			}
		}
		return null;
	}

	public static List<AlgorithmsName> asList() {
		List<AlgorithmsName> list = Arrays.asList(values());
		return list;
	}
	
}
