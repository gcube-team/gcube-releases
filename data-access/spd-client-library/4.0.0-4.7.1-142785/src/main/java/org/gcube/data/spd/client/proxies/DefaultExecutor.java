package org.gcube.data.spd.client.proxies;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.data.spd.model.service.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.model.service.types.CompleteJobStatus;
import org.gcube.data.spd.model.service.types.JobType;
import org.gcube.data.spd.model.service.types.MetadataDetails;
import org.gcube.data.spd.model.service.types.SubmitJob;
import org.gcube.data.spd.model.service.types.SubmitJobResponse;
import org.gcube.data.streams.Stream;

import com.thoughtworks.xstream.XStream;

public class DefaultExecutor implements ExecutorClient{

	private final ProxyDelegate<WebTarget> delegate; 
			
	public DefaultExecutor(ProxyDelegate<WebTarget> delegate) {
		super();
		this.delegate = delegate;
	}

	private Call<WebTarget, SubmitJobResponse> getCallForJobs(final String input, final JobType job){
		Call<WebTarget, SubmitJobResponse> call = new Call<WebTarget, SubmitJobResponse>() {
			@Override
			public SubmitJobResponse call(WebTarget executor) throws Exception {
				SubmitJob jobRequest = new SubmitJob(input, job); 
				return executor.path("execute").request().post(Entity.xml(jobRequest), SubmitJobResponse.class);
			}
		};
		return call;
	}

	@Override
	public String getErrorLink(final String jobId) throws InvalidIdentifierException {
		Call<WebTarget, String> call = new Call<WebTarget, String>() {
			@Override
			public String call(WebTarget executor) throws Exception {
				return executor.path("error").path(jobId).request().get(String.class);
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw new InvalidIdentifierException();
		}

	}

	@Override
	public String getResultLink(final String jobId) throws InvalidIdentifierException {
		Call<WebTarget, String> call = new Call<WebTarget, String>() {
			@Override
			public String call(WebTarget executor) throws Exception {
				return executor.path("result").path(jobId).request().get(String.class);
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw new InvalidIdentifierException();
		}
	}
		
	@Override
	public CompleteJobStatus getStatus(final String jobId)
			throws InvalidIdentifierException {
		Call<WebTarget, CompleteJobStatus> call = new Call<WebTarget, CompleteJobStatus>() {
			@Override
			public CompleteJobStatus call(WebTarget executor) throws Exception {
				return executor.path("status").path(jobId).request().get(CompleteJobStatus.class);
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw new InvalidIdentifierException();
		}
	}

	@Override
	public void removeJob(final String jobId) throws InvalidIdentifierException {
		Call<WebTarget, Empty> call = new Call<WebTarget, Empty>() {
			@Override
			public Empty call(WebTarget executor) throws Exception {
				executor.path(jobId).request().delete();
				return new Empty();
			}
		};
		try {
			delegate.make(call);
		}catch(Exception e) {
			throw new InvalidIdentifierException();
		}
		
	}
	
	
	@Override
	public String createDwCAByChildren(String taxonKey) throws Exception {
		return delegate.make(getCallForJobs(taxonKey, JobType.DWCAByChildren)).getJobId();
	}
	
	@Override
	public String createDwCAByIds(Stream<String> ids) throws Exception {
		SubmitJobResponse response = delegate.make(getCallForJobs(null, JobType.DWCAById));
		try{
			DefaultResultSet.sendInput(response.getEndpointId(), response.getInputLocator(), ids);
		}catch(Exception e){
			e.printStackTrace();
		}
		return response.getJobId();
		
	}

	@Override
	public String createCSV(Stream<String> ids) throws Exception {
		SubmitJobResponse response = delegate.make(getCallForJobs(null, JobType.CSV));
		try{
			DefaultResultSet.sendInput(response.getEndpointId(), response.getInputLocator(), ids);
		}catch(Exception e){
			e.printStackTrace();
		}
		return response.getJobId();
	}

	@Override
	public String createLayer(Stream<String> keys, MetadataDetails metadata) throws Exception {
		SubmitJobResponse response = delegate.make(getCallForJobs(new XStream().toXML(metadata), JobType.LayerCreator));
		try{
			DefaultResultSet.sendInput(response.getEndpointId(), response.getInputLocator(), keys);
		}catch(Exception e){
			e.printStackTrace();
		}
		return response.getJobId();
	}
	
	@Override
	public String createCSVforOM(Stream<String> ids) throws Exception {
		SubmitJobResponse response = delegate.make(getCallForJobs(null, JobType.CSVForOM));
		try{
			DefaultResultSet.sendInput(response.getEndpointId(), response.getInputLocator(), ids);
		}catch(Exception e){
			e.printStackTrace();
		}
		return response.getJobId();
	}

	@Override
	public String createDarwincoreFromOccurrenceKeys(Stream<String> ids)
			throws Exception {
		SubmitJobResponse response = delegate.make(getCallForJobs(null, JobType.DarwinCore));
		try{
			DefaultResultSet.sendInput(response.getEndpointId(), response.getInputLocator(), ids);
		}catch(Exception e){
			e.printStackTrace();
		}
		return response.getJobId();
	}
	
}
