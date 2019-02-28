package org.gcube.resources.discovery.client.queries.api;



/**
 * A query for resources.
 * <p>
 * The interface is intended for clients that consume queries and are not otherwise concerned with their construction.
 *
 */
public interface Query {

	   /**
	    * Returns the textual expression of the query.
	    * @return the expression.
	    */
	   public String expression();
	  
}
