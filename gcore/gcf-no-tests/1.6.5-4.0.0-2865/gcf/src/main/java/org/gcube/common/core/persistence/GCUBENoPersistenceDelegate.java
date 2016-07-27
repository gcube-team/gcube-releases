package org.gcube.common.core.persistence;

import java.util.Collections;
import java.util.List;

import org.gcube.common.core.state.GCUBEStatefulResource;

/**
 * An implementation of {@link GCUBEPersistenceDelegate} which simulates serialisation and deserialisation
 * of {@link org.gcube.common.core.state.GCUBEStatefulResource GCUBEStatefulResource}s. <p>
 * It is used as the delegate of resources which do not explicitly indicate one in the configuration of their
 * {@link org.gcube.common.core.state.GCUBEResourceHome GCUBEResourceHome}.
 * 
 * 
 *@param <RESOURCEID> the identifier of the resource type.
 *@author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBENoPersistenceDelegate<RESOURCEID> extends GCUBEPersistenceDelegate<RESOURCEID,GCUBEStatefulResource<RESOURCEID>> {

	/**{@inheritDoc}*/
	public void load(GCUBEStatefulResource<RESOURCEID> resource,boolean firstLoad) throws Exception {throw new Exception();}
	/**{@inheritDoc}*/
	public void store(GCUBEStatefulResource<RESOURCEID> resource) {}
	/**{@inheritDoc}*/
	public void remove(GCUBEStatefulResource<RESOURCEID> resource)  {}
	/**{@inheritDoc}*/
	protected void onLoad(GCUBEStatefulResource<RESOURCEID> resource,boolean firstLoad) throws Exception{}		
	/**{@inheritDoc}*/
	protected void onStore(GCUBEStatefulResource<RESOURCEID> resource) throws Exception{}
	/**{@inheritDoc}*/
	protected void onRemove(GCUBEStatefulResource<RESOURCEID> resource) throws Exception{}
	/**{@inheritDoc}*/
	public List<RESOURCEID> getResourceIdentifiers() {return Collections.EMPTY_LIST;}

	 


}
