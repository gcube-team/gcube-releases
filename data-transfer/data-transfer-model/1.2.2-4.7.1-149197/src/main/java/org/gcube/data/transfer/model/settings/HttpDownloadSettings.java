package org.gcube.data.transfer.model.settings;

import java.net.URL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.options.Range;
import org.gcube.data.transfer.model.options.TransferOptions.Protocol;


@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class HttpDownloadSettings extends TransferSettings<HttpDownloadOptions> {

	
	
	@XmlElement
	private URL source;
	
	@XmlElement
	private HttpDownloadOptions options;
	
	
	
	@Override
	public Protocol getToUseProtocol() {
		return Protocol.HTTP;
	}
	
	
	@Override
	public HttpDownloadOptions getOptions() {
		return options;
	}

	

	
	@Override
	public Range getToUseRange() {
		return Range.ONLY_80;
	}

}
