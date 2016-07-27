package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.fhn_manager_portlet.client.FhnManagerController;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RefreshGridEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.RefreshGridEvent.RefreshGridOptions;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.Constants;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

public class StorableGrid <T extends Storable> implements DataContainer<T>{

	private AdvancedGridConfiguration configuration;
	private ListDataProvider<T> dataProvider=new ListDataProvider<T>();
	
	private DataGrid<T> theGrid;
	private SingleSelectionModel<T> selectionModel;
	
	private Map<String,String> filters=new HashMap<String, String>();
	
	public StorableGrid(final AdvancedGridConfiguration configuration) {
		this.configuration=configuration;
		
		
		ProvidesKey<T> keyProvider=new ProvidesKey<T>() {
			public Object getKey(T item) {
				return item.getKey();
			};
		};


		theGrid=new DataGrid<T>();
		theGrid.setStyleName("table-overflow");
		theGrid.setStriped(true);
//		theGrid.setBordered(true);
		
		theGrid.setTitle(configuration.getTitle());
		theGrid.setWidth("100%");
		theGrid.setHeight("620px");
		theGrid.setAutoHeaderRefreshDisabled(true);
		
		selectionModel = new SingleSelectionModel<T>(keyProvider);

		
		theGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<T> createCheckboxManager());
		
		Column<T, Boolean> checkColumn =
		        new Column<T, Boolean>(new CheckboxCell(true, false)) {
		          @Override
		          public Boolean getValue(T object) {
		            return selectionModel.isSelected(object);
		          }
		        };
		
		theGrid.addColumn(checkColumn,"Select");
		theGrid.setColumnWidth(checkColumn,80,Unit.PX);
		
		for(final FieldDefinition fieldDefinition:configuration.getFieldDefinitions()){
			
			final String fieldName=fieldDefinition.getField();
			TextColumn<T> column=new TextColumn<T>() {
				public String getValue(T object) {
					return fieldDefinition.format(object.getObjectField(fieldName));};
			};
			
			column.setSortable(true);
			ListHandler<T> columnSortHandler=new ListHandler<T>(dataProvider.getList());
			columnSortHandler.setComparator(column, new Comparator<T>() {
				public int compare(T o1, T o2) {
					return ((Comparable)o1.getObjectField(fieldName)).compareTo(o2.getObjectField(fieldName));
				};
			});
			theGrid.addColumnSortHandler(columnSortHandler);
			theGrid.addColumn(column,fieldDefinition.getLabel());
			theGrid.setColumnWidth(column, 100/configuration.getFieldDefinitions().size() , Unit.PCT);
		}

		
		
		theGrid.setEmptyTableWidget(new Label(Constants.EMPTY_GRID));
		dataProvider.addDataDisplay(theGrid);
		
	}
	
	@Override
	public void setData(List<T> toSet){
		dataProvider.getList().clear();

		for (T pckg : toSet)
			dataProvider.getList().add(pckg);
		
		theGrid.setPageSize(toSet.size()+1);
		theGrid.redraw();
		
		selectionModel.clear();
	}
	
	@Override
	public T getSelected(){
		return selectionModel.getSelectedObject();
	}
	
	@Override
	public boolean hasSelection(){
		return !selectionModel.getSelectedSet().isEmpty();
	}
	
	public RefreshGridOptions getRefreshOption(){
		RefreshGridOptions toReturn=new RefreshGridOptions(configuration.getManagedObjectType(), filters);
		return toReturn;
	}
	
	@Override
	public Widget getTheWidget() {
		return theGrid;
	}
	
	@Override
	public void setFilters(Map<String, String> toSet) {
		filters=toSet;		
	}
	
	@Override
	public void fireRefreshData() {
		FhnManagerController.eventBus.fireEvent(new RefreshGridEvent(getRefreshOption(),this));
	}
	
}
