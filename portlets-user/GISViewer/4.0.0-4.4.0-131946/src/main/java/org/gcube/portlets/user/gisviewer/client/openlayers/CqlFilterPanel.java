package org.gcube.portlets.user.gisviewer.client.openlayers;

import java.util.List;

import org.gcube.portlets.user.gisviewer.client.commons.beans.CQLQueryObject;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.commons.beans.Property;
import org.gcube.portlets.user.gisviewer.client.resources.Images;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.KeyCodes;

public class CqlFilterPanel {
	private LayerItem layerItem;
	private Dialog dialog = null;
	private TextField<String> filterTextField;
	private Button executeQueryButton;
	private Button removeFilterButton;
	private CqlFilterHandler cqlFilterHandler;

	public CqlFilterPanel(CqlFilterHandler _cqlFilterHandler) {
		this.cqlFilterHandler = _cqlFilterHandler;
		
		dialog = new Dialog();
		dialog.setHeading("CQL Filter");  
		dialog.setButtons(Dialog.CLOSE);  
		//dialog.setBodyStyleName("pad-text");  
		dialog.addText("");  
		dialog.setModal(true);
		dialog.setScrollMode(Scroll.AUTO);  
		dialog.setHideOnButtonClick(true);  
		dialog.addListener(Events.Close, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				dialog.hide();
			}
		});
		
		// init text field
		filterTextField = new TextField<String>();
		filterTextField.setWidth(300);
		filterTextField.addKeyListener(new KeyListener(){
			@Override
			public void componentKeyDown(ComponentEvent event) {
				super.componentKeyDown(event);
				if (event.getKeyCode() == KeyCodes.KEY_ENTER)
					execQuery();
			}
		});
		
		// init execute query button
		executeQueryButton = new Button("", Images.iconExecuteQuery(), new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				execQuery();
			}
		});
		executeQueryButton.setSize(25, 25);
		executeQueryButton.setToolTip("Execute Query");

		// init remove filter button
		removeFilterButton = new Button("", Images.iconRemoveCqlFilter(), new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				cqlFilterHandler.removeCqlFilter(layerItem);
				dialog.hide();
			}
		});
		removeFilterButton.setSize(25, 25);
		removeFilterButton.setToolTip("Remove Filter");
	}
	
	
	public void open(LayerItem layerItem, int x, int y) {
		this.layerItem = layerItem;
		String cqlFilter = layerItem.getCqlFilter();

		dialog.removeAll();
		dialog.setHeading("CQL Filter on " + layerItem.getTitle());
		dialog.add(new Label("<br/>Current filter: <b>"+(cqlFilter.equals("") ? "none" : cqlFilter)+"</b>"+"<br/><br/>"));
		
		List<Property> properties = layerItem.getProperties();
		if (properties!=null && properties.size()>0) {
			HorizontalPanel hp = new HorizontalPanel();
			String str = "";
			for (Property p: properties)
				str += (p==properties.get(0) ? "" : ", ") + p.getName();
			Label l = new Label("Fields: ");
			l.setStyleAttribute("font-size", "12px");
			hp.add(l);
			hp.add(new Html("<div class=\"cqlPropertiesText\"><nobr>"+str+"<nobr></div>"));
			dialog.add(hp);
		}
			
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new Label("CQL Filter: "));
		hp.add(filterTextField);
		hp.add(executeQueryButton);
		hp.add(removeFilterButton);
		dialog.add(hp);
		
		dialog.setPagePosition(x, y);
		dialog.setWidth(450);
		//dialog.setSize(450, 180);
		dialog.setAutoHeight(true);
		dialog.show();
	};

	private void execQuery() {
		CQLQueryObject cqlQueryObject = new CQLQueryObject();
		cqlQueryObject.setCqlQuery(filterTextField.getValue());
		cqlFilterHandler.setCQLFilter(layerItem, cqlQueryObject.getCqlQuery());
		dialog.hide();
	}
}
