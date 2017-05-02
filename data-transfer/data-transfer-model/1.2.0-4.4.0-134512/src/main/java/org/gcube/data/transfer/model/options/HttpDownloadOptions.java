package org.gcube.data.transfer.model.options;

import java.util.Arrays;
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
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class HttpDownloadOptions extends TransferOptions {

	
	public static final HttpDownloadOptions DEFAULT=new HttpDownloadOptions(Range.ONLY_80);
	
	@XmlElement
	private Range range;
	
	@Override
	public TransferMethod getMethod() {
		return TransferOptions.TransferMethod.HTTPDownload;
	}

	
	@Override
	public List<Protocol> getAvailableProtocols() {
		return Arrays.asList(new Protocol[]{
			Protocol.HTTP	
		});
	}

	
	
	@Override
	public Range getAvailableRange() {
		return range;
	}

}
