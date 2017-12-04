package org.gcube.data.tr;

import java.util.Calendar;
import java.util.List;

import javax.xml.namespace.QName;

import org.gcube.data.tmf.api.SourceLifecycle;
import org.gcube.data.tr.neo.NeoStore;
import org.gcube.data.tr.requests.BindSource;

/**
 * A {@link Provider} that returns dependencies used in production.
 * 
 * @author Fabio Simeoni
 *
 */
public class ProductionProvider implements Provider {

	@Override
	public TreeSource newSource(BindSource request) {
		
		//obtains a store for that identifier 
		Store store = newStore(request.name());
		
		//extract types
		List<QName> types = request.types();
		
		//create source
		TreeSource source = new TreeSource(request.name(),types,store);
		
		//configure as per request
		source.setName(request.name());
		source.setDescription(request.description());
		
		
		source.setLifecycle(newLifecycle(source));
		source.setMode(request.mode());
		
		source.setCreationTime(Calendar.getInstance());
		source.setLastUpdate(Calendar.getInstance());
		
		return source;
	}
	
	SourceLifecycle newLifecycle(TreeSource source) {
		return new Lifecycle(source);
	}
	
	@Override
	public Store newStore(String sourceId) {
		return new NeoStore(sourceId);
	}

}
