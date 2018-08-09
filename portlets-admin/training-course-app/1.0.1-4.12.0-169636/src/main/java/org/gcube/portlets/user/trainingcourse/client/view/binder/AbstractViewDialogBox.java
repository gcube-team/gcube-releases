package org.gcube.portlets.user.trainingcourse.client.view.binder;


import org.gcube.portlets.user.trainingcourse.client.view.LoaderIcon;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.base.AlertBase;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


// TODO: Auto-generated Javadoc
/**
 * The Class AbstractViewDialogBox.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 8, 2018
 */
public abstract class AbstractViewDialogBox extends Composite {

	/** The ui binder. */
	private static AbstractViewDialogBoxUiBinder uiBinder =
		GWT.create(AbstractViewDialogBoxUiBinder.class);
	
	/** The default width. */
	public static int DEFAULT_WIDTH = 500;

	/**
	 * The Interface AbstractViewDialogBoxUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jan 9, 2018
	 */
	interface AbstractViewDialogBoxUiBinder
		extends UiBinder<Widget, AbstractViewDialogBox> {
	}
	
	
	/** The button close dialog. */
	@UiField
	Button button_close_dialog;
	
	/** The field root panel. */
	@UiField
	HTMLPanel field_root_panel;
	
	/** The scroll panel. */
	@UiField
	ScrollPanel scroll_panel;
	
	/** The validator field. */
	@UiField
	FluidRow validator_field;
	
	/** The loader icon. */
	private LoaderIcon loaderIcon = new LoaderIcon();
	
	/** The alert error. */
	protected Alert alertError = new Alert();

	
	/** The alert info. */
	protected AlertBase alertInfo = new Alert();
	
	/**
	 * Close handler.
	 */
	public abstract void closeHandler();

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public AbstractViewDialogBox() {

		initWidget(uiBinder.createAndBindUi(this));
		setWidth(DEFAULT_WIDTH+"px");
	
		alertError.setType(AlertType.ERROR);
		alertError.setVisible(false);
		alertError.setClose(false);
		validator_field.add(alertError);
		
		showLoading(false, "");
		field_root_panel.add(loaderIcon);
		
		bindEvents();
	}
	
	/**
	 * Bind events.
	 */
	private void bindEvents() {
		
		button_close_dialog.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				closeHandler();

			}
		});
	}
	

	/**
	 * Adds the view as widget.
	 *
	 * @param child the child
	 */
	public void addViewAsWidget(Widget child) {

		scroll_panel.add(child);
	}
	
	/**
	 * Show loading.
	 *
	 * @param visible the visible
	 * @param text the text
	 */
	public void showLoading(boolean visible, String text){
		loaderIcon.setVisible(visible);
		loaderIcon.setText(text);
	}
	

	/**
	 * Sets the error.
	 *
	 * @param visible the visible
	 * @param error the error
	 */
	public void setError(boolean visible, String error) {
		alertError.setVisible(visible);
		alertError.setText(error==null?"":error);
	}
	

}
