/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.view.gcubeitem;

import java.util.Map;

import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.resources.WorkspaceExplorerResources;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class DialogShowGcubeItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 17, 2016
 *
 * Shows a Dialog with gcube items related to WorkspaceItemId.
 */
public class DialogShowGcubeItem extends DialogBox implements ClickHandler {

	private DockPanel dock = new DockPanel();
	private Button yesButton;
	private ScrollPanel spCenterContainer;
	private ImageResource loading = WorkspaceExplorerResources.ICONS.loading();
	private HorizontalPanel hpButtons = new HorizontalPanel();
	public int ccMaxHeigth = 300;
	public int ccMaxWidth = 580;

	/**
	 * Instantiates a new dialog show gcube items related to WorkspaceItemId.
	 *
	 * @param caption the caption
	 * @param text the text
	 * @param item the worksapce item id
	 * @param autoHide the auto hide
	 */
	public DialogShowGcubeItem(String caption, String text, Item item, boolean autoHide) {
		getElement().setClassName("gwt-DialogBoxNew");
		dock.setSpacing(4);
		dock.setWidth("100%");
//		dock.getElement().getStyle().setProperty("maxWidth", "590px");

		String title = Util.ellipsis(caption, 70, false);
		setText(title);
		setTitle(caption);
		setAutoHideEnabled(autoHide);

		yesButton = new Button("Ok");
		yesButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		spCenterContainer = new ScrollPanel();
		spCenterContainer.getElement().getStyle().setProperty("maxHeight", ccMaxHeigth+"px");
		spCenterContainer.getElement().getStyle().setProperty("maxWidth", ccMaxWidth+"px");
		spCenterContainer.add(new HTML(text));
		hpButtons = new HorizontalPanel();
		hpButtons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hpButtons.getElement().getStyle().setMarginTop(10.0, Unit.PX);
//		hpButtons.getElement().getStyle().setMarginBottom(10.0, Unit.PX);
//		hpButtons.setSpacing(3);
//		yesButton.getElement().getStyle().setMarginRight(20.0, Unit.PX);
		hpButtons.add(yesButton);

		dock.add(hpButtons, DockPanel.SOUTH);
		dock.setCellHorizontalAlignment(hpButtons, DockPanel.ALIGN_CENTER);

		dock.add(spCenterContainer, DockPanel.CENTER);
		setWidget(dock);


		/*if(item.getGcubeProperties()!=null){
			FlexTable table = buildGcubeItemView(item.getGcubeProperties());
			addToCenterPanel(table);

		}else{*/
			showLoader("Loading Gcube Properties...");
			WorkspaceExplorerConstants.workspaceNavigatorService.getGcubePropertiesForWorspaceId(item.getId(), new AsyncCallback<Map<String,String>>() {

				@Override
				public void onSuccess(Map<String, String> result) {
					FlexTable table = buildGcubeItemView(result);
					clearCenterContainer();
					addToCenterPanel(table);
				}

				@Override
				public void onFailure(Throwable caught) {
					clearCenterContainer();
				}
			});
		//}
	}

	/**
	 * Builds the gcube item view.
	 *
	 * @param map the map
	 * @return the flex table
	 */
	private FlexTable buildGcubeItemView(Map<String, String> map){

		FlexTable table = new FlexTable();
		table.setWidth("100%");
//		table.setHeight("50px");

		if(map.size()==0){
			table.setHTML(0, 0, "No properties");
			return table;
		}

		table.addStyleName("gwt-DialogBoxNew-table");
		table.setHTML(0, 0, "N.");
		table.setHTML(0, 1, "Key");
		table.setHTML(0, 2, "Value");
		table.getRowFormatter().getElement(0).addClassName("gwt-DialogBoxNew-table-header");

		int index = 1;
		GWT.log("Properties are: "+map.keySet().size());
		for (String key : map.keySet()) {
			String value = map.get(key);
			table.setHTML(index, 0, index+"");
			table.setHTML(index, 1, key);
			table.setHTML(index, 2, value);
			index++;

		}
		return table;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
	 * .dom.client.ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
//		hide();
	}



	/**
	 * Show loader.
	 *
	 * @param message the message
	 */
	private void showLoader(String message){
		spCenterContainer.clear();
		HorizontalPanel hpMask = new HorizontalPanel();
		hpMask.add(new Image(loading));
		HTML html = new HTML(message);
		html.getElement().getStyle().setMarginLeft(5, Unit.PX);
		hpMask.add(html);
		spCenterContainer.add(hpMask);
	}


	/**
	 * Clear center container.
	 */
	private void clearCenterContainer(){
		try{
			spCenterContainer.clear();
		}catch(Exception e){}
	}

	/**
	 * Adds the to center panel.
	 *
	 * @param w the w
	 */
	public void addToCenterPanel(Widget w) {
		spCenterContainer.add(w);
	}

	/**
	 * Gets the dock.
	 *
	 * @return the dock
	 */
	public DockPanel getDock() {
		return dock;
	}

	/**
	 * Gets the yes button.
	 *
	 * @return the yes button
	 */
	public Button getYesButton() {
		return yesButton;
	}
}