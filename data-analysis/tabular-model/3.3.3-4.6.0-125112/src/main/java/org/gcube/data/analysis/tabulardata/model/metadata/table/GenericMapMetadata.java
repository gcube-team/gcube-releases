package org.gcube.data.analysis.tabulardata.model.metadata.table;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GenericMapMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericMapMetadata implements TableMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6741126684130360282L;

	private Map<String, String> metadataMap = new HashMap<String, String>();

	public GenericMapMetadata() {
	}

	public GenericMapMetadata(Map<String, String> metadataMap) {
		this.metadataMap = metadataMap;
	}

	public boolean isInheritable() {
		return false;
	}

	public Map<String, String> getMetadataMap() {
		return metadataMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metadataMap == null) ? 0 : metadataMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericMapMetadata other = (GenericMapMetadata) obj;
		if (metadataMap == null) {
			if (other.metadataMap != null)
				return false;
		} else if (!metadataMap.equals(other.metadataMap))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GenericMapMetadata [metadataMap=");
		builder.append(metadataMap);
		builder.append("]");
		return builder.toString();
	}

}
