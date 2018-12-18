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
 * Filename: TaskbarWindow.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.taskbar;

import java.util.ArrayList;

import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.resourcemanagement.support.shared.util.Configuration;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class TaskbarWindow extends Window {
	private TaskbarItem relatedItem = null;
	private boolean isMinimized = false;
	
	public static ArrayList<Integer> extractedColors = new ArrayList<Integer>();

	protected TaskbarWindow(final TaskbarItem relatedItem) {
		super();
		this.setModal(false);
		this.setClosable(false);
		this.setLayout(new FitLayout());
		this.relatedItem = relatedItem;

		this.setWidth(800);
		this.setHeight(500);

		this.getHeader().addTool(new ToolButton("x-tool-minimize", new SelectionListener<IconButtonEvent>() {
			@Override
			public void componentSelected(final IconButtonEvent ce) {
				doMinimize();
			}
		}));
		/* removed the close functionality from the window. only the button can be closed
		this.getHeader().addTool(new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() {
			@Override
			public void componentSelected(final IconButtonEvent ce) {
				doClose();
			}
		}));
		 */
		if (Configuration.openProfileOnLoad) {
			this.show();
		}
	}

	protected final void doClose() {
		ConsoleMessageBroker.trace(this, "Closing taskbar item: " + this.relatedItem.getResourceID());
		this.relatedItem.destroy();
		this.hide();
	}

	protected final void doMinimize() {
		if (this.isMinimized) {
			if (!Configuration.allowMultipleProfiles) {
				TaskbarRegister.minimizeAll();
			}
			this.show();
			this.relatedItem.activate();
		} else {
			this.hide();
			this.relatedItem.disactivate();
		}
		this.isMinimized = !isMinimized;
	}
	
	public final void setIsMinimized(boolean minimized) {
		this.isMinimized = minimized;
	}
	
	public final boolean isMinimized() {
		return this.isMinimized;
	}

	public final void setMainWidget(final Component widget) {
		this.removeAll();
		this.add(widget);
		this.layout(true);
	}
}
