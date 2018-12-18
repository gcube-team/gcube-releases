package org.gcube.informationsystem.resourceregistry.api.rest;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AccessPath {
	
	public static final String TYPE_PATH_PARAM = "TYPE_NAME";
	public static final String UUID_PATH_PARAM = "UUID";
	public static final String CONTEXT_UUID_PATH_PARAM = "CONTEXT_UUID";
	
	
	public static final String ACCESS_PATH_PART = "access";
	public static final String CONTEXTS_PATH_PART = ContextPath.CONTEXTS_PATH_PART;
	public static final String TYPES_PATH_PART = TypePath.TYPES_PATH_PART;
	public static final String INSTANCES_PATH_PART = InstancePath.INSTANCES_PATH_PART;
	
	
	public static final String QUERY_PATH_PART = "query";
	
	public static final String QUERY_PARAM = "q";
	
	public static final String LIMIT_PARAM = "limit";
	public static final int UNBOUNDED = -1;
	public static final int DEFAULT_LIMIT = 20;
	
	public static final String FETCH_PLAN_PARAM = "fetchPlan";
	public static final String DEFAULT_FETCH_PLAN = "*:-1";
	
	public static final String RESOURCE_TYPE_PATH_PART = "RESOURCE_TYPE_NAME";
	public static final String RELATION_TYPE_PATH_PART = "RELATION_TYPE_NAME";
	public static final String REFERENCE_TYPE_PATH_PART = "REFERENCE_TYPE_NAME";
	
	public static final String REFERENCE_PARAM = "reference";
	public static final String POLYMORPHIC_PARAM = "polymorphic";
	public static final String DIRECTION_PARAM = "direction";
	
	public static final String CURRENT_CONTEXT = "CURRENT_CONTEXT";
	
}
