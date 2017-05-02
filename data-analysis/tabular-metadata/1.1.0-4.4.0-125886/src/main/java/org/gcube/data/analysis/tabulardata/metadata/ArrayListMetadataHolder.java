package org.gcube.data.analysis.tabulardata.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArrayListMetadataHolder<T extends Metadata> implements MetadataHolder<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8914487262378634439L;
	
	public ArrayList<T> metadata = new ArrayList<T>();

	@SuppressWarnings("unchecked")
	@Override
	public <C extends T> C getMetadata(Class<C> metadataType) {
		for (T m : metadata) {
			if (m.getClass().equals(metadataType))
				return (C) m;
		}
		throw new NoSuchMetadataException(metadataType);
	}

	@Override
	public boolean contains(Class<? extends T> metadataType) {
		for (T m : metadata) {
			if (m.getClass().equals(metadataType))
				return true;
		}
		return false;
	}
	
	@Override
	public void removeMetadata(Class<? extends T> metadataType) {
		List<T> toRemove = new ArrayList<T>();
		for (T m : metadata) {
			if (m.getClass().equals(metadataType))
				toRemove.add(m);
		}
		for (T t : toRemove) {
			this.metadata.remove(t);
		}
	}

	@Override
	public void setMetadata(T metadata) {
		List<T> toRemove = new ArrayList<T>();
		for (T m : this.metadata) {
			if (m.getClass().equals(metadata.getClass()))
				toRemove.add(m);
		}
		for (T t : toRemove) {
			this.metadata.remove(t);
		}
		this.metadata.add(metadata);
	}

	@Override
	public Collection<T> getAllMetadata() {
		return new ArrayList<T>(metadata);
	}

	@Override
	public void setAllMetadata(Collection<T> metadata) {
		for (T t : metadata) {
			setMetadata(t);
		}
	}

	@Override
	public void removeAllMetadata() {
		metadata = new ArrayList<T>();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArrayListMetadataHolder [metadata=");
		builder.append(metadata);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArrayListMetadataHolder other = (ArrayListMetadataHolder) obj;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		return true;
	}

	

}
