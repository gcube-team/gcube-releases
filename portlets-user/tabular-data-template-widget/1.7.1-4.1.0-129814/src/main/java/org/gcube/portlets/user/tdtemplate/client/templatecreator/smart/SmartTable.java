package org.gcube.portlets.user.tdtemplate.client.templatecreator.smart;


import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;

import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 27, 2014
 *
 */
public class SmartTable extends LayoutContainer {

//	private final static String BASE_STYLE = "smartButton";

	/**
	 * 
	 */
	private static final String SMART_TABLE = "SmartTable";

	private HorizontalPanel hp = new HorizontalPanel();

	private Image delete = new Image(TdTemplateAbstractResources.INSTANCE.delete());

//	private HTML text = new HTML();
	
	public SmartTable(ColumnDefinitionView view, AbstractImagePrototype img) {
		hp.setStyleName(SMART_TABLE);
		FlexTable flex = new FlexTable();
		flex.setCellSpacing(2);
		flex.setWidget(0, 0, img.createImage());
		flex.setWidget(0, 1, new Label("Rule"));

		TdTColumnCategory cat = view.getSelectedColumnCategory();
		TdTDataType dt = view.getSelectedDataType();

		flex.setWidget(1, 0, new Label("Category:"));
		flex.setWidget(1, 1, new Label(cat.getName()));
		flex.setWidget(2, 0, new Label("Data Type:"));
		flex.setWidget(2, 1, new Label(dt.getName()));
		
//		String caption = "Column Type as";
//		
//		caption+= "\nCategory: "+cat.getName();
//		caption+="\nData Type: "+dt.getName();
//
//		text.setHTML("<div style=\"width: 90%; line-height: 32px; text-align:left; padding: 5px;\">" +
//				"<span style=\"display:inline-block; vertical-align:middle;\" >" + img.getHTML() + "</span>" +
//				"<span style=\"padding-left: 20px;\">"+ caption+"</span></div>");
//
//		hp.add(text);
		
		hp.add(flex);
		
		add(hp);

	}

	public SmartTable(ColumnDefinitionView view, AbstractImagePrototype img, boolean isDeletable) {
		this(view, img);

		delete.getElement().getStyle().setOpacity(0.6);
		delete.getElement().getStyle().setMarginTop(3, com.google.gwt.dom.client.Style.Unit.PX);
		hp.add(delete);
		delete.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				deleteFolder();				
			}
		});
		
		delete.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				delete.getElement().getStyle().setOpacity(0.6);				
			}
		});
		
		delete.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				delete.getElement().getStyle().setOpacity(0.9);
			}
		});
	}
	
	private void deleteFolder() {
//		TdTemplateController.getInternalBus().fireEvent(event);
	}

	private void deselectOthers() {
//		caller.toggleOthers(this);
	}
}

