package org.gcube.data.transfer.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.gcube.data.transfer.model.utils.DateWrapper;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TransferTicket extends TransferRequest{
	
	
	@XmlEnum
	public static enum Status{
		PENDING,
		WAITING,		// transfer ready, waiting for sender
		TRANSFERRING,
		SUCCESS,
		ERROR,
		STOPPED,
		PLUGIN_EXECUTION
	}
	
	@XmlElement
	private Status status;
	@XmlElement
	private long transferredBytes;
	@XmlElement
	private double percent;
	@XmlElement
	private long averageTransferSpeed;
	@XmlElement	
	private DateWrapper submissionTime;
	@XmlElement
	private String destinationFileName;
	@XmlElement
	private String message;
	
//	
//	public TransferTicket(String id, TransferOptions options, Status status,
//			long transferredBytes, double percent, long averageTransferSpeed,
//			DateWrapper submissionTime,String destinationFileName) {
//		super(id, options);
//		this.status = status;
//		this.transferredBytes = transferredBytes;
//		this.percent = percent;
//		this.averageTransferSpeed = averageTransferSpeed;
//		this.submissionTime = submissionTime;
//		this.destinationFileName=destinationFileName;
//	}
	
	public TransferTicket(TransferRequest request, Status status,
			long transferredBytes, double percent, long averageTransferSpeed,
			DateWrapper submissionTime,String destinationFileName,String message) {
		super(request.getId(), request.getSettings(),request.getDestinationSettings(),request.getPluginInvocations());
		this.status = status;
		this.transferredBytes = transferredBytes;
		this.percent = percent;
		this.averageTransferSpeed = averageTransferSpeed;
		this.submissionTime = submissionTime;
		this.destinationFileName=destinationFileName;
	}

	
	public TransferTicket(TransferRequest request){
		super(request.getId(),request.getSettings(),request.getDestinationSettings(),request.getPluginInvocations());
		this.status=Status.PENDING;
		this.transferredBytes=0l;
		this.averageTransferSpeed=0l;
		this.destinationFileName="/dev/null";
		this.percent=0d;
		this.submissionTime=DateWrapper.getInstance();	
		this.message="";
	}
	

	
}
