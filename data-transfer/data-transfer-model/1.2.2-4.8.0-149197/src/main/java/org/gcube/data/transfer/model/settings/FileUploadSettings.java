package org.gcube.data.transfer.model.settings;

import java.io.InputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.transfer.model.options.FileUploadOptions;
import org.gcube.data.transfer.model.options.Range;
import org.gcube.data.transfer.model.options.TransferOptions.Protocol;

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
public class FileUploadSettings extends TransferSettings<FileUploadOptions> {

	private InputStream passedStream;
	
	@XmlElement
	private FileUploadOptions options;
	
	@Override
	public FileUploadOptions getOptions() {
		return options;
	}

	@Override
	public Protocol getToUseProtocol() {
		return Protocol.HTTP;
	}

	@Override
	public Range getToUseRange() {
		return Range.ONLY_80;
	}

}
