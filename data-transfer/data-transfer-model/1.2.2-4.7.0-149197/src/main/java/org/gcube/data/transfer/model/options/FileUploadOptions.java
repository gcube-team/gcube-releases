package org.gcube.data.transfer.model.options;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class FileUploadOptions extends TransferOptions{

	private static final List<Protocol> protocol=Collections.singletonList(Protocol.HTTP);
	
	
	@Override
	public TransferMethod getMethod() {
		return TransferMethod.FileUpload;
	}

	@Override
	public List<Protocol> getAvailableProtocols() {
		return protocol;
	}

	@Override
	public Range getAvailableRange() {
		return Range.ONLY_80;
	}

	
	
}
