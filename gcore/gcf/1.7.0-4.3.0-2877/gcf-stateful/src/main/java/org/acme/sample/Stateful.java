package org.acme.sample;

import org.acme.sample.ServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.VOID;
import org.globus.wsrf.ResourceException;

public class Stateful extends GCUBEPortType {

    @Override
	protected ServiceContext getServiceContext() {return ServiceContext.getContext();}

	public String visit(VOID voidType) throws GCUBEFault {
		ServiceContext sctx = ServiceContext.getContext();
		try {
		    StringBuilder output = new StringBuilder();
			GHNContext nctx = GHNContext.getContext();
			GCUBEStatefulPortTypeContext pctx = StatefulContext.getContext();
		    Resource resource = this.getResource();
		    resource.addVisit();
		    output.append("Hello " + resource.getName()).append(", you have invoked porttype ").
		    	append(pctx.getName() + " \nof service " + sctx.getName()).
		    	append(", \nwhich you found at ").
		    	append(pctx.getEPR() + "in the gCube infrastructure " + nctx.getGHN().getInfrastructure()).	    
		    	append( " \nand you are in the Scope " + sctx.getScope()).
			append(" \nThis is your invocation N." + resource.getVisits() + "\n");
		    resource.store();
			return output.toString();
		    
		}catch (GCUBEException e) {throw e.toFault();}
		catch (Exception e) {
		    throw sctx.getDefaultException(e).toFault();
		}

    }

    /**
     * 
     * @return the stateful resource
     * @throws ResourceException if no resource was found in the current context
     */
    private Resource getResource() throws ResourceException {
    	return (Resource) StatefulContext.getContext().getWSHome().find();
    }
    
    
    
}
