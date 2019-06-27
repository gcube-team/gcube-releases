/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.viewannualbinder;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
public class AnnualListKPIView extends Composite {

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
		extends UiBinder<Widget, AnnualListKPIView> {
	}

	/** The the panel container. */
	@UiField
	VerticalPanel the_panel_container;


	/** The the panel error. */
	@UiField
	HorizontalPanel the_panel_error;


	/** The perform fish response. */
	private PerformFishResponse performFishResponse;

	/** The map parameters. */
	private Map<String, List<String>> mapParameters;



	/**
	 * Instantiates a new batch id and list kpi view.
	 */
	public AnnualListKPIView() {

		initWidget(uiBinder.createAndBindUi(this));
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
//
//	/**
//	 * Gets the perform fish response.
//	 *
//	 * @return the performFishResponse
//	 */
//	public PerformFishResponse getPerformFishResponse() {
//
//		return performFishResponse;
//	}
//
//
//	/**
//	 * Gets the map parameters.
//	 *
//	 * @return the mapParameters
//	 */
//	public Map<String, List<String>> getMapParameters() {
//
//		return mapParameters;
//	}
	
}
