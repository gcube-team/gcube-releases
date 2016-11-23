/**
 *
 */

package org.gcube.datacatalogue.metadatadiscovery.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.DataType;


/**
 * The Class DataTypeAdapter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 12, 2016
 */
public class DataTypeAdapter extends XmlAdapter<String, DataType> {

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	public String marshal(DataType dt) {

		return dt.name();
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	public DataType unmarshal(String dt) {

		return DataType.fromValue(dt);
	}
}
