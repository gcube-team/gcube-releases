package org.gcube.data.analysis.tabulardata.metadata;

import java.io.Serializable;
import java.util.Collection;

public interface MetadataHolder<T extends Metadata> extends Serializable{

	public <C extends T> C getMetadata(Class<C> metadataType);

	public void removeMetadata(Class<? extends T> metadataType);

	public void setMetadata(T metadata);

	public Collection<T> getAllMetadata();

	public boolean contains(Class<? extends T> metadataType);
	
	public void setAllMetadata(Collection<T> metadata);

	public void removeAllMetadata();

}
