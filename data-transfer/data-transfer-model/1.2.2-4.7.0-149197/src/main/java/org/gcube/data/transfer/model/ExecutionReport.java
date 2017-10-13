package org.gcube.data.transfer.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.transfer.model.TransferTicket.Status;
import org.gcube.data.transfer.model.utils.DateWrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ExecutionReport {

	@XmlEnum
	public static enum ExecutionReportFlag{
		SUCCESS,
		WRONG_PARAMETER,
		UNABLE_TO_EXECUTE,
		FAILED_EXECUTION,
		FAILED_CLEANUP
	}
	
	@XmlElement
	private PluginInvocation invocation;
	@XmlElement
	private String message;
	@XmlElement
	private ExecutionReportFlag flag;
	
}
