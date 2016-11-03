/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.facet.AccessPointInterfaceFacet;
import org.gcube.informationsystem.model.entity.resource.Actor;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.model.relation.consistsof.HasContact;
import org.gcube.informationsystem.model.relation.isrelatedto.BelongsTo;
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
		SchemaInitializator.addPackage(AccessPointInterfaceFacet.class.getPackage());
		SchemaInitializator.addPackage(Actor.class.getPackage());
		SchemaInitializator.addPackage(HasContact.class.getPackage());
		SchemaInitializator.addPackage(BelongsTo.class.getPackage());
		SchemaInitializator.createTypes();
	}
	
}
