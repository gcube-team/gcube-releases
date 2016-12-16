/**
 * 
 */
package org.gcube.dataaccess.spql.model;

import java.util.List;

import org.gcube.dataaccess.spql.model.error.QueryError;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface CheckableElement {
	
	List<QueryError> check(); 

}
