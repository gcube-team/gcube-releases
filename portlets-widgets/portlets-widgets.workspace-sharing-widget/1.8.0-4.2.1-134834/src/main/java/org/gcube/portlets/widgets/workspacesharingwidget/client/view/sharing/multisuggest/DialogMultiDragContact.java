/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.resources.Resources;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 27, 2015
 *
 */
public class DialogMultiDragContact extends Dialog{
	
	private static final String GROUP_DRAGGING_CONTACTS = "Group dragging contacts";
	private static final int HEIGHT_DIALOG = 542;
	private static final int WIDTH_DIALOG = 620;
	
	private String txtHelp = "Drag one or more contacts from the left (All Contacts) to the right (Share with) to add users in your sharing list.";
	private String titleHelp = "Group dragging action";
	
	private boolean hideOnPressOkButton;
	private MultiDragContact multiDragContact;

	private ToolBar toolBar;
	private String headTitle;
	private boolean visibleTextArea;
	private LayoutContainer lcTop = new LayoutContainer();
	private LayoutContainer lcMiddle = new LayoutContainer();
	private LayoutContainer lcBottom = new LayoutContainer();
	/**
	 * 
	 * @param headTitle
	 * @param leftListContactsTitle
	 * @param rightListContactsTitle
	 * @param visibleAlreadyShared
	 * @param hideOnPressOk
	 */
	public DialogMultiDragContact(String headTitle, String leftListContactsTitle, String rightListContactsTitle, boolean visibleAlreadyShared, boolean hideOnPressOk) {
		this.multiDragContact = new MultiDragContact(leftListContactsTitle, rightListContactsTitle, visibleAlreadyShared);
		this.hideOnPressOkButton = hideOnPressOk;
		this.visibleTextArea = visibleAlreadyShared;
		init();
		setHeadTitle(headTitle);
//		add(multiDragContact);
	}
	
	/**
	 * 
	 * @param visibleAlreadyShared
	 * @param hideOnPressOk
	 */
	public DialogMultiDragContact(boolean visibleAlreadyShared, boolean hideOnPressOk){
		this.multiDragContact = new MultiDragContact(visibleAlreadyShared);
		this.hideOnPressOkButton = hideOnPressOk;
		this.visibleTextArea = visibleAlreadyShared;
		init();
//		add(multiDragContact);
	}
	
	public void setHeadTitle(String headTitle){
		this.headTitle = headTitle;
		
		if(headTitle==null)
			this.setHeading(GROUP_DRAGGING_CONTACTS);
		else
			this.setHeading(headTitle);
	}
	
	
	/**
	 * 
	 * @param bool
	 */
	public void showToolBar(boolean bool){
		this.toolBar.setVisible(bool);
	}
	
	/**
	 * 
	 */
	private void init() {

		setSize(WIDTH_DIALOG, HEIGHT_DIALOG);
		setResizable(false);
		setMaximizable(false);
		setIcon(Resources.getIconUsers());
		setModal(true);
//		setScrollMode(Scroll.AUTOY);

//		setResizable(true);
		setButtonAlign(HorizontalAlignment.CENTER);
		setButtons(Dialog.OKCANCEL);
		
		if(!visibleTextArea)
			setHeight(HEIGHT_DIALOG-60);
		
		toolBar = new ToolBar();
		Button buttonHelp = new Button();
		buttonHelp.setIcon(Resources.getIconInfo());

		buttonHelp.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox.info(titleHelp,
						txtHelp,null);

			}
		});

		toolBar.add(buttonHelp);
		setTopComponent(toolBar);
		
		this.getButtonById(Dialog.CANCEL).addSelectionListener(
				new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						hide();
					}
				});

		this.getButtonById(Dialog.OK).addSelectionListener(
				new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						if(hideOnPressOkButton){
							
							List<InfoContactModel> shareContacts = multiDragContact.getTargetListContact();
							if (shareContacts == null || shareContacts.isEmpty()) {

								MessageBox mbc = MessageBox.confirm(
								"Confirm exit?",
								"You have not selected any contact to share, confirm exit?",
								null);

								mbc.addCallback(new Listener<MessageBoxEvent>() {

									@Override
									public void handleEvent(MessageBoxEvent be) {
										String clickedButton = be.getButtonClicked().getItemId();
										if (clickedButton.equals(Dialog.YES)) {
											hide();
										}
									}
								});
							} else
								hide();
						}
					}
				});
		
		lcMiddle.add(multiDragContact);
		add(lcTop);
		add(lcMiddle);
		add(lcBottom);
		
	}

	/**
	 * 
	 */
	public MultiDragContact getMultiDrag() {
		return multiDragContact;
	}

	public String getHeadTitle() {
		return headTitle;
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
