package org.gcube.data.spd.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gcube.common.authorization.library.AuthorizedTasks;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.data.spd.executor.jobs.SpeciesJob;
import org.gcube.data.spd.executor.jobs.URLJob;
import org.gcube.data.spd.executor.jobs.csv.CSVCreator;
import org.gcube.data.spd.executor.jobs.csv.CSVCreatorForOMJob;
import org.gcube.data.spd.executor.jobs.darwincore.DarwinCoreJob;
import org.gcube.data.spd.executor.jobs.dwca.DWCAJobByChildren;
import org.gcube.data.spd.executor.jobs.dwca.DWCAJobByIds;
import org.gcube.data.spd.executor.jobs.layer.LayerCreatorJob;
import org.gcube.data.spd.manager.AppInitializer;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.service.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.model.service.exceptions.InvalidJobException;
import org.gcube.data.spd.model.service.types.CompleteJobStatus;
import org.gcube.data.spd.model.service.types.JobStatus;
import org.gcube.data.spd.model.service.types.NodeStatus;
import org.gcube.data.spd.model.service.types.SubmitJob;
import org.gcube.data.spd.model.service.types.SubmitJobResponse;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.utils.DynamicMap;
import org.gcube.data.spd.utils.ExecutorsContainer;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("job")
public class Executor {

	private static Logger logger = LoggerFactory.getLogger(Executor.class);

	private static final String ID_SEPARATOR = "_"; 

	AppInitializer initializer = (AppInitializer)ApplicationManagerProvider.get(AppInitializer.class);

	ApplicationContext cxt = ContextProvider.get();

	@GET
	@Path("result/{jobKey}")
	public String getResultLink(@PathParam("jobKey") String jobKey) throws InvalidIdentifierException {
		String node;
		String jobId;
		try{
			node = extractNode(jobKey);
			jobId = extractId(jobKey);
		}catch (IdNotValidException e) {
			logger.error("id not valid "+jobKey,e);
			throw new InvalidIdentifierException(jobKey);
		}

		if (node.equals(cxt.container().profile(HostingNode.class).id())){
			if (!initializer.getJobMap().containsKey(jobId)){ 
				logger.error("id not valid {} ",jobId);
				throw new InvalidIdentifierException(jobId);
			}
			return ((URLJob)initializer.getJobMap().get(jobId)).getResultURL();
		}else {
			logger.error("node not valid {} ",node);
			throw new InvalidIdentifierException();
		}
	}

	@GET
	@Path("error/{jobKey}")
	public String getErrorLink(@PathParam("jobKey") String jobKey) throws InvalidIdentifierException {

		String node;
		String jobId;

		try{
			node = extractNode(jobKey);
			jobId = extractId(jobKey);
		}catch (IdNotValidException e) {
			logger.error("id not valid "+jobKey,e);
			throw new InvalidIdentifierException();
		}

		if (node.equals(cxt.container().profile(HostingNode.class).id())){
			if (!initializer.getJobMap().containsKey(jobId)){
				logger.error("id not valid {} ",jobId);
				throw new InvalidIdentifierException();
			}
			return ((URLJob)initializer.getJobMap().get(jobId)).getErrorURL();
		}else{
			logger.error("node not valid {} ",node);
			throw new InvalidIdentifierException();
		}
	}

	@GET
	@Path("status/{jobKey}")
	public CompleteJobStatus getStatus(@PathParam("jobKey") String jobKey) throws InvalidIdentifierException {
		logger.trace("[TEST] job status called with id {}", jobKey);
		String node;
		String jobId;

		try{
			node = extractNode(jobKey);
			jobId = extractId(jobKey);
		}catch (IdNotValidException e) {
			logger.error("id not valid {} ",jobKey,e);
			throw new InvalidIdentifierException(jobKey);
		}		

		if (node.equals(cxt.container().profile(HostingNode.class).id())){

			if (!initializer.getJobMap().containsKey(jobId)){
				logger.warn("id not found, throwing IDNotValidExceoption");
				throw new InvalidIdentifierException(jobId);
			}

			SpeciesJob job = initializer.getJobMap().get(jobId);

			CompleteJobStatus status = new CompleteJobStatus();

			if (job instanceof DWCAJobByChildren){
				DWCAJobByChildren dwcaJob = (DWCAJobByChildren) job;

				List<NodeStatus> childrenStatus = new ArrayList<NodeStatus>();
				for (Entry<TaxonomyItem, JobStatus> entry : dwcaJob.getMapSubJobs().entrySet()){
					NodeStatus childStatus = new NodeStatus(entry.getKey().getScientificName(), entry.getValue());
					childrenStatus.add(childStatus);
				}
				status.setSubNodes(childrenStatus);
			}

			status.setStatus(job.getStatus());
			status.setStartDate(job.getStartDate());
			status.setEndDate(job.getEndDate());
			status.setCompletedEntries(job.getCompletedEntries());

			return status;
		}else{
			logger.error("node not valid {} ",node);
			throw new InvalidIdentifierException();
		}

	}

	@DELETE
	@Path("{jobKey}")
	public void removeJob(@PathParam("jobKey") String jobId) throws InvalidIdentifierException {
		if (!initializer.getJobMap().containsKey(jobId)) throw new InvalidIdentifierException(jobId);
		initializer.getJobMap().remove(jobId);
	}


	@POST
	@Path("execute")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public SubmitJobResponse submitJob(SubmitJob request) throws InvalidJobException {
		PluginManager pluginManger = initializer.getPluginManager();
		SpeciesJob job = null;
		switch (request.getJob()) {
		case DWCAByChildren:
			job = new DWCAJobByChildren(request.getInput(), pluginManger.plugins());
			break;
		case DWCAById:
			job = new DWCAJobByIds(pluginManger.plugins());
			DynamicMap.put(job.getId());
			break;
		case CSV:
			job = new CSVCreator(pluginManger.plugins());
			DynamicMap.put(job.getId());
			break;
		case CSVForOM:
			job = new CSVCreatorForOMJob(pluginManger.plugins());
			DynamicMap.put(job.getId());
			break;
		case DarwinCore:
			job = new DarwinCoreJob(pluginManger.plugins());
			DynamicMap.put(job.getId());
			break;
		case LayerCreator:
			job = new LayerCreatorJob(request.getInput(),pluginManger.plugins());
			DynamicMap.put(job.getId());
			break;	
		default:
			throw new InvalidJobException();
		}

		if (job ==null || !job.validateInput(request.getInput())) 
			throw new InvalidJobException();

		String jobId = executeJob(job);
		logger.trace("[TEST] job submitted with id {}", jobId);
		return new SubmitJobResponse(job.getId(), jobId, cxt.profile(GCoreEndpoint.class).id());
	}


	private String executeJob(SpeciesJob job){
		initializer.getJobMap().put(job.getId(), job);
		ExecutorsContainer.execJob(AuthorizedTasks.bind(job));
		return createKey(job.getId());
	}

	private static String extractNode(String key) throws IdNotValidException{
		logger.trace("id arrived is {}", key );
		String[] splitted = key.split(ID_SEPARATOR);
		if (splitted.length==2)
			return splitted[0];
		else throw new IdNotValidException();
	}

	private static String extractId(String key) throws IdNotValidException{
		logger.trace("id arrived is {}", key );
		String[] splitted = key.split(ID_SEPARATOR);
		if (splitted.length==2)
			return splitted[1];
		else throw new IdNotValidException();
	}

	private String createKey(String id){
		logger.trace("id arrived is {}", id );
		String node = cxt.container().profile(HostingNode.class).id();
		return node+ID_SEPARATOR+id;
	}
	/*
	private Executor remoteJobCall(String riId) throws InvalidIdentifierException{
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/ID/text() eq '"+riId+"'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> addresses = client.submit(query);
		if (addresses.size()>0){
			GCoreEndpoint endpoint =  addresses.get(0);
			URI address = endpoint.profile().endpointMap().get("gcube/data/speciesproductsdiscovery/executor").uri();
			try {
				Executor executorPT = executor().at(address).build();
				return executorPT;
			}  catch (Exception e) {
				logger.trace("remote service error");
				throw new InvalidIdentifierException();
			}

		}else {
			logger.trace("remote job not found");
			throw new InvalidIdentifierException();
		}
	}*/
}
