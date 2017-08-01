package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.widget.ActionCellClass;
import org.gcube.portlets.admin.authportletmanager.shared.PolicyAuth;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

/**
 * Table for list policy (data grid)
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class PolicyDataGrid extends Composite {

	private static PolicyDataGridUiBinder uiBinder = GWT
			.create(PolicyDataGridUiBinder.class);

	interface PolicyDataGridUiBinder extends UiBinder<Widget, PolicyDataGrid> {
	}
	/**
	 * The main DataGrid.
	 */
	@UiField(provided = true)
	DataGrid<PolicyAuth> dataGrid;

	@UiField(provided = true)
	SimplePager pager;

	public PolicyDataGrid() {
		onInitialize();
		initWidget(uiBinder.createAndBindUi(this));

	}

	private Column<PolicyAuth, String> callerColumn;
	private Column<PolicyAuth, String> callerType;
	private Column<PolicyAuth, String> serviceClassColumn;
	private Column<PolicyAuth, String> accessColumn;
	private Column<PolicyAuth, Date> dataInsertColumn;
	private Column<PolicyAuth, Date> dataUpdateColumn;
	public static SelectionModel<PolicyAuth> selectionModel;
	public static ArrayList<PolicyAuth>selectedPolicy= new ArrayList<PolicyAuth>();

	public void onInitialize() {
		dataGrid = new DataGrid<PolicyAuth>();
		dataGrid.setWidth("100%");
		dataGrid.setHeight("400px");
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.addStyleName("table_policy");
		dataGrid.getElement().setId("idGridPolicy");
		//Set the message to display when the table is empty.
		dataGrid.setEmptyTableWidget(new Label("No Policy Entry"));
		dataGrid.setSkipRowHoverStyleUpdate(false);
		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(dataGrid);
		// Attach a column sort handler to the ListDataProvider to sort the list.
		ListHandler<PolicyAuth> sortHandler =
				new ListHandler<PolicyAuth>(PolicyDataProvider.get().getDataProvider().getList());
		dataGrid.addColumnSortHandler(sortHandler);
		//Add a selection model so we can select cells.
		selectionModel =  new MultiSelectionModel<PolicyAuth>();
		dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager
				.<PolicyAuth> createCheckboxManager());
		//Init Table Columns 
		initTableColumns(selectionModel, sortHandler);
		PolicyDataProvider.get().addDataDisplay(dataGrid);
		dataGrid.setVisible(true);
	}	

	/**
	 * Init the columns to the table.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initTableColumns(final SelectionModel<PolicyAuth> selectionModel,
			ListHandler<PolicyAuth> sortHandler) {

		// Checkbox column. This table will uses a checkbox column for selection.
		Column<PolicyAuth, Boolean> checkColumn =
				new Column<PolicyAuth, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(PolicyAuth object) {
				// Get the value from the selection model.
				if (selectionModel.isSelected(object)){
					//if already exist no add
					if (!selectedPolicy.contains(object))
						selectedPolicy.add(object);
				}
				else
					selectedPolicy.remove(object);
				return selectionModel.isSelected(object);
			}
		};
		dataGrid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
		dataGrid.setColumnWidth(checkColumn, 5, Unit.PCT);
		// CallerColumn column this is caller for the service 
		callerColumn = new Column<PolicyAuth, String>(new TextCell()) {
			@Override
			public String getValue(PolicyAuth object) {
				boolean excludeCaller =object.getExcludesCaller();				
				String caller=object.getCallerAsString();  
				if (excludeCaller)
					caller="All execpt: "+caller;
				return caller;
			}
			/** for override style on clik */ 
			@Override
			public String getCellStyleNames(Context context, PolicyAuth object) {

				if (selectionModel.isSelected(object)) {
					return "boldStyle";
				}
				else
					return null;
			}
		};
		callerColumn.setSortable(true);
		sortHandler.setComparator(callerColumn, new Comparator<PolicyAuth>() {
			@Override
			public int compare(PolicyAuth o1, PolicyAuth o2) {
				return o1.getCallerAsString().compareTo(o2.getCallerAsString());
			}
		});
		dataGrid.setColumnWidth(callerColumn, 20, Unit.PCT);
		dataGrid.addColumn(callerColumn, new ResizableHeader("Caller", dataGrid, callerColumn));
		// CallerType column this is caller for the service 
		callerType = new Column<PolicyAuth, String>(new TextCell()) {
			@Override
			public String getValue(PolicyAuth object) {
				String caller=object.getCallerTypeAsDataGrid().toUpperCase();  
				return caller;
			}
			/** for override style on clik */ 
			@Override
			public String getCellStyleNames(Context context, PolicyAuth object) {

				if (selectionModel.isSelected(object)) {
					return "boldStyle";
				}
				else
					return null;
			}
		};
		callerType.setSortable(true);
		sortHandler.setComparator(callerType, new Comparator<PolicyAuth>() {
			@Override
			public int compare(PolicyAuth o1, PolicyAuth o2) {
				return o1.getCallerTypeAsString().compareTo(o2.getCallerTypeAsString());
			}
		});
		dataGrid.setColumnWidth(callerType, 20, Unit.PCT);
		dataGrid.addColumn(callerType, new ResizableHeader("Type", dataGrid, callerType));

		// Service Class this a service class column for the caller.
		serviceClassColumn = new Column<PolicyAuth, String>(new TextCell()) {
			@Override
			public String getValue(PolicyAuth object) {

				String service=object.getServiceAsString();
				return service;
			}
		};
		serviceClassColumn.setSortable(true);
		sortHandler.setComparator(serviceClassColumn, new Comparator<PolicyAuth>() {
			@Override
			public int compare(PolicyAuth o1, PolicyAuth o2) {

				return o1.getServiceAsString().compareTo(o2.getServiceAsString());
			}
		});
		dataGrid.setColumnWidth(serviceClassColumn, 30, Unit.PCT);
		dataGrid.addColumn(serviceClassColumn, new ResizableHeader("Service", dataGrid, serviceClassColumn));

		// accessColumn.
		accessColumn = new Column<PolicyAuth, String>(new TextCell()) {
			@Override
			public String getValue(PolicyAuth object) {
				return object.getAccessString();
			}
		};
		accessColumn.setSortable(true);
		sortHandler.setComparator(accessColumn, new Comparator<PolicyAuth>() {
			@Override
			public int compare(PolicyAuth o1, PolicyAuth o2) {
				return o1.getAccess().compareTo(o2.getAccess());
			}
		}); 
		dataGrid.setColumnWidth(accessColumn, 10, Unit.PCT);
		dataGrid.addColumn(accessColumn, new ResizableHeader("Access", dataGrid, accessColumn)); 

		dataInsertColumn = new Column<PolicyAuth, Date>(new DateCell(DateTimeFormat.getFormat("EEE, d MMM yyyy HH:mm:ss"))) {
			@Override
			public Date getValue(PolicyAuth object) {
				return object.getDataInsert();
			}
		};
		dataInsertColumn.setSortable(true);
		sortHandler.setComparator(dataInsertColumn, new Comparator<PolicyAuth>() {
			@Override
			public int compare(PolicyAuth o1, PolicyAuth o2) {
				if (o1 == o2) {
					return 0;
				}
				if (o1.getDataInsert() != null) {
					Date date1 =o1.getDataInsert();
					Date date2 =o2.getDataInsert();

					return (o2.getDataInsert() != null) ? date1.compareTo(date2) : 1;
				}
				return -1;
			}
		}); 
		dataGrid.setColumnWidth(dataInsertColumn, 10, Unit.PCT);

		dataGrid.addColumn(dataInsertColumn, new ResizableHeader("Insert", dataGrid, dataInsertColumn)); 

		dataUpdateColumn = new Column<PolicyAuth, Date>(new DateCell(DateTimeFormat.getFormat("EEE, d MMM yyyy HH:mm:ss "))) {
			@Override
			public Date getValue(PolicyAuth object) {
				return object.getDataUpdate();
			}
		};
		dataUpdateColumn.setSortable(true);
		sortHandler.setComparator(dataUpdateColumn, new Comparator<PolicyAuth>() {
			@Override
			public int compare(PolicyAuth o1, PolicyAuth o2) {
				if (o1 == o2) {
					return 0;
				}
				if (o1.getDataUpdate() != null) {
					Date date1 =o1.getDataUpdate();
					Date date2 =o2.getDataUpdate();
					return (o2.getDataUpdate() != null) ? date1.compareTo(date2) : 1;
				}
				return -1;
			}		}); 
		dataGrid.setColumnWidth(dataUpdateColumn, 10, Unit.PCT);
		dataGrid.addColumn(dataUpdateColumn, new ResizableHeader("Last Update", dataGrid, dataUpdateColumn)); 

		//add Columns Actions whit button Edit 
		List<HasCell<PolicyAuth, ?>> cells = new LinkedList<HasCell<PolicyAuth, ?>>();
		cells.add(new ActionHasCell("Edit", new Delegate<PolicyAuth>() {
			@Override
			public void execute(PolicyAuth policy) {
				// EDIT CODE
				PolicyAddDialog dialogedit = new PolicyAddDialog();
				ArrayList<PolicyAuth> listModifiedPolicy= new ArrayList<PolicyAuth>();
				listModifiedPolicy.add(policy);
				dialogedit.setModifyPolicy(listModifiedPolicy);
				dialogedit.show();	
			}
		}));	
		cells.add(new ActionHasCell("Delete", new Delegate<PolicyAuth>() {
			@Override
			public void execute(PolicyAuth policy) {
				// DELETE CODE		        	
				ArrayList<PolicyAuth> listDeletePolicy =new ArrayList<PolicyAuth>();
				listDeletePolicy.add(policy);
				PolicyDeleteDialog confirmDeleteDialog = new PolicyDeleteDialog(listDeletePolicy);
				confirmDeleteDialog.show();
			}
		}));
		CompositeCell<PolicyAuth> cell = new CompositeCell<PolicyAuth>(cells);
		Column<PolicyAuth, PolicyAuth> actionColumn =new Column<PolicyAuth, PolicyAuth>(cell) {
			@Override
			public PolicyAuth getValue(PolicyAuth object) {
				return object;
			}
		};
		dataGrid.setColumnWidth(actionColumn, 15, Unit.PCT);
		dataGrid.addColumn(actionColumn, "Actions");	    

	}

	private class ActionHasCell implements HasCell<PolicyAuth, PolicyAuth> {
		private ActionCellClass<PolicyAuth> cell;
		public ActionHasCell(String text, Delegate<PolicyAuth> delegate) {
			cell = new ActionCellClass<PolicyAuth>(text, delegate);
		}

		@Override
		public Cell<PolicyAuth> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<PolicyAuth, PolicyAuth> getFieldUpdater() {
			return null;
		}

		@Override
		public PolicyAuth getValue(PolicyAuth object) {
			return object;
		}
	}
}
