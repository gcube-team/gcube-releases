package org.gcube.common.calls.jaxrs;

import static org.gcube.common.calls.jaxrs.Utils.notNull;

import javax.xml.namespace.QName;

import org.gcube.common.calls.jaxrs.GcubeServiceBuilderDSL.NameClause;
import org.gcube.common.calls.jaxrs.GcubeServiceBuilderDSL.StubClause;


/**
 * Builds {@link GCoreService} instances.
 * 
 * @author Fabio Simeoni
 *
 */
public class GcubeServiceBuilder implements NameClause, StubClause {

	private QName name;
	
	public StubClause withName(QName name) {
		
		notNull("service name", name);
		
		this.name=name;
		
		return this;		
	}

	public GcubeService andPath(String path) {
		return new GcubeService(name, path);
	}
	
	public GcubeService useRootPath() {
		return new GcubeService(name, "/");
	}
	
}
