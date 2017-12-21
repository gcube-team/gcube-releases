package org.gcube.data.analysis.rconnector.client;

import javax.xml.namespace.QName;

import org.gcube.data.analysis.rconnector.client.GcubeServiceBuilderDSL.NameClause;
import org.gcube.data.analysis.rconnector.client.GcubeServiceBuilderDSL.StubClause;

import static org.gcube.data.analysis.rconnector.client.Utils.*;


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
	public GcubeService andPath(String path) {
		return new GcubeService(name, path);
	}
	
	@Override
	public GcubeService useRootPath() {
		return new GcubeService(name, "/");
	}
	
}
