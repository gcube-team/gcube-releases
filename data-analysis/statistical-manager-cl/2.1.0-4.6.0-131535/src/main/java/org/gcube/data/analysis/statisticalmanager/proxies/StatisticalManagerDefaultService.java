package org.gcube.data.analysis.statisticalmanager.proxies;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.FaultDSL;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.data.analysis.statisticalmanager.stubs.ComputationStub;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationConfig;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMAbstractResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMOperationInfo;

public class StatisticalManagerDefaultService implements StatisticalManagerService{

	private final ProxyDelegate<ComputationStub> delegate;
	
	public StatisticalManagerDefaultService(ProxyDelegate<ComputationStub> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public SMOperationInfo getComputationInfo(final String computationId) {
		Call<ComputationStub,SMOperationInfo> call = new Call<ComputationStub, SMOperationInfo>(){
			@Override
			public SMOperationInfo call(ComputationStub endpoint) throws Exception {
				return endpoint.getComputationInfo(computationId);
			};
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public SMAbstractResource getComputationOutput(final String computationId) {
		Call<ComputationStub, SMAbstractResource> call = new Call<ComputationStub, SMAbstractResource>() {

			@Override
			public SMAbstractResource call(ComputationStub endpoint)
					throws Exception {
				return endpoint.getOutput(computationId);
			}
		};
		
		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

		@Override
	public void removeComputation(final String computationId) {
		Call<ComputationStub, Empty> call = new Call<ComputationStub, Empty>() {

			@Override
			public Empty call(ComputationStub endpoint)
					throws Exception {
//				return endpoint.remove(computationId);
				endpoint.remove(computationId);
				return new Empty();
			}
			
		};
		
		try {
			delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public String executeComputation(SMComputationConfig computationConfig) {
		
			Call<ComputationStub, String> call = new Call<ComputationStub, String>(){
			
			@Override
			public String call(ComputationStub endpoint) throws Exception {
				
				return null;
				//return endpoint.executeComputation(computationConfig);
			};
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}
	
}
