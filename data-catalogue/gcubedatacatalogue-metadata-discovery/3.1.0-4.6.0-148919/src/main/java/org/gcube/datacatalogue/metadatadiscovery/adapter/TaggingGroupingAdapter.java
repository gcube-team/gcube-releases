/**
 *
 */

package org.gcube.datacatalogue.metadatadiscovery.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.TaggingGroupingValue;



/**
 * The Class TaggingGroupingAdapter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 21, 2017
 */
public class TaggingGroupingAdapter extends XmlAdapter<String, TaggingGroupingValue> {

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	public String marshal(TaggingGroupingValue dt) {

		return dt.value();
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	public TaggingGroupingValue unmarshal(String dt) {

		return TaggingGroupingValue.fromValue(dt);
	}
}
