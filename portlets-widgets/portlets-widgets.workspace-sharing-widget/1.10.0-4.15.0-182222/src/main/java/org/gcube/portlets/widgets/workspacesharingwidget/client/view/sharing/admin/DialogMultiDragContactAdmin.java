/**
 *
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.admin;

import org.gcube.portlets.widgets.workspacesharingwidget.client.resources.Resources;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;

/**
 * @author Francesco Mangiacrapa Jan 27, 2015
 *
 */
public class DialogMultiDragContactAdmin extends Dialog {

	private static final String GROUP_DRAGGING_CONTACTS = "Group dragging contacts";
	private static final int HEIGHT_DIALOG = 542;
	private static final int WIDTH_DIALOG = 620;

	private String txtHelp = "Select a VRE and after drag one or more contacts from the left (All Contacts) to the right (Administrators) to add users in the administrators list.";
	private String titleHelp = "Group dragging action";

	// private boolean hideOnPressOkButton=false;
	private PanelMultiDragContactAdmin multiDragContactAdmin;
	// private boolean visibleTextArea=false;

	private ToolBar toolBar;

	private LayoutContainer lcTop = new LayoutContainer();
	private LayoutContainer lcMiddle = new LayoutContainer();
	private LayoutContainer lcBottom = new LayoutContainer();
	private String workspaceItemId;

	public DialogMultiDragContactAdmin(String workspaceItemId) {

		GWT.log("DialogMultiDragContactAdmin(): " + workspaceItemId);
		try {
			this.workspaceItemId=workspaceItemId;
			init();
			create();
		} catch (Throwable e) {
			GWT.log("Error in DialogMultiDragContactAdmin()" + e.getLocalizedMessage(), e);
		}
	}

	public void showToolBar(boolean bool) {
		this.toolBar.setVisible(bool);
	}

	private void init() {
		setHeading(GROUP_DRAGGING_CONTACTS);

		setId(DialogMultiDragContactAdmin.class.getName() + Random.nextInt());
		setSize(WIDTH_DIALOG, HEIGHT_DIALOG);
		setResizable(false);
		setMaximizable(false);
		setIcon(Resources.getIconUsers());
		setModal(true);
		// setScrollMode(Scroll.AUTOY);

	}

	private void create() {

		// setResizable(true);
		setButtonAlign(HorizontalAlignment.CENTER);
		setButtons(Dialog.OKCANCEL);

		toolBar = new ToolBar();
		Button buttonHelp = new Button();
		buttonHelp.setIcon(Resources.getIconInfo());

		buttonHelp.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox.info(titleHelp, txtHelp, null);

			}
		});

		toolBar.add(buttonHelp);
		setTopComponent(toolBar);

		this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});

		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});

		this.multiDragContactAdmin = new PanelMultiDragContactAdmin(workspaceItemId);
		lcMiddle.add(multiDragContactAdmin);
		add(lcTop);
		add(lcMiddle);
		add(lcBottom);

	}

	public PanelMultiDragContactAdmin getMultiDrag() {
		return multiDragContactAdmin;
	}

	public String getTxtHelp() {
		return txtHelp;
	}

	public String getTitleHelp() {
		return titleHelp;
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	public void setTxtHelp(String txtHelp) {
		this.txtHelp = txtHelp;
	}

	public void setTitleHelp(String titleHelp) {
		this.titleHelp = titleHelp;
	}

	public void setToolBar(ToolBar toolBar) {
		this.toolBar = toolBar;
	}

	public LayoutContainer getLcTop() {
		return lcTop;
	}

	public LayoutContainer getLcMiddle() {
		return lcMiddle;
	}

	public LayoutContainer getLcBottom() {
		return lcBottom;
	}

}
