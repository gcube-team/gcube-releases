package org.gcube.common.calls.jaxrs;

import javax.xml.namespace.QName;

/**
 * The clauses of a simple DSL to build {@link GCoreService}.
 * 
 * @author Fabio Simeoni
 *
 */
public interface GcubeServiceBuilderDSL {

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
		StubClause withName(QName name);
		
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
		GcubeService useRootPath();

		GcubeService andPath(String path);
	}
}
