package org.gcube.datatransfer.scheduler.library.fws;

import static javax.jws.soap.SOAPBinding.ParameterStyle.BARE;
import static org.gcube.datatransfer.scheduler.library.fws.Constants.*;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.gcube.datatransfer.scheduler.library.obj.SchedulerObj;
import org.gcube.datatransfer.scheduler.library.outcome.CallingSchedulerResult;


@WebService(name=porttypeSchedulerLocalName,targetNamespace=porttypeSchedulerNS)
public interface SchedulerServiceJAXWSStubs {
	
	@SOAPBinding(parameterStyle=BARE)
	public String storeInfoScheduler(String msg);
	
	@SOAPBinding(parameterStyle=BARE)
	public String cancelScheduledTransfer(String msg);
	
	@SOAPBinding(parameterStyle=BARE)
	public String monitorScheduledTransfer(String msg);
	
	@SOAPBinding(parameterStyle=BARE)
	public String getScheduledTransferOutcomes(String msg);
	
}

