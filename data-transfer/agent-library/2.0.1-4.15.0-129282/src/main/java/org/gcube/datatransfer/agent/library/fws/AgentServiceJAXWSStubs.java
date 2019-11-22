package org.gcube.datatransfer.agent.library.fws;

import static javax.jws.soap.SOAPBinding.ParameterStyle.BARE;
import static javax.jws.soap.SOAPBinding.ParameterStyle.WRAPPED;
import static org.gcube.datatransfer.agent.library.fws.Constants.porttypeLocalName;
import static org.gcube.datatransfer.agent.library.fws.Constants.porttypeNS;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.datatransfer.common.agent.Types.CancelTransferMessage;
import org.gcube.datatransfer.common.agent.Types.CreateTreeSourceMsg;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.agent.Types.StartTransferMessage;


@WebService(name=porttypeLocalName,targetNamespace=porttypeNS)
public interface AgentServiceJAXWSStubs {
	
	@SOAPBinding(parameterStyle=BARE)
	public String monitorTransfer(String s);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String startTransfer(StartTransferMessage req);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String cancelTransfer(CancelTransferMessage req);


	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public MonitorTransferReportMessage monitorTransferWithProgress(String s);


	@SOAPBinding(parameterStyle=BARE)
	public String getTransferOutcomes(String s);
	
	
	@SOAPBinding(parameterStyle=BARE)
	public String getLocalSources(String s);
	
	@SOAPBinding(parameterStyle=BARE)
	public String createTreeSource(CreateTreeSourceMsg msg);
	
	@SOAPBinding(parameterStyle=BARE)
	public String getTreeSources(String reader_or_writer_TYPE);
	
	@SOAPBinding(parameterStyle=BARE)
	public String removeGenericResource(String id);
}

