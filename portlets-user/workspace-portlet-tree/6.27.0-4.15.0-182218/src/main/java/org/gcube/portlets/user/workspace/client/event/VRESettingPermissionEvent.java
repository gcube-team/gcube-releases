package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Mar 14, 2014
 *
 */
public class VRESettingPermissionEvent extends GwtEvent<VRESettingPermissionEventHandler> {
	public static Type<VRESettingPermissionEventHandler> TYPE = new Type<VRESettingPermissionEventHandler>();

	private FileModel targetFile = null;

	public VRESettingPermissionEvent(FileModel target) {
		this.targetFile = target;
	}

	@Override
	public Type<VRESettingPermissionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(VRESettingPermissionEventHandler handler) {
		handler.onPermissionSetting(this);

	}

	public FileModel getSourceFile() {
		return targetFile;
	}
}