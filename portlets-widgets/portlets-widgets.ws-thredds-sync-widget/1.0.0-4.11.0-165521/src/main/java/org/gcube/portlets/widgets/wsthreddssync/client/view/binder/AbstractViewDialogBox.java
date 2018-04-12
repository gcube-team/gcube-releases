package org.gcube.portlets.widgets.wsthreddssync.client.view.binder;


import org.gcube.portlets.widgets.wsthreddssync.client.dialog.PanelConfirmBuilder;
import org.gcube.portlets.widgets.wsthreddssync.client.view.LoaderIcon;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
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
	 * The Enum CONFIRM_VALUE.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Mar 13, 2018
	 */
	public static enum CONFIRM_VALUE {YES, NO}
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
	protected Alert alertInfo = new Alert();


	protected PanelConfirmBuilder confirmBuilder;

	/**
	 * Close handler.
	 */
	public abstract void closeHandler();


	/**
	 * Confirm hanlder.
	 *
	 * @param confirm the confirm
	 */
	public abstract void confirmHanlder(CONFIRM_VALUE confirm, Command command);

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
		//setWidth(DEFAULT_WIDTH+"px");

		alertError.setType(AlertType.ERROR);
		alertError.setVisible(false);
		alertError.setClose(false);
		validator_field.add(alertError);

		alertInfo.setType(AlertType.INFO);
		alertInfo.setVisible(false);
		alertInfo.setClose(false);
		validator_field.add(alertInfo);

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

		try{
			validator_field.remove(loaderIcon);
		}catch(Exception e){

		}

		loaderIcon.setVisible(visible);
		loaderIcon.setText(text);

		validator_field.add(loaderIcon);
	}


	/**
	 * Sets the error.
	 *
	 * @param visible the visible
	 * @param error the error
	 */
	public void setError(boolean visible, String error) {

		try{
			validator_field.remove(alertError);
		}catch(Exception e){

		}

		alertError.setVisible(visible);
		alertError.setText(error==null?"":error);

		validator_field.add(alertError);
	}


	/**
	 * Sets the error.
	 *
	 * @param visible the visible
	 * @param msg the msg
	 */
	public void setInfo(boolean visible, String msg) {


		try{
			validator_field.remove(alertInfo);
		}catch(Exception e){

		}

		alertInfo.setVisible(visible);
		alertInfo.setText(msg==null?"":msg);

		validator_field.add(alertInfo);
	}


	/**
	 * Sets the confirm.
	 *
	 * @param visible the visible
	 * @param msg the msg
	 */
	public void setConfirm(boolean visible, String msg, final Command yes, final Command no){

		try{
			validator_field.remove(confirmBuilder.getPanel());
		}catch(Exception e){

		}

		if(visible){

			confirmBuilder = new PanelConfirmBuilder(null, null, msg, AlertType.WARNING) {

				@Override
				public void onClickYesButton() {

					confirmHanlder(CONFIRM_VALUE.YES, yes);
				}

				@Override
				public void onClickNoButton() {

					confirmHanlder(CONFIRM_VALUE.NO, no);
				}
			};

			validator_field.add(confirmBuilder.getPanel());
		}

	}

}
