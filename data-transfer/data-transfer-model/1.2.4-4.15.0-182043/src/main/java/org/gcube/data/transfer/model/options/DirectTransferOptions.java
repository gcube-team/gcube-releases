package org.gcube.data.transfer.model.options;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
public class DirectTransferOptions extends TransferOptions {

	@XmlElement
	private String source;
	
	@Override
	public List<Protocol> getAvailableProtocols() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Range getAvailableRange() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public TransferMethod getMethod() {
		return TransferMethod.DirectTransfer;
	}

}
