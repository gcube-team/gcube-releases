package org.gcube.common.core.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.persistence.GCUBEWSFields2FilePersistenceDelegate.WSFieldsSerializable;

/**
 * Base class for {@link GCUBEWSResource} implementing the {@link WSFieldsSerializable} interface. It can be used to serialize/deserialize an arbitrary 
 * list of (eventually dynamic) objects extending the {@link Serializable} interface. Such fields do not need to be RPs.  
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class GCUBEWSFieldsSerializableResource extends GCUBEWSResource implements WSFieldsSerializable {
		
	/**
	 * {@inheritDoc}
	 */
	public List<? extends Serializable> getFieldsToSerialize() {return new ArrayList<Serializable>();}

	/**
	 * {@inheritDoc}
	 */
	public void setFieldsToSerialize(List<? extends Serializable> objs) {}
	
	}
