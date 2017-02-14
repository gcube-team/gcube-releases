/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.client.util.UtilsGXT3;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.WKTParameter;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.event.SelectAreaDialogEvent;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.event.SelectAreaDialogEvent.SelectAreaDialogEventHandler;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.widgets.AreaSelectionDialog;
import org.gcube.portlets.widgets.openlayerbasicwidgets.client.widgets.GeometryType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class WKTFld extends AbstractFld {

	private VerticalLayoutContainer vp;

	// FileSelector fileSelector;
	private AreaSelectionDialog areaSelectionDialog;
	private TextButton selectButton, selectButton2, cancelButton;
	private String selectedArea = null;

	private WKTParameter wktParameter;

	private SimpleContainer fieldContainer;

	private HBoxLayoutContainer horiz;

	private TextField selectedAreaField;

	/**
	 * @param parameter
	 */
	public WKTFld(Parameter parameter) {
		super(parameter);
		wktParameter = (WKTParameter) parameter;

		SimpleContainer wktContainer = new SimpleContainer();
		vp = new VerticalLayoutContainer();
		init();
		wktContainer.add(vp, new MarginData(new Margins(0)));

		fieldContainer = new SimpleContainer();
		horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		HtmlLayoutContainer descr;

		if (wktParameter.getDescription() == null) {
			descr = new HtmlLayoutContainer(
					"<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			descr = new HtmlLayoutContainer(
					"<p style='margin-left:5px !important;'>"
							+ wktParameter.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}

		horiz.add(wktContainer, new BoxLayoutData(new Margins()));
		horiz.add(descr, new BoxLayoutData(new Margins()));

		fieldContainer.add(horiz);
		showNoSelectionField();

	}

	private GeometryType getGeometryType() {
		if (wktParameter.getWktGeometryType() == null) {
			return GeometryType.Polygon;
		}

		switch (wktParameter.getWktGeometryType()) {
		case Box:
			return GeometryType.Box;
		case Circle:
			return GeometryType.Circle;
		case Hexagon:
			return GeometryType.Hexagon;
		case LineString:
			return GeometryType.LineString;
		case Pentagon:
			return GeometryType.Pentagon;
		case Point:
			return GeometryType.Point;
		case Polygon:
			return GeometryType.Polygon;
		case Square:
			return GeometryType.Square;
		case Triangle:
			return GeometryType.Triangle;
		default:
			return GeometryType.Polygon;

		}
	}

	private void drawAGeometry() {
		SelectAreaDialogEventHandler handler = new SelectAreaDialogEventHandler() {

			@Override
			public void onResponse(SelectAreaDialogEvent event) {
				GWT.log("SelectAreaDialog Response: " + event);
				switch (event.getSelectAreaDialogEventType()) {
				case Aborted:
					Log.debug("No area selected!");
					break;
				case Completed:
					selectedArea = event.getArea();
					Log.debug("SelectedFileItem: " + selectedArea);
					showFieldWithSelection();
					break;
				case Failed:
					Log.error("Error during area selection: "
							+ event.getException());
					UtilsGXT3.alert("Error", event.getErrorMessage());
					break;
				default:
					break;

				}
			}
		};

		areaSelectionDialog = new AreaSelectionDialog(getGeometryType());
		areaSelectionDialog.setZIndex(XDOM.getTopZIndex());
		areaSelectionDialog.addSelectAreaDialogEventHandler(handler);
		areaSelectionDialog.show();

	}

	private void init() {
		selectButton = new TextButton("Draw a "
				+ wktParameter.getWktGeometryType().getLabel());
		selectButton.setIcon(DataMinerManager.resources.drawGeometry());
		selectButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				drawAGeometry();

			}
		});
		//selectButton.setToolTip("Draw a "
		//		+ wktParameter.getWktGeometryType().getLabel());

		selectButton2 = new TextButton("");
		selectButton2.setIcon(DataMinerManager.resources.folderExplore());
		selectButton2.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				drawAGeometry();
			}
		});
		//selectButton2.setToolTip("Select Another "
		//		+ wktParameter.getWktGeometryType().getLabel());

		cancelButton = new TextButton("");
		cancelButton.setIcon(DataMinerManager.resources.cancel());
		cancelButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				selectedArea = null;
				showNoSelectionField();
			}
		});

	}

	private void showNoSelectionField() {
		vp.clear();
		vp.add(selectButton);
		vp.forceLayout();
		fieldContainer.forceLayout();
	}

	private void showFieldWithSelection() {
		if (selectedArea == null || selectedArea.isEmpty()) {
			selectedArea = "";
		}

		selectedAreaField = new TextField();
		if (selectedArea != null && !selectedArea.isEmpty()) {
			selectedAreaField.setValue(selectedArea);
		}
		selectedAreaField.setReadOnly(true);

		HBoxLayoutContainer h = new HBoxLayoutContainer();
		h.add(selectedAreaField, new BoxLayoutData(new Margins()));
		h.add(selectButton2, new BoxLayoutData(new Margins()));
		h.add(cancelButton, new BoxLayoutData(new Margins()));
		vp.clear();
		vp.add(h);
		vp.forceLayout();
		fieldContainer.forceLayout();

	}

	@Override
	public String getValue() {
		if (selectedAreaField != null) {
			return selectedAreaField.getCurrentValue();
		} else {
			return wktParameter.getDefaultValue();
		}
	}

	@Override
	public boolean isValid() {
		if (selectedAreaField != null) {
			return selectedAreaField.isValid();
		} else {
			return true;
		}
	}

	@Override
	public Widget getWidget() {
		return fieldContainer;
	}

}
