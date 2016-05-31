package org.gcube.vremanagement.vremodeler.cl;

import static org.gcube.vremanagement.vremodel.cl.plugin.AbstractPlugin.manager;
import static org.gcube.vremanagement.vremodel.cl.plugin.AbstractPlugin.factory;
import java.util.Calendar;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodel.cl.proxy.Manager;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityNodes;
import org.gcube.vremanagement.vremodel.cl.stubs.types.GHN;
import org.gcube.vremanagement.vremodel.cl.stubs.types.GHNsPerFunctionality;
import org.junit.Before;
import org.junit.Test;

public class ManagerTest {


	String resourceId = "27090040-2c1a-11e3-a7e2-deb3e542b746";
	W3CEndpointReference epr;

	@Before
	public void init() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		epr  = factory().build().getEPRbyId(resourceId);
		System.out.println(epr);
	}

	@Test
	public void  setAndGetDescription() throws Exception{
		try{
			Manager manager = manager().at(epr).build();
			Calendar now = Calendar.getInstance();
			Calendar nextYear = Calendar.getInstance();
			nextYear.add(Calendar.YEAR, 1);
			manager.setDescription("lucio", "test", "lucio.lelii", "lucio.lelii", now, nextYear);

			System.out.println(manager.getDescription());
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	@Test
	public void isUseCloud() throws Exception{
		try{
			Manager manager = manager().at(epr).build();
			System.out.println(manager.isUseCloud());
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void getFunctionalities() throws Exception{
		try{
			Manager manager = manager().at(epr).build();
			for (FunctionalityItem item: manager.getFunctionalities())
				System.out.println(item);
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void getFunctionalityNodes() throws Exception{
		try{
			Manager manager = manager().at(epr).build();
			FunctionalityNodes nodes =  manager.getFunctionalityNodes();
			System.out.println("ghns per functionality ---");
			for(GHNsPerFunctionality functionality: nodes.functionalities()){
				System.out.println(functionality);
			}
			System.out.println("selectable ghns ---");
			for (GHN ghn : nodes.selectableGHNs())
				System.out.println(ghn);
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void setVREtoPendingState() throws Exception{
		try{
			Manager manager = manager().at(epr).build();
			manager.setVREtoPendingState();
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void checkStatus() throws Exception{
		try{
			Manager manager = manager().at(epr).build();
			System.out.println(manager.checkStatus());
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void deploy() throws Exception{
		try{
			Manager manager = manager().at(epr).build();
			manager.deployVRE();
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void renewVRE() throws Exception{
		try{
			Manager manager = manager().at(epr).build();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, 2);
			System.out.println(cal);
			manager.renewVRE(cal);
			Calendar endCal = manager.getDescription().endTime();
			System.out.println(endCal);
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
