/**
 * 
 */
package org.gcube.portlets.user.workspace.client.view;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class WorkspaceQuotes.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it Nov 6, 2015
 */
public class WorkspaceQuotesView extends Composite {

	@UiField
	HorizontalPanel hp_quotes;

	@UiField
	Label ws_quote;

	private static WorkspaceFeaturesUiBinder uiBinder = GWT
			.create(WorkspaceFeaturesUiBinder.class);

	/**
	 * The Interface PageTemplateUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it Feb 19,
	 *         2015
	 */
	interface WorkspaceFeaturesUiBinder extends
			UiBinder<Widget, WorkspaceQuotesView> {
	}

	/**
	 * Instantiates a new workspace quotes.
	 */
	public WorkspaceQuotesView() {

		initWidget(uiBinder.createAndBindUi(this));
		hp_quotes.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp_quotes.getElement().getStyle().setMarginTop(10, Unit.PX);
	}

	/**
	 * Update quotes.
	 *
	 * @param html
	 *            the html
	 */
	public void updateQuotes(String html) {
		ws_quote.setText(html);
	}

	/**
	 * @param bool
	 */
	public void setQuoteVisible(boolean bool) {
		ws_quote.setVisible(bool);
		
	}

}
