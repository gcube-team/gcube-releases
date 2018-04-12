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
 * Filename: DetachablePanel.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.panels;

import java.util.List;
import java.util.Vector;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Commands;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.WidgetsRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.viewport.MainContainer;
import org.gcube.resourcemanagement.support.shared.util.Assertion;
import org.gcube.resourcemanagement.support.shared.exceptions.InvalidParameterException;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

/**
 * Consists of a panel that can be inserted inside a container and
 * provides a functionality to be detached (and attached to it again).
 *
 * The detach button is executed in toggle mode.
 *
 * Once detached a dialog window is created to contain it.
 *
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class DetachablePanel {

	public interface DetachablePanelHandler {
		void onDetachEvent(DetachablePanel container, Component content);
		void onEmbedEvent(DetachablePanel container, Component content);
	}

	private ToolBar toolBar = new ToolBar();
	private LayoutContainer parent = null;
	private boolean modal = true;
	private boolean isDetached = false;
	private ContentPanel rootPanel = null;
	private Button detachButton = null;
	private String title = "";
	private Component mainWidget = null;
	private final List<DetachablePanelHandler> handlers = new Vector<DetachablePanelHandler>();

	/**
	 * Depending on the status of detached panel, the
	 * actual container can be the container in which it is
	 * embedded or a dialog window.
	 */
	private LayoutContainer actualContainer = null;

	/**
	 * A detachable panel is created by specifying the widget
	 * suitable to contain it.
	 * Once detached the panel will be inserted in an ad-hoc dialog
	 * window.
	 * @param container the widget that will embed the panel
	 * @param modal if the detached window must be modal
	 */
	public DetachablePanel(
			final LayoutContainer container,
			final String title,
			final String id,
			final boolean modal)
	throws InvalidParameterException {
		super();
		Assertion<InvalidParameterException> checker = new Assertion<InvalidParameterException>();
		checker.validate(container != null, new InvalidParameterException("The container parameter is null"));
		this.parent = container;
		this.parent.setLayout(new FitLayout());
		this.modal = modal;
		this.title = title;
		this.onLoad();
		this.rootPanel.setId(id);
		this.rootPanel.setHeaderVisible(false);
		this.rootPanel.getHeader().setStyleName("x-hide-panel-header");
		WidgetsRegistry.registerWidget(id, this.rootPanel);
		this.embedWindow();
	}

	/**
	 * Return the panel toolbar.
	 * <p>
	 * <b>Notice</b> that the button for detaching the panel from its parent
	 * must not be removed to keep its functionality.
	 * </p>
	 * @return
	 */
	public final ToolBar getToolBar() {
		return this.toolBar;
	}

	private void onLoad() {
		this.rootPanel = new ContentPanel() {
			@Override
			public void hide() {
				super.hide();
				if (actualContainer != null) {
					actualContainer.hide();
				}
			}
			@Override
			public void show() {
				super.show();
				if (actualContainer != null) {
					actualContainer.show();
				}
			}
		};
		this.initToolBar();
		this.rootPanel.setId("detachable-panel-root");
		this.rootPanel.setTopComponent(this.toolBar);
		this.rootPanel.setLayout(new FitLayout());
		//this.rootPanel.setAutoHeight(true);
		this.rootPanel.setHeight("100%");
		this.rootPanel.remove(this.rootPanel.getHeader());
		this.rootPanel.layout();
	}

	private void initToolBar() {
		this.detachButton = new Button() {
			@Override
			protected void onClick(final ComponentEvent ce) {
				toggleDetachWindow();
			}
		};
		detachButton.setToolTip("Embeds/Detaches the dialog");
		detachButton.setIconStyle("detach-icon");
		detachButton.setEnabled(true);
		toolBar.add(detachButton);
		toolBar.add(new SeparatorToolItem());
	}

	/**
	 * Detaches the console from the main panel and inserts it
	 * inside a new dialog window.
	 * This happens in toggle mode. So further detach will
	 * attach again the console inside the main panel.
	 */
	private void toggleDetachWindow() {
		// Console will be detached and inserted inside
		// a newly created dialog.
		if (!isDetached) {
			this.detachWindow();
			this.onDetach();
		} else {
			this.embedWindow();
			this.onEmbed();
		}

		isDetached = !isDetached;
	}

	/**
	 * Here is implemented the detaching of the panel from its
	 * parent.
	 */
	private void detachWindow() {
		GWT.log("Detaching panel " + this.title);
		this.rootPanel.removeFromParent();

		// The new window that will contain the detached panel
		Dialog dlg = new Dialog();

		// Closes the widgets that initially contained the panel
		parent.disable();
		parent.removeFromParent();

		dlg.setLayout(new FitLayout());
		// Setup the dialog window
		dlg.setHeading(this.title);
		dlg.setClosable(false);
		dlg.setModal(this.modal);
		dlg.setWidth(800);
		dlg.setHeight(500);
		dlg.setResizable(true);
		dlg.getButtonBar().removeAll();
		dlg.add(this.rootPanel);
		//dlg.setScrollMode(Scroll.AUTO);

		actualContainer = dlg;

		// Registers the dialog window
		WidgetsRegistry.registerWidget(this.rootPanel.getId() + "-dlg-detached", dlg);
		dlg.show();
	}

	/**
	 * Embeds the panel inside its parent and closes the detached window.
	 */
	private void embedWindow() {
		GWT.log("Embedding panel " + this.title);
		parent.add(this.rootPanel);
		parent.enable();
		parent.layout(true);

		actualContainer = parent;

		Widget elem = WidgetsRegistry.getWidget(this.rootPanel.getId() + "-dlg-detached");
		if (elem != null) {
			elem.removeFromParent();
		}
		WidgetsRegistry.unregisterWidget(this.rootPanel.getId() + "-dlg-detached");
	}

	public final ContentPanel getRootPanel() {
		return this.rootPanel;
	}

	/**
	 * This method is called every time the panel is detached
	 * from its parent.
	 *
	 * The custom behavior when detached must be defined here.
	 */
	protected final void onDetach() {
		// Forces the refresh of main container
		Commands.refreshViewport();
		this.rootPanel.layout(true);

		for (DetachablePanelHandler handler : this.handlers) {
			handler.onDetachEvent(this, this.mainWidget);
		}
	}

	/**
	 * This method is called once the detached panel is embedded
	 * inside its container.
	 */
	protected final void onEmbed() {
		MainContainer viewport = Commands.getViewport();
		// Re-insert the component inside the viewport
		viewport.add(this.parent);

		// Forces the refresh of main container
		Commands.refreshViewport();
		this.rootPanel.layout(true);

		for (DetachablePanelHandler handler : this.handlers) {
			handler.onEmbedEvent(this, this.mainWidget);
		}
	}

	public final void insertMainWidget(final Component widget) {
		this.mainWidget = widget;
		this.getRootPanel().add(widget);
	}

	public final void layout(final boolean force) {
		this.rootPanel.layout(force);
	}

	public final void addHandler(final DetachablePanelHandler handler) {
		this.handlers.add(handler);
	}

	public final void removeHandler(final DetachablePanelHandler handler) {
		this.handlers.remove(handler);
	}
}
