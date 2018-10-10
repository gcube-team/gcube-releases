/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.bean.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * The Enum DataType.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 12, 2016
 */
@XmlType(name = "dataType")
@XmlEnum
public enum DataType {

	String,
	Time,
	Time_Interval,
	Times_ListOf,
	Text,
	Boolean,
	Number,
	GeoJSON;

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return name();
    }

    /**
     * From value.
     *
     * @param v the v
     * @return the data type
     */
    public static DataType fromValue(String v) {
    	DataType vv;
    	try{
    		vv = valueOf(v);
    	}catch(Exception e){
    		return DataType.String;
    	}

    	return vv;
    }
}
