/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import javax.xml.bind.annotation.XmlEnum;


/**
 * The Enum TaggingGroupingValue.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 21, 2017
 */
@XmlEnum
public enum TaggingGroupingValue {

	onFieldName("onFieldName"),
	onValue("onValue"),
	onFieldName_onValue("onFieldName_onValue"),
	onValue_onFieldName("onValue_onFieldName");

	 private String value;

	 /**
 	 * Instantiates a new tagging grouping value.
 	 *
 	 * @param value the value
 	 */
 	TaggingGroupingValue(String value) {
	        this.value = value;
	    }


    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return this.value;
    }



    /**
     * From value.
     *
     * @param value the value
     * @return the TaggingGroupingValue matching the input value or default value {@link TaggingGroupingValue#onValue}
     */
    public static TaggingGroupingValue fromValue(String value) {

    	try{
    		for (TaggingGroupingValue tgv : TaggingGroupingValue.values()) {
    			if(tgv.value.equals(value))
    				return tgv;
			}
    	}catch(Exception e){
    		return TaggingGroupingValue.onValue;
    	}

    	return TaggingGroupingValue.onValue;
    }
}
