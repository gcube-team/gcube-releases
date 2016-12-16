package org.gcube.datatransfer.scheduler.impl.porttype;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.scheduler.impl.check.CheckDBForTransfersThread;
import org.gcube.datatransfer.scheduler.impl.context.SchedulerContext;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerResource;


public class Factory extends GCUBEPortType {    

	GCUBELog logger = new GCUBELog(this);


	@Override
	protected ServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	/*
	 * create
	 * input: String which represents the unique name of the Resource
	 * output: EndpointReferenceType of the Stateful porttype
	 */
	public EndpointReferenceType create(String name) throws GCUBEFault {		

		GCUBEStatefulPortTypeContext ptcxt = SchedulerContext.getContext();
		GCUBEWSHome home = ptcxt.getWSHome();
		try{
			if (home.find(ptcxt.makeKey(name))!=null){
				logger.debug("\nFactory - Name: '"+name+"' already exists. Retrieving ws");
				return home.find(ptcxt.makeKey(name)).getEPR();	  
			}
		}catch (Exception e) {
			logger.error("\nFactory - there is no name: '"+name+"'");
		}

		GCUBEWSResourceKey key = null;
		try{
			logger.debug("Factory - Creating a resource for name: '"+name+"'");
			key = ptcxt.makeKey(name);
		}catch (Exception e) {	  
			logger.error("unable to make a new key because of:\n"+e+"\n");
			//throw new GCUBEUnrecoverableException(e).toFault();
		} 

		GCUBEWSResource ws = null;
		logger.debug("created key:\n"+key.getValue()+"\n");
		try{
			ws = home.create(key,name);
		}catch (Exception e) {	  
			logger.error("unable to create a new resource because of:\n"+e+"\n");
			// throw new GCUBEUnrecoverableException(e).toFault();
		} 

		try{
			
			
			//creating Thread for checking the DB
			CheckDBForTransfersThread checkDBForTransfersThread = new CheckDBForTransfersThread(ws);
			checkDBForTransfersThread.start();		
			ws.store();
			
			//storing the name of checkDBForTransfers thread
			SchedulerResource resource = (SchedulerResource) ws;
			resource.setCheckDBThread(checkDBForTransfersThread.getName());
			resource.store();
			
			return ws.getEPR(); 
		} catch (Exception e) {	  
			logger.error("unable to store and return the resource because of:\n"+e+"\n");
			//throw new GCUBEUnrecoverableException(e).toFault();
		} 

		return null;
	}
}
