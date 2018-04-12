/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.process;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class TemplateDescriptor {

	public static TemplateDescriptor descriptors[] = { new TemplateDescriptor("HCAF", "HCAF Data Set", ""),
			new TemplateDescriptor("OCCURRENCE_SPECIES", "Occurrence Species Data Set", ""),
			new TemplateDescriptor("OCCURRENCE_AQUAMAPS", "Occurrence Aquamaps", ""),
			new TemplateDescriptor("HSPEN", "HSPEN Data Set", ""),
			new TemplateDescriptor("HSPEC", "HSPEC Data Set", ""),
			new TemplateDescriptor("CLUSTER", "Cluster Data Set", ""),
			new TemplateDescriptor("TRAININGSET", "Neural Network Training Set", ""),
			new TemplateDescriptor("TESTSET", "Neural Network Test Set", ""),
			new TemplateDescriptor("GENERIC", "Generic Data set", ""),
			new TemplateDescriptor("MINMAXLAT", "Min Max Lat Data Set", ""),
			new TemplateDescriptor("TIMESERIES", "time Series Data Set", ""), };

	public static String[] s = new String[] { "" };
	public static Map<String, String[][]> map;
	static {
		map = new HashMap<String, String[][]>();
		map.put("HCAF", new String[][] { { "csquarecode", "string" }, { "depthmin", "real" }, { "depthmax", "real" },
				{ "depthmean", "real" }, { "depthsd", "real" }, { "sstanmean", "real" }, { "sstansd", "real" },
				{ "sstmnmax", "real" }, { "sstmnmin", "real" }, { "sstmnrange", "real" }, { "sbtanmean", "real" },
				{ "salinitymean", "real" }, { "salinitysd", "real" }, { "salinitymax", "real" },
				{ "salinitymin", "real" }, { "salinitybmean", "real" }, { "primprodmean", "integer" },
				{ "iceconann", "real" }, { "iceconspr", "real" }, { "iceconsum", "real" }, { "iceconfal", "real" },
				{ "iceconwin", "real" }, { "faoaream", "integer" }, { "eezall", "string" }, { "lme", "integer" },
				{ "landdist", "integer" }, { "oceanarea", "real" }, { "centerlat", "real" },
				{ "centerlong", "real" }, });
	}
	public static TemplateDescriptor defaultDescriptor = descriptors[0];

	// public enum Template {
	// HCAF,
	// OCCURRENCE_SPECIES,
	// OCCURRENCE_AQUAMAPS,
	// HSPEN,
	// HSPEC,
	// CLUSTER,
	// TRAININGSET,
	// TESTSET,
	// GENERIC,
	// MINMAXLAT,
	// TIMESERIES,
	// }

	private String id, title, description;

	/**
	 * 
	 * @param id
	 *            id
	 * @param title
	 *            title
	 * @param description
	 *            description
	 */
	public TemplateDescriptor(String id, String title, String description) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
}
