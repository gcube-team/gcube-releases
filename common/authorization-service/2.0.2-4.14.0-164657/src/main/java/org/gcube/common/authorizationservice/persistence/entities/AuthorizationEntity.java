package org.gcube.common.authorizationservice.persistence.entities;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.ExternalServiceInfo;
import org.gcube.common.authorization.library.provider.ServiceIdentifier;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.authorizationservice.persistence.entities.ServicePolicyEntity.ServiceAccessEntity;
import org.gcube.common.authorizationservice.persistence.entities.converters.StringListConverter;
import org.gcube.common.authorizationservice.util.Constants;
import org.jboss.weld.exceptions.IllegalArgumentException;

@Entity
@Inheritance
@DiscriminatorColumn(name="EntryType")
@Table(name="Authorizations")
@NamedQueries({
	@NamedQuery(name="Authz.get", query="SELECT DISTINCT info FROM AuthorizationEntity info WHERE  "
		+ " info.token=:token"),
		@NamedQuery(name="Authz.getQualifiers", query="SELECT DISTINCT info FROM AuthorizationEntity info WHERE  "
				+ " info.id.qualifier!='"+Constants.DEFAULT_TOKEN_QUALIFIER+"' AND info.id.clientId=:clientId AND info.id.context=:context"),
	@NamedQuery(name="Authz.getByToken", query="SELECT DISTINCT info FROM AuthorizationEntity info WHERE  "
			+ " info.token=:token"),
	@NamedQuery(name="Authz.getGeneratedTokenByClientId", query="SELECT DISTINCT info FROM AuthorizationEntity info WHERE  "
			+ " (info.id.clientId=:clientid OR info.generatedBy=:clientid) AND info.id.context=:context")
})
public abstract class AuthorizationEntity {

	
	private static final String DEFAULT_GENERATOR ="default";
	
	@EmbeddedId 
	AuthorizationId id;
	
	@Column(unique=true)
	String token;
	
	@Column
	String generatedBy = DEFAULT_GENERATOR;
	
	@Transient
	ClientInfo info;
	
	@Embedded
	private ClientInfoEntity internalInfo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column
	Calendar creationTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=true)
	Calendar lastTimeUsed;
	
	@Column(name="EntryType")
	String entryType;
			
	protected AuthorizationEntity() {
		super();
	}

	public AuthorizationEntity(String token, String context, ClientInfo clientInfo, String qualifier, String entryType ) {
		super();
		this.id = new AuthorizationId(context, clientInfo.getId(), qualifier);
		this.token = token;
		this.entryType = entryType;
		this.creationTime = Calendar.getInstance();
		this.info = clientInfo;
		this.internalInfo = retriveInternalInfo(this.info);
	}
	
	public AuthorizationEntity(String token, String context, ClientInfo clientInfo, String qualifier, String entryType, String generatedBy ) {
		this(token, context, clientInfo, qualifier, entryType);
		this.generatedBy = generatedBy;
	}

	public String getToken() {
		return this.token;
	}

	public String getContext() {
		return id.context;
	}
	
	public String getClientId() {
		return id.clientId;
	}
	
	public ClientInfo getInfo() {
		if (this.info == null)
			this.info = retieveInfo();
		 
		return this.info;
	}
	
	public String getEntryType() {
		return entryType;
	}
	
	public String getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}

	public String getQualifier(){
		return id.qualifier;
	}
		
	public Calendar getLastTimeUsed() {
		return lastTimeUsed;
	}

	public void setLastTimeUsed(Calendar lastTimeUsed) {
		this.lastTimeUsed = lastTimeUsed;
	}

	private ClientInfo retieveInfo() {
		switch (entryType) {
		case EntityConstants.USER_AUTHORIZATION:
			return new UserInfo(this.internalInfo.identifier, this.internalInfo.roles);
		case EntityConstants.SERVICE_AUTHORIZATION:
			return new ServiceInfo(new ServiceIdentifier(this.internalInfo.service.clientServiceClass, this.internalInfo.service.clientServiceName, this.internalInfo.service.clientServiceIdentifier));
		case EntityConstants.EXTERNAL_SERVICE_AUTHORIZATION:
			return	new ExternalServiceInfo(this.internalInfo.identifier, this.internalInfo.generatedBy);		
		case EntityConstants.CONTAINER_AUTHORIZATION:
			return new ContainerInfo(this.internalInfo.host, this.internalInfo.port);
		default:
			throw new IllegalArgumentException("invalid entity type");
		}
		
	}
	
	private ClientInfoEntity retriveInternalInfo(ClientInfo info2) {
		switch (entryType) {
		case EntityConstants.USER_AUTHORIZATION:
			return ClientInfoEntity.forUser(info.getId(), info.getRoles());
		case EntityConstants.SERVICE_AUTHORIZATION:
			ServiceIdentifier identifier = ((ServiceInfo) this.info).getServiceIdentifier();
			return ClientInfoEntity.forService(identifier.getServiceClass(), identifier.getServiceName(), identifier.getServiceId());
		case EntityConstants.EXTERNAL_SERVICE_AUTHORIZATION:
			ExternalServiceInfo externalServiceInfo = (ExternalServiceInfo) this.info;
			return ClientInfoEntity.forExternalService(externalServiceInfo.getId(), externalServiceInfo.getGeneratedBy());
		case EntityConstants.CONTAINER_AUTHORIZATION:
			ContainerInfo containerInfo = (ContainerInfo) this.info;
			return ClientInfoEntity.forContainer(containerInfo.getHost(), containerInfo.getPort());
		default:
			throw new IllegalArgumentException("invalid entity type");
		}
	}	
	
	@Override
	public String toString() {
		return "AuthorizationEntity [id=" + id + ", token=" + token
				+ ", creationTime=" + creationTime + ", entryType=" + entryType
				+ "]";
	}
	
	@Embeddable
	private static class ClientInfoEntity {
				
		// userInfo
		@Column(name="info_identifier")
		private String identifier;
		
		@Column(name="info_roles", length=2000)
		@Convert(converter=StringListConverter.class)
		private List<String> roles;
		
		//serviceInfo
		@Embedded
		private ServiceAccessEntity service;
		
		//external service info
		@Column(name="info_generatedby")
		private String generatedBy;
		
		//containerInfo
		@Column(name="info_host")
		private String host;
		@Column(name="info_port")
		private int port;
		
		protected static ClientInfoEntity forExternalService(String identifier, String generatedBy){
			ClientInfoEntity entry =new ClientInfoEntity();
			entry.generatedBy= generatedBy;
			entry.identifier = identifier;
			return entry;
		}
		
		protected static ClientInfoEntity forService(String serviceClass, String serviceName, String serviceId){
			ClientInfoEntity entry =new ClientInfoEntity();
			ServiceAccessEntity service = new ServiceAccessEntity();
			service.clientServiceClass = serviceClass;
			service.clientServiceName = serviceName;
			service.clientServiceIdentifier = serviceId;
			entry.service = service;
			return entry;
			
		}
		
		protected static ClientInfoEntity forUser(String identifier, List<String> roles){
			ClientInfoEntity entry =new ClientInfoEntity();
			entry.roles= roles;
			entry.identifier = identifier;
			return entry;
		}
		
		protected static ClientInfoEntity forContainer(String host, int port){
			ClientInfoEntity entry =new ClientInfoEntity();
			entry.host = host;
			entry.port = port;
			return entry;
		}
		
	}
}
