/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.facet.CPUFacet;
import org.gcube.informationsystem.model.relation.Host;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.model.resource.Configuration;
import org.junit.Test;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class ContextCreation {

	@Test
	public void test() throws Exception{
		SchemaInitializator.addPackage(Embedded.class.getPackage());
		SchemaInitializator.addPackage(Entity.class.getPackage());
		SchemaInitializator.addPackage(Relation.class.getPackage());
		SchemaInitializator.addPackage(CPUFacet.class.getPackage());
		SchemaInitializator.addPackage(Host.class.getPackage());
		SchemaInitializator.addPackage(Configuration.class.getPackage());
		SchemaInitializator.createTypes();
	}
	
}
