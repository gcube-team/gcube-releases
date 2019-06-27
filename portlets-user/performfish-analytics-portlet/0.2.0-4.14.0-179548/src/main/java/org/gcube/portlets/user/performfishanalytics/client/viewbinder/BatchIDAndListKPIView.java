/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.viewbinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant.POPULATION_LEVEL;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsServiceAsync;
import org.gcube.portlets.user.performfishanalytics.client.controllers.PerformFishAnalyticsController;
import org.gcube.portlets.user.performfishanalytics.client.event.AddedBatchIdEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadBatchesEvent;
import org.gcube.portlets.user.performfishanalytics.client.view.LoaderIcon;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVRow;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


// TODO: Auto-generated Javadoc
/**
 * The Class BatchIDAndListKPIView.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 28, 2019
 */
public class BatchIDAndListKPIView extends Composite {

	/** The ui binder. */
	private static BatchIDAndListKPIViewUiBinder uiBinder =
		GWT.create(BatchIDAndListKPIViewUiBinder.class);


	/**
	 * The Interface BatchIDAndListKPIViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * Feb 28, 2019
	 */
	interface BatchIDAndListKPIViewUiBinder
		extends UiBinder<Widget, BatchIDAndListKPIView> {
	}

	/** The the panel container. */
	@UiField
	VerticalPanel the_panel_container;

	/** The uib list batch id. */
	@UiField
	ListBox uib_list_batch_id;

	/** The the panel error. */
	@UiField
	HorizontalPanel the_panel_error;

	/** The cg batch id. */
	@UiField
	ControlGroup cg_batch_id;
	
	/** The uib button load batches. */
	@UiField
	Button uib_button_load_batches;

	/** The perform fish response. */
	private PerformFishResponse performFishResponse;

	/** The map parameters. */
	private Map<String, List<String>> mapParameters;


	/** The list batches ID. */
	private List<String> listBatchesID = new ArrayList<String>();


	/**
	 * Instantiates a new batch id and list kpi view.
	 */
	public BatchIDAndListKPIView() {

		initWidget(uiBinder.createAndBindUi(this));
		uib_list_batch_id.setEnabled(false);
		
		
		uib_button_load_batches.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PerformFishAnalyticsController.eventBus.fireEvent(new LoadBatchesEvent());
			}
		});
	}


	/**
	 * Adds the.
	 *
	 * @param w the w
	 */
	public void add(Widget w){
		the_panel_container.add(w);

	}

	/**
	 * Gets the all batches ID.
	 *
	 * @return the all batches ID
	 */
	public List<String> getAllBatchesID(){
		return listBatchesID;
	}


	/**
	 * Gets the selected.
	 *
	 * @param listBox the list box
	 * @return the selected
	 */
	private List<String> getSelected(ListBox listBox){

		if(listBox==null)
			listBox = uib_list_batch_id;

		List<String> selected = new ArrayList<String>();
		for (int i=0; i<listBox.getItemCount(); i++) {
			if (listBox.isItemSelected(i)) {
				selected.add(listBox.getValue(i)); //reading the value
	        }
		}
		return selected;
	}


	/**
	 * Gets the selected batch id.
	 *
	 * @return the selected batch id
	 */
	public List<String> getSelectedBatchID(){
		return getSelected(uib_list_batch_id);
	}

	/**
	 * Manage perform fish service response.
	 *
	 * @param performFishResponse the perform fish response
	 * @param mapParameters the map parameters
	 * @param level the level
	 */
	public void managePerformFishServiceResponse(
		PerformFishResponse performFishResponse,
		Map<String, List<String>> mapParameters, final POPULATION_LEVEL level) {
		this.performFishResponse = performFishResponse;
		this.mapParameters = mapParameters;

		GWT.log("PerformFish Response: "+performFishResponse);

		String fileURL = performFishResponse.getMapParameters().get(PerformFishAnalyticsConstant.BATCHES_TABLE_INTERNAL);

		GWT.log("BatchesTable_internal is: "+fileURL);

		//Resetting batch ID and panel error after calling Perform Fish Service
		resetBatchIdStatus();

		//Managing the Perform Fish Service Response
		if(fileURL==null){
			showAlert("No select found for "+PerformFishAnalyticsConstant.BATCHES_TABLE_INTERNAL, AlertType.ERROR, false, the_panel_error);
		}else{

			final LoaderIcon loader = new LoaderIcon("Loading Values...");
			the_panel_container.insert(loader, 1);
			//field_list_focus_id_dea.setEnabled(false);
			uib_list_batch_id.setEnabled(false);

			PerformFishAnalyticsServiceAsync.Util.getInstance().readCSVFile(fileURL, new AsyncCallback<CSVFile>() {

				@Override
				public void onFailure(Throwable caught) {
					loader.setVisible(false);
					the_panel_container.remove(loader);
					Window.alert(caught.getMessage());

				}

				@Override
				public void onSuccess(CSVFile result) {
					loader.setVisible(false);
					the_panel_container.remove(loader);

					if(result==null){
						showAlert("No value found for "+PerformFishAnalyticsConstant.BATCHES_TABLE_INTERNAL, AlertType.ERROR, false, the_panel_error);
						enableButtonLoadBatches(false);
						return;
					}

					//field_list_focus_id_dea.setEnabled(true);
					uib_list_batch_id.setEnabled(true);

					//IT CAN BE "BATCH", "FARM", etc.
					String theScalePParamValue = level.name();
					int indexOfTheScaleValue = result.getHeaderRow().getListValues().indexOf(theScalePParamValue);

					if(indexOfTheScaleValue>-1){
						List<CSVRow> rows = result.getValueRows();

						if(rows==null || rows.isEmpty()){
							PerformFishAnalyticsController.eventBus.fireEvent(new AddedBatchIdEvent());
							return;
						}

						for (CSVRow row : rows) {
							String valuePerScaleP = row.getListValues().get(indexOfTheScaleValue);
							//field_list_focus_id_dea.addItem(valuePerScaleP, valuePerScaleP);
							uib_list_batch_id.addItem(valuePerScaleP, valuePerScaleP);
							listBatchesID.add(valuePerScaleP);
						}

						uib_list_batch_id.addItem(PerformFishAnalyticsConstant.DM_FOCUS_ID_ALL_ITEM_TEXT, PerformFishAnalyticsConstant.DM_FOCUS_ID_ALL_ITEM_VALUE);
						listBatchesID.add(PerformFishAnalyticsConstant.DM_FOCUS_ID_ALL_ITEM_VALUE);

						PerformFishAnalyticsController.eventBus.fireEvent(new AddedBatchIdEvent());
					}
				}
			});
		}
	}
	
	/**
	 * Reset batch id status.
	 */
	public void resetBatchIdStatus() {
		//Resetting batch ID and panel error after calling Perform Fish Service
		uib_list_batch_id.clear();
		the_panel_error.clear();
		listBatchesID.clear();
	}

	/**
	 * Show alert.
	 *
	 * @param error the error
	 * @param type the type
	 * @param closable the closable
	 * @param panel the panel
	 */
	private void showAlert(String error, AlertType type, boolean closable, ComplexPanel panel){
		panel.clear();
		Alert alert = new Alert(error);
		alert.setType(type);
		alert.setClose(closable);
		alert.getElement().getStyle().setMargin(10, Unit.PX);
		panel.add(alert);
	}

	
	/**
	 * Show alert.
	 *
	 * @param error the error
	 * @param type the type
	 * @param closable the closable
	 */
	public void showAlert(String error, AlertType type, boolean closable){
		showAlert(error, type, closable, the_panel_error);
	}


	/**
	 * Show selection ok.
	 *
	 * @param msg the msg
	 * @param closable the closable
	 */
	public void showSelectionOK(String msg, boolean closable){
		showAlert(msg, AlertType.INFO, closable, the_panel_error);
	}


	/**
	 * Gets the control group batch id.
	 *
	 * @return the control group batch id
	 */
	public ControlGroup getControlGroupBatchID() {

		return cg_batch_id;
	}


	/**
	 * Gets the list box batch id.
	 *
	 * @return the list box batch id
	 */
	public ListBox getListBoxBatchId() {

		return uib_list_batch_id;
	}


	/**
	 * Gets the perform fish response.
	 *
	 * @return the performFishResponse
	 */
	public PerformFishResponse getPerformFishResponse() {

		return performFishResponse;
	}


	/**
	 * Gets the map parameters.
	 *
	 * @return the mapParameters
	 */
	public Map<String, List<String>> getMapParameters() {

		return mapParameters;
	}
	
	/**
	 * Enable button load batches.
	 *
	 * @param enable the enable
	 */
	public void enableButtonLoadBatches(boolean enable) {
		uib_button_load_batches.setEnabled(enable);
		
		if(enable)
			uib_list_batch_id.setTitle("Click the 'Load Batches' button");
		else
			uib_list_batch_id.setTitle("");

	}


}
