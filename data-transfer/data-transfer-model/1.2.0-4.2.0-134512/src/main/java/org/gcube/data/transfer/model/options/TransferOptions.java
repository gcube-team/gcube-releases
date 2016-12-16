package org.gcube.data.transfer.model.options;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@XmlRootElement
@XmlSeeAlso({HttpDownloadOptions.class,DirectTransferOptions.class})
public abstract class TransferOptions {

	public static enum TransferMethod{
		HTTPDownload,
		DirectTransfer
	}

	public static enum Protocol{
		HTTP,
		TCP,
		UDP,
		SMP
	}
	
	
	public abstract TransferMethod getMethod();
			
	
	public abstract List<Protocol> getAvailableProtocols();
	
	
	
	public abstract Range getAvailableRange();
	
	
	
}
