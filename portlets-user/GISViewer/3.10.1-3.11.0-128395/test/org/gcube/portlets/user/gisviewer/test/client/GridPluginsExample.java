package org.gcube.portlets.user.gisviewer.test.client;

/* 
 * Ext GWT 2.2.5 - Ext for GWT 
 * Copyright(c) 2007-2010, Ext JS, LLC. 
 * licensing@extjs.com 
 *  
 * http://extjs.com/license 
 */  
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.table.NumberCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
public class GridPluginsExample extends LayoutContainer {  

	private VerticalPanel panel;  
	private GridCellRenderer<Stock> gridNumber;  
	private GridCellRenderer<Stock> change;  

	public GridPluginsExample() {  
		final NumberFormat currency = NumberFormat.getCurrencyFormat();  
		final NumberFormat number = NumberFormat.getFormat("0.00");  
		final NumberCellRenderer<Grid<Stock>> numberRenderer = new NumberCellRenderer<Grid<Stock>>(currency);  

		change = new GridCellRenderer<Stock>() {  
			public String render(Stock model, String property, ColumnData config, int rowIndex, int colIndex,  
					ListStore<Stock> store, Grid<Stock> grid) {  
				double val = (Double) model.get(property);  
				String style = val < 0 ? "red" : "green";  
				return "<span style='color:" + style + "'>" + number.format(val) + "</span>";  
			}  
		};  

		gridNumber = new GridCellRenderer<Stock>() {  
			public String render(Stock model, String property, ColumnData config, int rowIndex, int colIndex,  
					ListStore<Stock> store, Grid<Stock> grid) {  
				return numberRenderer.render(null, property, model.get(property));  
			}  
		};  

		panel = new VerticalPanel();  
		panel.setSpacing(20);  

	}  

	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  

		createExpander();  
		//createCheckBox();  
		//createNumberer();  
		//createFramed();  
		add(panel);  
	}  

	private void createCheckBox() {  
		List<Stock> stocks = getStocks();  

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		final CheckBoxSelectionModel<Stock> sm = new CheckBoxSelectionModel<Stock>();  
		// selection model supports the SIMPLE selection mode  
		// sm.setSelectionMode(SelectionMode.SIMPLE);  

		configs.add(sm.getColumn());  

		ColumnConfig column = new ColumnConfig();  
		column.setId("name");  
		column.setHeader("Company");  
		column.setWidth(300);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("symbol");  
		column.setHeader("Symbol");  
		column.setWidth(100);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("last");  
		column.setHeader("Last");  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setWidth(75);  
		column.setRenderer(gridNumber);  
		configs.add(column);  

		column = new ColumnConfig("change", "Change", 100);  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setRenderer(change);  
		configs.add(column);  

		ListStore<Stock> store = new ListStore<Stock>();  
		store.add(stocks);  

		ColumnModel cm = new ColumnModel(configs);  

		ContentPanel cp = new ContentPanel();  
		cp.setHeading("Framed with Checkbox Selection and Horizontal Scrolling");  
		cp.setFrame(true);  
		//cp.setIcon(Resources.ICONS.table());  
		cp.setLayout(new FitLayout());  
		cp.setSize(600, 300);  

		Grid<Stock> grid = new Grid<Stock>(store, cm);  
		grid.setSelectionModel(sm);  
		grid.setBorders(true);  
		grid.setColumnReordering(true);  
		grid.getAriaSupport().setLabelledBy(cp.getHeader().getId() + "-label");  
		grid.addPlugin(sm);  

		ToolBar toolBar = new ToolBar();  
		toolBar.getAriaSupport().setLabel("Grid Options");  
		toolBar.add(new LabelToolItem("Selection Mode: "));  
		final SimpleComboBox<String> type = new SimpleComboBox<String>();  
		type.setTriggerAction(TriggerAction.ALL);  
		type.setEditable(false);  
		type.setFireChangeEventOnSetValue(true);  
		type.setWidth(100);  
		type.add("Multi");  
		type.add("Simple");  
		type.setSimpleValue("Multi");  
		type.addListener(Events.Change, new Listener<FieldEvent>() {  
			public void handleEvent(FieldEvent be) {  
				boolean simple = type.getSimpleValue().equals("Simple");  
				sm.deselectAll();  
				sm.setSelectionMode(simple ? SelectionMode.SIMPLE : SelectionMode.MULTI);  
			}  
		});  

		toolBar.add(type);  
		toolBar.add(new SeparatorToolItem());  
		cp.setTopComponent(toolBar);  

		cp.add(grid);  
		panel.add(cp);  
	}  

	private void createExpander() {  
		List<Stock> stocks = getStocks();  
		for (Stock s : stocks) {  
			s.set(  
					"desc",  
			"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus tincidunt diam nec urna. Curabitur velit.");  
		}  

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		XTemplate tpl = XTemplate.create("<p><b>Company:</b> {name}</p><br><p><b>Summary:</b> {desc}</p>");  

		RowExpander expander = new RowExpander();  
		expander.setTemplate(tpl);  

		configs.add(expander);  

		ColumnConfig column = new ColumnConfig();  
		column.setId("name");
		column.setHeader("Company");  
		column.setWidth(200);
		configs.add(column);

		column = new ColumnConfig();  
		column.setId("symbol");  
		column.setHeader("Symbol");  
		column.setWidth(100);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("last");  
		column.setHeader("Last");  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setWidth(75);  
		column.setRenderer(gridNumber);  
		configs.add(column);  

		column = new ColumnConfig("change", "Change", 100);  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setRenderer(change);  
		configs.add(column);  

		column = new ColumnConfig("date", "Last Updated", 100);  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setDateTimeFormat(DateTimeFormat.getFormat("MM/dd/yyyy"));  
		configs.add(column);  

		ListStore<Stock> store = new ListStore<Stock>();  
		store.add(stocks);  

		ColumnModel cm = new ColumnModel(configs);  

		ContentPanel cp = new ContentPanel();  
		cp.setHeading("Expander Rows, Collapse and Auto Fill");  
		//cp.setIcon(Resources.ICONS.table());  
		cp.setAnimCollapse(false);  
		cp.setCollapsible(true);  
		cp.setLayout(new FitLayout());  
		cp.setSize(600, 300);  

		Grid<Stock> grid = new Grid<Stock>(store, cm);
		//grid.addPlugin(expander);  
		grid.setColumnReordering(true);  
		grid.getView().setAutoFill(true);  
		grid.getAriaSupport().setLabelledBy(cp.getHeader().getId() + "-label");  
		cp.add(grid);
		
		new GridDragSource(grid);

		GridDropTarget target = new GridDropTarget(grid);
		target.setAllowSelfAsSource(true);
		target.setFeedback(Feedback.INSERT);

		panel.add(cp);  
	}  

	private void createFramed() {  
		List<Stock> stocks = getStocks();  

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		CheckBoxSelectionModel<Stock> sm = new CheckBoxSelectionModel<Stock>();  

		configs.add(sm.getColumn());  

		ColumnConfig column = new ColumnConfig();  
		column.setId("name");  
		column.setHeader("Company");  
		column.setWidth(200);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("symbol");  
		column.setHeader("Symbol");  
		column.setWidth(100);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("last");  
		column.setHeader("Last");  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setWidth(75);  
		column.setRenderer(gridNumber);  
		configs.add(column);  

		column = new ColumnConfig("change", "Change", 100);  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setRenderer(change);  
		configs.add(column);  

		column = new ColumnConfig("date", "Last Updated", 100);  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setDateTimeFormat(DateTimeFormat.getFormat("MM/dd/yyyy"));  
		configs.add(column);  

		ListStore<Stock> store = new ListStore<Stock>();  
		store.add(stocks);  

		ColumnModel cm = new ColumnModel(configs);  

		ContentPanel cp = new ContentPanel();  
		cp.setHeading("Support for standard Panel features such as framing, buttons and toolbars");  
		cp.setFrame(true);  
		//cp.setIcon(Resources.ICONS.table());  
		cp.addButton(new Button("Save"));  
		cp.addButton(new Button("Cancel"));  
		cp.setButtonAlign(HorizontalAlignment.CENTER);  
		cp.setLayout(new FitLayout());  
		cp.setSize(600, 300);  

		ToolBar toolBar = new ToolBar();  
		toolBar.getAriaSupport().setLabel("Grid Options");  
		toolBar.add(new Button("Add"));//, Resources.ICONS.add()));  
		toolBar.add(new SeparatorToolItem());  
		toolBar.add(new Button("Remove"));//, Resources.ICONS.delete()));  
		toolBar.add(new SeparatorToolItem());  
		toolBar.add(new Button("Configure"));//, Resources.ICONS.plugin()));  
		cp.setTopComponent(toolBar);  

		Grid<Stock> grid = new Grid<Stock>(store, cm);  
		grid.setSelectionModel(sm);  
		grid.setAutoExpandColumn("name");  
		grid.setColumnReordering(true);  
		grid.setBorders(true);  
		grid.addPlugin(sm);  
		grid.getAriaSupport().setLabelledBy(cp.getHeader().getId() + "-label");  
		cp.add(grid);  
		panel.add(cp);  
	}  

	private void createNumberer() {  
		List<Stock> stocks = getStocks();  

		RowNumberer r = new RowNumberer();  

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		configs.add(r);  

		ColumnConfig column = new ColumnConfig();  
		column.setId("name");  
		column.setHeader("Company");  
		column.setWidth(200);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("symbol");  
		column.setHeader("Symbol");  
		column.setWidth(100);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("last");  
		column.setHeader("Last");  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setWidth(75);  
		column.setRenderer(gridNumber);  
		configs.add(column);  

		column = new ColumnConfig("change", "Change", 100);  
		column.setAlignment(HorizontalAlignment.RIGHT);  
		column.setRenderer(change);  
		configs.add(column);  

		ListStore<Stock> store = new ListStore<Stock>();  
		store.add(stocks);  

		ColumnModel cm = new ColumnModel(configs);  

		final Grid<Stock> grid = new Grid<Stock>(store, cm);  
		grid.addPlugin(r);  
		grid.getView().setForceFit(true);  


		Button btn = new Button("Remove a Row", new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				grid.getStore().remove(grid.getStore().getAt(0));  
				if (grid.getStore().getCount() == 0) {  
					ce.<Component> getComponent().disable();  
				}  
			}  

		});  
		//btn.setIcon(Resources.ICONS.delete());  

		ContentPanel cp = new ContentPanel();  
		cp.setHeading("Grid with Numbered Rows and Force Fit");  
		//cp.setIcon(Resources.ICONS.table());  
		cp.setLayout(new FitLayout());  
		cp.setSize(600, 300);  
		cp.add(grid);  
		cp.addButton(btn);  
		grid.getAriaSupport().setLabelledBy(cp.getHeader().getId() + "-label");  
		panel.add(cp);  
	}
	
	private List<Stock> getStocks() {
	    List<Stock> stocks = new ArrayList<Stock>();

	    stocks.add(new Stock("Apple Inc.", "AAPL", 125.64, 123.43));
	    stocks.add(new Stock("Cisco Systems, Inc.", "CSCO", 25.84, 26.3));
	    stocks.add(new Stock("Google Inc.", "GOOG", 516.2, 512.6));
	    stocks.add(new Stock("Intel Corporation", "INTC", 21.36, 21.53));
	    stocks.add(new Stock("Level 3 Communications, Inc.", "LVLT", 5.55, 5.54));
	    stocks.add(new Stock("Microsoft Corporation", "MSFT", 29.56, 29.72));
	    stocks.add(new Stock("Nokia Corporation (ADR)", "NOK", 27.83, 27.93));
	    stocks.add(new Stock("Oracle Corporation", "ORCL", 18.73, 18.98));
	    stocks.add(new Stock("Starbucks Corporation", "SBUX", 27.33, 27.36));
	    stocks.add(new Stock("Yahoo! Inc.", "YHOO", 26.97, 27.29));
	    stocks.add(new Stock("Applied Materials, Inc.", "AMAT", 18.4, 18.66));
	    stocks.add(new Stock("Comcast Corporation", "CMCSA", 25.9, 26.4));
	    stocks.add(new Stock("Sirius Satellite", "SIRI", 2.77, 2.74));
	    stocks.add(new Stock("First Data Corporation", "FDC", 32.7, 32.65));

	    return stocks;
	  }


	private class Stock extends BaseModel {

		public Stock() {
		}

		public Stock(String name, double open, double change, double pctChange, Date date, String industry) {
			set("name", name);
			set("open", open);
			set("change", change);
			set("percentChange", pctChange);
			set("date", date);
			set("industry", industry);
			set("split", new Boolean(Math.random() > .5));
			set("type", getType());
		}

		public Stock(String name, String symbol, double open, double last) {
			set("name", name);
			set("symbol", symbol);
			set("open", open);
			set("last", last);
			set("date", new DateWrapper().addDays(-(int)(Math.random() * 100)).asDate());
			set("change", last - open);
			set("split", new Boolean(Math.random() > .5));
			set("type", getType());
		}

		public double getChange() {
			return getLast() - getOpen();
		}

		public String getIndustry() {
			return get("industry");
		}

		public double getLast() {
			Double open = (Double) get("last");
			return open.doubleValue();
		}

		public Date getLastTrans() {
			return (Date) get("date");
		}

		public String getName() {
			return (String) get("name");
		}

		public double getOpen() {
			Double open = (Double) get("open");
			return open.doubleValue();
		}

		public double getPercentChange() {
			return getChange() / getOpen();
		}

		public String getSymbol() {
			return (String) get("symbol");
		}

		public void setIndustry(String industry) {
			set("industry", industry);
		}

		public String toString() {
			return getName();
		}

		private String getType() {
			double r = Math.random();
			if (r <= .25) {
				return "Auto";
			} else if (r > .25 && r <= .50) {
				return "Media";
			} else if (r > .5 && r <= .75) {
				return "Medical";
			} else {
				return "Tech";
			}
		}
	}
}  