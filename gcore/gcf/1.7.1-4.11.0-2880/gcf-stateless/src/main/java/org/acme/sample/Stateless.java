/**
 * 
 */
package org.acme.sample;

import org.acme.sample.stubs.SampleFault;
import org.gcube.common.core.contexts.GCUBEPortTypeContext;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * @author Fabio Simeoni
 *
 */
public class Stateless extends GCUBEPortType {

	private static GCUBELog logger = new GCUBELog(Stateless.class);
	
	public String about(String name) throws GCUBEFault, SampleFault {
		
		StringBuilder output = new StringBuilder();		
		GHNContext nctx = GHNContext.getContext();
		ServiceContext sctx = ServiceContext.getContext();
		GCUBEPortTypeContext pctx = StatelessContext.getContext();
		try {
			output.append("Hello "+name).append(", you have invoked porttype ").
			append(pctx.getName()+" of service "+sctx.getName()).append(", which you found ").
			append (" on the GHN "+nctx.getGHNID()).
			append(" at "+pctx.getEPR()+" in the gCube infrastructure "+nctx.getGHN().getInfrastructure());
		}
		
		catch(GCUBEException e) {
			logger.error("Problem in about():",e);
			throw e.toFault();}
		catch(Exception e) {
			logger.error("Problem in about()",e);
			throw sctx.getDefaultException("Problem in about()", e).toFault();}
		
		return output.toString();
	}
	
	/**{@inheritDoc}*/
	@Override
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
}
