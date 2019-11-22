package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class GetShareableLink.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Sep 20, 2019
 */
public class GetShareableLink extends GwtEvent<GetPublicLinkEventHandler> {
  public static Type<GetPublicLinkEventHandler> TYPE = new Type<GetPublicLinkEventHandler>();

  private FileModel targetFile = null;
  private String version;

	/**
	 * Instantiates a new gets the public link event.
	 *
	 * @param target the target
	 * @param version the version
	 */
	public GetShareableLink(FileModel target, String version) {
		this.targetFile = target;
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<GetPublicLinkEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(GetPublicLinkEventHandler handler) {
		handler.onGetPublicLink(this);

	}

	/**
	 * Gets the source file.
	 *
	 * @return the source file
	 */
	public FileModel getSourceFile() {
		return targetFile;
	}


	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {

		return version;
	}
}