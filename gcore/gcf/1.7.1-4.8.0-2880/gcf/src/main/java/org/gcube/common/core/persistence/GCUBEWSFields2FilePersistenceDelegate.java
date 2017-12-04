package org.gcube.common.core.persistence;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import org.gcube.common.core.state.GCUBEWSFieldsSerializableResource;

/**
 * Delegate to persist an arbitrary list of {@link java.io.Serializable} objects, plus the standard RPs
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 * @param <RESOURCE> the type of the associated resource
 */
public class GCUBEWSFields2FilePersistenceDelegate<RESOURCE extends GCUBEWSFieldsSerializableResource> extends
		GCUBEWSFilePersistenceDelegate<RESOURCE> {
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override protected void onLoad(RESOURCE resource,ObjectInputStream stream) throws Exception {
				
		super.onLoad(resource, stream);		
				
		Class<List<? extends Serializable>> cs = (Class) stream.readObject();		
		List<Serializable> objs = (List<Serializable>) cs.newInstance();
		//loop on all the other potentially stored objects
		Integer numOfSerializedWSObjs = (Integer) stream.readObject();		
		for (int i = 1; i <= numOfSerializedWSObjs;  i++) {		
			try {
				Serializable obj = (Serializable) stream.readObject();
				if (obj == null)
					break;										
				objs.add(obj);
			} catch (java.io.EOFException eofe) {break;}
 
		} 
		resource.setFieldsToSerialize(objs);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override protected void onStore(RESOURCE resource,ObjectOutputStream stream) throws Exception {	
				
		super.onStore(resource,stream);				
		List<? extends Serializable> objs = resource.getFieldsToSerialize();
		stream.writeObject(objs.getClass());
		stream.writeObject((Integer)objs.size());
		int i=0;
		for (Serializable obj : objs) {
			++i;
			stream.writeObject(obj);	 
		}
	}
		
	/**
	 * Enable serialization/deserialization of an arbitrary list of objects using the {@link GCUBEWSFields2FilePersistenceDelegate}
	 * 
	 * @author Manuele Simi
	 * 
	 *
	 */
	public interface WSFieldsSerializable extends Serializable {

		/**
		 * Gets the list of fields to serialize
		 * 
		 * @return the list of fields to serialize
		 */
		public List<? extends Serializable> getFieldsToSerialize();

		
		/**
		 * Restores the list of deserialized fields
		 * 
		 * @param objs the list of deserialized fields
		 */
		public void setFieldsToSerialize(List<? extends Serializable> objs);
	}
	
}
