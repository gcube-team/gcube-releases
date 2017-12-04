package org.gcube.data.tr;

import java.util.Collections;
import java.util.List;

import org.gcube.data.tmf.api.SourceBinder;
import org.gcube.data.tmf.api.exceptions.InvalidRequestException;
import org.gcube.data.tr.requests.AbstractRequest;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.tr.requests.RequestBinder;
import org.w3c.dom.Element;

/**
 * The {@link SourceBinder} of the plugin.
 * 
 * @author Fabio Simeoni
 *
 */
public class Binder implements SourceBinder {

	RequestBinder binder = new RequestBinder();
	Provider provider;
	
	/**
	 * Creates an instance with a {@link ProductionProvider}
	 */
	public Binder() {
		provider = new ProductionProvider();
	}
	
	/**
	 * Creates an instance with a given {@link Provider}.
	 * @param provider provider
	 */
	//supports testing
	public Binder(Provider provider) {
		this.provider=provider;
	}
	
	@Override
	public List<TreeSource> bind(Element DOMRequest) throws InvalidRequestException, Exception {
		
		AbstractRequest request = null;
		try {
			request = binder.bind(DOMRequest, AbstractRequest.class);
		}
		catch(Throwable t) {
			throw new InvalidRequestException("unrecognised request "+DOMRequest.getTagName(),t);
		}
		
		TreeSource source = provider.newSource((BindSource)request);
									
		return Collections.singletonList(source);
	}
}
