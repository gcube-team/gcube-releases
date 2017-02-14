/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import org.gcube.informationsystem.impl.utils.discovery.ERDiscovery;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;
import org.junit.Test;

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ContextCreation {

	@Test
	public void aux() throws Exception{
		OrientGraphFactory factory = new OrientGraphFactory("remote:pc-frosini.isti.cnr.it", "admin", "admin").setupPool(1, 10);
		factory.getTx();
	}
	
	@Test
	public void test() throws Exception{
		ERDiscovery.addPackage(Embedded.class.getPackage());
		ERDiscovery.addPackage(Entity.class.getPackage());
		ERDiscovery.addPackage(Relation.class.getPackage());

		ERDiscovery erDiscovery = new ERDiscovery();
		erDiscovery.discoverERTypes();
		
		
		//EntityRegistrationAction erEntityRegistrationAction = new EntityRegistrationAction();
		//erDiscovery.manageDiscoveredERTypes(erEntityRegistrationAction);
	}
	
}
