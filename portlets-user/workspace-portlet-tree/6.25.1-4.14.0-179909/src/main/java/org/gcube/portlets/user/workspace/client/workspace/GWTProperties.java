/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public class GWTProperties implements IsSerializable {

	protected String id;

	protected List<Data> properties;

	public GWTProperties() {
	}

	/**
	 * 
	 * @param id
	 *            Id
	 * @param mapProperties
	 *            map properties
	 */
	public GWTProperties(String id, Map<String, String> mapProperties) {
		this.id = id;

		this.properties = new LinkedList<Data>();

		// workaround for GWT issue 2862
		for (Entry<String, String> entry : mapProperties.entrySet()) {
			properties.add(new Data(entry.getKey(), entry.getValue()));
		}
	}

	public String getId() {

		return id;
	}

	public String getMetaData(String metaDataId) {

		for (Data data : properties) {
			if (data.getKey().equals(metaDataId))
				return data.value;
		}
		return null;
	}

	
	protected class Data {

		protected String key;
		protected String value;

		protected Data(String key, String value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * @return the key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

	}

}
