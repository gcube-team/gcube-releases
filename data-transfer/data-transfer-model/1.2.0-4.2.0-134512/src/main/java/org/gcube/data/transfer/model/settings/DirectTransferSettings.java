package org.gcube.data.transfer.model.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.gcube.data.transfer.model.options.DirectTransferOptions;
import org.gcube.data.transfer.model.options.Range;
import org.gcube.data.transfer.model.options.TransferOptions.Protocol;


@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class DirectTransferSettings extends TransferSettings<DirectTransferOptions> {

	
	
	@XmlElement
	private DirectTransferOptions options;
	
	
	@Override
	public Range getToUseRange() {
		return Range.ONLY_80;
	}
	
	@Override
	public DirectTransferOptions getOptions() {
		return options;
	}

	@Override
	public Protocol getToUseProtocol() {
		// TODO Auto-generated method stub
		return null;
	}


}
