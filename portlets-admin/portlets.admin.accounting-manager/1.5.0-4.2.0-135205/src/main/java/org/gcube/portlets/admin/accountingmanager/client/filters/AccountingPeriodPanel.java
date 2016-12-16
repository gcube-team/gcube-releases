package org.gcube.portlets.admin.accountingmanager.client.filters;

import java.util.Date;

import org.gcube.portlets.admin.accountingmanager.client.event.AccountingPeriodEvent;
import org.gcube.portlets.admin.accountingmanager.client.event.AccountingPeriodRequestEvent;
import org.gcube.portlets.admin.accountingmanager.client.event.StateChangeEvent;
import org.gcube.portlets.admin.accountingmanager.client.properties.AccountingPeriodModePropertiesCombo;
import org.gcube.portlets.admin.accountingmanager.client.util.UtilsGXT3;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingPeriod;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingPeriodMode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingPeriodPanel extends SimpleContainer {
	private DateTimeFormat dtf = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_DAY);
	private DateTimeFormat dtfYear = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR);
	private DateTimeFormat dtfShort = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);
	
	
	private EventBus eventBus;
	private DateField startDate;
	private DateField endDate;
	private ListStore<AccountingPeriodMode> storeCombo;
	private ComboBox<AccountingPeriodMode> comboPeriodMode;
	
	public AccountingPeriodPanel(EventBus eventBus) {
		super();
		Log.debug("AccountingPeriodPanel");
		this.eventBus = eventBus;
		init();
		create();
		bindToEvents();

	}

	private void init() {

	}

	private void create() {

		// Aggreagation Mode
		AccountingPeriodModePropertiesCombo props = GWT
				.create(AccountingPeriodModePropertiesCombo.class);
		storeCombo = new ListStore<AccountingPeriodMode>(props.id());
		storeCombo.addAll(AccountingPeriodMode.asList());

		comboPeriodMode = new ComboBox<AccountingPeriodMode>(storeCombo,
				props.label());
		comboPeriodMode.setMinListWidth(250);
		comboPeriodMode.setEditable(false);
		comboPeriodMode.setTypeAhead(false);
		comboPeriodMode.setAllowBlank(false);
		comboPeriodMode.setTriggerAction(TriggerAction.ALL);
		comboPeriodMode.setValue(AccountingPeriodMode.DAILY);
		addHandlersForComboPeriodMode(props.label());

		Log.debug("ComboPeriodMode created");

		FieldLabel periodModeLabel = new FieldLabel(comboPeriodMode,
				"Aggregation");

		//
		startDate = new DateField();
		startDate.addValidator(new EmptyValidator<Date>());
		startDate.addParseErrorHandler(new ParseErrorEvent.ParseErrorHandler() {

			@Override
			public void onParseError(ParseErrorEvent event) {
				Log.debug("Parse Error", event.getErrorValue()
						+ " could not be parsed as a date");

			}
		});
		startDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				String v = event.getValue() == null ? "Nothing"
						: DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)
								.format(event.getValue());
				Log.debug("Start Date Selected " + v);
				if (comboPeriodMode.getCurrentValue() == null) {
					UtilsGXT3.alert("Attention", "Select Aggregation!");
					startDate.reset();
					return;
				}

				switch (comboPeriodMode.getCurrentValue()) {
				case DAILY:
				case HOURLY:
				case MINUTELY:
				case YEARLY:	
					break;
				case MONTHLY:
					Date monthStartDate = event.getValue();
					CalendarUtil.setToFirstDayOfMonth(monthStartDate);
					startDate.setValue(monthStartDate);
					startDate.redraw();
					break;
				
					/*Date yearStartDate = event.getValue();
					String currentYearS = dtfYear.format(yearStartDate);
					Log.debug("YearStartSet=" + currentYearS + "-01-01");
					Date currentYearGen;
					try {
						currentYearGen = dtfShort.parse(currentYearS
								+ "-01-01");
					} catch (Exception e) {
						Log.debug("Error: "+e.getLocalizedMessage());
						UtilsGXT3.alert("Attention",
								"Error creating Start Date at begin of year!");
						startDate.reset();
						return;
					}
					Log.debug("CurrentYearGen="+dtfShort.format(currentYearGen));
					startDate.clear();
					startDate.setValue(currentYearGen);
					break;*/
				default:
					break;

				}

			}
		});

		FieldLabel startDateLabel = new FieldLabel(startDate, "Start Date");

		//
		endDate = new DateField();
		endDate.addValidator(new EmptyValidator<Date>());
		endDate.addParseErrorHandler(new ParseErrorEvent.ParseErrorHandler() {
			@Override
			public void onParseError(ParseErrorEvent event) {
				Log.debug("Parse Error", event.getErrorValue()
						+ " could not be parsed as a date");
			}
		});
		endDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				String v = event.getValue() == null ? "Nothing"
						: DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)
								.format(event.getValue());
				Log.debug("End Date Selected " + v);
				if (comboPeriodMode.getCurrentValue() == null) {
					UtilsGXT3.alert("Attention", "Select Aggregation!");
					endDate.reset();
					return;
				}

				switch (comboPeriodMode.getCurrentValue()) {
				case DAILY:
				case HOURLY:
				case MINUTELY:
				case YEARLY:	
					break;
				case MONTHLY:
					Date monthEndDate = event.getValue();
					CalendarUtil.setToFirstDayOfMonth(monthEndDate);
					CalendarUtil.addMonthsToDate(monthEndDate, 1);
					CalendarUtil.addDaysToDate(monthEndDate, -1);
					endDate.setValue(monthEndDate);
					endDate.redraw();
					break;
					/*Date yearEndDate = event.getValue();
					String currentYearS = dtfYear.format(yearEndDate);
					Log.debug("YearEndSet=" + currentYearS + "-12-31");
					Date yearEndDec;
					try {
						yearEndDec = dtfShort.parse(currentYearS
								+ "-12-31");
					} catch (Exception e) {
						Log.debug("Error: "+e.getLocalizedMessage());
						UtilsGXT3.alert("Attention",
								"Error creating End Date at end of year!");
						endDate.reset();
						return;
					}
					Log.debug("YearEndDec="+dtfShort.format(yearEndDec));
					endDate.clear();
					endDate.setValue(yearEndDec);
					break;*/
				default:
					break;

				}
			

			}
		});

		FieldLabel endDateLabel = new FieldLabel(endDate, "End Date");

		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(periodModeLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		vlc.add(startDateLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		vlc.add(endDateLabel, new VerticalLayoutData(1, -1, new Margins(0)));

		FieldSet fieldSet = new FieldSet();
		fieldSet.setHeadingHtml("<b>Temporal Constraint</b>");
		fieldSet.setCollapsible(false);
		fieldSet.add(vlc);

		add(fieldSet, new MarginData(0));

	}

	// Bind to Events
	private void bindToEvents() {
		eventBus.addHandler(StateChangeEvent.TYPE,
				new StateChangeEvent.StateChangeEventHandler() {

					@Override
					public void onStateChange(StateChangeEvent event) {
						Log.debug("Catch Event State Change");
						doStateChangeCommand(event);

					}
				});
		
		eventBus.addHandler(AccountingPeriodRequestEvent.TYPE,
				new AccountingPeriodRequestEvent.AccountingPeriodRequestEventHandler() {
					
					@Override
					public void onRequest(AccountingPeriodRequestEvent event) {
						Log.debug("Catch Event Accounting Period Request Event");
						manageAccountingPeriodRequestEvent(event);

						
					}
				});

		
	}
	
	private void manageAccountingPeriodRequestEvent(
			AccountingPeriodRequestEvent event) {
		AccountingPeriod accountingPeriod=getAccountingPeriod();
		AccountingPeriodEvent accountingPeriodEvent=new AccountingPeriodEvent(accountingPeriod);
		eventBus.fireEvent(accountingPeriodEvent);
		
	}

	private void addHandlersForComboPeriodMode(
			final LabelProvider<AccountingPeriodMode> labelProvider) {
		comboPeriodMode
				.addSelectionHandler(new SelectionHandler<AccountingPeriodMode>() {
					public void onSelection(
							SelectionEvent<AccountingPeriodMode> event) {
						Log.debug("ComboPeriodMode selected: "
								+ event.getSelectedItem());
						updateTimeInterval(event.getSelectedItem());
					}

				});
	}

	private void updateTimeInterval(AccountingPeriodMode accountingPeriodMode) {
		if (accountingPeriodMode == null) {
			startDate.disable();
			endDate.disable();
			startDate.setValue(new Date());
			endDate.setValue(new Date());
		}

		switch (accountingPeriodMode) {
		case MINUTELY:
		case HOURLY:
			startDate.disable();
			endDate.disable();
			startDate.setValue(new Date());
			endDate.setValue(new Date());
			break;
		case DAILY:
			Date lastMonth = new Date();
			CalendarUtil.addMonthsToDate(lastMonth, -1);
			startDate.enable();
			endDate.enable();
			startDate.setValue(lastMonth);
			endDate.setValue(new Date());
			break;
		case MONTHLY:
			startDate.enable();
			endDate.enable();
			Date lastYear = new Date();
			CalendarUtil.setToFirstDayOfMonth(lastYear);
			CalendarUtil.addMonthsToDate(lastYear, -12);
			startDate.setValue(lastYear);
			Date currentMonth = new Date();
			CalendarUtil.setToFirstDayOfMonth(currentMonth);
			CalendarUtil.addMonthsToDate(currentMonth, 1);
			CalendarUtil.addDaysToDate(currentMonth, -1);
			endDate.setValue(currentMonth);
			break;
		case YEARLY:
			startDate.enable();
			endDate.enable();
			Date currentYear = new Date();
			String currentYearS = dtfYear.format(currentYear);
			int yearLast3 = Integer.parseInt(currentYearS) - 3;
			Date last3Year = dtfShort.parse(yearLast3 + "-01-01");
			startDate.setValue(last3Year);
			Date endOfYear = dtfShort.parse(currentYearS + "-12-31");
			endDate.setValue(endOfYear);
			break;
		default:
			startDate.disable();
			endDate.disable();
			startDate.setValue(new Date());
			endDate.setValue(new Date());
			break;

		}

	}

	private void doStateChangeCommand(StateChangeEvent event) {
		if (event.getStateChangeType() == null) {
			return;
		}
		switch (event.getStateChangeType()) {
		case Restore:
			onRestoreStateChange(event);
			break;
		case Update:
			break;
		default:
			break;

		}

	}

	private void onRestoreStateChange(StateChangeEvent event) {
		if (event.getAccountingStateData() != null
				&& event.getAccountingStateData().getSeriesRequest() != null
				&& event.getAccountingStateData().getSeriesRequest()
						.getAccountingPeriod() != null) {
			AccountingPeriod accountingPeriod = event.getAccountingStateData()
					.getSeriesRequest().getAccountingPeriod();
			startDate.setValue(dtf.parse(accountingPeriod.getStartDate()));
			endDate.setValue(dtf.parse(accountingPeriod.getEndDate()));

			comboPeriodMode.setValue(accountingPeriod.getPeriod());
		} else {
			startDate.reset();
			endDate.reset();
			comboPeriodMode.reset();
			comboPeriodMode.setValue(AccountingPeriodMode.DAILY);
		}

		forceLayout();
	}

	public AccountingPeriod getAccountingPeriod() {
		try {

			if (startDate.validate() && endDate.validate()) {
				if (startDate.getCurrentValue().compareTo(new Date()) <= 0) {
					if (endDate.getCurrentValue().compareTo(new Date()) <= 0
							|| comboPeriodMode.getCurrentValue().compareTo(
									AccountingPeriodMode.MONTHLY) == 0
							|| comboPeriodMode.getCurrentValue().compareTo(
									AccountingPeriodMode.YEARLY) == 0) {

						Date startD = startDate.getCurrentValue();
						Date endD = endDate.getCurrentValue();

						if (startD.compareTo(endD) <= 0) {
							if (comboPeriodMode.validate()
									&& comboPeriodMode.getCurrentValue() != null) {

								switch (comboPeriodMode.getCurrentValue()) {
								case HOURLY:
								case MINUTELY:
									break;
								case DAILY:
									Date maximumDistantDay = new Date();
									CalendarUtil.addMonthsToDate(
											maximumDistantDay, -1);
									CalendarUtil.addDaysToDate(
											maximumDistantDay, -1);
									if (maximumDistantDay.compareTo(startD) >= 0) {
										UtilsGXT3
												.alert("Attention",
														"Invalid Start Date (Daily: the max interval should the last month)!");
										return null;
									}

									if (maximumDistantDay.compareTo(endD) >= 0) {
										UtilsGXT3
												.alert("Attention",
														"Invalid End Date (Daily: the max interval should the last month)!");

										return null;
									}
									break;
								case MONTHLY:
									Date maximumDistantMonth = new Date();
									CalendarUtil
											.setToFirstDayOfMonth(maximumDistantMonth);
									CalendarUtil.addMonthsToDate(
											maximumDistantMonth, -60);
									CalendarUtil.addDaysToDate(
											maximumDistantMonth, -1);
									if (maximumDistantMonth.compareTo(startD) >= 0) {
										UtilsGXT3
												.alert("Attention",
														"Invalid Start Date (Monthly: the max interval should in the last 5 years)!");
										return null;
									}

									Date maximumDistantMonthFuture = new Date();
									CalendarUtil
											.setToFirstDayOfMonth(maximumDistantMonthFuture);
									CalendarUtil.addMonthsToDate(
											maximumDistantMonthFuture, 1);
									Log.debug("[EndDate=" + endD
											+ ", maximumDistant="
											+ maximumDistantMonthFuture + "");
									if (maximumDistantMonthFuture
											.compareTo(endD) < 0) {
										UtilsGXT3
												.alert("Attention",
														"Invalid End Date (Monthly: the max interval should in the last 5 years)!");
										return null;
									}
									break;
								case YEARLY:
									break;
								default:
									break;

								}

								AccountingPeriod accountingPeriod = new AccountingPeriod(
										dtf.format(startD), dtf.format(endD),
										comboPeriodMode.getCurrentValue());
								return accountingPeriod;
							} else {
								UtilsGXT3.alert("Attention",
										"Select a valid aggregation mode!");
								return null;
							}
						} else {
							UtilsGXT3
									.alert("Attention",
											"The start date must be less than or equal to the end date!");
							return null;
						}

					} else {
						String endD = DateTimeFormat.getFormat(
								PredefinedFormat.DATE_SHORT).format(new Date());
						UtilsGXT3.alert("Attention", "The end date must be "
								+ endD + " or earlier!");
						return null;
					}
				} else {
					String startD = DateTimeFormat.getFormat(
							PredefinedFormat.DATE_SHORT).format(new Date());
					UtilsGXT3.alert("Attention", "The start date must be "
							+ startD + " or earlier!");
					return null;
				}

			} else {
				return null;
			}

		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
			UtilsGXT3.alert("Attention", e.getLocalizedMessage());
			return null;
		}
	}

}
