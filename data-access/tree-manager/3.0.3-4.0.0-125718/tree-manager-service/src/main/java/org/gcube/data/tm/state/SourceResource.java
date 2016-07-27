/**
 * 
 */
package org.gcube.data.tm.state;

import org.gcube.common.core.state.GCUBELocalResource;
import org.gcube.data.tm.context.TBinderContext;
import org.gcube.data.tmf.api.Source;
import org.gcube.data.tmf.api.SourceConsumer;
import org.gcube.data.tmf.api.SourceEvent;
import org.globus.wsrf.ResourceException;

/**
 * A {@link GCUBELocalResource} that models data sources.
 * @author Fabio Simeoni
 *
 */
public class SourceResource extends GCUBELocalResource implements SourceConsumer {

	private Source source;
	private String pluginName;
	
	/**{@inheritDoc}*/
	@Override 
	protected void initialise(String id,Object... params) throws Exception {
		
		super.initialise(id,params);
		
		//TODO remove after next gCore release (it works around a bug of older version)
		if (params[0].getClass().isArray())
			params = (Object[]) params[0];
		
		Source source = (Source) params[0];
		String name = (String) params[1];
		
		setSource(source);
		setPluginName(name);
		
	}
	
	/**
	 * Returns the source.
	 * @return the source
	 */
	public Source source() {
		return source;
	}
	
	/**
	 * Sets the source.
	 * @param source the source
	 */
	public void setSource(Source source) {
		logger.trace(getID()+" resource is subscribing for source removal");
		source.notifier().subscribe(this,SourceEvent.REMOVE);
		this.source=source;
		
	}


	/**
	 * Sets the name of the associated plugin.
	 * @param n the name.
	 */
	public void setPluginName(String n) {
		pluginName = n;;
	}

	/**
	 * Returns the name of the associated plugin.
	 * @return the name.
	 */
	public String getPluginName() throws ResourceException {
		return pluginName;
	}

	public void onEvent(SourceEvent ... event) {
		
		try {
			TBinderResource binder = TBinderContext.getContext().binder();
			binder.deleteAccessors(getID());
		}
		catch(Exception e) {
			logger.error("could not process collection removal",e);
		}
		
	};
	
	/**{@inheritDoc}*/
	@Override
	protected void onRemove() throws ResourceException {
		super.onRemove();
		source().lifecycle().terminate();
	}

}
