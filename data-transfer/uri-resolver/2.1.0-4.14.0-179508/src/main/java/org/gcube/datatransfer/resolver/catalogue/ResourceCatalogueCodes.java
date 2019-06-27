/**
 *
 */
package org.gcube.datatransfer.resolver.catalogue;

import java.util.ArrayList;
import java.util.List;


/**
 * The Enum ResourceCatalogueCodes.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 31, 2017
 *
 * see wiki page: https://wiki.gcube-system.org/gcube/URI_Resolver#CATALOGUE_Resolver
 */
public enum ResourceCatalogueCodes {

	CTLG("ctlg","dataset", "Catalogue Product/Dataset"),
	//CTLGP("ctlg-p","product", "Catalogue Product"),
	CTLGD("ctlg-d","dataset", "Catalogue Dataset"),
	CTLGO("ctlg-o","organization", "Catalogue Organization"),
	CTLGG("ctlg-g","group", "Catalogue Group");

	private String id; //the code id
	private String value; //the code value
	private String description;

	/**
	 * Instantiates a new resource catalogue codes.
	 *
	 * @param id the id
	 * @param value the value
	 * @param description the description
	 */
	private ResourceCatalogueCodes(String id, String value, String description) {
		this.id = id;
		this.value = value;
		this.description = description;
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
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {

		return value;
	}


	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {

		return description;
	}


	/**
	 * Codes.
	 *
	 * @return the list
	 */
	public static List<String> codes(){

		List<String> codes = new ArrayList<String>(ResourceCatalogueCodes.values().length);
		for (ResourceCatalogueCodes value : ResourceCatalogueCodes.values()) {
			codes.add(value.getId());
		}

		return codes;
	}


	/**
	 * Value of code id.
	 *
	 * @param id the id
	 * @return the resource catalogue codes
	 */
	public static ResourceCatalogueCodes valueOfCodeId(String id){
		if(id==null || id.isEmpty())
			return null;

		for (ResourceCatalogueCodes value : ResourceCatalogueCodes.values()) {
			if(value.id.compareTo(id)==0)
				return value;
		}
		return null;
	}


	/**
	 * Value of code value.
	 *
	 * @param codeValue the code value
	 * @return the resource catalogue codes
	 */
	public static ResourceCatalogueCodes valueOfCodeValue(String codeValue){
		if(codeValue==null || codeValue.isEmpty())
			return null;

		for (ResourceCatalogueCodes rcc : ResourceCatalogueCodes.values()) {
			if(rcc.value.compareTo(codeValue)==0)
				return rcc;
		}
		return null;
	}
}
