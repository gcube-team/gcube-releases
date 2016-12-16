package org.gcube.data.analysis.tabulardata.metadata;

import java.util.Collection;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Metadata")
public class HashMapMetadataHolder<T extends Metadata> implements MetadataHolder<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7323571157037927718L;

	private HashMap<String, T> metadata = new HashMap<String, T>();

	@SuppressWarnings("unchecked")
	public <C extends T> C getMetadata(Class<C> metadataType) {
		if (metadata.containsKey(metadataType.getCanonicalName())) 
			return (C) metadata.get(metadataType.getCanonicalName());
		else throw new NoSuchMetadataException(metadataType);
	}

	public void removeMetadata(Class<? extends T> metadataType) {
		metadata.remove(metadataType.getCanonicalName());
	}

	public void setMetadata(T metadata) {
		this.metadata.put(metadata.getClass().toString(), metadata);
	}

	public Collection<T> getAllMetadata() {
		return metadata.values();
	}

	public void setAllMetadata(Collection<T> metadata) {
		for (T t : metadata) {
			setMetadata(t);
		}
	}


	@Override
	public boolean contains(Class<? extends T> metadataType) {
		return metadata.containsKey(metadataType.getCanonicalName());
	}
	
	public void removeAllMetadata() {
		this.metadata = new HashMap<String, T>();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HashMapMetadataHolder [metadata=");
		builder.append(metadata);
		builder.append("]");
		return builder.toString();
	}


}
