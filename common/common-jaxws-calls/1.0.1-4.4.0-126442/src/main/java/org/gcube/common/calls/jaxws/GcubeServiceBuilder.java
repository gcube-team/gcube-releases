package org.gcube.common.calls.jaxws;

import static org.gcube.common.calls.jaxws.JAXWSUtils.*;

import javax.xml.namespace.QName;

import org.gcube.common.calls.jaxws.GcubeServiceBuilderDSL.NameClause;
import org.gcube.common.calls.jaxws.GcubeServiceBuilderDSL.StubClause;

/**
 * Builds {@link GCoreService} instances.
 * 
 * @author Fabio Simeoni
 *
 */
public class GcubeServiceBuilder implements NameClause, StubClause {

	private QName name;
	
	@Override
	public StubClause withName(QName name) {
		
		notNull("service name", name);
		
		this.name=name;
		
		return this;		
	}
		
	
	@Override
	public <T> GcubeService<T> andInterface(Class<T> type) {
		
		notNull("service interface", type);
		
		return new GcubeService<T>(name, type);
	}
	
}
