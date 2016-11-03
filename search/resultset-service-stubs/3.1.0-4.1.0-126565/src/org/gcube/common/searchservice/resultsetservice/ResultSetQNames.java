package org.gcube.common.searchservice.resultsetservice;
 
import javax.xml.namespace.QName;
//hello new version
/**
 * The Qualified names used by the service
 * 
 * @author UoA
 */
public interface ResultSetQNames {
	/**
	 * The service namespace
	 */
	public static final String NS = "http://gcube.org/namespaces/common/searchservice/ResultSetService";
	/**
	 * The service resource proeperties
	 */
	public static final QName RESOURCE_PROPERTIES = new QName(NS,"ResultSetResourceProperties");
	/**
	 * The service resource reference
	 */
	public static final QName RESOURCE_REFERENCE = new QName(NS,"ResultSetResourceReference");
	/**
	 * The ResultSet resource
	 */
	public static final QName RP_RESULTSET = new QName(NS,"ResultSet");
}
