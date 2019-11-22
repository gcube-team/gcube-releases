package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.user;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.resources.Resources;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

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
 * 
 * @author Giancarlo Panichi
 *
 */
public class DialogMultiDragWorkspaceContacts extends Dialog {

	private static final String GROUP_DRAGGING_CONTACTS = "Group dragging contacts";

	private static final int HEIGHT_DIALOG = 580;
	private static final int WIDTH_DIALOG = 620;

	private String txtHelp = "Select a VRE and after drag one or more contacts from the left (All Contacts) to the right (Share with) to add users in your sharing list.";
	private String titleHelp = "Group dragging action";

	// private boolean hideOnPressOkButton=false;
	private PanelMultiDragWorkspaceContact multiDragContactPanel;
	// private boolean visibleTextArea=false;

	private ToolBar toolBar;

	private LayoutContainer lcTop = new LayoutContainer();
	private LayoutContainer lcMiddle = new LayoutContainer();
	private LayoutContainer lcBottom = new LayoutContainer();

	private InfoContactModel owner;
	private List<InfoContactModel> targetContact;

	public DialogMultiDragWorkspaceContacts(InfoContactModel owner, List<InfoContactModel> targetContact) {

		GWT.log("DialogMultiDragWorkspaceContacts()");
		try {
			this.owner = owner;
			this.targetContact = targetContact;
			init();
			create();
		} catch (Throwable e) {
			GWT.log("Error in DialogMultiDragWorkspaceContacts()" + e.getLocalizedMessage(), e);
		}

	}

	public void showToolBar(boolean bool) {
		this.toolBar.setVisible(bool);
	}

	private void init() {
		GWT.log("DialogMultiDragWorkspaceContacts init");
		setHeading(GROUP_DRAGGING_CONTACTS);
		setId(DialogMultiDragWorkspaceContacts.class.getName() + Random.nextInt());
		setSize(WIDTH_DIALOG, HEIGHT_DIALOG);
		setResizable(false);
		setMaximizable(false);

		setIcon(Resources.getIconUsers());
		setModal(true);

	}

	private void create() {
		GWT.log("DialogMultiDragWorkspaceContacts create");
		// Configure Toolbar
		GWT.log("DialogMultiDragWorkspaceContacts Configure Toolbar");
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

		//
		GWT.log("DialogMultiDragWorkspaceContacts Configure Button");
		setButtonAlign(HorizontalAlignment.CENTER);
		setButtons(Dialog.OKCANCEL);
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

		this.multiDragContactPanel = new PanelMultiDragWorkspaceContact(owner, targetContact);
		lcMiddle.add(multiDragContactPanel);
		add(lcTop);
		add(lcMiddle);
		add(lcBottom);
	}

	public PanelMultiDragWorkspaceContact getMultiDrag() {
		return multiDragContactPanel;
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
