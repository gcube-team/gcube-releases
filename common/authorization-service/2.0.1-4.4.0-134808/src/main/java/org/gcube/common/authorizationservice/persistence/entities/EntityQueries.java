package org.gcube.common.authorizationservice.persistence.entities;

public class EntityQueries {

	public static final String SERVICE_POLICY_GET ="SELECT DISTINCT p FROM ServicePolicyEntity p LEFT JOIN FETCH p.excludes ae WHERE  "
			+ " p.context=:context AND (( p.excludeType=org.gcube.common.authorizationservice.persistence.entities.ExcludeType.NOTEXCLUDE " +
			" AND (( p.clientAccessEntity.clientServiceClass = null AND p.clientAccessEntity.clientServiceName=null AND p.clientAccessEntity.clientServiceIdentifier=null) OR" +
			" ( p.clientAccessEntity.clientServiceClass = :serviceClass AND p.clientAccessEntity.clientServiceName=null AND p.clientAccessEntity.clientServiceIdentifier=null) OR" +
			" ( p.clientAccessEntity.clientServiceClass = :serviceClass AND p.clientAccessEntity.clientServiceName=:serviceName AND p.clientAccessEntity.clientServiceIdentifier=null) OR" +
			" ( p.clientAccessEntity.clientServiceClass = :serviceClass AND p.clientAccessEntity.clientServiceName=:serviceName AND p.clientAccessEntity.clientServiceIdentifier=:identifier)))" +
			" OR ( p.excludeType=org.gcube.common.authorizationservice.persistence.entities.ExcludeType.EXCLUDE  AND NOT(( ae.clientServiceClass = null AND ae.clientServiceName=null AND ae.clientServiceIdentifier=null) OR" +
			" ( ae.clientServiceClass = :serviceClass AND ae.clientServiceName=null AND ae.clientServiceIdentifier=null) OR" +
			" ( ae.clientServiceClass = :serviceClass AND ae.clientServiceName=:serviceName AND ae.clientServiceIdentifier=null) OR" +
			" ( ae.clientServiceClass = :serviceClass AND ae.clientServiceName=:serviceName AND ae.clientServiceIdentifier=:identifier))))";
	
	
	
	
	public static final String USER_POLICY_GET = "SELECT policy FROM UserPolicyEntity policy WHERE  "
			+ " policy.context=:context AND (( policy.type=org.gcube.common.authorization.library.policies.UserEntity$UserEntityType.ROLE AND policy.identifier in :rolesList) " +
			"OR (policy.type=org.gcube.common.authorization.library.policies.UserEntity$UserEntityType.USER AND policy.identifier=:user ) OR " +
			" ( policy.type=org.gcube.common.authorization.library.policies.UserEntity$UserEntityType.ROLE AND policy.excludes not in :rolesList) OR" +
			" (policy.type=org.gcube.common.authorization.library.policies.UserEntity$UserEntityType.USER AND :user not in (policy.excludes)))";
	
	
	/*
	 OR (SELECT count(*) FROM p.excludes ae where (( ae.clientServiceClass = null AND ae.clientServiceName=null AND ae.clientServiceIdentifier=null) OR" +
			" ( ae.clientServiceClass = :serviceClass AND ae.clientServiceName=null AND ae.clientServiceIdentifier=null) OR" +
			" ( ae.clientServiceClass = :serviceClass AND ae.clientServiceName=:serviceName AND ae.clientServiceIdentifier=null) OR" +
			" ( ae.clientServiceClass = :serviceClass AND ae.clientServiceName=:serviceName AND ae.clientServiceIdentifier=:identifier))) 
	 */
}
