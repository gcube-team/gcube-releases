package org.gcube.data.spd.executor;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.VOID;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.data.spd.context.ServiceContext;
import org.gcube.data.spd.executor.jobs.JobStatus;
import org.gcube.data.spd.executor.jobs.JobType;
import org.gcube.data.spd.executor.jobs.SpeciesJob;
import org.gcube.data.spd.executor.jobs.URLJob;
import org.gcube.data.spd.executor.jobs.csv.CSVCreator;
import org.gcube.data.spd.executor.jobs.csv.CSVCreatorForOMJob;
import org.gcube.data.spd.executor.jobs.darwincore.DarwinCoreJob;
import org.gcube.data.spd.executor.jobs.dwca.DWCAJobByChildren;
import org.gcube.data.spd.executor.jobs.dwca.DWCAJobByIds;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.stubs.ExecutorPortType;
import org.gcube.data.spd.stubs.IdNotValidFault;
import org.gcube.data.spd.stubs.InputNotValidFault;
import org.gcube.data.spd.stubs.JobNotValidFault;
import org.gcube.data.spd.stubs.NodeStatus;
import org.gcube.data.spd.stubs.StatusResponse;
import org.gcube.data.spd.stubs.SubmitJobRequest;
import org.gcube.data.spd.stubs.service.ExecutorServiceAddressingLocator;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorPT extends GCUBEPortType implements ExecutorPortType{

	private static Logger logger = LoggerFactory.getLogger(ExecutorPT.class);

	public static HashMap<String, SpeciesJob> jobMap; 

	private static final String jobMapFileName = "jobs.ser";


	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	@Override
	public String getResultLink(String jobKey) throws RemoteException, IdNotValidFault,
		GCUBEFault {

		String node;
		String jobId;

		try{
			node = extractNode(jobKey);
			jobId = extractId(jobKey);
		}catch (IdNotValidException e) {
			logger.error("id not valid "+jobKey,e);
			throw new IdNotValidFault();
		}

		if (node.equals(ServiceContext.getContext().getInstance().getID())){
			if (!jobMap.containsKey(jobId)) throw new IdNotValidFault();
			return ((URLJob)jobMap.get(jobId)).getResultURL();
		}else{
			ExecutorPortType executorPt = remoteJobCall(node);
			return executorPt.getResultLink(jobKey);
		}
	}

	@Override
	public String getErrorLink(String jobKey) throws RemoteException, IdNotValidFault,
		GCUBEFault {

		String node;
		String jobId;

		try{
			node = extractNode(jobKey);
			jobId = extractId(jobKey);
		}catch (IdNotValidException e) {
			logger.error("id not valid "+jobKey,e);
			throw new IdNotValidFault();
		}

		if (node.equals(ServiceContext.getContext().getInstance().getID())){
			if (!jobMap.containsKey(jobId)) throw new IdNotValidFault();
			return ((URLJob)jobMap.get(jobId)).getErrorURL();
		}else{
			ExecutorPortType executorPt = remoteJobCall(node);
			return executorPt.getErrorLink(jobKey);
		}
	}
	
	@Override
	public StatusResponse getStatus(String jobKey) throws RemoteException, IdNotValidFault,
		GCUBEFault {

		String node;
		String jobId;

		try{
			node = extractNode(jobKey);
			jobId = extractId(jobKey);
		}catch (IdNotValidException e) {
			logger.error("id not valid "+jobKey,e);
			throw new IdNotValidFault();
		}		

		if (node.equals(ServiceContext.getContext().getInstance().getID())){

			if (!jobMap.containsKey(jobId)){
				logger.trace("id not found, throwing IDNotValidExceoption");
				throw new IdNotValidFault();
			}

			StatusResponse sr = new StatusResponse();

			SpeciesJob job = jobMap.get(jobId);

			if (job instanceof DWCAJobByChildren){
				DWCAJobByChildren dwcaJob = (DWCAJobByChildren) job;

				List<NodeStatus> childrenStatus = new ArrayList<NodeStatus>();
				for (Entry<TaxonomyItem, JobStatus> entry : dwcaJob.getMapSubJobs().entrySet()){
					NodeStatus childStatus = new NodeStatus(entry.getKey().getScientificName(), entry.getValue().name());
					childrenStatus.add(childStatus);
				}
				sr.setSubNodesStatus(childrenStatus.toArray(new NodeStatus[childrenStatus.size()]));
			}

			sr.setStatus(job.getStatus().name());
			sr.setStartDate(job.getStartDate());
			sr.setEndDate(job.getEndDate());
			sr.setCompletedEntries(job.getCompletedEntries());

			return sr;
		}else{
			ExecutorPortType executorPt = remoteJobCall(node);
			return executorPt.getStatus(jobKey);
		}

	}


	public static void storeJobMap(){
		logger.trace("calling store job Map");
		ObjectOutputStream oos = null;
		File file = null;
		try {
			file = new File(ServiceContext.getContext().getPersistenceRoot()+File.separator+jobMapFileName);
			//if (file.exists()) file.delete();
			//file.createNewFile();
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(jobMap);
			
		} catch (Exception e) {
			logger.error("error writing jobMapof type "+jobMap.getClass().getName()+" on disk",e);
			if (file !=null && file.exists()) file.delete(); 
		}finally{
			if (oos!=null)
				try {
					oos.close();
				} catch (IOException e) {
					logger.warn("error closing stream",e);
				}
		}
	}

	@SuppressWarnings("unchecked")
	public  static void loadJobMap(){
		logger.trace("calling load job Map");
		ObjectInput ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(ServiceContext.getContext().getPersistenceRoot()+File.separator+jobMapFileName));
			jobMap = (HashMap<String, SpeciesJob>) ois.readObject();
			for (Entry<String, SpeciesJob> entry : jobMap.entrySet())
				if (entry.getValue().getStatus().equals(JobStatus.RUNNING))
					entry.getValue().setStatus(JobStatus.FAILED);
			ois.close();
		} catch (Exception e) {
			logger.trace("the file doesn't exist, creating an empty map");
			jobMap = new HashMap<String, SpeciesJob>();
		} 
	}




	@Override
	public VOID removeJob(String jobId) throws JobNotValidFault, InputNotValidFault, GCUBEFault {
		if (!jobMap.containsKey(jobId)) throw new IdNotValidFault();
		jobMap.remove(jobId);
		return new VOID();
	}


	@Override
	public String submitJob(SubmitJobRequest request) throws 
	GCUBEFault, IdNotValidFault {
		SpeciesJob job = null;
		switch (JobType.valueOf(request.getJob().toString())) {
		case DWCAByChildren:
			job = new DWCAJobByChildren(request.getInput());
			break;
		case DWCAById:
			job = new DWCAJobByIds(request.getInput());
			break;
		case CSV:
			job = new CSVCreator(request.getInput());
			break;
		case CSVForOM:
			job = new CSVCreatorForOMJob(request.getInput());
			break;
		case DarwinCore:
			job = new DarwinCoreJob(request.getInput());
			break;
		default:
			throw new JobNotValidFault();
		}

		if (job ==null || !job.validateInput(request.getInput())) 
			throw new InputNotValidFault();
		return executeJob(job);
	}


	private String executeJob(SpeciesJob job){
		jobMap.put(job.getId(), job);
		ServiceContext.getContext().executeJob(job);
		return createKey(job.getId());
	}

	private static String extractNode(String key) throws IdNotValidException{
		String[] splitted = key.split("\\|\\|");
		if (splitted.length==2)
			return splitted[0];
		else throw new IdNotValidException();
	}

	private static String extractId(String key) throws IdNotValidException{
		String[] splitted = key.split("\\|\\|");
		if (splitted.length==2)
			return splitted[1];
		else throw new IdNotValidException();
	}

	private String createKey(String id){
		String node = ServiceContext.getContext().getInstance().getID();
		return node+"||"+id;
	}

	private ExecutorPortType remoteJobCall(String riId) throws IdNotValidFault{
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/ID/text() eq '"+riId+"'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> addresses = client.submit(query);
		if (addresses.size()>0){
			GCoreEndpoint endpoint =  addresses.get(0);
			String address = endpoint.profile().endpointMap().get("gcube/data/speciesproductsdiscovery/executor").uri().toString();
			try {
				ExecutorPortType executorPT = new ExecutorServiceAddressingLocator().getExecutorPortTypePort(new EndpointReferenceType(new Address(address)));
				return executorPT;
			}  catch (Exception e) {
				logger.trace("remote service error");
				throw new IdNotValidFault();
			}

		}else {
			logger.trace("remote job not found");
			throw new IdNotValidFault();
		}
	}
}
