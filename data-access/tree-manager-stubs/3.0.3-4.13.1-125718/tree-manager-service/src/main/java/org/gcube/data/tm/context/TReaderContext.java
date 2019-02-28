/**
 * 
 */
package org.gcube.data.tm.context;

import org.gcube.data.tm.Constants;


/**
 * The context of the T-Reader service.
 * 
 * @author Fabio Simeoni
 *
 */
public class TReaderContext extends PortTypeContext {

	/** Singleton instance. */
	protected static TReaderContext singleton = new TReaderContext();

	/** Creates an instance . */
	private TReaderContext(){}
	
	/** Returns a context instance.
	 * @return the context
	 * */
	public static TReaderContext getContext() {
		return singleton;
	}
	
	/**{@inheritDoc}*/
	public String getJNDIName() {
		return Constants.TREADER_NAME;
	}

}
