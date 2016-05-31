package org.gcube.data.transfer.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.gcube.data.transfer.model.settings.DirectTransferSettings;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;
import org.gcube.data.transfer.model.settings.TransferSettings;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TransferRequest{

	
	@XmlID
	private String id;
	@XmlElementRefs({
		@XmlElementRef(type = DirectTransferSettings.class),
		@XmlElementRef(type = HttpDownloadSettings.class),
	})
	private TransferSettings settings;
	
}
