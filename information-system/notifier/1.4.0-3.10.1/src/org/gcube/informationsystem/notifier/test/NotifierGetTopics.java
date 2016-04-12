package org.gcube.informationsystem.notifier.test;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.informationsystem.notifier.stubs.ListTopics;
import org.gcube.informationsystem.notifier.stubs.ListTopicsResponse;
import org.gcube.informationsystem.notifier.stubs.NotifierPortType;
import org.gcube.informationsystem.notifier.stubs.service.NotifierServiceAddressingLocator;

/**
 * 
 * @author andrea
 *
 */
public class NotifierGetTopics {
	static NotifierServiceAddressingLocator brokerLocator = new NotifierServiceAddressingLocator();
	
	/**
	 * test
	 * @param args args
	 */
	public static void  main ( String [] args) {

		EndpointReferenceType brokerEPR = new EndpointReferenceType();
		
		NotifierPortType port = null;
		try {
			brokerEPR.setAddress(new AttributedURI(args[0]));
			port = brokerLocator.getNotifierPortTypePort(brokerEPR);
			
			port= GCUBERemotePortTypeContext.getProxy(port,GCUBEScope.getScope(args[1]));
			
		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedScopeExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ListTopicsResponse topics = null;
		try {
			topics = port.listTopics(new ListTopics());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (topics == null) System.out.println("ERROR");
		if (topics.getTopicList() == null)  System.out.println("ERROR");
		for (String topic :topics.getTopicList()) {
			System.out.println(topic);
		}
	}
		
}
