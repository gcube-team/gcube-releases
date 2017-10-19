package org.gcube.portlets.user.databasesmanager.client.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.databasesmanager.client.datamodel.SubmitQueryData;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;

//Form that the is used from the user to insert data useful for submit a query
public class GxtFormSubmitQuery extends LayoutContainer {
	// private VerticalPanel vp;
	private FormPanel form;
	private FormData formData;
	// query for the field TextArea
	private String inputQuery;

	// GWT logger
	private static Logger rootLogger = Logger.getLogger("GxtFormSubmitQuery");

	public GxtFormSubmitQuery(String q) {
		inputQuery = q;
		this.setLayout(new FitLayout());
		// this.setSize(600, 400);
		formData = new FormData("-20");
		// vp = new VerticalPanel();
		// vp.setSpacing(10);
		createLayout();
		// add(vp);

	}

	public GxtFormSubmitQuery() {
		this.setLayout(new FitLayout());
		// this.setSize(600, 400);
		formData = new FormData("-20");
		// vp = new VerticalPanel();
		// vp.setSpacing(10);
		createLayout();
		// add(vp);
	}

	private void createLayout() {
		form = new FormPanel();
		// form.setHeading("Submit Query");
		form.setHeaderVisible(false);
		// form.setFrame(true);
		// form.setWidth(350);
		// form.setWidth(350);
		// form.setHeight(250);

		// Query field
		TextArea query = new TextArea();
		query.setHeight(300);
		query.setPreventScrollbars(false);
		query.setFieldLabel("Query");
		if (inputQuery != null) {
			query.setValue(inputQuery);
		}
		// query.setHeight(50);
		// query.setWidth(50);
		// query.setAllowBlank(false);
		query.getFocusSupport().setPreviousId(form.getButtonBar().getId());
		form.add(query, formData);

		// Read-Only button
		// CheckBoxGroup readOnlyQuery = new CheckBoxGroup();
		// readOnlyQuery.setFieldLabel("Read Only Query");

		// CheckBox checkRO = new CheckBox();
		// checkRO.setFieldLabel("Read Only Query");
		// checkRO.setValue(true);
		// form.add(checkRO, formData);

		// Smart Correction button
		// CheckBoxGroup smartCorrection = new CheckBoxGroup();
		// smartCorrection.setFieldLabel("Apply Smart Correction");

		CheckBox checkSC = new CheckBox();
		checkSC.setFieldLabel("Apply Smart Corrections");
		checkSC.setValue(false);

		// smartCorrection.add(checkSC);
		// form.add(smartCorrection, formData);
		form.add(checkSC, formData);

		// SQL Dialect ComboBox
		//
		// ListStore<SQLDialect> store = new ListStore<SQLDialect>();
		// List<SQLDialect> elements = new ArrayList<SQLDialect>();
		//
		// load data to display in comboBox
		// elements = SQLDialect.loadData();
		// store.add(elements);
		//
		// ComboBox<SQLDialect> combo = new ComboBox<SQLDialect>();
		// combo.setFieldLabel("Language");
		// combo.setDisplayField("name");
		// combo.setTriggerAction(TriggerAction.ALL);
		// combo.setValue(store.getAt(0));
		// combo.setStore(store);
		// form.add(combo, formData);

		// vp.add(form);
		this.add(form);
	}

	// get the query
	private String getQuery() {
		String query = ((TextArea) form.getWidget(0)).getValue();
		// print check
		rootLogger.log(Level.SEVERE, "query: " + query);
		return query;
	}

	// get the smart correction value
	private boolean getSmartCorrectionValue() {
		// CheckBox checkSC = ((CheckBox) form.getWidget(2));
		CheckBox checkSC = ((CheckBox) form.getWidget(1));
		// print check
		rootLogger.log(Level.SEVERE, "smartcorrection: " + checkSC.getValue());
		return (checkSC.getValue());
	}

	// get data for submit a query
	public SubmitQueryData getSubmitQueryData() {
		layout(true);
		SubmitQueryData data = new SubmitQueryData();
		data.setQuery(this.getQuery());
		// TO REMOVE
		// data.setReadOnlyQuery(this.getReadOnlyQueryValue());
		data.setSmartCorrection(this.getSmartCorrectionValue());
		// TO REMOVE
		// data.setLanguage(this.getLanguage());
		return data;
	}

	// private String getLanguage() {
	// ComboBox<SQLDialect> combo = ((ComboBox<SQLDialect>) form.getWidget(3));
	// SQLDialect item = combo.getValue();
	// System.out.println("GxtFormSubmitQuery->language: " + item.getName());
	// return item.getName();
	// }

	// private boolean getReadOnlyQueryValue() {
	// CheckBox checkRO = ((CheckBox) form.getWidget(1));
	// System.out.println("GxtFormSubmitQuery->readonlyquery: "
	// + checkRO.getValue());
	// return (checkRO.getValue());
	// }
}
