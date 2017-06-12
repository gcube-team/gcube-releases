package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.widget.ActionCellClass;
import org.gcube.portlets.admin.authportletmanager.shared.Quote;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
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
 * Table for list quote (data grid)
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class QuoteDataGrid extends Composite {

	private static QuoteDataGridUiBinder uiBinder = GWT
			.create(QuoteDataGridUiBinder.class);

	interface QuoteDataGridUiBinder extends UiBinder<Widget, QuoteDataGrid> {
	}
	/**
	 * The main DataGrid.
	 */
	@UiField(provided = true)
	DataGrid<Quote> dataGrid;


	@UiField(provided = true)
	SimplePager pager;

	public QuoteDataGrid() {
		onInitialize();
		initWidget(uiBinder.createAndBindUi(this));

	}

	private Column<Quote, String> callerColumn;


	//private Column<Quote, String> targetColumn;
	private Column<Quote, String> managerColumn;
	private Column<Quote, String> timeIntervalColumn;

	private Column<Quote, Number> quoteColumn;

	private Column<Quote, Date> dataInsertColumn;

	private Column<Quote, Date> dataUpdateColumn;


	public static SelectionModel<Quote> selectionModel;

	public static ArrayList<Quote>selectedQuote= new ArrayList<Quote>();



	public void onInitialize() {
		//resources = GWT.create(Resources.class);
		//resources.styles().ensureInjected();


		dataGrid = new DataGrid<Quote>();
		dataGrid.setWidth("100%");
		dataGrid.setHeight("400px");
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setAutoHeaderRefreshDisabled(true);


		dataGrid.addStyleName("table_quote");
		dataGrid.getElement().setId("idGridQuote");
		//Set the message to display when the table is empty.
		dataGrid.setEmptyTableWidget(new Label("No Quote Entry"));

		dataGrid.setSkipRowHoverStyleUpdate(false);


		/* 
	  		// dataGrid.setHeaderBuilder(new CustomHeaderBuilder());
	  		//  dataGrid.setFooterBuilder(new CustomFooterBuilder());
		 */
		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(dataGrid);


		// Attach a column sort handler to the ListDataProvider to sort the list.
		ListHandler<Quote> sortHandler =
				new ListHandler<Quote>(QuoteDataProvider.get().getDataProvider().getList());
		dataGrid.addColumnSortHandler(sortHandler);

		//Add a selection model so we can select cells.
		selectionModel =  new MultiSelectionModel<Quote>();
		dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager
				.<Quote> createCheckboxManager());


		//Init Table Columns 
		initTableColumns(selectionModel, sortHandler);



		QuoteDataProvider.get().addDataDisplay(dataGrid);
		dataGrid.setVisible(true);



	}	

	/**
	 * Init the columns to the table.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initTableColumns(final SelectionModel<Quote> selectionModel,
			ListHandler<Quote> sortHandler) {
		// Checkbox column. This table will uses a checkbox column for selection.
		Column<Quote, Boolean> checkColumn =
				new Column<Quote, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(Quote object) {
				// Get the value from the selection model.
				if (selectionModel.isSelected(object)){
					//if already exist no add
					if (!selectedQuote.contains(object))
						selectedQuote.add(object);

				}
				else
					selectedQuote.remove(object);

				return selectionModel.isSelected(object);
			}

		};


		dataGrid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
		dataGrid.setColumnWidth(checkColumn, 5, Unit.PCT);

		// CallerColumn column this is caller for the service 
		callerColumn = new Column<Quote, String>(new TextCell()) {
			@Override
			public String getValue(Quote object) {
				String caller=object.getCallerAsString();  
				return caller;
			}
			/** for override style on clik */ 
			@Override
			public String getCellStyleNames(Context context, Quote object) {

				if (selectionModel.isSelected(object)) {
					return "boldStyle";
				}
				else
					return null;
			}
		};
		callerColumn.setSortable(true);
		sortHandler.setComparator(callerColumn, new Comparator<Quote>() {
			@Override
			public int compare(Quote o1, Quote o2) {
				return o1.getCallerAsString().compareTo(o2.getCallerAsString());
			}
		});
		dataGrid.setColumnWidth(callerColumn, 20, Unit.PCT);
		dataGrid.addColumn(callerColumn, new ResizableHeader("Caller", dataGrid, callerColumn));


		//timeIntervalColumn
		timeIntervalColumn = new Column<Quote, String>(new TextCell()) {
			@Override
			public String getValue(Quote object) {
				String target=object.getTimeInterval().toString();  
				return target;
			}
			/** for override style on clik */ 
			@Override
			public String getCellStyleNames(Context context, Quote object) {

				if (selectionModel.isSelected(object)) {
					return "boldStyle";
				}
				else
					return null;
			}
		};
		timeIntervalColumn.setSortable(true);
		sortHandler.setComparator(timeIntervalColumn, new Comparator<Quote>() {
			@Override
			public int compare(Quote o1, Quote o2) {
				return o1.getTimeInterval().compareTo(o2.getTimeInterval());
			}
		});
		dataGrid.setColumnWidth(timeIntervalColumn, 20, Unit.PCT);
		dataGrid.addColumn(timeIntervalColumn, new ResizableHeader("Time", dataGrid, timeIntervalColumn));


		//managerColumn
		managerColumn = new Column<Quote, String>(new TextCell()) {
			@Override
			public String getValue(Quote object) {
				String target=object.getManager().toString();  
				return target;
			}
			/** for override style on clik */ 
			@Override
			public String getCellStyleNames(Context context, Quote object) {

				if (selectionModel.isSelected(object)) {
					return "boldStyle";
				}
				else
					return null;
			}
		};
		managerColumn.setSortable(true);
		sortHandler.setComparator(managerColumn, new Comparator<Quote>() {
			@Override
			public int compare(Quote o1, Quote o2) {
				return o1.getManager().compareTo(o2.getManager());
			}
		});
		dataGrid.setColumnWidth(managerColumn, 20, Unit.PCT);
		dataGrid.addColumn(managerColumn, new ResizableHeader("Type", dataGrid, managerColumn));



		//quoteColumn
		quoteColumn = new Column<Quote, Number>(new NumberCell()) {
			@Override
			public Double getValue(Quote object) {
				Double target=object.getQuota();
				
				return target;
			}

		};
		quoteColumn.setSortable(true);
		sortHandler.setComparator(quoteColumn, new Comparator<Quote>() {
			@Override
			public int compare(Quote o1, Quote o2) {
				return o1.getQuota().compareTo(o2.getQuota());
			}
		});
		dataGrid.setColumnWidth(quoteColumn, 20, Unit.PCT);
		dataGrid.addColumn(quoteColumn, new ResizableHeader("Quota", dataGrid, quoteColumn));

		dataInsertColumn = new Column<Quote, Date>(new DateCell(DateTimeFormat.getFormat("EEE, d MMM yyyy HH:mm:ss"))) {
			@Override
			public Date getValue(Quote object) {
				return object.getDataInsert();
			}
		};
		dataInsertColumn.setSortable(true);

		sortHandler.setComparator(dataInsertColumn, new Comparator<Quote>() {
			@Override
			public int compare(Quote o1, Quote o2) {
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


		// dataUpdate.

		dataUpdateColumn = new Column<Quote, Date>(new DateCell(DateTimeFormat.getFormat("EEE, d MMM yyyy HH:mm:ss "))) {

			@Override
			public Date getValue(Quote object) {
				return object.getDataUpdate();
			}
		};
		dataUpdateColumn.setSortable(true);
		sortHandler.setComparator(dataUpdateColumn, new Comparator<Quote>() {
			@Override
			public int compare(Quote o1, Quote o2) {
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
		List<HasCell<Quote, ?>> cells = new LinkedList<HasCell<Quote, ?>>();
		cells.add(new ActionHasCell("Edit", new Delegate<Quote>() {
			@Override
			public void execute(Quote quote) {
				// EDIT CODE

				QuoteAddDialog dialogedit = new QuoteAddDialog();
				ArrayList<Quote> listModifiedQuote= new ArrayList<Quote>();
				listModifiedQuote.add(quote);
				dialogedit.setModifyQuote(listModifiedQuote);
				dialogedit.show();

			}
		}));	
		cells.add(new ActionHasCell("Delete", new Delegate<Quote>() {
			@Override
			public void execute(Quote quote) {
				// DELETE CODE	



				ArrayList<Quote> listDeleteQuotes =new ArrayList<Quote>();
				listDeleteQuotes.add(quote);
				QuoteDeleteDialog confirmDeleteDialog= new QuoteDeleteDialog(listDeleteQuotes);
				confirmDeleteDialog.show();

			}
		}));
		CompositeCell<Quote> cell = new CompositeCell<Quote>(cells);
		Column<Quote, Quote> actionColumn =new Column<Quote, Quote>(cell) {
			@Override
			public Quote getValue(Quote object) {
				return object;
			}
		};
		dataGrid.setColumnWidth(actionColumn, 15, Unit.PCT);
		dataGrid.addColumn(actionColumn, "Actions");	    

	}


	private class ActionHasCell implements HasCell<Quote, Quote> {
		private ActionCellClass<Quote> cell;

		public ActionHasCell(String text, Delegate<Quote> delegate) {
			cell = new ActionCellClass<Quote>(text, delegate);
		}

		@Override
		public Cell<Quote> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<Quote, Quote> getFieldUpdater() {
			return null;
		}

		@Override
		public Quote getValue(Quote object) {
			return object;
		}
	}

}
