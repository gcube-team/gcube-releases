/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.td.widgetcommonevent.client.event.ExpressionWrapperEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnElement;
import org.gcube.portlets.user.tdtemplate.shared.ColumnNotTypedException;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 29, 2014
 *
 */
public class MultiFilterByColumns extends FilterColumnEvent{
	
	private List<ColumnElement> columns;

	private Map<String, FilterColumnView> hashFilterColumn = new HashMap<String, FilterColumnView>();
	
//	private Window window;

	private LayoutContainer lc = new LayoutContainer();
	private FlexTable flexTable = new FlexTable();
	
	private static EventBus bus = new SimpleEventBus();
	
	/**
	 * @throws ColumnNotTypedException 
	 * 
	 */
	public MultiFilterByColumns(List<ColumnElement> listColumns) throws ColumnNotTypedException {
		super(bus);
		this.columns = listColumns;
		
		areTypedColumns();

//		createWindow();
		
		flexTable.setCellPadding(10);
		flexTable.setStyleName("FilterFlexTableTemplate");

		Label op = new Label("Operator");
		flexTable.setWidget(0, 0, op);
		flexTable.setWidget(0, 1, new Label("Column"));
		flexTable.setWidget(0, 2, new Label("Filter"));
		flexTable.setWidget(0, 3, new Label("Filter Description"));
		
		flexTable.getCellFormatter().setWidth(0, 0, "80px");
	
		if(listColumns!=null && listColumns.size()>0){
			createFilterRow(false, listColumns.get(0));
			for (int i = 1; i < listColumns.size(); i++)
				createFilterRow(true, listColumns.get(i));
		}
		
		
		lc.add(flexTable);
		lc.setScrollMode(Scroll.AUTOY);
		
//		window.add(lc);
		
		if(listColumns.size()<=6){
			flexTable.setWidth("100%");
			lc.layout();
		}else
			lc.setLayout(new FitLayout());
		
		lc.setWidth(490);
		
	}


	/**
	 * @throws ColumnNotTypedException 
	 * 
	 */
	public boolean areTypedColumns() throws ColumnNotTypedException {
		
		if(columns!=null && columns.size()>0){
			for (ColumnElement column : columns) {
				if(column.getColumnDataType()==null)
					throw new ColumnNotTypedException("Column id: "+column.getColumnId()+" is not typed");
			}
				
		}
		
		return true;
	}


	private void createFilterRow(boolean addAndOr, final ColumnElement column){
		
		FilterColumnView filterColumn = new FilterColumnView("Column ", addAndOr, column, bus);

		int index = filterColumn.getIndex();
		
		if(addAndOr)
			flexTable.setWidget(index, 0, filterColumn.getComboAndOr());
		

		flexTable.setWidget(index, 1, filterColumn.getCaption());
		flexTable.setWidget(index, 2, filterColumn.getFilter());
		flexTable.setWidget(index, 3, filterColumn.getTextFilterDescr());
	
		lc.layout();
		
		hashFilterColumn.put(filterColumn.getFilterColumnId(), filterColumn);

	}

	/**
	 * Use for GXT 2.5
	 * @return
	 */
	/*private void createWindow() {
		
		window = new Window();
		window.setLayout(new FitLayout());
		window.setIcon(TdTemplateAbstractResources.filter16());
		window.setResizable(false);
//		window.setAnimCollapse(true);
		window.setMaximizable(false);
		window.setHeading("Multi-Columns Filter");
		window.setSize(500+"px", 250+"px");
		window.setLayout(new FitLayout());
//	    window.setScrollMode(Scroll.AUTO);
	}*/

//	public Window getWindow() {
//		return window;
//	}
	
	public LayoutContainer getPanel(){
		return lc;
	}

	public List<ColumnElement> getColumns() {
		return columns;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.external.FilterColumnEvent#updateDescription(org.gcube.portlets.user.td.widgetcommonevent.client.event.ExpressionEvent)
	 */
	@Override
	public void updateDescription(ExpressionWrapperEvent event) {
		ExpressionWrapper exWrapper=event.getExpressionWrapper();
		FilterColumnView filter = hashFilterColumn.get(exWrapper.getColumnData().getColumnId());
		if(filter!=null)
			filter.setFilterDescriptionValue(exWrapper.getConditionExpressionContainer().getExp().toString());
	}
}
