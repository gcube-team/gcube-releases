package org.gcube.common.authorizationservice.persistence.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.gcube.common.authorization.library.policies.Action;
import org.gcube.common.authorization.library.policies.ServiceAccess;
import org.gcube.common.authorization.library.policies.ServiceEntity;
import org.gcube.common.authorization.library.policies.Services;

@Entity
@DiscriminatorValue(EntityConstants.SERVICE_POLICY)
@NamedQueries({
	@NamedQuery(name="ServicePolicy.get", query=EntityQueries.SERVICE_POLICY_GET)
})
public class ServicePolicyEntity extends PolicyEntity {
		
	@Embedded 
	ServiceAccessEntity clientAccessEntity = null;		

	@ElementCollection
	private List<ServiceAccessEntity> excludes;
	
	protected ServicePolicyEntity() {
		super();
	}

	public ServicePolicyEntity(String context, ServiceAccess serviceAccess, ServiceEntity serviceEntityClient, Action mode) {
		super(context, serviceAccess, EntityConstants.SERVICE_POLICY, mode);
		if (serviceEntityClient.getService()!=null){
			ServiceAccess clientAccess = serviceEntityClient.getService();
			clientAccessEntity = new ServiceAccessEntity();
			clientAccessEntity.clientServiceClass= clientAccess.getServiceClass();
			clientAccessEntity.clientServiceName = clientAccess.getName();
			clientAccessEntity.clientServiceIdentifier = clientAccess.getServiceId();
			this.excludeType = ExcludeType.NOTEXCLUDE;
		} else {
			excludes = new ArrayList<ServicePolicyEntity.ServiceAccessEntity>();			
			for(ServiceAccess sa : serviceEntityClient.getExcludes()){
				ServiceAccessEntity sae = new ServiceAccessEntity();
				sae.clientServiceClass= sa.getServiceClass();
				sae.clientServiceName = sa.getName();
				sae.clientServiceIdentifier = sa.getServiceId();
				excludes.add(sae);
			}
			this.excludeType = ExcludeType.EXCLUDE;
		}
	}


	public ServiceEntity getClientAccess() {
		if (excludes!=null && !excludes.isEmpty()){
			ServiceAccess[] services = new ServiceAccess[excludes.size()];
			for (int i=0;i<excludes.size(); i++)
				services[i] = new ServiceAccess(excludes.get(i).clientServiceName, excludes.get(i).clientServiceClass, excludes.get(i).clientServiceIdentifier);
			return Services.allExcept(services);
		}	
		else
			return Services.specialized(new ServiceAccess(clientAccessEntity.clientServiceName, clientAccessEntity.clientServiceClass, clientAccessEntity.clientServiceIdentifier));
	}

	@Override
	public boolean isRewritable() {
		return true;
	}
	

	@Override
	public String toString() {
		return "ServicePolicyEntity [clientAccessEntity=" + clientAccessEntity
				+ ", excludes=" + excludes + "]";
	}




	@Embeddable public static class ServiceAccessEntity{

		@Column(nullable=true)
		protected String clientServiceClass;

		@Column(nullable=true)
		protected String clientServiceName;

		@Column(nullable=true)
		protected String clientServiceIdentifier;

		@Override
		public String toString() {
			return clientServiceClass + ":"+ clientServiceName + ":"+ clientServiceIdentifier;
		}
		
		
	}
}
