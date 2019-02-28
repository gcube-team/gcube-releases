package org.gcube.common.clients.stubs.jaxws;

import javax.xml.namespace.QName;

/**
 * The clauses of a simple DSL to build {@link GCoreService}.
 * 
 * @author Fabio Simeoni
 *
 */
public interface GCoreServiceDSL {

	/**
	 * The clause that sets the name of the target service. 
	 * 
	 * @author Fabio Simeoni
	 *
	 */
	static interface NameClause {
		
		/**
		 * Sets the qualified name of the target service.
		 * 
		 * @param name the qualified name of the target service
		 * @return the next clause
		 */
		CoordinateClause withName(QName name);
		
	}

	/**
	 * The clause that sets the gCube coordinates of the target service. 
	 * 
	 * @author Fabio Simeoni
	 *
	 */
	static interface CoordinateClause {
		
		/**
		 * Sets the gCube coordinates of the target service
		 * @param gcubeClass the gCube class of the target service
		 * @param gcubeName the gCube name of the target service
		 * @return
		 */
		StubClause coordinates(String gcubeClass, String gcubeName);
		
	}
	
	/**
	 * The clause that sets the stub interface of the target service. 
	 * 
	 * @author Fabio Simeoni
	 *
	 */
	static interface StubClause {
		
		/**
		 * Sets the stub interface of the target service.
		 * @param type the interface
		 * @return the {@link GCoreService} that described the target service.
		 */
		<T> GCoreService<T> andInterface(Class<T> type);
	}
}
