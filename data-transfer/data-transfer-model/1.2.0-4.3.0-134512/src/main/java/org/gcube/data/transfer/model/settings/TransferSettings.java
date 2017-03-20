package org.gcube.data.transfer.model.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.gcube.data.transfer.model.options.Range;
import org.gcube.data.transfer.model.options.TransferOptions;
import org.gcube.data.transfer.model.options.TransferOptions.Protocol;

@Data
@EqualsAndHashCode
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({HttpDownloadSettings.class,DirectTransferSettings.class})
public abstract class TransferSettings<T extends TransferOptions> {

	public abstract T getOptions();
	
	public abstract Protocol getToUseProtocol();
	
	public abstract Range getToUseRange();
	
}
