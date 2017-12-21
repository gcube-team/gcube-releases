package org.gcube.portlets.widgets.workspacesharingwidget.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Icons extends ClientBundle {

	@Source("readonly.png")
	ImageResource readonly();

	@Source("writeown.png")
	ImageResource writeown();

	@Source("writeall.png")
	ImageResource writeall();

	@Source("admin.png")
	ImageResource administrator();

	@Source("users.png")
	ImageResource users();

	@Source("info-icon.png")
	ImageResource info();

	@Source("share.png")
	ImageResource share();

	/**
	 * @return
	 */
	@Source("shareuser.png")
	ImageResource user();

	/**
	 * @return
	 */
	@Source("sharegroup.png")
	ImageResource group();

	/**
	 * @return
	 */
	@Source("aoneleft.png")
	ImageResource selectedLeft();

	/**
	 * @return
	 */
	@Source("aoneright.png")
	ImageResource selectedRight();

	/**
	 * @return
	 */
	@Source("amoreleft.png")
	ImageResource allLeft();

	/**
	 * @return
	 */
	@Source("amoreright.png")
	ImageResource allRight();
}
