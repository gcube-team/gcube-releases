package or.gcube.data.analysis.tabulardata.metadata.table;

import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.metadata.ArrayListMetadataHolder;
import org.gcube.data.analysis.tabulardata.metadata.MetadataHolder;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Container implements MetadataHolder<ContainerMetadata> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7711641752460224048L;
	
	ArrayListMetadataHolder<ContainerMetadata> delegate = new ArrayListMetadataHolder<ContainerMetadata>();
	
	@XmlElementRefs({
		@XmlElementRef(type=MetaString.class),
		@XmlElementRef(type=MetaInt.class)
	})
	private List<ContainerMetadata> getMetadata(){
		return delegate.metadata;
	}

	public <C extends ContainerMetadata> C getMetadata(Class<C> metadataType) {
		C meta = delegate.getMetadata(metadataType);
		if (meta== null) throw new NoSuchMetadataException(metadataType);
		return meta;
	}

	public void removeMetadata(Class<? extends ContainerMetadata> metadataType) {
		delegate.removeMetadata(metadataType);
	}

	public void setMetadata(ContainerMetadata metadata) {
		delegate.setMetadata(metadata);
	}

	@Override
	public boolean contains(Class<? extends ContainerMetadata> metadataType) {
		return delegate.contains(metadataType);
	}
	
	public Collection<ContainerMetadata> getAllMetadata() {
		return delegate.getAllMetadata();
	}

	public void setAllMetadata(Collection<ContainerMetadata> metadata) {
		delegate.setAllMetadata(metadata);
	}

	public void removeAllMetadata() {
		delegate.removeAllMetadata();
	}
	
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Container [delegate=");
		builder.append(delegate);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
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
		Container other = (Container) obj;
		if (delegate == null) {
			if (other.delegate != null)
				return false;
		} else if (!delegate.equals(other.delegate))
			return false;
		return true;
	}


}
