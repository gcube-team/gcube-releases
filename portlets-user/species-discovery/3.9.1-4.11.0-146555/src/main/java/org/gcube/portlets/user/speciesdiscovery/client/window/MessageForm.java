/**
 *
 */

package org.gcube.portlets.user.speciesdiscovery.client.window;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.ResizeType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class DialogInfo.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 12, 2017
 */
public abstract class MessageForm extends Composite {

	/**
	 *
	 */
	private static final String LOADING_DEFAULT_MSG = "Loading...";
	@UiField
	TextArea text_info;
	@UiField
	Button close_dialog;
	@UiField
	Form form_info;
	@UiField
	ControlGroup text_info_group;
	@UiField
	FlowPanel loading_field;
	@UiField
	HorizontalPanel hp_form_actions;
	private Label loadingLabel = new Label();
	private static AbstractFormReleaseUiBinder uiBinder =
		GWT.create(AbstractFormReleaseUiBinder.class);
	private int width = 400;

	/**
	 * Close handler.
	 */
	public abstract void closeHandler();

	/**
	 * The Interface AbstractFormReleaseUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 12,
	 *         2017
	 */
	interface AbstractFormReleaseUiBinder extends UiBinder<Widget, MessageForm> {
	}

	/**
	 * Instantiates a new dialog info.
	 *
	 * @param waiting
	 *            the waiting
	 * @param msgWaiting
	 *            the msg waiting
	 */
	public MessageForm(boolean waiting, String msgWaiting) {

		initDialog();
		if (waiting) {
			showWaiting(msgWaiting);
		}
	}

	/**
	 * Instantiates a new dialog info.
	 *
	 * @param msgInfo
	 *            the msg info
	 */
	public MessageForm(String msgInfo) {

		initDialog();
		setTextMessage(msgInfo, true);
	}

	/**
	 * Inits the dialog.
	 */
	private void initDialog() {

		initWidget(uiBinder.createAndBindUi(this));
		setWaitingMessageVisible(false);
		text_info.setWidth(width - 10 + "px");
		text_info.setReadOnly(true);
		text_info.setResize(ResizeType.VERTICAL);
		close_dialog.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				closeHandler();
			}
		});
		hp_form_actions.setCellHorizontalAlignment(
			close_dialog, HasHorizontalAlignment.ALIGN_RIGHT);
	}

	/**
	 * Inits the waiting.
	 *
	 * @param msg
	 *            the msg
	 */
	private void showWaiting(String msg) {

		HorizontalPanel hp = new HorizontalPanel();
		loadingLabel.getElement().getStyle().setMarginBottom(2, Unit.PX);
		loadingLabel.getElement().getStyle().setMarginRight(2, Unit.PX);
		// loadingLabel.setClose(false);
		msg = msg == null || msg.isEmpty() ? LOADING_DEFAULT_MSG : msg;
		loadingLabel.setText(msg);
		HTML imgLoading =
			new HTML(
				"<img src=\"data:image/gif;base64,R0lGODlhEAAQAPQAAP///z1NjfP096Wtyufp8HJ9rJmiwz1NjX+KtFhmnb/E2c3R4UxalbO50kBPjmZypYyVuwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH+GkNyZWF0ZWQgd2l0aCBhamF4bG9hZC5pbmZvACH5BAAKAAAAIf8LTkVUU0NBUEUyLjADAQAAACwAAAAAEAAQAAAFdyAgAgIJIeWoAkRCCMdBkKtIHIngyMKsErPBYbADpkSCwhDmQCBethRB6Vj4kFCkQPG4IlWDgrNRIwnO4UKBXDufzQvDMaoSDBgFb886MiQadgNABAokfCwzBA8LCg0Egl8jAggGAA1kBIA1BAYzlyILczULC2UhACH5BAAKAAEALAAAAAAQABAAAAV2ICACAmlAZTmOREEIyUEQjLKKxPHADhEvqxlgcGgkGI1DYSVAIAWMx+lwSKkICJ0QsHi9RgKBwnVTiRQQgwF4I4UFDQQEwi6/3YSGWRRmjhEETAJfIgMFCnAKM0KDV4EEEAQLiF18TAYNXDaSe3x6mjidN1s3IQAh+QQACgACACwAAAAAEAAQAAAFeCAgAgLZDGU5jgRECEUiCI+yioSDwDJyLKsXoHFQxBSHAoAAFBhqtMJg8DgQBgfrEsJAEAg4YhZIEiwgKtHiMBgtpg3wbUZXGO7kOb1MUKRFMysCChAoggJCIg0GC2aNe4gqQldfL4l/Ag1AXySJgn5LcoE3QXI3IQAh+QQACgADACwAAAAAEAAQAAAFdiAgAgLZNGU5joQhCEjxIssqEo8bC9BRjy9Ag7GILQ4QEoE0gBAEBcOpcBA0DoxSK/e8LRIHn+i1cK0IyKdg0VAoljYIg+GgnRrwVS/8IAkICyosBIQpBAMoKy9dImxPhS+GKkFrkX+TigtLlIyKXUF+NjagNiEAIfkEAAoABAAsAAAAABAAEAAABWwgIAICaRhlOY4EIgjH8R7LKhKHGwsMvb4AAy3WODBIBBKCsYA9TjuhDNDKEVSERezQEL0WrhXucRUQGuik7bFlngzqVW9LMl9XWvLdjFaJtDFqZ1cEZUB0dUgvL3dgP4WJZn4jkomWNpSTIyEAIfkEAAoABQAsAAAAABAAEAAABX4gIAICuSxlOY6CIgiD8RrEKgqGOwxwUrMlAoSwIzAGpJpgoSDAGifDY5kopBYDlEpAQBwevxfBtRIUGi8xwWkDNBCIwmC9Vq0aiQQDQuK+VgQPDXV9hCJjBwcFYU5pLwwHXQcMKSmNLQcIAExlbH8JBwttaX0ABAcNbWVbKyEAIfkEAAoABgAsAAAAABAAEAAABXkgIAICSRBlOY7CIghN8zbEKsKoIjdFzZaEgUBHKChMJtRwcWpAWoWnifm6ESAMhO8lQK0EEAV3rFopIBCEcGwDKAqPh4HUrY4ICHH1dSoTFgcHUiZjBhAJB2AHDykpKAwHAwdzf19KkASIPl9cDgcnDkdtNwiMJCshACH5BAAKAAcALAAAAAAQABAAAAV3ICACAkkQZTmOAiosiyAoxCq+KPxCNVsSMRgBsiClWrLTSWFoIQZHl6pleBh6suxKMIhlvzbAwkBWfFWrBQTxNLq2RG2yhSUkDs2b63AYDAoJXAcFRwADeAkJDX0AQCsEfAQMDAIPBz0rCgcxky0JRWE1AmwpKyEAIfkEAAoACAAsAAAAABAAEAAABXkgIAICKZzkqJ4nQZxLqZKv4NqNLKK2/Q4Ek4lFXChsg5ypJjs1II3gEDUSRInEGYAw6B6zM4JhrDAtEosVkLUtHA7RHaHAGJQEjsODcEg0FBAFVgkQJQ1pAwcDDw8KcFtSInwJAowCCA6RIwqZAgkPNgVpWndjdyohACH5BAAKAAkALAAAAAAQABAAAAV5ICACAimc5KieLEuUKvm2xAKLqDCfC2GaO9eL0LABWTiBYmA06W6kHgvCqEJiAIJiu3gcvgUsscHUERm+kaCxyxa+zRPk0SgJEgfIvbAdIAQLCAYlCj4DBw0IBQsMCjIqBAcPAooCBg9pKgsJLwUFOhCZKyQDA3YqIQAh+QQACgAKACwAAAAAEAAQAAAFdSAgAgIpnOSonmxbqiThCrJKEHFbo8JxDDOZYFFb+A41E4H4OhkOipXwBElYITDAckFEOBgMQ3arkMkUBdxIUGZpEb7kaQBRlASPg0FQQHAbEEMGDSVEAA1QBhAED1E0NgwFAooCDWljaQIQCE5qMHcNhCkjIQAh+QQACgALACwAAAAAEAAQAAAFeSAgAgIpnOSoLgxxvqgKLEcCC65KEAByKK8cSpA4DAiHQ/DkKhGKh4ZCtCyZGo6F6iYYPAqFgYy02xkSaLEMV34tELyRYNEsCQyHlvWkGCzsPgMCEAY7Cg04Uk48LAsDhRA8MVQPEF0GAgqYYwSRlycNcWskCkApIyEAOwAAAAAAAAAAAA==\"></img>");
		loading_field.add(imgLoading);
		setWaitingMessageVisible(true);
		hp.add(loadingLabel);
		hp.add(imgLoading);
		loading_field.add(hp);
	}

	/**
	 * Sets the waiting alert visible.
	 *
	 * @param bool
	 *            the new waiting alert visible
	 */
	public void setWaitingMessageVisible(boolean bool) {

		loading_field.setVisible(bool);
		// text_info.setEnabled(!bool);
	}

	/**
	 * Sets the text message.
	 *
	 * @param msg
	 *            the msg
	 * @param hideLoadingAlertIfShown
	 *            the hide loading alert if shown
	 */
	public void setTextMessage(String msg, boolean hideLoadingAlertIfShown) {

		if (hideLoadingAlertIfShown)
			setWaitingMessageVisible(false);
		else
			setWaitingMessageVisible(true);

		text_info.getElement().getStyle().setBackgroundColor("#fcfcfc");
		text_info.setText(msg);
		//text_info.setVisible(true);
		text_info.setFocus(true);
		text_info.selectAll();
		//markText(text_info.getElement());
	}
}
