package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;

import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.util.SurveyOptions;
/**
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class SurveyTable extends Composite {
	/** The greeting service. */
	private final GreetingServiceAsync rpcService = GWT.create(GreetingService.class);
	/**
	 * Get a cell value from a record.
	 * 
	 * @param <C> the cell type
	 */
	private static interface GetValue<C> {
		C getValue(SurveyModel invite);
	}

	private CellTable<SurveyModel> table = new CellTable<SurveyModel>();

	private ListDataProvider<SurveyModel> dataProvider = new ListDataProvider<SurveyModel>();

	private List<SurveyOptions> optionsMap = new ArrayList<>();
	private List<String> options = new ArrayList<>();

	private SurveyHomePage surveyHp;

	public SurveyTable(final SurveyHomePage surveyHp, List<SurveyModel> surveys) {
		this.surveyHp = surveyHp;
		dataProvider.setList(surveys);
		table.setVisibleRange(new Range(0, surveys.size()));
		// Connect the table to the data provider.
		dataProvider.addDataDisplay(table);
		table.setWidth("95%", false);
		table.setStriped(true);
		table.setBordered(true);
		table.setRowCount(surveys.size(), true);
		table.addStyleName("invitesTable");

		optionsMap.add(SurveyOptions.SELECT);
		optionsMap.add(SurveyOptions.GET_LINK);
		optionsMap.add(SurveyOptions.STATISTICS);
		optionsMap.add(SurveyOptions.MODIFY);
		optionsMap.add(SurveyOptions.DELETE);

		for (SurveyOptions opt : optionsMap) {
			options.add(opt.getDisplayLabel());
		}

		Column<SurveyModel, String> surveyName = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(SurveyModel survey) {
				return survey.getTitlesurvey();
			}
		});

		Column<SurveyModel, String> surveyCreator = getColumn(new TextCell(), new GetValue<String>() {
			@Override
			public String getValue(SurveyModel survey) {
				return survey.getCreatorFullname();
			}
		});

		// DateCell.
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);
		Column<SurveyModel, Date> surveyCreateDate = getColumn(new DateCell(dateFormat), new GetValue<Date>() {
			@Override
			public Date getValue(SurveyModel survey) {
				return survey.getDateSurvay();
			}
		});

		Column<SurveyModel, Date> surveyExpireDate = getColumn(new DateCell(dateFormat), new GetValue<Date>() {
			@Override
			public Date getValue(SurveyModel survey) {
				return survey.getExpiredDateSurvay();
			}
		});

		Column<SurveyModel, Number> surveysFilled = getColumn(new NumberCell(), new GetValue<Number>() {
			@Override
			public Number getValue(SurveyModel survey) {
				int number = surveyHp.getFilledSurveys().get(survey.getIdsurvey()) == null ? 0 : surveyHp.getFilledSurveys().get(survey.getIdsurvey());
				return number;
			}
		});

		Column<SurveyModel, String> selectColumn = new Column<SurveyModel, String>(new SelectionCell(options)) {			
			@Override
			public String getValue(SurveyModel survey) {
				return survey.getIdsurvey()+"";
			}

			@Override
			public void onBrowserEvent(Cell.Context context, final Element parent, final SurveyModel survey, NativeEvent event) {
				event.preventDefault();
				if (BrowserEvents.CHANGE.equals(event.getType())) {
					SelectElement select = parent.getFirstChild().cast();
					switch (optionsMap.get(select.getSelectedIndex())) {
					case GET_LINK:
						handleInviteSurvey(survey, select);
						break;
					case STATISTICS:	
						handleSeeSurveyStats(survey, select);
						break;
					case MODIFY:
						handleEditSurvey(survey, select);
						break;
					case DELETE:
						handleDeleteSurvey(survey, select);
						break;
					default:
						break;
					}
				}		
			}
		};

		table.addColumn(surveyName, "Survey name");
		table.addColumn(surveyCreator, "Created by");
		table.addColumn(surveyCreateDate, "Create date");
		table.addColumn(surveyExpireDate, "Expires on");
		table.addColumn(surveysFilled, "Filled Surveys");		
		table.addColumn(selectColumn, "Available options");

		surveyName.setSortable(true);
		ListHandler<SurveyModel> surveyTitleColHandler = new ListHandler<SurveyModel>(dataProvider.getList());
		surveyTitleColHandler.setComparator(surveyName, new Comparator<SurveyModel>() {
			@Override
			public int compare(SurveyModel o1, SurveyModel o2) {
				return o1.getTitlesurvey().compareTo(o2.getTitlesurvey());

			}
		});
		table.addColumnSortHandler(surveyTitleColHandler);

		surveyCreator.setSortable(true);
		ListHandler<SurveyModel> surveyCreatorColHandler = new ListHandler<SurveyModel>(dataProvider.getList());
		surveyCreatorColHandler.setComparator(surveyCreator, new Comparator<SurveyModel>() {
			@Override
			public int compare(SurveyModel o1, SurveyModel o2) {
				return o1.getCreatorFullname().compareTo(o2.getCreatorFullname());

			}
		});
		table.addColumnSortHandler(surveyCreatorColHandler);

		surveyCreateDate.setSortable(true);	
		surveyCreateDate.setDefaultSortAscending(true);
		ListHandler<SurveyModel> dateColHandler = new ListHandler<SurveyModel>(dataProvider.getList());
		dateColHandler.setComparator(surveyCreateDate, new Comparator<SurveyModel>() {
			@Override
			public int compare(SurveyModel o1, SurveyModel o2) {
				Date date1 = o1.getDateSurvay();
				Date date2 = o2.getDateSurvay();

				if(date1.before(date2))
					return -1;
				else if (date1.after(date2))
					return 1;
				else
					return 0;

			}
		});		
		table.addColumnSortHandler(dateColHandler);


		surveyExpireDate.setSortable(true);	
		surveyExpireDate.setDefaultSortAscending(true);
		ListHandler<SurveyModel> dateExpiryColHandler = new ListHandler<SurveyModel>(dataProvider.getList());
		dateExpiryColHandler.setComparator(surveyExpireDate, new Comparator<SurveyModel>() {
			@Override
			public int compare(SurveyModel o1, SurveyModel o2) {
				Date date1 = o1.getExpiredDateSurvay();
				Date date2 = o2.getExpiredDateSurvay();

				if(date1.before(date2))
					return -1;
				else if (date1.after(date2))
					return 1;
				else
					return 0;

			}
		});		
		table.addColumnSortHandler(dateExpiryColHandler);

		// We know that the data is sorted alphabetically by default.
		table.getColumnSortList().push(surveyCreateDate);

		initWidget(table);
	}

	/**
	 * get a column 
	 * 
	 * @param <C> the cell type
	 * @param cell the cell used to render the column
	 * @param getter the value getter for the cell
	 */
	private <C> Column<SurveyModel, C> getColumn(Cell<C> cell, final GetValue<C> getter) {
		Column<SurveyModel, C> column = new Column<SurveyModel, C>(cell) {
			@Override
			public C getValue(SurveyModel object) {
				return getter.getValue(object);
			}
		};		
		return column;
	}
	/**
	 * 
	 * @param selectedSurvey
	 * @param select
	 */
	private void handleInviteSurvey(final SurveyModel selectedSurvey, final SelectElement select) {
		if (selectedSurvey.getExpiredDateSurvay().before(new Date())) {
			Window.alert("Sorry, this survey is expired. You cannot invite any user to fill in an expired survey");
			select.setSelectedIndex(0);
			return;
		}
		rpcService.getSurveyInvitationLink(selectedSurvey.getIdsurvey(), selectedSurvey.getIsAnonymous(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Some error occurred in the server " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				if (result == null) {
					Window.alert("Ops, we think the take survey application is not enabled on this VRE, please report this issue.");
					select.setSelectedIndex(0);
				}
				Location.assign(result);
			}
		});
	}
	/**
	 * 
	 * @param selectedSurvey
	 * @param select
	 */
	private void handleEditSurvey(final SurveyModel selectedSurvey, final SelectElement select) {
		this.surveyHp.getSurveyQuestionModelListModifySelectedSurvey().clear();
		this.surveyHp.surveySelected(selectedSurvey);
		select.setSelectedIndex(0);
		this.surveyHp.getQuestionsSurvey(selectedSurvey.getIdsurvey());
	}
	/**
	 * 
	 * @param selectedSurvey
	 * @param select
	 */
	private void handleSeeSurveyStats(final SurveyModel selectedSurvey, final SelectElement select) {
		this.surveyHp.surveySelected(selectedSurvey);
		int number = surveyHp.getFilledSurveys().get(selectedSurvey.getIdsurvey()) == null ? 0 : surveyHp.getFilledSurveys().get(selectedSurvey.getIdsurvey());
		GWT.log("Stats"+number);
		if(number == 0){
			Window.alert("No VRE's member have partecipated to this survey, no statistics to show.");
			select.setSelectedIndex(0);
			return;
		}
		GWT.log("Stats3");
		this.surveyHp.statView(selectedSurvey, this.surveyHp.getFilledSurveys());
	}

	private void handleDeleteSurvey(final SurveyModel selectedSurvey, final SelectElement select) {
		if (Window.confirm("Are you sure you want to delete this survey? \nThis action deletes VRE's members answers too.")) {
			rpcService.deleteSurvey(selectedSurvey, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					select.setSelectedIndex(0);
				}
				@Override
				public void onSuccess(Void result) {
					select.setSelectedIndex(0);
					Window.Location.reload();
				}
			});
		} else {
			select.setSelectedIndex(0);
			return;
		}

	}
}

