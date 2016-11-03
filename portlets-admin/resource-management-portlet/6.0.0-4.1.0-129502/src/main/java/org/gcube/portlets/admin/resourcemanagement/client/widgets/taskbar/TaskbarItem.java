/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: IconizablePanel.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.taskbar;

import org.gcube.portlets.admin.resourcemanagement.client.remote.ProxyRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Callbacks;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Commands;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.MainPanel;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.util.Configuration;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

/**
 * @author Massimiliano Assante (ISTI-CNR)
 * @author Daniele Strollo
 *
 */
public class TaskbarItem {
	private MainPanel parent = null;
	private TaskbarButton detachButton = null;
	private TaskbarWindow relatedWidget = null;
	private String resourceID = null;
	private ResourceTypeDecorator type = null;


	private String scope = null;

	public TaskbarItem(
			final String scope,
			final ResourceTypeDecorator type, // optional
			final MainPanel container,
			final String resourceID,
			final String title,
			final String uiComponentID,
			final String buttonIcon) {
		this.parent = container;
		this.type = type;
		this.initUI(title, uiComponentID, buttonIcon);
		this.resourceID = resourceID;
		this.scope = scope;
	}

	private String getScope() {
		return this.scope;
	}

	private void initUI(final String title,	final String id,final String buttonIcon) {
		
		this.detachButton = new TaskbarButton("btn" + id, type, title) {
			protected void onClick(final com.extjs.gxt.ui.client.event.ComponentEvent ce) {
				relatedWidget.doMinimize();
			}
		};
		this.detachButton.setStyleName("taskbar-button");

		this.detachButton.setIconStyle(buttonIcon);
		this.parent.add(this.detachButton, false);

		this.relatedWidget = new TaskbarWindow(this);
		this.relatedWidget.setHeading(title);

		if (Configuration.openProfileOnLoad) {
			this.activate();
		} else {
			this.relatedWidget.setIsMinimized(true);
		}

		Menu mnu = new Menu();
		MenuItem close = new MenuItem("Close") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				relatedWidget.doClose();
			}
		};
		close.setIconStyle("close-icon");
		mnu.add(close);

		MenuItem closeAll = new MenuItem("CloseAll") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				TaskbarRegister.closeAll();
			}
		};
		closeAll.setIconStyle("closeall-icon");
		mnu.add(closeAll);

		if (type != null && type == ResourceTypeDecorator.DeployReport) {
			MenuItem refresh = new MenuItem("Refresh") {
				@Override
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					ProxyRegistry.getProxyInstance().checkDeployStatus(
							getScope(),
							resourceID.trim(),
							Callbacks.handleGetDeploymentReport);
				}
			};
			refresh.setIconStyle("refresh-icon");
			mnu.add(refresh);
		}

		if (type != null &&
				(type == ResourceTypeDecorator.GHN ||
				type == ResourceTypeDecorator.VIEW ||
				type == ResourceTypeDecorator.Collection ||
				type == ResourceTypeDecorator.GenericResource ||
				type == ResourceTypeDecorator.RunningInstance ||
				type == ResourceTypeDecorator.Service)
			) {
			MenuItem refresh = new MenuItem("Refresh") {
				@Override
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					Commands.doGetResourceProfileByID(
							this,
							getScope(),
							resourceID.trim());
				}
			};
			refresh.setIconStyle("refresh-icon");
			mnu.add(refresh);
		}

		this.detachButton.setContextMenu(mnu);
	}

	public final String getResourceID() {
		return this.resourceID;
	}

	public final void destroy() {
		try {
			TaskbarRegister.unregisterTaskbarWidget(this.resourceID);
			this.parent.getContainer().remove(this.detachButton);
			this.relatedWidget.hide();
		} catch (Exception e) {
		}
	}

	protected final void disactivate() {
		this.detachButton.setStyleName("taskbar-button");
	}

	protected final void activate() {
		this.detachButton.setStyleName("taskbar-button-active");
	}

	public final TaskbarWindow getRelatedWindow() {
		return this.relatedWidget;
	}
	
	public ResourceTypeDecorator getType() {
		return type;
	}
}
