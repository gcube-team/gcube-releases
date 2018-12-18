package org.acme.sample;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.common.core.utils.logging.GCUBELog;

public class Factory extends GCUBEPortType {

	GCUBELog logger = new GCUBELog(this);

	@Override
	protected ServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	public EndpointReferenceType create(String name) throws GCUBEFault {
		// create/reuse the resource
		try {
			GCUBEStatefulPortTypeContext ptcxt = StatefulContext.getContext();
			GCUBEWSHome home = ptcxt.getWSHome();
			GCUBEWSResourceKey key = ptcxt.makeKey(name);
			GCUBEWSResource ws = home.create(key, name);
			ws.store();
			return ws.getEPR();

		} catch (Exception e) {
			logger.error("unable to logon", e);
			throw new GCUBEUnrecoverableException(e).toFault();
		}
	}
}
