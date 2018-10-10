package org.gcube.portlets.admin.fhn_manager_portlet.shared.model;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RemoteNode implements Storable,IsSerializable{

	public static final ObjectType TYPE=ObjectType.REMOTE_NODE;

	public static final String prefix="NODE.";

	public static final String ID_FIELD=prefix+"ID";
	public static final String HOST_FIELD=prefix+"HOST";
	public static final String STATUS=prefix+"STATUS";
	public static final String TEMPLATE=prefix+"TEMPLATE";
	public static final String SERVICE_PROFILE=prefix+"SERVICE_PROFILE";
	public static final String VM_PROVIDER=prefix+"VM_PROVIDER";
	public static final String NOW_WORKLOAD=prefix+"NOW_WORKLOAD";
	public static final String LAST_HOUR_WORKLOAD=prefix+"LAST_HOUR_WORKLOAD";
	public static final String LAST_DAY_WORKLOAD=prefix+"LAST_DAY_WORKLOAD";
	public static final String AVG_WORKLOAD=prefix+"AVG_WORKLOAD";





	/**
	 *  Describes a remote node created on a cloud provider using a pre existing template
	 *  
	 */

	private String id=null;
	private String host=null;
	private RemoteNodeStatus status=null;
	private String vmTemplateId=null; 
	private String vmProviderId=null;
	private String serviceProfileId=null;


	// Workload
	private Double nowWorkload;
	private Double lastHourWorkload;
	private Double lastDayWorkload;
	private Double allTimeAverageWorkload;



	//Objects
	
	private VMTemplate vmTemplate=null;
	private VMProvider vmProvider=null;
	private ServiceProfile serviceProfile=null;
	

	public RemoteNode() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the nowWorkload
	 */
	public Double getNowWorkload() {
		return nowWorkload;
	}



	public RemoteNode(String id, String host, RemoteNodeStatus status,
			String vmTemplate, String vmProviderId, String serviceProfileId,
			Double nowWorkload, Double lastHourWorkload, Double lastDayWorkload,
			Double allTimeAverageWorkload) {
		super();
		this.id = id;
		this.host = host;
		this.status = status;
		this.vmTemplateId = vmTemplate;
		this.vmProviderId = vmProviderId;
		this.serviceProfileId = serviceProfileId;
		this.nowWorkload = nowWorkload;
		this.lastHourWorkload = lastHourWorkload;
		this.lastDayWorkload = lastDayWorkload;
		this.allTimeAverageWorkload = allTimeAverageWorkload;
	}



	/**
	 * @param nowWorkload the nowWorkload to set
	 */
	public void setNowWorkload(Double nowWorkload) {
		this.nowWorkload = nowWorkload;
	}



	/**
	 * @return the lastHourWorkload
	 */
	public Double getLastHourWorkload() {
		return lastHourWorkload;
	}



	/**
	 * @param lastHourWorkload the lastHourWorkload to set
	 */
	public void setLastHourWorkload(Double lastHourWorkload) {
		this.lastHourWorkload = lastHourWorkload;
	}



	/**
	 * @return the lastDayWorkload
	 */
	public Double getLastDayWorkload() {
		return lastDayWorkload;
	}



	/**
	 * @param lastDayWorkload the lastDayWorkload to set
	 */
	public void setLastDayWorkload(Double lastDayWorkload) {
		this.lastDayWorkload = lastDayWorkload;
	}



	/**
	 * @return the allTimeAverageWorkload
	 */
	public Double getAllTimeAverageWorkload() {
		return allTimeAverageWorkload;
	}



	/**
	 * @param allTimeAverageWorkload the allTimeAverageWorkload to set
	 */
	public void setAllTimeAverageWorkload(Double allTimeAverageWorkload) {
		this.allTimeAverageWorkload = allTimeAverageWorkload;
	}



	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the status
	 */
	public RemoteNodeStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(RemoteNodeStatus status) {
		this.status = status;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}




	/**
	 * @return the vmProviderId
	 */
	public String getVmProviderId() {
		return vmProviderId;
	}

	/**
	 * @param vmProviderId the vmProviderId to set
	 */
	public void setVmProviderId(String vmProviderId) {
		this.vmProviderId = vmProviderId;
	}

	/**
	 * @return the serviceProfileId
	 */
	public String getServiceProfileId() {
		return serviceProfileId;
	}

	/**
	 * @param serviceProfileId the serviceProfileId to set
	 */
	public void setServiceProfileId(String serviceProfileId) {
		this.serviceProfileId = serviceProfileId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoteNode other = (RemoteNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String getKey() {
		return getId();
	}

	@Override
	public Object getObjectField(String fieldName) {		
		if(fieldName.startsWith(VMTemplate.prefix)) return getVmTemplate().getObjectField(fieldName);
		if(fieldName.startsWith(VMProvider.prefix)) return getVmProvider().getObjectField(fieldName);
		if(fieldName.startsWith(ServiceProfile.prefix)) return getServiceProfile().getObjectField(fieldName);
		
		if(fieldName.equals(ID_FIELD)) return getId();
		if(fieldName.equals(HOST_FIELD))return getHost();
		if(fieldName.equals(STATUS)) return getStatus().toString();
		if(fieldName.equals(TEMPLATE)) return getVmTemplateId();
		if(fieldName.equals(SERVICE_PROFILE)) return getServiceProfileId();
		if(fieldName.equals(VM_PROVIDER)) return getVmProviderId();
		if(fieldName.equals(NOW_WORKLOAD)) return getNowWorkload();
		if(fieldName.equals(LAST_DAY_WORKLOAD)) return getLastDayWorkload();
		if(fieldName.equals(LAST_HOUR_WORKLOAD)) return getLastHourWorkload();
		if(fieldName.equals(AVG_WORKLOAD)) return getAllTimeAverageWorkload();
		return null;

	}

	/**
	 * @return the vmTemplate
	 */
	public String getVmTemplateId() {
		return vmTemplateId;
	}

	/**
	 * @param vmTemplate the vmTemplate to set
	 */
	public void setVmTemplateId(String vmTemplate) {
		this.vmTemplateId = vmTemplate;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	public VMTemplate getVmTemplate() {
		return vmTemplate;
	}


	public void setVmTemplate(VMTemplate vmTemplate) {
		this.vmTemplate = vmTemplate;
	}


	public VMProvider getVmProvider() {
		return vmProvider;
	}


	public void setVmProvider(VMProvider vmProvider) {
		this.vmProvider = vmProvider;
	}


	public ServiceProfile getServiceProfile() {
		return serviceProfile;
	}


	public void setServiceProfile(ServiceProfile serviceProfile) {
		this.serviceProfile = serviceProfile;
	}


	@Override
	public ObjectType getType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RemoteNode [id=");
		builder.append(id);
		builder.append(", host=");
		builder.append(host);
		builder.append(", status=");
		builder.append(status);
		builder.append(", vmTemplate=");
		builder.append(vmTemplateId);
		builder.append(", vmProviderId=");
		builder.append(vmProviderId);
		builder.append(", serviceProfileId=");
		builder.append(serviceProfileId);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public String getName() {
		return host!=null?getHost():getId();
	}

}
