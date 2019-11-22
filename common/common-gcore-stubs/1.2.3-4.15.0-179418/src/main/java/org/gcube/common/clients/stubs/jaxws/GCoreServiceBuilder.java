package org.gcube.common.clients.stubs.jaxws;

import static org.gcube.common.clients.stubs.jaxws.JAXWSUtils.*;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreServiceDSL.CoordinateClause;
import org.gcube.common.clients.stubs.jaxws.GCoreServiceDSL.NameClause;
import org.gcube.common.clients.stubs.jaxws.GCoreServiceDSL.StubClause;

/**
 * Builds {@link GCoreService} instances.
 * 
 * @author Fabio Simeoni
 *
 */
public class GCoreServiceBuilder implements NameClause, CoordinateClause, StubClause {

	private QName name;
	private String gcubeclass;
	private String gcubename;
	
	/**
	 * Starts the bulding process for a {@link GCoreService}.
	 * @return the service
	 */
	public static NameClause service() {
		return new GCoreServiceBuilder();
	}
	
	@Override
	public CoordinateClause withName(QName name) {
		
		notNull("service name", name);
		
		this.name=name;
		
		return this;		
	}
	
	@Override
	public StubClause coordinates(String gcubeClass, String gcubeName) {
		
		notNull("service class", gcubeClass);
		notNull("service name", gcubeName);
		
		this.gcubeclass=gcubeClass;
		this.gcubename=gcubeName;
		
		return this;
	}
	
	@Override
	public <T> GCoreService<T> andInterface(Class<T> type) {
		
		notNull("service interface", type);
		
		return new GCoreService<T>(gcubeclass, gcubename, name, type);
	}
	
}
