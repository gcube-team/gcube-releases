package org.gcube.data.analysis.dataminermanagercl.shared.service;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ServiceInfo implements Serializable {

	private static final long serialVersionUID = 9046784925213335261L;
	private String serviceAddress;
	private ArrayList<ServiceInfoData> serviceProperties;

	public ServiceInfo() {
		super();
	}

	public ServiceInfo(String serviceAddress, ArrayList<ServiceInfoData> serviceProperties) {
		super();
		this.serviceAddress = serviceAddress;
		this.serviceProperties = serviceProperties;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	public ArrayList<ServiceInfoData> getServiceProperties() {
		return serviceProperties;
	}

	public void setServiceProperties(ArrayList<ServiceInfoData> serviceProperties) {
		this.serviceProperties = serviceProperties;
	}

	@Override
	public String toString() {
		return "ServiceInfo [serviceAddress=" + serviceAddress + ", serviceProperties=" + serviceProperties + "]";
	}

}
