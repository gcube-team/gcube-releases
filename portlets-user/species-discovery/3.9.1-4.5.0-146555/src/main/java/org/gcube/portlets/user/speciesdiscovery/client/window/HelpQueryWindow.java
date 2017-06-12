package org.gcube.portlets.user.speciesdiscovery.client.window;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.model.QueryModel;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * 
 */
public class HelpQueryWindow extends Dialog {

	private Html helpHtml = new Html();

	public HelpQueryWindow() {

		setButtonAlign(HorizontalAlignment.CENTER);
		setHideOnButtonClick(true);
		setHeading("Query examples");
		setModal(true);
		// setBodyBorder(true);
		setBodyStyle("padding: 9px; background: none");
		setWidth(830);
		setResizable(false);
		setButtons(Dialog.OK);

		/*helpHtml.setHtml("All result items with scientfic name 'Mola mola' in the Obis and GBIF datasources:"
				+ "'Mola mola', 'Abra alba' as ScientificName in Obis, GBIF return *");

		ListStore<QueryModel> employeeList = new ListStore<QueryModel>();
		employeeList.add(getQueries());

		ListView<QueryModel> lView = new ListView<QueryModel>();
		// getTemplate() returns the desired template
		lView.setTemplate(getTemplate());
		lView.setStore(employeeList);*/
		
		helpHtml.setHtml(Resources.INSTANCE.help().getText());

		ContentPanel cp = new ContentPanel();
		cp.setBodyBorder(false);
		cp.setHeaderVisible(false);
		cp.setButtonAlign(HorizontalAlignment.CENTER);
		cp.setLayout(new FitLayout());
		cp.setSize(800, 420);
		cp.add(helpHtml);
		cp.setScrollMode(Scroll.AUTOY);

		add(cp);

		this.show();
	}

	private native String getTemplate() /*-{
		return [
				'<tpl for=".">',
				'<div style="border: 1px solid #DDDDDD;float:left;margin:4px 0 4px  4px; padding:2px;width:480px;">',
				'<div style="color:#1C3C78;font-weight:bold;padding-bottom:5px;padding-top:7px;">{name}</div>',
				'<div style="color:blue;padding-bottom:5px;">Description:<br/>{description}</div>',
				'<div style="color:black;padding-bottom:5px;">Query:<br/>{queryString}</div>',
				'</div>', '</tpl>', '' ].join("");

	}-*/;

	public static List<QueryModel> getQueries() {
		List<QueryModel> listQueries = new ArrayList<QueryModel>();

		listQueries.add(new QueryModel(
						"Example Query by scientific name",
						"All result items with scientfic name 'Mola mola' and 'Abra alba' in the Obis and GBIF datasources",
						"'Mola mola', 'Abra alba' as ScientificName in Obis, GBIF return *"));
		
		

		return listQueries;

	}

}