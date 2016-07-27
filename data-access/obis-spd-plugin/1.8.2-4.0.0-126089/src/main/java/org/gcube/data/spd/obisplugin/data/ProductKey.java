/**
 * 
 */
package org.gcube.data.spd.obisplugin.data;

import com.thoughtworks.xstream.XStream;


/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ProductKey {
	
	protected static XStream stream;
	
	protected int taxonId;
	protected int dataSetId;
	protected SearchFilters filters;
	
	/**
	 * @param taxonId
	 * @param dataSetId
	 * @param filters
	 */
	public ProductKey(int taxonId, int dataSetId, SearchFilters filters) {
		this.taxonId = taxonId;
		this.dataSetId = dataSetId;
		this.filters = filters;
	}
		
	protected ProductKey() {}
	
	/**
	 * @return the taxonId
	 */
	public int getTaxonId() {
		return taxonId;
	}

	/**
	 * @return the dataSetId
	 */
	public int getDataSetId() {
		return dataSetId;
	}

	/**
	 * @return the filters
	 */
	public SearchFilters getFilters() {
		return filters;
	}
	
	protected static XStream getStream()
	{
		if (stream == null) stream = new XStream();
		return stream;
	}
	
	public String serialize()
	{
		XStream stream = getStream();
		return stream.toXML(this);
	}
	
	public static ProductKey deserialize(String key)
	{
		XStream stream = getStream();
		return (ProductKey) stream.fromXML(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductKey [taxonId=");
		builder.append(taxonId);
		builder.append(", dataSetId=");
		builder.append(dataSetId);
		builder.append(", filters=");
		builder.append(filters);
		builder.append("]");
		return builder.toString();
	}
}
