package org.gcube.vremanagement.vremodel.cl.proxy;

import static org.gcube.common.clients.exceptions.FaultDSL.again;
import static org.gcube.vremanagement.vremodel.cl.Constants.EMPTY_VALUE;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.vremodel.cl.stubs.ManagerStub;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityList;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityNodes;
import org.gcube.vremanagement.vremodel.cl.stubs.types.GHNArray;
import org.gcube.vremanagement.vremodel.cl.stubs.types.SelectedResourceDescriptionType;
import org.gcube.vremanagement.vremodel.cl.stubs.types.SetFunctionalityRequest;
import org.gcube.vremanagement.vremodel.cl.stubs.types.VREDescription;
import org.gcube.vremanagement.vremodeler.utils.Utils;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;


public class DefaultManager implements Manager {

	private final ProxyDelegate<ManagerStub> delegate;
	
	public DefaultManager(ProxyDelegate<ManagerStub> config){
		this.delegate = config;
	}

	@Override
	public void setDescription(final String name, final String description, final String designer, 
								final String manager, final Calendar startTime, final Calendar endTime) {
		Call<ManagerStub, Empty> call =  new Call<ManagerStub, Empty>(){
			@Override
			public Empty call(ManagerStub endpoint)
					throws Exception {
				endpoint.setDescription(new VREDescription(name, description, designer, manager, startTime, endTime));
				return new Empty();
			}
		};

		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public VREDescription getDescription() {
		Call<ManagerStub, VREDescription> call =  new Call<ManagerStub, VREDescription>(){
			@Override
			public VREDescription call(ManagerStub endpoint)
					throws Exception {
				return endpoint.getDescription(EMPTY_VALUE);
				
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public void setUseCloud(final boolean useCloud) {
		Call<ManagerStub, Empty> call =  new Call<ManagerStub, Empty>(){
			@Override
			public Empty call(ManagerStub endpoint)
					throws Exception {
				endpoint.setUseCloud(useCloud);
				return EMPTY_VALUE;
			}
		};

		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public void setCloudVMs(final int vms) {
		Call<ManagerStub, Empty> call =  new Call<ManagerStub, Empty>(){
			@Override
			public Empty call(ManagerStub endpoint)
					throws Exception {
				endpoint.setCloudVMs(vms);
				return EMPTY_VALUE;
			}
		};

		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public int getCloudVMs() {
		Call<ManagerStub, Integer> call =  new Call<ManagerStub, Integer>(){
			@Override
			public Integer call(ManagerStub endpoint)
					throws Exception {
				return endpoint.getCloudVMs(EMPTY_VALUE);
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public boolean isUseCloud() {
		Call<ManagerStub, Boolean> call =  new Call<ManagerStub, Boolean>(){
			@Override
			public Boolean call(ManagerStub endpoint)
					throws Exception {
				return endpoint.isUseCloud(EMPTY_VALUE);
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public String getQuality() {
		Call<ManagerStub, String> call =  new Call<ManagerStub, String>(){
			@Override
			public String call(ManagerStub endpoint)
					throws Exception {
				return endpoint.getQuality(EMPTY_VALUE);
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public void setQuality(final String quality) {
		Call<ManagerStub, Empty> call =  new Call<ManagerStub, Empty>(){
			@Override
			public Empty call(ManagerStub endpoint)
					throws Exception {
				endpoint.setQuality(quality);
				return EMPTY_VALUE;
			}
		};

		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
		
	}

	@Override
	public List<FunctionalityItem> getFunctionalities() {
		Call<ManagerStub, FunctionalityList> call =  new Call<ManagerStub, FunctionalityList>(){
			@Override
			public FunctionalityList call(ManagerStub endpoint)
					throws Exception {
				return endpoint.getFunctionality(EMPTY_VALUE);
			}
		};
		try {
			FunctionalityList functionalityList = delegate.make(call);
			if(functionalityList.items()!=null)
				return functionalityList.items();
			else return  Collections.emptyList();
			
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public void setFunctionality(final List<Integer> functionalityIds, final List<SelectedResourceDescriptionType> resourceDescriptions) {
		Call<ManagerStub, Empty> call =  new Call<ManagerStub, Empty>(){
			@Override
			public Empty call(ManagerStub endpoint)
					throws Exception {
				endpoint.setFunctionality(new SetFunctionalityRequest(functionalityIds, resourceDescriptions));
				return EMPTY_VALUE;
			}
		};

		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
		
	}

	@Override
	public FunctionalityNodes getFunctionalityNodes() {
		Call<ManagerStub, FunctionalityNodes> call =  new Call<ManagerStub, FunctionalityNodes>(){
			@Override
			public FunctionalityNodes call(ManagerStub endpoint)
					throws Exception {
				return endpoint.getFunctionalityNodes(EMPTY_VALUE);
			}
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public void setGHNs(final List<String> ghns) {
		Call<ManagerStub, Empty> call =  new Call<ManagerStub, Empty>(){
			@Override
			public Empty call(ManagerStub endpoint)
					throws Exception {
				endpoint.setGHNs(new GHNArray(ghns));
				return EMPTY_VALUE;
			}
		};
		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public void setVREtoPendingState() {
		Call<ManagerStub, Empty> call =  new Call<ManagerStub, Empty>(){
			@Override
			public Empty call(ManagerStub endpoint)
					throws Exception {
				endpoint.setVREtoPendingState(EMPTY_VALUE);
				return EMPTY_VALUE;
			}
		};
		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
		
	}

	@Override
	public void deployVRE() {
		Call<ManagerStub, Empty> call =  new Call<ManagerStub, Empty>(){
			@Override
			public Empty call(ManagerStub endpoint)
					throws Exception {
				endpoint.deployVRE(EMPTY_VALUE);
				return EMPTY_VALUE;
			}
		};
		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}

	}

	@Override
	public void undeployVRE() {
		Call<ManagerStub, Empty> call =  new Call<ManagerStub, Empty>(){
			@Override
			public Empty call(ManagerStub endpoint)
					throws Exception {
				endpoint.undeployVRE(EMPTY_VALUE);
				return EMPTY_VALUE;
			}
		};
		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
		
	}

	@Override
	public void renewVRE(final Calendar untilDate) {
		Call<ManagerStub, Empty> call =  new Call<ManagerStub, Empty>(){
			@Override
			public Empty call(ManagerStub endpoint)
					throws Exception {
				endpoint.renewVRE(untilDate);
				return EMPTY_VALUE;
			}
		};
		try {
			delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}

		
	}

	@Override
	public DeployReport checkStatus() {
		Call<ManagerStub, String> call =  new Call<ManagerStub, String>(){
			@Override
			public String call(ManagerStub endpoint)
					throws Exception {
				return endpoint.checkStatus(EMPTY_VALUE);
			}
		};
		try {
			String report =  delegate.make(call);
			return Utils.fromXML(report);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
		
	}
	
	
		
		
}
