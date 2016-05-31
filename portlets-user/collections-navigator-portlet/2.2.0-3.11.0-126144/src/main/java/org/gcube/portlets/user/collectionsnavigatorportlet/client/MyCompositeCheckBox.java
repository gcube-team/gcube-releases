package org.gcube.portlets.user.collectionsnavigatorportlet.client;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MyCompositeCheckBox extends Composite {

	private MyCheckBox colCheckBox;
	private HorizontalPanel horPanel;
	private HTML html;
	private TreeItem item;
	private static DescPopUp colDescPopup = null;

	public MyCompositeCheckBox(final MyCheckBox colCheck, final String colDesc, boolean isGroup) {
		String cutDesc;
		FlowPanel descPanel = new FlowPanel();

		// create the description popup if it does not exist
		if (colDescPopup == null) {
			colDescPopup = new DescPopUp(true);
			colDescPopup.setWidth("300px");
		}

		this.colCheckBox = colCheck;
		this.colCheckBox.setContainerCheckBox(this);

		if (colDesc.length() <= 71)
			cutDesc = colDesc;
		else
			cutDesc = colDesc.substring(0, 70) + "...";


		HTML colDescHTML = new HTML("<span id=\"colDescription\" style=\"font-style: italic; font-size: smaller\">" + cutDesc + "</span>", true);
		descPanel.add(colDescHTML);

		/* Add a "more" button if the description has been truncated */
		if (isGroup) {
			if (!cutDesc.equals(colDesc)) {
				MyHyperlink moreLink = (new MyHyperlink(colDescHTML, "<span style=\"font-style: italic; font-size: smaller\">(more)</span>",new ClickListener() {
					public void onClick(Widget sender) {
						int left = sender.getAbsoluteLeft() + sender.getOffsetWidth();
						int top =  sender.getAbsoluteTop() + sender.getOffsetHeight();
						colDescPopup.setPopupPosition(left, top);
						colDescPopup.clear();
						HTML collectionInformation = new HTML("<ul><li>Description: " + getCheckBox().getAltText() +  "</li></ul>");
						colDescPopup.addDock(getCheckBox().getTitle(),collectionInformation);
						colDescPopup.show();
					}
				}));				
			}
		}
		else {			
			MyHyperlink moreLink = (new MyHyperlink(colDescHTML, "<span style=\"font-style: italic; font-size: smaller\">(more)</span>",new ClickListener() {
				public void onClick(Widget sender) {
					int left = sender.getAbsoluteLeft() + sender.getOffsetWidth();
					int top =  sender.getAbsoluteTop() + sender.getOffsetHeight();
					colDescPopup.setPopupPosition(left, top);
					colDescPopup.clear();

					String s = "<ul>";
					if (getCheckBox().getAltText().trim().length() > 0)
						s += "<li>Description: " + getCheckBox().getAltText() + "</li>";
					s += "<li>Number of records: " + getCheckBox().getRecNo() + "</li><li>Creation Date: " + getCheckBox().getCreationDate() + "</li></ul>";
					HTML collectionInformation = new HTML(s);
					colDescPopup.addDock(getCheckBox().getTitle(),collectionInformation);
					colDescPopup.show();
				}
			}));
		}

		VerticalPanel panel = new VerticalPanel();
		horPanel = new HorizontalPanel();
		String label = colCheck.getHTML();
		colCheck.setHTML("");
		colCheck.addClickHandler( new ClickHandler(){
			public void onClick(ClickEvent arg0) {
				colCheck.doClick();
			}
		});

		horPanel.add(colCheck);
		html = new HTML(label,true);
		html.addClickHandler( new ClickHandler(){
			public void onClick(ClickEvent arg0) {
				colCheck.setValue(!colCheck.getValue());
				colCheck.doClick();
			}
		});
		html.setStyleName("hand");
		horPanel.add(html);

		panel.add(horPanel);
		panel.add(descPanel);

		initWidget(panel);
	}

	public void setTreeItem(TreeItem item) { this.item = item; }
	public MyCheckBox getCheckBox() { return colCheckBox; }

	/**
	 * Select or unselect a given collection in the tree.
	 * @param colID the ID of the collection to select/unselect
	 * @param select true to select the collection, or false to unselect
	 */
	public void selectCollection(String colID, boolean select) {
		colCheckBox.selectCollection(colID, select);
	}

	/**
	 * Opens or closes the tree item corresponding to this composite checkbox,
	 * so that its children are displayed or hidden.
	 * @param open the desired state (open or closed)
	 */
	public void setItemState(boolean open) {
		item.setState(open, true);
	}

	public static class DescPopUp extends GCubeDialog {
		public DescPopUp(boolean autoHide) {
			super(autoHide);
		}

		public void onClick(Widget sender) {
			hide();
		}

		public void addDock(String title,HTML collectionInformation)
		{
			setText(title);
			add(collectionInformation);
		}
	}
}
