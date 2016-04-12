package org.gcube.data.spd.client.proxies;

import static org.gcube.common.clients.exceptions.FaultDSL.again;
import static org.gcube.data.streams.dsl.Streams.publishStringsIn;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.data.spd.stubs.ExecutorStub;
import org.gcube.data.spd.stubs.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.stubs.types.JobType;
import org.gcube.data.spd.stubs.types.Status;
import org.gcube.data.spd.stubs.types.SubmitJob;
import org.gcube.data.streams.Stream;

public class DefaultExecutor implements Executor{

	private final ProxyDelegate<ExecutorStub> delegate; 
			
	public DefaultExecutor(ProxyDelegate<ExecutorStub> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public String createDwCAByChildren(final String taxonKey) throws Exception {
		return delegate.make(getCallForJobs(taxonKey, JobType.DWCAByChildren));
	}

	@Override
	public String createDwCAByIds(final Stream<String> ids) throws Exception {
		String idsLocator = publishStringsIn(ids).withDefaults().toString();
		return delegate.make(getCallForJobs(idsLocator, JobType.DWCAById));
	}
	
	@Override
	public String createCSV(Stream<String> keys) throws Exception {
		String idsLocator = publishStringsIn(keys).withDefaults().toString();
		return delegate.make(getCallForJobs(idsLocator, JobType.CSV));
	}

	@Override
	public String createCSVforOM(Stream<String> keys) throws Exception {
		String idsLocator = publishStringsIn(keys).withDefaults().toString();
		return delegate.make(getCallForJobs(idsLocator, JobType.CSVForOM));
		
	}
	

	@Override
	public String createDarwincoreFromOccurrenceKeys(Stream<String> keys)
			throws Exception {
		String idsLocator = publishStringsIn(keys).withDefaults().toString();
		return delegate.make(getCallForJobs(idsLocator, JobType.DarwinCore));
	}
	
	
	@Override
	public String getResultLink(final String jobId) throws InvalidIdentifierException{
		Call<ExecutorStub, String> call = new Call<ExecutorStub, String>() {
			@Override
			public String call(ExecutorStub executor) throws Exception {
				return executor.getResultLink(jobId);
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw again(e).asServiceException();
		}

	}

	@Override
	public Status getStatus(final String jobId) throws InvalidIdentifierException{
		Call<ExecutorStub, Status> call = new Call<ExecutorStub, Status>() {
			@Override
			public Status call(ExecutorStub executor) throws Exception {
				return executor.getStatus(jobId);
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw again(e).asServiceException();
		}

	}

	@Override
	public void removeJob(final String jobId) throws InvalidIdentifierException{
		Call<ExecutorStub, Empty> call = new Call<ExecutorStub, Empty>() {
			@Override
			public Empty call(ExecutorStub executor) throws Exception {
				return executor.removeJob(jobId);
			}
		};
		try {
			delegate.make(call);
		}catch(Exception e) {
			throw again(e).asServiceException();
		}
		
	}

	private Call<ExecutorStub, String> getCallForJobs(final String input, final JobType job){
		return new Call<ExecutorStub, String>() {
			@Override
			public String call(ExecutorStub executor) throws Exception {
				return executor.submitJob(new SubmitJob(input, job));
			}
		};
	}

	@Override
	public String getErrorLink(final String jobId) throws InvalidIdentifierException {
		Call<ExecutorStub, String> call = new Call<ExecutorStub, String>() {
			@Override
			public String call(ExecutorStub executor) throws Exception {
				return executor.getErrorLink(jobId);
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw again(e).asServiceException();
		}

	}
	
}
