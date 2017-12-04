/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.experimentArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.Services;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.StatisticalManagerExperimentsWidget;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.TableItemSimple;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.resources.Images;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * @author ceras
 *
 */

/*
 HSPEN, Half-degree Species Environmental Envelope
 HCAF, Half-degree Cells Authority File
 HSPEC, Half-degree Species Assignment 
 OCCURRENCE,
 MINMAXLAT,
 TRAININGSET,
 TESTSET,
 GENERIC,
 CLUSTER
 */

public class TableSelector extends Dialog {
	protected static Logger logger = Logger.getLogger("logger");

	private List<String> templates;
	
	private ListView<BeanModel> view;  
	private LayoutContainer details;
	private ListStore<BeanModel> store;
	private SimpleComboBox<String> sort;
	//	private OpenTableButton openTableButton;

	/**
	 * 
	 */
	public TableSelector(List<String> templates) {
		super();
		logger.log(Level.SEVERE,"Passed templates "+templates);
		this.templates = templates;

		this.setBodyBorder(false);
		this.setButtons(Dialog.OKCANCEL);
		this.setIcon(Images.folderExplore());  
		this.setHeading("Data Set Explorer");  
		this.setWidth(640);  
		this.setHeight(480);  
		this.setHideOnButtonClick(true);  
		//		this.setModal(true);

		this.setLayout(new BorderLayout());  
		this.setBodyStyle("border: none;background: none");

		//		openTableButton = new OpenTableButton(){
		//			@Override
		//			public void onOpenTable() {
		////				TableSelector.this.mask();
		//				TableSelector.this.setVisible(false);
		//			}
		//
		//			@Override
		//			public void onHideTable() {
		////				TableSelector.this.unmask();
		//				TableSelector.this.setVisible(true);
		//			}
		//			
		//		};
	}

	protected void onRender(com.google.gwt.user.client.Element parent, int pos) {
		super.onRender(parent, pos);
		



		RpcProxy<List<TableItemSimple>> proxy = new RpcProxy<List<TableItemSimple>>() {  
			@Override
			protected void load(Object loadConfig, AsyncCallback<List<TableItemSimple>> callback) {
				ArrayList<TableItemSimple> callerDefined=new ArrayList<TableItemSimple>();
				callerDefined.addAll(StatisticalManagerExperimentsWidget.instance().getListSelectedList().values());
//				logger.log(Level.SEVERE,"GET TABLE ITEMS, Caller defined are "+callerDefined);
				Services.getStatisticalService().getTableItems(templates,callerDefined, callback);  
				logger.log(Level.SEVERE,"GET TABLE ITEMS, sent Call");
			}  
		};  

		ListLoader<ListLoadResult<BeanModel>> loader = new BaseListLoader<ListLoadResult<BeanModel>>(proxy, new BeanModelReader());  
		loader.setRemoteSort(false);
		loader.setSortField("name");
		loader.setSortDir(SortDir.ASC);
		loader.load();

		store = new ListStore<BeanModel>(loader);  
		//		store.sort("name", SortDir.ASC);
		//		store.setSortDir(SortDir.ASC);
		//		store.setSortField("name");
		//		store.setDefaultSort("name", SortDir.ASC);

		store.sort("name", SortDir.ASC);
		this.addListener(Events.Hide, new Listener<WindowEvent>() {  
			public void handleEvent(WindowEvent be) {  
				BeanModel model = view.getSelectionModel().getSelectedItem();  
				if (model != null) {  
					TableItemSimple tableItem = model.getBean();  
					if (be.getButtonClicked() == TableSelector.this.getButtonById("ok")) {
						fireSelection(tableItem);
					}  
				}  
			}  
		});  


		ContentPanel main = new ContentPanel();  
		main.setBorders(true);  
		main.setBodyBorder(false);  
		main.setLayout(new FitLayout());  
		main.setHeaderVisible(false);  

		ToolBar bar = new ToolBar();  
		bar.add(new LabelToolItem("Filter:"));  

		StoreFilterField<BeanModel> field = new StoreFilterField<BeanModel>() {  
			@Override  
			protected boolean doSelect(Store<BeanModel> store, BeanModel parent, BeanModel record, String property, String filter) {  
				TableItemSimple tableItem = record.getBean();  
				String name = tableItem.getName().toLowerCase();  
				if (name.indexOf(filter.toLowerCase()) != -1) {  
					return true;  
				}  
				return false;  
			}  

			@Override  
			protected void onFilter() {  
				super.onFilter();  
				view.getSelectionModel().select(0, false);  
			}
		};  
		field.setWidth(100);  
		field.bind(store);  

		bar.add(field);  
		bar.add(new SeparatorToolItem());
		bar.add(new LabelToolItem("Sort By:"));  

		sort = new SimpleComboBox<String>();  
		sort.setTriggerAction(TriggerAction.ALL);  
		sort.setEditable(false);
		sort.setForceSelection(true);  
		sort.setWidth(90);  
		sort.add("Name");  
		sort.add("Type");  
		//		sort.add("Last Modified");  
		sort.setSimpleValue("Name");  
		sort.addListener(Events.Select, new Listener<FieldEvent>() {  
			public void handleEvent(FieldEvent be) {  
				sort();  
			}  
		});  

		bar.add(sort);  

		main.setTopComponent(bar);  

		view = new ListView<BeanModel>() {  
			@Override  
			protected BeanModel prepareData(BeanModel model) {  
				TableItemSimple tableItem = model.getBean();  
				model.set("tableName", Format.ellipse(tableItem.getName(), 40));  
				//				model.set("tableDescription", Format.ellipse(tableItem.getDescription(), 15));
				//				model.set("tableType", Format.ellipse(tableItem.getType(), 15));
				return model;  
			}  
		};  
		view.setId("img-chooser-view");  
		view.setTemplate(getTemplate(GWT.getModuleBaseURL()));  
		view.setBorders(false);  
		view.setStore(store);  
		view.setItemSelector("div.thumb-wrap");  
		view.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);  
		view.getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<BeanModel>>() {  
			public void handleEvent(SelectionChangedEvent<BeanModel> be) {  
				onSelectionChange(be);  
			}  
		});  
		main.add(view);  

		details = new LayoutContainer();  
		details.setBorders(true);  
		details.setStyleAttribute("backgroundColor", "white");  

		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 200, 150, 400);  
		eastData.setSplit(true);  

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
		centerData.setMargins(new Margins(0, 5, 0, 0));  

		this.add(main, centerData);  
		this.add(details, eastData);  

		view.getSelectionModel().select(0, false);  
		//		add(new Button("Choose", new SelectionListener<ButtonEvent>() {  
		//			@Override
		//			public void componentSelected(ButtonEvent ce) {  
		//				TableSelector.this.show();  
		//				view.getSelectionModel().select(0, false);  
		//			} 
		//		}));

	};


	@Override
	public void show() {
		super.show();
		if (store!=null && store.getLoader()!=null)
			store.getLoader().load();
	}

	/**
	 * @param tableItem
	 */
	public void fireSelection(TableItemSimple tableItem) {
		System.out.println("selected "+tableItem.getName());
	}

	private void sort() {  
		String v = sort.getSimpleValue();  
		if (v.equals("Name")) {
			store.sort("name", SortDir.ASC);
		} else if (v.equals("Type")) {
			store.sort("type", SortDir.ASC);  
		}
	}  

	private void onSelectionChange(SelectionChangedEvent<BeanModel> se) {  
		if (se.getSelection().size() > 0) {
			TableItemSimple tableItem = se.getSelectedItem().getBean();
			//			openTableButton.setTable(tableItem);
			String imgUri=Images.templateImagesMap.get(se.getSelection().get(0).get("type")).getSafeUri().asString();
			XTemplate detailTp= XTemplate.create(getDetailTemplate(imgUri));			
			
			detailTp.overwrite(details.getElement(), Util.getJsObject(se.getSelection().get(0)));
			if(!tableItem.getTDFlag())

				//			details.add(openTableButton);

				details.layout();

			this.getButtonById("ok").enable();  
		} else {  
			this.getButtonById("ok").disable();  
			details.el().setInnerHtml("");  
		}  
	}  

	private native String getTemplate(String base) /*-{ 
		    return [
		    '<tpl for=".">', 
		    	'<div class="thumb-wrap tableSelector-item" id="{name}" style="border: 1px solid white">', 
		    	'<span class="x-editable">{tableName}</span></div>', 
		    '</tpl>'
			].join(""); 
		  }-*/;  

	/*
		    return ['<tpl for=".">', 
		    '<div class="thumb-wrap" id="{name}" style="border: 1px solid white">', 
		    '<span class="x-editable">{tableName}</span></div>', 
		    '</tpl>', 
		    '<div class="x-clear"></div>'].join(""); 
	 */

	/**
	 * @return
	 */
	private native String getDetailTemplate(String imgUri) /*-{ 
    	return [    	
    	'<div class="tableSelector-details">', 
    	'<tpl for=".">', 
    		'<img src="'+imgUri+'"/>',
    		'<b>Data Set Name: </b>{name}<br/><br/>', 
    		'<b>Data Set Id: </b>{id}<br/><br/>', 
    		'<b>Description: </b>{description}<br/><br/>', 
    		'<b>Type: </b>{type}', 
    	'</tpl>', 
    	'</div>'].join(""); 
  	}-*/;
}
