package org.gcube.vremanagement.vremodeler;

import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.VOID;

import org.gcube.vremanagement.vremodeler.stubs.ModelerFactoryPortType;
import org.gcube.vremanagement.vremodeler.stubs.service.ModelerFactoryServiceAddressingLocator;

public class VREModelerInitializer {

	
	public static void main(String[] args) {
		try{
			ISClient client = GHNContext.getImplementation(ISClient.class);
			GCUBERIQuery riquery= client.getQuery(GCUBERIQuery.class);
			riquery.addAtomicConditions(new AtomicCondition("//ServiceName", "VREModeler"));
			List<GCUBERunningInstance> results=client.execute(riquery, GCUBEScope.getScope(args[0]));
			
			ModelerFactoryServiceAddressingLocator mfal =new ModelerFactoryServiceAddressingLocator();
			EndpointReferenceType epr= results.get(0).getAccessPoint().getEndpoint("gcube/vremanagement/vremodeler/ModelerFactoryService");
			System.out.println(epr);
			ModelerFactoryPortType mfptp= mfal.getModelerFactoryPortTypePort(epr);
			
			mfptp = GCUBERemotePortTypeContext.getProxy(mfptp, GCUBEScope.getScope(args[0]));
			
			mfptp.initDB(new VOID());
		}catch (Exception e){e.printStackTrace();}
	}
}
