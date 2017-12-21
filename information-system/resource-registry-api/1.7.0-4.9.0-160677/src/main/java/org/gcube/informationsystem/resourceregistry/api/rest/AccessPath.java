/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api.rest;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AccessPath {
	
	public static final String ACCESS_PATH_PART = "access";
	
	public static final String QUERY_PARAM = "query";
	public static final String LIMIT_PARAM = "limit";
	public static final String FETCH_PLAN_PARAM = "fetchPlan";
	
	public static final int UNBOUNDED = -1;
	public static final int DEFAULT_LIMIT = 20;
	
	public static final String FACET_PATH_PART = ERPath.FACET_PATH_PART;
	public static final String RESOURCE_PATH_PART = ERPath.RESOURCE_PATH_PART;
	
	public static final String EMBEDDED_PATH_PART = ERPath.EMBEDDED_PATH_PART;
	
	public static final String CONSISTS_OF_PATH_PART = ERPath.CONSISTS_OF_PATH_PART;
	public static final String IS_RELATED_TO_PATH_PART = ERPath.IS_RELATED_TO_PATH_PART;
	
	public static final String CONTEXT_PATH_PART = ContextPath.CONTEXT_PATH_PART;
	public static final String ALL_PATH_PART = ContextPath.ALL_PATH_PART;
	
	public static final String SCHEMA_PATH_PART = SchemaPath.SCHEMA_PATH_PART;
	public static final String INSTANCE_PATH_PART = "instance";
	
	public static final String INSTANCES_PATH_PART = "instances";
	
	public static final String POLYMORPHIC_PARAM = "polymorphic";
	
	public static final String REFERENCE = "reference";
	public static final String DIRECTION = "direction";

	public static final String RESOURCE_INSTANCES_PATH_PART = "resourceInstances";
	
	public static final String RELATION_TYPE_PATH_PART = "relationType";
	public static final String FACET_TYPE_PATH_PART = "facetType";
	
}
