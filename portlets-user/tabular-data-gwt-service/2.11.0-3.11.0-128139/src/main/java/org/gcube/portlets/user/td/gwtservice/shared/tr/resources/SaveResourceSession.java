package org.gcube.portlets.user.td.gwtservice.shared.tr.resources;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.destination.Destination;
import org.gcube.portlets.user.td.widgetcommonevent.shared.mime.MimeTypeSupport;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SaveResourceSession implements Serializable {

	private static final long serialVersionUID = 2282498402676932983L;

	private ResourceTDDescriptor resourceTDDescriptor;
	private MimeTypeSupport mime;
	private Destination destination;
	private String itemId;
	private String fileName;
	private String fileDescription;

	public SaveResourceSession() {
		super();

	}

	public SaveResourceSession(ResourceTDDescriptor resourceTDDescriptor,
			MimeTypeSupport mime, Destination destination, String itemId,
			String fileName, String fileDescription) {
		super();
		this.resourceTDDescriptor = resourceTDDescriptor;
		this.mime = mime;
		this.destination = destination;
		this.itemId = itemId;
		this.fileName = fileName;
		this.fileDescription = fileDescription;
	}

	public ResourceTDDescriptor getResourceTDDescriptor() {
		return resourceTDDescriptor;
	}

	public void setResourceTDDescriptor(
			ResourceTDDescriptor resourceTDDescriptor) {
		this.resourceTDDescriptor = resourceTDDescriptor;
	}

	public MimeTypeSupport getMime() {
		return mime;
	}

	public void setMime(MimeTypeSupport mime) {
		this.mime = mime;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

	@Override
	public String toString() {
		return "SaveResourceSession [resourceTDDescriptor="
				+ resourceTDDescriptor + ", mime=" + mime + ", destination="
				+ destination + ", itemId=" + itemId + ", fileName=" + fileName
				+ ", fileDescription=" + fileDescription + "]";
	}

}
