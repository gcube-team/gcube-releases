package org.gcube.data.transfer.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.transfer.model.utils.DateWrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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
	
	@XmlElement
	private Map<String,ExecutionReport> executionReports;
	
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
			DateWrapper submissionTime,String destinationFileName,String message, Map<String,ExecutionReport> executionReports) {
		super(request.getId(), request.getSettings(),request.getDestinationSettings(),request.getPluginInvocations());
		this.status = status;
		this.transferredBytes = transferredBytes;
		this.percent = percent;
		this.averageTransferSpeed = averageTransferSpeed;
		this.submissionTime = submissionTime;
		this.destinationFileName=destinationFileName;
		this.executionReports=executionReports;
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
		this.executionReports=new HashMap<String,ExecutionReport>();
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransferTicket other = (TransferTicket) obj;
		if (averageTransferSpeed != other.averageTransferSpeed)
			return false;
		if (destinationFileName == null) {
			if (other.destinationFileName != null)
				return false;
		} else if (!destinationFileName.equals(other.destinationFileName))
			return false;
		
		
		//Check reports map
		if ((executionReports == null || executionReports.isEmpty()))
			if(other.executionReports!=null&& (!other.executionReports.isEmpty()))
				return false;
			else if(executionReports!=null && (!executionReports.isEmpty()))
				if(other.executionReports==null || other.executionReports.isEmpty())
					return false;
				else if(executionReports.size()!=other.executionReports.size())
					return false;
				else for(String key:executionReports.keySet())
					if(!other.executionReports.containsKey(key)||
							(!executionReports.get(key).equals(other.executionReports.get(key))))
						return false;
		
		
		
		
		
		
		
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (Double.doubleToLongBits(percent) != Double.doubleToLongBits(other.percent))
			return false;
		if (status != other.status)
			return false;
		if (submissionTime == null) {
			if (other.submissionTime != null)
				return false;
		} else if (!submissionTime.equals(other.submissionTime))
			return false;
		if (transferredBytes != other.transferredBytes)
			return false;
		return true;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (averageTransferSpeed ^ (averageTransferSpeed >>> 32));
		result = prime * result + ((destinationFileName == null) ? 0 : destinationFileName.hashCode());
		
		if(executionReports!=null){
			for(Entry<String,ExecutionReport> entry:executionReports.entrySet())
				result=prime*result+(entry.getKey().hashCode()+entry.getValue().hashCode());
		}
		
		result = prime * result + ((executionReports == null) ? 0 : executionReports.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		long temp;
		temp = Double.doubleToLongBits(percent);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((submissionTime == null) ? 0 : submissionTime.hashCode());
		result = prime * result + (int) (transferredBytes ^ (transferredBytes >>> 32));
		return result;
	}
	
 
	
}
