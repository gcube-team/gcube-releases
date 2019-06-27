/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.viewbinder;


import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class ShowResult.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 24, 2019
 */
public class ShowResult extends Composite {

	private static ShowResultUiBinder uiBinder =
		GWT.create(ShowResultUiBinder.class);

	/**
	 * The Interface ShowResultUiBinder.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * Feb 1, 2019
	 */
	interface ShowResultUiBinder extends UiBinder<Widget, ShowResult> {
	}

	@UiField
	HTML theTitle;

	@UiField
	HTMLPanel theContent;


	/**
	 * Instantiates a new show result.
	 *
	 * @param title the title
	 * @param content the content
	 */
	public ShowResult(String title) {

		initWidget(uiBinder.createAndBindUi(this));
		theTitle.getElement().addClassName("theSubTitle");
		theTitle.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		theTitle.setHTML(title);
	}

	/**
	 * Show image.
	 *
	 * @param base64Content the base64 content
	 */
	public void showImage(String base64Content){
		theContent.add(new HTMLPanel(base64Content));
	}



	/**
	 * Show csv file.
	 *
	 * @param csvFile the csv file
	 */
	public void showCSVFile(CSVFile csvFile){

		final FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("simpletable");
		flexTable.getElement().getStyle().setMarginLeft(30, Unit.PX);
		flexTable.getElement().getStyle().setMarginRight(30, Unit.PX);

		CSVRow headerRow = csvFile.getHeaderRow();

		for (int i=0; i<headerRow.getListValues().size(); i++) {
			String headerValue = headerRow.getListValues().get(i);
			flexTable.setWidget(0, i,new HTML(headerValue));
		}

		for (int i=0; i<csvFile.getValueRows().size(); i++) {
			CSVRow row = csvFile.getValueRows().get(i);
			for (int j=0; j<row.getListValues().size(); j++) {
				String rowValue = row.getListValues().get(j);
				flexTable.setWidget(i+1, j,new HTML(rowValue));
			}

		}

		theContent.add(flexTable);
	}
}
