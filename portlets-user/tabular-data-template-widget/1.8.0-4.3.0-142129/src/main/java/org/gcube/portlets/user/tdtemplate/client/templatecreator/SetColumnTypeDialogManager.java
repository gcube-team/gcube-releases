/**
 *
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.locale.SelectableSimpleComboBox;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.BaloonPanel;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external.DialogCodelistResolver;
import org.gcube.portlets.user.tdtemplate.shared.SPECIAL_CATEGORY_TYPE;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;
import org.gcube.portlets.user.tdtemplate.shared.TdTFormatReference;
import org.gcube.portlets.user.tdtemplate.shared.TdTTimePeriod;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 30, 2014
 *
 */
public class SetColumnTypeDialogManager {

	private static final String CHOOSE_LOCALE = "Choose Locale";

	private SimpleComboBox<String> scbCategory = new SimpleComboBox<String>();

//	private SimpleComboBox<String> scbDataType = new SimpleComboBox<String>();

	private ComboBox<TdTDataType> scbDataType = new ComboBox<TdTDataType>();

	private ComboBox<TdTFormatReference> scbDataTypeFormat = new ComboBox<TdTFormatReference>();

	private SimpleComboBox<String> scbReferenceTable = new SimpleComboBox<String>();

	private SimpleComboBox<String> scbReferenceColumn = new SimpleComboBox<String>();

	private SelectableSimpleComboBox<String> scbLocales = new SelectableSimpleComboBox<String>();

	private SimpleComboBox<String> scbPeriodTypes = new SimpleComboBox<String>();

	private ComboBox<TdTFormatReference> scbPeriodTypeValueFormats = new ComboBox<TdTFormatReference>();

	private String currentLocaleSelect;

	private List<TdTColumnCategory> listCategory;

	private SetTypeColumnContainer typingContainer;

	private SetColumnTypeDialogManager INSTANCE = this;

	private List<ColumnData> lsColumnData = null;

	private BaloonPanel baloon;

//	private DialogCodelistResolver resolver;

	private TdTemplateController templateController;

	private String tabularResourceName;

	/**
	 *
	 */
	public SetColumnTypeDialogManager(List<TdTColumnCategory> columnCategories, TdTemplateController controller) {
		this.listCategory = columnCategories;
		this.templateController = controller;
//		scbDataType.setEnabled(false);
		initComboSetCategory();
		initComboSetDataType();
		initComboLocales();
		initComboPeriodTypes();
		initComboReferenceTable();
		initComboPeriodTypesValueFormats();
		initComboDataTypeFormat();

		scbCategory.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {

				scbDataType.reset();
				scbReferenceColumn.reset();
				scbReferenceTable.reset();
				scbLocales.reset();
				scbPeriodTypes.reset();
				scbPeriodTypeValueFormats.reset();
				scbDataType.setVisible(true);
				scbDataTypeFormat.setVisible(true);
				typingContainer.removeCombo(scbLocales, "scbLocales");
				typingContainer.removeCombo(scbReferenceTable, "scbReferenceTable");
				typingContainer.removeCombo(scbDataTypeFormat, "scbDataTypeFormat");
				typingContainer.layout(true);

				scbDataType.removeListener(Events.OnClick, listenerDimension);

				selectDataType();

				if(scbCategory.getSimpleValue().compareToIgnoreCase(SPECIAL_CATEGORY_TYPE.DIMENSION.getLabel())==0){
//					scbDataType.setEnabled(true);
					categoryDimensionHandler();
					scbDataType.setVisible(false);
					scbDataTypeFormat.setVisible(false);
//					scbDataTypeFormat.setVisible(false);
					typingContainer.removeCombo(scbPeriodTypes, "scbPeriodTypes");
					typingContainer.removeCombo(scbPeriodTypeValueFormats, "scbPeriodTypeValueFormats");
				}else if(scbCategory.getSimpleValue().compareToIgnoreCase(SPECIAL_CATEGORY_TYPE.TIMEDIMENSION.getLabel())==0){
//					scbDataType.setEnabled(true);
					categoryTimeDimensionHandler();
					scbDataType.setVisible(false);
					scbDataTypeFormat.setVisible(false);
//					scbDataTypeFormat.setVisible(false);
					typingContainer.removeCombo(scbReferenceColumn, "scbReferenceColumn");
				}else if(scbCategory.getSimpleValue().compareToIgnoreCase(SPECIAL_CATEGORY_TYPE.CODENAME.getLabel())==0){
					categoryCodeNameHandler();
					typingContainer.removeCombo(scbReferenceColumn, "scbReferenceColumn");
					typingContainer.removeCombo(scbPeriodTypes, "scbPeriodTypes");
					typingContainer.removeCombo(scbPeriodTypeValueFormats, "scbPeriodTypeValueFormats");
				}else{ //NORMAL BEHAVIOUR
					typingContainer.removeCombo(scbReferenceColumn, "scbReferenceColumn");

					//TIMEDIMENSION
					typingContainer.removeCombo(scbPeriodTypes, "scbPeriodTypes");
					typingContainer.removeCombo(scbPeriodTypeValueFormats, "scbPeriodTypeValueFormats");

				}
			}
		});


		scbDataType.addSelectionChangedListener(new SelectionChangedListener<TdTDataType>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<TdTDataType> se) {

//				typingContainer.removeCombo(scbDataTypeFormat);

				//ONLY IF IS A VALID SELECTION CHANGE
				TdTDataType dataTypeSelected = getSelectedDataType();

				if(dataTypeSelected!=null && dataTypeSelected.getFormatReferenceIndexer()!=null){

					scbDataTypeFormat.reset();
					scbDataTypeFormat.getStore().removeAll();
					ArrayList<TdTFormatReference> formats = dataTypeSelected.getFormatReferenceIndexer().getFormats();
					scbDataTypeFormat.getStore().add(formats);
					categoryDataTypeFormatHandler();
				}

			}
		});

		scbReferenceColumn.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {

				scbReferenceColumn.getElement().getStyle().setBorderWidth(0, Unit.PX);
				if(baloon!=null)
					baloon.hide();
			}
		});

		scbPeriodTypes.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {

				scbPeriodTypes.getElement().getStyle().setBorderWidth(0, Unit.PX);

				//ONLY IF IS A VALID SELECTION CHANGE
				String periodTypeName = getSelectedPeriodType();
				if(periodTypeName!=null){
					typingContainer.removeCombo(scbPeriodTypeValueFormats, "scbPeriodTypeValueFormats");
					scbPeriodTypeValueFormats.reset();
					scbPeriodTypeValueFormats.getStore().removeAll();
					Map<String, String> valuesFormat = templateController.getValueFormatsForPeriodTypeName(periodTypeName);

					for (String keyVf : valuesFormat.keySet()){
//						String exampleVF = valuesFormat.get(keyVf);
//						ls.add(keyVf +" -> "+exampleVF);
//						scbPeriodTypeValueFormats.setData(exampleVF, keyVf);

						scbPeriodTypeValueFormats.getStore().add(new TdTFormatReference(keyVf, valuesFormat.get(keyVf)));
					}
					categoryTimeDimensionValueFormatHandler();
				}
			}
		});

		scbPeriodTypeValueFormats.addSelectionChangedListener(new SelectionChangedListener<TdTFormatReference>() {

			@Override
			public void selectionChanged(
					SelectionChangedEvent<TdTFormatReference> se) {
				scbPeriodTypeValueFormats.getElement().getStyle().setBorderWidth(0, Unit.PX);

			}
		});

		scbDataTypeFormat.addSelectionChangedListener(new SelectionChangedListener<TdTFormatReference>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<TdTFormatReference> se) {
				scbDataTypeFormat.getElement().getStyle().setBorderWidth(0, Unit.PX);

			}
		});

		typingContainer = new SetTypeColumnContainer(scbCategory, scbDataType);
	}

	/**
	 * @return the templateController
	 */
	public TdTemplateController getTemplateController() {
		return templateController;
	}

	private void initComboSetCategory() {

		List<String> ls = new ArrayList<String>();

		for (TdTColumnCategory category : listCategory)
			ls.add(category.getName());

		scbCategory = new SimpleComboBox<String>();
		scbCategory.addStyleName("combo-fixed-size");
		scbCategory.setFieldLabel("Category *");
		scbCategory.setTypeAhead(true);
		scbCategory.setEditable(false);
		scbCategory.setEmptyText("Categorize");
		scbCategory.setToolTip("Category");
		scbCategory.setTriggerAction(TriggerAction.ALL);
		scbCategory.add(ls);
	}

	private void selectDataType(){

		for (TdTColumnCategory category : listCategory) {
			if(scbCategory.getSimpleValue().compareTo(category.getName())==0){
//				scbDataType.setEnabled(true);
				updateComboSetDataType(category);
				break;
			}
		}
	}

	private void initComboSetDataType() {

		scbDataType.setFieldLabel("Data type *");
		scbDataType.addStyleName("combo-fixed-size");
		scbDataType.setTypeAhead(true);
		scbDataType.setEditable(false);
		scbDataType.setDisplayField("name");
		scbDataType.setEmptyText("Choose data type");
		scbDataType.setToolTip("Data Type");
		scbDataType.setTriggerAction(TriggerAction.ALL);
		scbDataType.setStore(new ListStore<TdTDataType>());

		GWT.log("Init combo set data type");

	}

	private void initComboDataTypeFormat() {

		scbDataTypeFormat.setFieldLabel("Format type *");
		scbDataTypeFormat.addStyleName("combo-fixed-size");
		scbDataTypeFormat.setTypeAhead(true);
		scbDataTypeFormat.setEditable(false);
		scbDataTypeFormat.setDisplayField("id");
		scbDataTypeFormat.setEmptyText("Choose data format");
		scbDataTypeFormat.setTemplate(getTemplateValueFormat());
		scbDataTypeFormat.setToolTip("Format Type");
		scbDataTypeFormat.setTriggerAction(TriggerAction.ALL);
		scbDataTypeFormat.setStore(new ListStore<TdTFormatReference>());

		GWT.log("Init combo scbDataTypeFormat");
	}

	private void updateComboSetDataType(TdTColumnCategory category) {

		scbDataType.reset();
		scbDataType.getStore().removeAll();
		scbDataType.getStore().add(category.getTdtDataType());
		GWT.log("Update combo set data type");
	}

	public TdTDataType getSelectedDataType(){

		TdTColumnCategory category = getCategorySelected();

		if(category==null)
			return null;

		if(scbDataType.getSelection().size()>0)
			return scbDataType.getSelection().get(0);

		return null;
	}


	/**
	 *
	 */
	private void initComboReferenceTable() {
		scbReferenceTable.removeAll();

		scbReferenceTable = new SimpleComboBox<String>();
		scbReferenceTable.addStyleName("combo-fixed-size");
		scbReferenceTable.setFieldLabel("Table *");
		scbReferenceTable.setTypeAhead(true);
		scbReferenceTable.setEditable(false);
		scbReferenceTable.setEmptyText("Choose Table");
		scbReferenceTable.setToolTip("Choose Table");
		scbReferenceTable.setTriggerAction(TriggerAction.ALL);
	}

	/**
	 *
	 */
	private void initComboLocales() {

		List<String> ls = new ArrayList<String>();

		for (String locale : templateController.getAllowedLocales())
			ls.add(locale);

		scbLocales = new SelectableSimpleComboBox<String>();
		scbLocales.addStyleName("combo-fixed-size");
		scbLocales.setFieldLabel("Locale *");
		scbLocales.setEmptyText(CHOOSE_LOCALE);
		scbLocales.setToolTip("Locale");

		scbLocales.add(templateController.getLocaleViewManager().getLocales());

		scbLocales.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {

				String localeSel = scbLocales.getSimpleValue();
				GWT.log("Selection Locale changed: "+localeSel +", OLD LOCALE: "+currentLocaleSelect);
				setSelectedLocale(localeSel, false);

			}
		});
	}

	private void initComboPeriodTypes() {

		List<String> ls = new ArrayList<String>();

		for (TdTTimePeriod period : templateController.getListPeriodTypes())
			ls.add(period.getName());

		scbPeriodTypes = new SimpleComboBox<String>();
		scbPeriodTypes.addStyleName("combo-fixed-size");
		scbPeriodTypes.setFieldLabel("Period *");
		scbPeriodTypes.setTypeAhead(true);
		scbPeriodTypes.setEditable(false);
		scbPeriodTypes.setEmptyText("Period");
		scbPeriodTypes.setToolTip("Period");
		scbPeriodTypes.setTriggerAction(TriggerAction.ALL);
		scbPeriodTypes.add(ls);
	}

	private void initComboPeriodTypesValueFormats(){

		scbPeriodTypeValueFormats.setFieldLabel("Time Format *");
		scbPeriodTypeValueFormats.addStyleName("combo-fixed-size");
		scbPeriodTypeValueFormats.setTypeAhead(true);
		scbPeriodTypeValueFormats.setTemplate(getTemplatePeriodTypesValueFormat());
		scbPeriodTypeValueFormats.setDisplayField("id");
		scbPeriodTypeValueFormats.setEditable(false);
		scbPeriodTypeValueFormats.setEmptyText("Time Format");
		scbPeriodTypeValueFormats.setToolTip("Time Format");
		scbPeriodTypeValueFormats.setTriggerAction(TriggerAction.ALL);

		scbPeriodTypeValueFormats.setStore(new ListStore<TdTFormatReference>());
	}

	private native String getTemplatePeriodTypesValueFormat() /*-{
    return [
            '<tpl for=".">',
            '<div class="x-combo-list-item" qtitle="Format {id}" qtip="{value}">{id}</div>',
            '</tpl>' ].join("");
	}-*/;

	private native String getTemplateValueFormat() /*-{
    return [
            '<tpl for=".">',
            '<div class="x-combo-list-item" qtitle="Format {id}" qtip="{value}">{id}</div>',
            '</tpl>' ].join("");
	}-*/;

	private void categoryDimensionHandler(){

		scbReferenceTable.removeAll();
		typingContainer.addCombo(scbReferenceTable);
		scbReferenceTable.addListener(Events.OnClick,listenerDimension);
	}

	private void categoryCodeNameHandler(){

		scbLocales.removeAll();

		//UPDATE LOCALES
		scbLocales.add(templateController.getLocaleViewManager().getLocales());
		typingContainer.addCombo(scbLocales);

		scbLocales.getElement().getStyle().setBorderColor("#EE2C2C");
		scbLocales.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		scbLocales.getElement().getStyle().setBorderWidth(1.0, Unit.PX);

		scbLocales.addListener(Events.OnClick,listenerLocale);
	}

	protected Listener<BaseEvent> listenerLocale = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			scbLocales.removeAll();
			scbLocales.add(templateController.getLocaleViewManager().getLocales());
			scbLocales.getElement().getStyle().setBorderWidth(0, Unit.PX);
		}
	};


	private void categoryTimeDimensionHandler(){

		typingContainer.addCombo(scbPeriodTypes);

		scbPeriodTypes.getElement().getStyle().setBorderColor("#EE2C2C");
		scbPeriodTypes.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		scbPeriodTypes.getElement().getStyle().setBorderWidth(1.0, Unit.PX);

		scbPeriodTypes.addListener(Events.OnClick,listenerDimension);
	}

	private void categoryDataTypeFormatHandler() {

		typingContainer.addCombo(scbDataTypeFormat);

		scbDataTypeFormat.getElement().getStyle().setBorderColor("#EE2C2C");
		scbDataTypeFormat.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		scbDataTypeFormat.getElement().getStyle().setBorderWidth(1.0, Unit.PX);

	}

	private void categoryTimeDimensionValueFormatHandler(){

		typingContainer.addCombo(scbPeriodTypeValueFormats);

		scbPeriodTypeValueFormats.getElement().getStyle().setBorderColor("#EE2C2C");
		scbPeriodTypeValueFormats.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		scbPeriodTypeValueFormats.getElement().getStyle().setBorderWidth(1.0, Unit.PX);

//		scbPeriodTypes.addListener(Events.OnClick,listenerDimension);
	}

	private Listener<BaseEvent> listenerDimension = new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			if(scbCategory.getSimpleValue().compareToIgnoreCase(SPECIAL_CATEGORY_TYPE.DIMENSION.getLabel())==0){
//				scbDataType.mask("Loading");
				DialogCodelistResolver resolver = new DialogCodelistResolver(INSTANCE, templateController);
				resolver.show();
			}else if(scbCategory.getSimpleValue().compareToIgnoreCase(SPECIAL_CATEGORY_TYPE.TIMEDIMENSION.getLabel())==0){
//				DialogCodelistResolver resolver = new DialogCodelistResolver(INSTANCE);
			}
		}
	};


	public void initComboSetReference(List<ColumnData> columnReference) {

		typingContainer.addCombo(scbReferenceColumn);

		scbReferenceColumn.removeAll();
		scbReferenceColumn.addStyleName("combo-fixed-size");
		scbReferenceColumn.getElement().getStyle().setBorderColor("#EE2C2C");
		scbReferenceColumn.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		scbReferenceColumn.getElement().getStyle().setBorderWidth(1.0, Unit.PX);
//		scbReference.setToolTip("Choose Reference");

		String tbResourceName = getReferenceTabularResourceName();

		//add and select the value
		scbReferenceTable.add(tbResourceName);
		scbReferenceTable.setSimpleValue(tbResourceName);

		String baloonDescr="";
		if(tbResourceName!=null){
			baloonDescr="Selected Codelist: '"+tbResourceName +"'<br/>";
		}
		baloonDescr = baloonDescr.length()>0?baloonDescr+"You have to pick column reference":"Choose Column Reference";

		baloon = new BaloonPanel(baloonDescr, false);

		int zIndex = templateController.getWindowZIndex();
		int zi = zIndex+1;
//		baloon.getElement().setAttribute("z-index", zi+"");
		baloon.getElement().getStyle().setZIndex(zi);
		baloon.showRelativeTo(scbReferenceColumn);

		scbReferenceColumn.removeListener(Events.OnClick, listenerDimension);

		lsColumnData = new ArrayList<ColumnData>(columnReference.size());

		for (ColumnData col : columnReference){
			GWT.log("Adding reference id: "+col.getId()+col.getLabel());
			scbReferenceColumn.setData(col.getId()+col.getLabel(), col);
			lsColumnData.add(col);
		}

		scbReferenceColumn.setFieldLabel("Choose Reference");
		scbReferenceColumn.setToolTip("Reference");
		scbReferenceColumn.setTypeAhead(true);
		scbReferenceColumn.setEditable(false);
		scbReferenceColumn.setTriggerAction(TriggerAction.ALL);

		for (ColumnData columnData : columnReference) {
			scbReferenceColumn.add(columnData.getLabel());
		}
	}


	/**
	 * @param name
	 */
	public void setTabularResourceName(String name) {
		this.tabularResourceName = name;

	}

	/**
	 * Called by Template Updater
	 * @param tmpColumnData
	 * @param tabularResourceName
	 * @param columnReference
	 */
	public void updateComboSetReference(ColumnData tmpColumnData, String tabularResourceName, List<ColumnData> columnReference){

		GWT.log("updateComboSetReference tmpColumnData is "+tmpColumnData);
		//TODO TEMPORARY - TABLE NAME IS MISSING
		if(tmpColumnData!=null && tmpColumnData.getName()!=null){
			String tabName = tabularResourceName!=null && !tabularResourceName.isEmpty()?tabularResourceName:tmpColumnData.getName();
			setTabularResourceName(tabName);
			scbReferenceTable.add(tabName);
			scbReferenceTable.setSimpleValue(tabName);
		}else
			typingContainer.removeCombo(scbReferenceTable, "scbReferenceTable");


		typingContainer.addCombo(scbReferenceColumn);
		scbReferenceColumn.removeAll();
		scbReferenceColumn.removeListener(Events.OnClick, listenerDimension);
		lsColumnData = new ArrayList<ColumnData>(columnReference.size());

		for (ColumnData col : columnReference){
			GWT.log("Adding reference id: "+col.getId()+col.getLabel());
			scbReferenceColumn.setData(col.getId()+col.getLabel(), col);
			lsColumnData.add(col);
		}

		scbReferenceColumn.setFieldLabel("Choose Reference");
		scbReferenceColumn.setToolTip("Reference");
		scbReferenceColumn.setTypeAhead(true);
		scbReferenceColumn.setEditable(false);
		scbReferenceColumn.setTriggerAction(TriggerAction.ALL);

		for (ColumnData columnData : columnReference) {
			scbReferenceColumn.add(columnData.getLabel());
		}

	}

	public TdTColumnCategory getCategorySelected(){

		for (TdTColumnCategory category : listCategory) {
			if(scbCategory.getSimpleValue().compareTo(category.getName())==0){
				return category;
			}

		}
		return null;
	}

	/**
	 *
	 * @return the name of the period type selected
	 */
	public String getSelectedPeriodType(){

			if(scbPeriodTypes.getSelectedIndex()!=-1)
				return scbPeriodTypes.getSimpleValue();

		return null;
	}

	/**
	 *
	 * @return the value format example
	 */
	public TdTFormatReference getSelectedPeriodValueFormat(){

		if(scbPeriodTypeValueFormats.getSelection().size()>0){
			GWT.log("scbPeriodTypeValueFormats.getSelection().get(0):" +scbPeriodTypeValueFormats.getSelection().get(0));
			return scbPeriodTypeValueFormats.getSelection().get(0);
		}

	return null;
	}

	/**
	 *
	 * @return the data type format
	 */
	public TdTFormatReference getSelectedDataTypeFormat(){

		if(scbDataTypeFormat.getSelection().size()>0){
			GWT.log("scbDataTypeFormat.getSelection().get(0):" +scbDataTypeFormat.getSelection().get(0));
			return scbDataTypeFormat.getSelection().get(0);
		}

		return null;
	}

	public ColumnData getReferenceSelected(){

		if(scbReferenceColumn.getSimpleValue()!=null && lsColumnData!=null){
			for (ColumnData columnData : lsColumnData) {
				String columnId =  columnData.getId()+scbReferenceColumn.getSimpleValue();
				GWT.log("Trying fetch reference id: "+columnId);
				ColumnData refernceSelected = scbReferenceColumn.getData(columnId);
				if(refernceSelected!=null){
					GWT.log("Returning reference: "+refernceSelected);
					return refernceSelected;
				}
			}
		}
		return null;
	}


	public SimpleComboBox<String> getScbCategory() {
		return scbCategory;
	}

	public ComboBox<TdTDataType> getScbDataType() {
		return scbDataType;
	}

	public SimpleComboBox<String> getScbReference() {
		return scbReferenceColumn;
	}

	public SetTypeColumnContainer getTypingContainer() {
		return typingContainer;
	}

	public String getReferenceTabularResourceName(){
		return tabularResourceName;
	}

	public SimpleComboBox<String> getScbLocales() {
		return scbLocales;
	}

	/**
	 * @return
	 */
	public String getSelectedLocale() {
		if(currentLocaleSelect==null)
			return null;

		if(currentLocaleSelect==scbLocales.getSimpleValue())
			return currentLocaleSelect;

		return null;
	}

	/**
	 * @return
	 */
	public void setSelectedLocale(String value, boolean isUpdate) {

		//DESELECT OLD LOCALE
		if(currentLocaleSelect!=null){
			templateController.getLocaleViewManager().deselectLocale(currentLocaleSelect);
		}

		//SELECT CURRENT LOCALE
		if(value!=null && !value.isEmpty()){
			templateController.getLocaleViewManager().selectLocale(value);
			currentLocaleSelect = value;

			if(isUpdate){
				scbLocales.getElement().getStyle().setBorderWidth(0, Unit.PX);
				scbLocales.setSimpleValue(value);
			}
		}
	}

	/**
	 *
	 * @param simpleValue
	 */
	/*private void setSelectedFirstDataType(){

		scbDataType.getStore().getCount();
		SimpleComboValue<String> value = scbDataType.getStore().getAt(0);
		value.getValue();

		scbDataType.setSimpleValue(value.getValue());
	}*/

	/**
	 * SELECTE FIRST COMBO VALUE IF THERE IS ONLY ONE VALUE
	 */
	public void selectDataTypeIfIsSingle(){
		if(scbDataType.getStore().getCount()==1){
			TdTDataType value = scbDataType.getStore().getAt(0);
			GWT.log("Data type has single value: "+value+", selecting");
			scbDataType.setValue(value);
		}
	}

	public SimpleComboBox<String> getScbPeriodTypes() {
		return scbPeriodTypes;
	}

//	/**
//	 * @param timePeriod
//	 */
//	public void setSelectedTimePeriod(String timePeriod) {
//		scbPeriodTypes.setSimpleValue(timePeriod);
//	}
//

	public void setSelectTimePeriod(TdTTimePeriod period){
		GWT.log("Selectig time period: "+period);
		scbPeriodTypes.setSimpleValue(period.getName());
		Map<String, String> valueFormat = period.getValueFormats();


		if(valueFormat!=null && valueFormat.size()>0){

			for (String keyVf : valueFormat.keySet()) {
				scbPeriodTypeValueFormats.setValue(new TdTFormatReference(keyVf, valueFormat.get(keyVf)));
				break;
			}
		}
	}

	public void setSelectedReferenceAs(ColumnData data){
		scbReferenceColumn.setSimpleValue(data.getLabel());
	}

	public ComboBox<TdTFormatReference> getScbPeriodTypeValueFormats() {
		return scbPeriodTypeValueFormats;
	}

	public ComboBox<TdTFormatReference> getScbDataTypeFormat() {
		return scbDataTypeFormat;
	}

	/**
	 *
	 */
	public void selectDataTypeFormatIfIsSingle() {
		if(scbDataTypeFormat.getStore().getCount()==1){
			TdTFormatReference format = scbDataTypeFormat.getStore().getAt(0);
			GWT.log("Data format has single value: "+format+", selecting");
			scbDataTypeFormat.setValue(format);
		}

	}

	/**
	 *
	 */
	public void selectPeriodTypeFormatIfIsSingle() {
		if(scbPeriodTypeValueFormats.getStore().getCount()==1){
			TdTFormatReference format = scbPeriodTypeValueFormats.getStore().getAt(0);
			GWT.log("Period Type format has single value: "+format+", selecting");
			scbPeriodTypeValueFormats.setValue(format);
		}

	}
}
