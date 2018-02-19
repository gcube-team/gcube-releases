package org.gcube.portlets.admin.fhn_manager_portlet.tests;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.server.RemoteServiceImpl;
import org.gcube.portlets.admin.fhn_manager_portlet.server.VMManagerServiceInterface;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNodeStatus;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.ServiceException;
import org.junit.Before;
import org.junit.Test;

public class StopTiming {

VMManagerServiceInterface client;
	
	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		client=new RemoteServiceImpl();
//		client=new RemoteServiceImpl("http://fedcloud.res.eng.it:80/fhn-manager-service/rest");
	}

	@Test
	public void stopNode() throws RemoteException, ServiceException{
		Collection<RemoteNode> nodes=client.getNodes(null, null);
		System.out.println("Found "+nodes.size()+" nodes");
		Map<String,Long> stopDelays=new HashMap<String, Long>();
		Map<String,Long> startDelays=new HashMap<String, Long>();
		
		for(RemoteNode node:nodes){
			Map<String,Long> toUpdateDelaysMap=(node.getStatus().equals(RemoteNodeStatus.active))?startDelays:stopDelays;
			
			try{
				toUpdateDelaysMap.put(node.getId(),changeStatus(node));
			}catch(Exception e){
				System.err.println("Exception qhile operating on "+node);
				e.printStackTrace(System.err);
			}
		}
		
		System.out.println("Completed!!!!");
		System.out.println("Stop avg delay "+getAvg(stopDelays.values()));
		System.out.println("Start avg delay "+getAvg(startDelays.values()));
		
		
	}
	
	
	private long changeStatus(RemoteNode node) throws RemoteException, ServiceException{
		System.out.println("Gonna change status for node "+node);
		RemoteNodeStatus previous=node.getStatus();
		if(previous.equals(RemoteNodeStatus.active)) client.stopNode(node.getId());
		else client.startNode(node.getId());
		RemoteNodeStatus current=null;
		long startTime=System.currentTimeMillis();
		long elapsed=0l;
		do{
			try{
				Thread.sleep(500);
			}catch(InterruptedException e){}
			current=client.getNodeById(node.getId()).getStatus();
			elapsed=System.currentTimeMillis()-startTime;
			System.out.println("Polled status : "+current+", previous was : "+previous+" elapsed time : "+elapsed);
		}while(current.equals(previous));
		return elapsed;
	}
	
	private long getAvg(Collection<Long> toEvaluate){
		long sum=0l;
		for(Long value:toEvaluate) sum+=value;
		return sum/toEvaluate.size();
	}
	
}
