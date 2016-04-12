package org.gcube.datatransfer.agent.impl.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.FutureTask;

import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.worker.Worker;
import org.globus.wsrf.ResourceException;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class AgentResource extends GCUBEWSResource{

		private HashMap<String,FutureWorker>  workerMap = null;
	
		protected static final String supportedTransferRP="SupportedTransfer";
		protected static final String fileSystemRootRP="FileSystemRoot";
		

		protected static String[] RPNames = {	fileSystemRootRP,supportedTransferRP	};
		
		@Override
		protected String[] getTopicNames() {		
			return RPNames;
		}
		
		/**
		 * 
		 *   Initializes Resource Properties. 
		 *   
		 *   @throws  Exception Exception
		 */
		protected void initialise(Object... o) throws ResourceException {
			logger.debug("Initialising the Agent-Resource...");
			/* Initialize the RP's */
			this.initialiseRPs();
			logger.debug("Agent-Resource RPs initialised");
			
				
		}
	
		public  HashMap<String, FutureWorker> getWorkerMap() {
			if (this.workerMap == null){
				 this.workerMap = new  HashMap<String, FutureWorker>();
			}
			return this.workerMap;
		}

		public  void setWorkerMap(HashMap<String, FutureWorker> workerMaps) {
			this.workerMap = workerMaps;
			this.store();
		}
		
		public static class FutureWorker{
			FutureTask futureTask;
			Worker worker;
			
			public FutureWorker(){				
			}
			public FutureWorker(FutureTask futureTask,Worker worker){				
				this.futureTask=futureTask;
				this.worker=worker;
			}
			
			public FutureTask getFutureTask() {
				return futureTask;
			}
			public Worker getWorker() {
				return worker;
			}
			public void setFutureTask(FutureTask futureTask) {
				this.futureTask = futureTask;
			}
			public void setWorker(Worker worker) {
				this.worker = worker;
			}
			
		}

		/** 
		 * Default Setter for the RP Generic
		 * 
		 * @param property the supported transfer property
		 * 
		 */ 
		public  void setSupportedTransfer (String property) {
			this.getResourcePropertySet().get(supportedTransferRP).clear();
			this.getResourcePropertySet().get(supportedTransferRP).add(property);
			this.store();
		}
		
		/** 
		 * Default getter for the RP Generic
		 * 
		 * @return   The Supported Trasnfers
		 * 
		 */ 
		public  ArrayList<String> getSupportedTransfer () {
			ArrayList<String> list = new ArrayList<String>();
			Iterator it =  this.getResourcePropertySet().get(supportedTransferRP).iterator() ;
			while (it.hasNext()){
				list.add((String)it.next());
			}				
			return list;
		}

		
		/** 
		 * Default Setter for the RP Generic
		 * 
		 * @param property the supported transfer property
		 * 
		 */ 
		public  void setFileSystemRoot (String property) {
			this.getResourcePropertySet().get(fileSystemRootRP).clear();
			this.getResourcePropertySet().get(fileSystemRootRP).add(property);
			this.store();
		}
		
		/** 
		 * Default getter for the RP Generic
		 * 
		 * @return   The Supported Trasnfers
		 * 
		 */ 
		public  String getFileSystemRoot () {	
			return (String)  this.getResourcePropertySet().get(supportedTransferRP).get(0) ;
		
		}
		private void initialiseRPs() {
		

			this.getResourcePropertySet().get(supportedTransferRP).clear();
			this.getResourcePropertySet().get(fileSystemRootRP).clear();
			
			try {
				for (String transfer :ServiceContext.getContext().getSupportedTransfers()){
					this.getResourcePropertySet().get(supportedTransferRP).add(transfer);
				}
				
				this.getResourcePropertySet().get(fileSystemRootRP).add(ServiceContext.getContext().getVfsRoot());
			
			
			}catch (Exception e ){
				logger.debug("Error inizializing RPs",e); 
			}
			this.store();
								
			
		}

}
