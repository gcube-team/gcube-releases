package gr.uoa.di.madgik.workflow.client.library.proxies;

import gr.uoa.di.madgik.workflow.client.library.beans.Types.CONDORParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.GRIDParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.HADOOPParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.JDLParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.StatusReport;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.StatusRequest;
import gr.uoa.di.madgik.workflow.client.library.exceptions.WorkflowEngineException;
import gr.uoa.di.madgik.workflow.client.library.stubs.WorkflowEngineStub;

import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.Call;

public class WorkflowEngineCLDefaultProxy implements WorkflowEngineCLProxyI {

	private final ProxyDelegate<WorkflowEngineStub> delegate;

	public WorkflowEngineCLDefaultProxy(ProxyDelegate<WorkflowEngineStub> config) {
		this.delegate = config;
	}

	@Override
	public String adaptJDL(final JDLParams jdlParams) throws WorkflowEngineException {
		Call<WorkflowEngineStub, String> call = new Call<WorkflowEngineStub, String>() {
			@Override
			public String call(WorkflowEngineStub endpoint) throws Exception{
				return endpoint.adaptJDL(jdlParams);
			}
		};

		String adaptJDLResponse = null;
		try {
			adaptJDLResponse = delegate.make(call);
		} catch (Exception e) {
			throw new WorkflowEngineException(e);
		}
		return adaptJDLResponse;
	}
	
	@Override
	public String adaptGRID(final GRIDParams gridParams) throws WorkflowEngineException {
		Call<WorkflowEngineStub, String> call = new Call<WorkflowEngineStub, String>() {
			@Override
			public String call(WorkflowEngineStub endpoint) throws Exception{
				return endpoint.adaptGRID(gridParams);
			}
		};

		String adaptGRIDResponse = null;
		try {
			adaptGRIDResponse = delegate.make(call);
		} catch (Exception e) {
			throw new WorkflowEngineException(e);
		}
		return adaptGRIDResponse;
	}

	@Override
	public String adaptHADOOP(final HADOOPParams hadoopParams) throws WorkflowEngineException {
		Call<WorkflowEngineStub, String> call = new Call<WorkflowEngineStub, String>() {
			@Override
			public String call(WorkflowEngineStub endpoint) throws Exception{
				return endpoint.adaptHADOOP(hadoopParams);
			}
		};

		String adaptHADOOPResponse = null;
		try {
			adaptHADOOPResponse = delegate.make(call);
		} catch (Exception e) {
			throw new WorkflowEngineException(e);
		}
		return adaptHADOOPResponse;
	}

	@Override
	public String adaptCONDOR(final CONDORParams condorParams) throws WorkflowEngineException {
		Call<WorkflowEngineStub, String> call = new Call<WorkflowEngineStub, String>() {
			@Override
			public String call(WorkflowEngineStub endpoint) throws Exception{
				return endpoint.adaptCONDOR(condorParams);
			}
		};

		String adaptCONDORResponse = null;
		try {
			adaptCONDORResponse = delegate.make(call);
		} catch (Exception e) {
			throw new WorkflowEngineException(e);
		}
		return adaptCONDORResponse;
	}

	@Override
	public StatusReport executionStatus(final StatusRequest statusRequest) throws WorkflowEngineException {
		Call<WorkflowEngineStub, StatusReport> call = new Call<WorkflowEngineStub, StatusReport>() {
			@Override
			public StatusReport call(WorkflowEngineStub endpoint) throws Exception{
				return endpoint.executionStatus(statusRequest);
			}
		};

		StatusReport statusReport = null;
		try {
			statusReport = delegate.make(call);
		} catch (Exception e) {
			throw new WorkflowEngineException(e);
		}
		return statusReport;

	}

	@Override
	public String about(final String about) throws WorkflowEngineException {
		Call<WorkflowEngineStub, String> call = new Call<WorkflowEngineStub, String>() {
			@Override
			public String call(WorkflowEngineStub endpoint) throws Exception{
				return endpoint.about(about);
			}
		};

		String aboutResponse = null;
		try {
			aboutResponse = delegate.make(call);
		} catch (Exception e) {
			throw new WorkflowEngineException(e);
		}
		return aboutResponse;
	}	


}
