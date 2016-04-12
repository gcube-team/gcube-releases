package org.gcube.common.core.informationsystem.client;


/**
 * Defines the minimal behaviour of queries accepted by {@link ISClient ISClients}:
 * they contain their textual expression and specify a time-to-live for their results.
 * 
 * <p>The definition is parametric with respect to the type of the expected results.
 * This is not required for the interface, though it may be for its implementations.
 * In addition, it allows static typing of the {@link ISClient} interface.
 * 
 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
 *
 * @param <RESULT> the type of the expected results.
 */
public interface ISQuery<RESULT> {

	   /**
	    * Returns the time-to-live of the results of the query.
	    * @return the time-to-live.
	    */
	   public long getTTL();

	   /**
	    * Sets the time-to-live of the results of the query.
	    * @param ttl the time-to-live.
	    */
	   public void setTTL(long ttl);

	   /**
	    * Returns the textual expression of the query.
	    * @return the expression.
	    */
	   public String getExpression();

	   /**
	    * Sets the textual expression of the query.
	    * @param exp the expression.
	    */
	   public void setExpression(String exp);
	  
}
