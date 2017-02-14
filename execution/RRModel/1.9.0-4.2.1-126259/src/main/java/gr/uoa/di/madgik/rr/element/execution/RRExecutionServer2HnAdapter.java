package gr.uoa.di.madgik.rr.element.execution;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.HostingNodeAdapter;
import gr.uoa.di.madgik.rr.element.infra.RRHostingNode2HnAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RRExecutionServer2HnAdapter extends HostingNodeAdapter {

	private static Logger logger = LoggerFactory.getLogger(RRExecutionServer2HnAdapter.class);
	
	@Override
	public HostingNode adapt(Object o) throws Exception {
		logger.info("Calling adapt on RRExecutionServer2HnAdapter on : " + o);
		if(o == null) return null;
		if(!(o instanceof ExecutionServer)) throw new Exception("Cannot adapt object of type " + o.getClass().getName()); 
		ExecutionServer es = (ExecutionServer)o;
		RRHostingNode2HnAdapter ad = new RRHostingNode2HnAdapter();
		return ad.adapt(es.getHostingNode());
	}

}
