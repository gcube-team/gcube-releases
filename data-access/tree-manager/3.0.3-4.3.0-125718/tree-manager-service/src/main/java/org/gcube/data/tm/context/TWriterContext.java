/**
 * 
 */
package org.gcube.data.tm.context;

import org.gcube.data.tm.Constants;


/**
 * 
 * The context of the T-Writer port-type.
 * 
 * @author Fabio Simeoni
 *
 */
public class TWriterContext extends PortTypeContext {

	/** Singleton instance. */
	protected static TWriterContext singleton = new TWriterContext();

	/** Creates an instance . */
	private TWriterContext(){}
	
	/** Returns a context instance.
	 * @return the context
	 * */
	public static TWriterContext getContext() {
		return singleton;
	}
	
	/**{@inheritDoc}*/
	public String getJNDIName() {
		return Constants.TWRITER_NAME ;
	}


}
