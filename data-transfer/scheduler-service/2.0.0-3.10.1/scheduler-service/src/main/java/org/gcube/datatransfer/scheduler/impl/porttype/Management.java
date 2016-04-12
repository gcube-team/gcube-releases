/**
 * 
 */
package org.gcube.datatransfer.scheduler.impl.porttype;



import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;

import org.gcube.datatransfer.common.scheduler.Types.FrequencyType;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.AgentStatistics;
import org.gcube.datatransfer.scheduler.db.model.ManuallyScheduled;
import org.gcube.datatransfer.scheduler.db.model.PeriodicallyScheduled;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.db.model.TransferObject;
import org.gcube.datatransfer.scheduler.db.model.TypeOfSchedule;
import org.gcube.datatransfer.scheduler.impl.context.ManagementContext;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerResource;
import org.gcube.datatransfer.scheduler.is.ISManager;
import org.gcube.datatransfer.scheduler.library.outcome.CallingManagementResult;
import org.gcube.datatransfer.scheduler.library.outcome.CallingSchedulerResult;
import org.gcube.datatransfer.scheduler.library.outcome.TransferInfo;
import org.gcube.datatransfer.scheduler.library.outcome.TransferObjectInfo;
import org.gcube.datatransfer.scheduler.stubs.datatransferscheduler.SampleFault;
import org.gcube.common.core.contexts.GCUBEPortTypeContext;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.types.VOID;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.ResourceException;





public class Management extends GCUBEPortType {

	GCUBELog logger = new GCUBELog(this);

	public DataTransferDBManager dbManager;


	public String about(String name) throws GCUBEFault, SampleFault {

		StringBuilder output = new StringBuilder();		
		GHNContext nctx = GHNContext.getContext();
		ServiceContext sctx = ServiceContext.getContext();
		GCUBEPortTypeContext pctx = ManagementContext.getContext();
		try {
			output.append("Hello "+name).append(", you have invoked porttype ").
			append(pctx.getName()+" of service "+sctx.getName()).append(", which you found ").
			append (" on the GHN "+nctx.getGHNID()).
			append(" at "+pctx.getEPR()+" in the gCube infrastructure "+nctx.getGHN().getInfrastructure());
		}		
		catch(GCUBEException e) {
			logger.error("Problem in about():",e);
			throw e.toFault();}
		catch(Exception e) {
			logger.error("Problem in about()",e);
			throw sctx.getDefaultException("Problem in about()", e).toFault();}

		return output.toString();
	}

	public String getAddr(String tmp) throws GCUBEFault, SampleFault {

		ServiceContext sctx = ServiceContext.getContext();
		GCUBEPortTypeContext pctx = ManagementContext.getContext();
		try{
			return pctx.getEPR().toString().replaceFirst("Address: ", "").replaceAll("\n","");
		}
		catch(Exception e) {
			logger.error("Problem in about()",e);
			throw sctx.getDefaultException("Problem in about()", e).toFault();
		}
	}

	/*
	 * getAllTransfersInfo
	 * input: String with the name of the submitter that we want to print the tansferInfo about (name of resource)
	 * ...... if the name is "ALL" there is no filter in the results 
	 * return: String with CallingManagementResult obj (xml)
	 * if exception or error occurred, CallingManagementResult contains the specific error message
	 */
	public String getAllTransfersInfo(String name) throws GCUBEFault, SampleFault {

		this.dbManager=ServiceContext.getContext().getDbManager();

		List<String> errors = new ArrayList<String>();
		List<TransferInfo> transfersInfo = new ArrayList<TransferInfo>();
		List<TransferObjectInfo> transfersObjsInfo = new ArrayList<TransferObjectInfo>();

		CallingManagementResult callingManagementResult = new CallingManagementResult();

		try{//Retrieve all the transfers by method
			Extent<?> transferExtent = ServiceContext.getContext().getDbManager().getPersistenceManager().getExtent(Transfer.class, true);
			Iterator<?> iter = transferExtent.iterator();

			while (iter.hasNext()){
				Object obj=iter.next();

				// if we dont want all the transfer we filter the results for the specific submitter
				if(name.compareTo("ALL")!=0){
					if(((Transfer) obj).getSubmitter().compareTo(name)!=0)continue;
				}

				TransferInfo temp = new TransferInfo();

				temp.setTransferId(((Transfer) obj).getTransferId());
				temp.setTransferIdOfAgent(((Transfer) obj).getTransferIdOfAgent());
				
				String[] errorsAray=((Transfer) obj).getTransferError();
				List<String> listError=new ArrayList<String>();
				if(errorsAray!=null){
					for(String tmp:errorsAray)listError.add(tmp);
				}
				
				temp.setTransferError(listError);
				temp.setStatus(((Transfer) obj).getStatus());
				temp.setObjectFailedIDs(((Transfer) obj).getObjectFailedIDs());
				temp.setObjectTrasferredIDs(((Transfer) obj).getObjectTrasferredIDs());
				temp.setSubmitter(((Transfer) obj).getSubmitter());
				temp.setSubmittedDate(((Transfer) obj).getSubmittedDate());
				temp.setNumOdUpdates(((Transfer) obj).getNum_updates());
				
				// giving also information about total size and bytes that have been transferred ... 
				temp.setTotal_size(((Transfer) obj).getTotal_size());
				temp.setBytes_have_been_transferred(((Transfer) obj).getBytes_have_been_transferred());

				String idOfScheduler = ((Transfer) obj).getTypeOfScheduleId();

				TypeOfSchedule typeOfSchedule = this.dbManager.getPersistenceManager().getObjectById(TypeOfSchedule.class,idOfScheduler );
				if(typeOfSchedule.isDirectedScheduled()){
					temp.getTypeOfSchedule().setDirectedScheduled(typeOfSchedule.isDirectedScheduled());
				}
				else if (typeOfSchedule.getManuallyScheduledId()!=null){
					ManuallyScheduled manuallyScheduled = new ManuallyScheduled();
					manuallyScheduled = this.dbManager.getPersistenceManager().getObjectById(ManuallyScheduled.class,typeOfSchedule.getManuallyScheduledId() );
					temp.getTypeOfSchedule().setManuallyScheduled(new org.gcube.datatransfer.scheduler.library.obj.ManuallyScheduled());
					//temp.getTypeOfSchedule().getManuallyScheduled().setCalendar(manuallyScheduled.getCalendar());
					temp.getTypeOfSchedule().getManuallyScheduled().setInstanceString(manuallyScheduled.getCalendarString());
				}
				else if (typeOfSchedule.getPeriodicallyScheduledId()!=null){
					PeriodicallyScheduled periodicallyScheduled = new PeriodicallyScheduled();
					periodicallyScheduled = this.dbManager.getPersistenceManager().getObjectById(PeriodicallyScheduled.class,typeOfSchedule.getPeriodicallyScheduledId() );
					temp.getTypeOfSchedule().setPeriodicallyScheduled(new org.gcube.datatransfer.scheduler.library.obj.PeriodicallyScheduled());

					if(periodicallyScheduled.getFrequency()==org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perMinute){
						temp.getTypeOfSchedule().getPeriodicallyScheduled().setFrequency(FrequencyType.perMinute);
					}
					if(periodicallyScheduled.getFrequency()==org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perHour){
						temp.getTypeOfSchedule().getPeriodicallyScheduled().setFrequency(FrequencyType.perHour);
					}
					if(periodicallyScheduled.getFrequency()==org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perDay){
						temp.getTypeOfSchedule().getPeriodicallyScheduled().setFrequency(FrequencyType.perDay);
					}
					if(periodicallyScheduled.getFrequency()==org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perWeek){
						temp.getTypeOfSchedule().getPeriodicallyScheduled().setFrequency(FrequencyType.perWeek);
					}
					if(periodicallyScheduled.getFrequency()==org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perMonth){
						temp.getTypeOfSchedule().getPeriodicallyScheduled().setFrequency(FrequencyType.perMonth);
					}
					if(periodicallyScheduled.getFrequency()==org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perYear){
						temp.getTypeOfSchedule().getPeriodicallyScheduled().setFrequency(FrequencyType.perYear);
					}
					//temp.getTypeOfSchedule().getPeriodicallyScheduled().setStartInstance(periodicallyScheduled.getStartInstance());
					temp.getTypeOfSchedule().getPeriodicallyScheduled().setStartInstanceString(periodicallyScheduled.getStartInstanceString());
					
				}			 
				transfersInfo.add(temp);
			}
			callingManagementResult.setAllTheTransfersInDB(transfersInfo);
		}catch(Exception e1){
			errors.add("Management Service(getAllTransfersInfo) - Exception in retrieving all the transfers\n"+e1.getMessage());
			callingManagementResult.setErrors(errors); 
			String msgStr = callingManagementResult.toXML();
			e1.printStackTrace();
			return msgStr;
		}


		try{//Printing - Retrieve all the transfer objects by method
			Extent<?> transferobjExtent = ServiceContext.getContext().getDbManager().getPersistenceManager().getExtent(TransferObject.class, true);
			Iterator<?> iter = transferobjExtent.iterator();

			while (iter.hasNext()){
				Object obj=iter.next();
				TransferObjectInfo temp = new TransferObjectInfo();
				temp.setObjectId(((TransferObject) obj).getObjectId());
				if(((TransferObject) obj).getSize()!=null)temp.setSize(((TransferObject) obj).getSize());
				else temp.setSize((long)0);

				temp.setTransferid(((TransferObject) obj).getTransferid());
				if(((TransferObject) obj).getSrcURI()!=null){
					temp.setURI(new URI(((TransferObject) obj).getSrcURI()));
				}
				else temp.setURI(null);
				transfersObjsInfo.add(temp);
			}
			callingManagementResult.setAllTheTransferObjectsInDB(transfersObjsInfo);
		}catch(Exception e1){
			errors.add("Management Service(getAllTransfersInfo) - Exception in retrieving all the transfer objects\n"+e1.getMessage());
			callingManagementResult.setErrors(errors);
			String msgStr = callingManagementResult.toXML();
			e1.printStackTrace();
			return msgStr;
		}

		callingManagementResult.setGetAllTransfersInfoResult("DONE");
		String msgStr = callingManagementResult.toXML();
		return msgStr;
	}

	/*
	 * get objects from IS either Agents or Data-sources or Storages 
	 */
	public String getObjectsFromIS(String type){
		if(type.compareTo("Agent")==0){
			ISManager isManagerForAgents;
			isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();
			List<String> agents = isManagerForAgents.getObjsFromIS();
			String agentsString = "";
			for(String tmp: agents)	agentsString=agentsString.concat(tmp+"\n");
					return agentsString;
		}
		else if(type.compareTo("DataSource")==0){
			ISManager isManagerForSources;
			isManagerForSources=ServiceContext.getContext().getIsManagerForSources();
			List<String> dataSources = isManagerForSources.getObjsFromIS();
			String sourcesString = "";
			for(String tmp: dataSources)	sourcesString=sourcesString.concat(tmp+"\n");
					return sourcesString;
		}
		else if(type.compareTo("DataStorage")==0){
			ISManager isManagerForStorages;
			isManagerForStorages=ServiceContext.getContext().getIsManagerForStorages();
			List<String> storages = isManagerForStorages.getObjsFromIS();
			String storagesString = "";
			for(String tmp: storages)	storagesString=storagesString.concat(tmp+"\n");
					return storagesString;
		}
		return null;
	}
	/*
	 * existAgentInIS
	 * input: String with the host name of agent
	 * output: String with the id of agent in IS if exists or null if not
	 */
	public String existAgentInIS(String agent){
		ISManager isManagerForAgents;
		isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();
		String idOfAgent= isManagerForAgents.checkIfObjExistsInIS_ByHostname(agent);
		return idOfAgent;
	}
	
	/*
	 * existAgentInDB
	 * input: String with the host name of agent
	 * output: String with the id of agent in DB if exists or null if not
	 */
	public String existAgentInDB(String agent){
		ISManager isManagerForAgents;
		isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();
		String idOfAgent= isManagerForAgents.checkIfObjExistsInIS_ByHostname(agent);
		if(idOfAgent!=null){
			return isManagerForAgents.checkIfObjExistsInDB_ById(idOfAgent);
		}
		else return null;
	}

	public String getAgentStatistics(String nothing){
		Extent<?> statsExtent = ServiceContext.getContext().getDbManager().getPersistenceManager().getExtent(AgentStatistics.class, true);
		Iterator<?> iter = statsExtent.iterator();

		String all="";
		//structure: agentIdOfIS--ongoing--failed--succeeded--canceled--total\n
		while (iter.hasNext()){
			AgentStatistics obj=(AgentStatistics)iter.next();
			all=all.concat(obj.getAgentIdOfIS()+"--"+
							obj.getOngoingTransfers()+"--"+
							obj.getFailedTransfers()+"--"+
							obj.getSucceededTransfers()+"--"+
							obj.getCanceledTransfers()+"--"+
							obj.getTotalFinishedTransfers()+"\n"
							);
		}
		return all;
	}
	
	/**{@inheritDoc}*/
	@Override
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
}
