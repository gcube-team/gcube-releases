package org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.datacatalogue.grsf_manage_widget.shared.SimilarGRSFRecord;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.ComplexWidget;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SimilarGRSFRecordWidget extends Composite {

	private static SimilarGRSFRecordWidgetUiBinder uiBinder = GWT
			.create(SimilarGRSFRecordWidgetUiBinder.class);

	interface SimilarGRSFRecordWidgetUiBinder extends
	UiBinder<Widget, SimilarGRSFRecordWidget> {
	}

	@UiField
	VerticalPanel similarGrsfRecordsPanel;

	@UiField
	VerticalPanel similarGrsfRecordsSuggestedPanel;

	@UiField
	Button addSimilarRecord;

	/**
	 * Class of Pairs
	 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
	 */
	class Pair {
		private SimilarGRSFRecord s;
		private Widget w;
		public Pair(SimilarGRSFRecord s, Widget w){
			this.s = s;
			this.w = w;
		}
		public SimilarGRSFRecord getS(){ return s; }
		public Widget getW(){ return w; }
		public void setS(SimilarGRSFRecord s){ this.s = s; }
		public void setW(Widget w){ this.w = w; }
	}

	protected List<Pair> extraSimilarRecordsList = new ArrayList<Pair>(0);
	private List<SimilarGRSFRecord> availableGRSFSimilarRecords;

	/**
	 * Get widget for available similar grsf records
	 * @param availableGRSFSimilarRecords
	 */
	public SimilarGRSFRecordWidget(List<SimilarGRSFRecord> availableGRSFSimilarRecords) {
		initWidget(uiBinder.createAndBindUi(this));

		this.availableGRSFSimilarRecords = availableGRSFSimilarRecords;
		similarGrsfRecordsPanel.setWidth("100%");
		similarGrsfRecordsSuggestedPanel.setWidth("100%");

		// add the existing ones, if any
		for (final SimilarGRSFRecord similarGRSFRecord : availableGRSFSimilarRecords) {
			Widget widget = buildWidgetForSimilarRecord(similarGRSFRecord);
			similarGrsfRecordsPanel.add(widget);
		}

		// manage the "suggest button"
		addSimilarRecord.setIcon(IconType.PLUS_SIGN);
		addSimilarRecord.getElement().getStyle().setFloat(Float.RIGHT);
		addSimilarRecord.setTitle("Suggest a Similar Record by using its GRSF UUID");

		addSimilarRecord.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {

				Widget w = new Widget();
				SimilarGRSFRecord s = new SimilarGRSFRecord(true);
				buildWidgetForExtraSimilarRecord(w, s);
				extraSimilarRecordsList.add(new Pair(s, w));
				similarGrsfRecordsSuggestedPanel.add(w);

			}
		});

	}

	/**
	 * Builds widget for already present similar GRSF records
	 * @param similarGRSFRecord
	 * @return a Widget (actually a VerticalPanel)
	 */
	public static Widget buildWidgetForSimilarRecord(final SimilarGRSFRecord similarGRSFRecord){

		VerticalPanel subPanel = new VerticalPanel();
		Paragraph name = new Paragraph("Record name: " + similarGRSFRecord.getShortName());
		subPanel.add(name);
		if(similarGRSFRecord.getDescription() != null){
			String shortDescrption = similarGRSFRecord.getDescription().length() > 30 ?
					similarGRSFRecord.getDescription().substring(0, 30) + " ... " :
						similarGRSFRecord.getDescription();
			Paragraph description = new Paragraph(
					"Description: " + shortDescrption);
			description.setTitle("Description: " + similarGRSFRecord.getDescription());
			subPanel.add(description);
		}
		Paragraph semanticIdentifier = new Paragraph("Semantic Identifier " + 
				similarGRSFRecord.getSemanticIdentifier());
		subPanel.add(semanticIdentifier);

		Anchor view = new Anchor();
		view.setHref(similarGRSFRecord.getUrl());
		view.setText("View"); 
		view.setTarget("_blank");
		subPanel.add(view);
		subPanel.setWidth("100%");

		// add merge checkbox
		final CheckBox mergeSuggested = new CheckBox("Merge");
		mergeSuggested.setValue(false);

		mergeSuggested.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {

				similarGRSFRecord.setSuggestedMerge(mergeSuggested.getValue());

			}
		});

		subPanel.add(mergeSuggested);
		return subPanel;
	}

	/**
	 * Builds up a widget for suggested similar grsf records. Changes are performed in place with respect to w and s.
	 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
	 * @param w the widget
	 * @param s the similar record.
	 */
	private void buildWidgetForExtraSimilarRecord(Widget w, final SimilarGRSFRecord s){

		w = new HorizontalPanel();
		w.setWidth("100%");

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("70%");
		ControlLabel cLabel = new ControlLabel("Semantic Identifier");
		TextBox box = new TextBox();
		box.setPlaceholder("Insert the Semantic Identifier of the suggested record");
		HelpBlock hb =  new HelpBlock();
		hb.setVisible(false);

		// TODO add handler for remote GRSF semantic id check

		vp.add(cLabel);
		vp.add(box);
		vp.add(hb);

		// add merge checkbox
		final CheckBox mergeSuggested = new CheckBox("Merge");
		mergeSuggested.setValue(false);

		mergeSuggested.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {

				s.setSuggestedMerge(mergeSuggested.getValue());

			}
		});

		vp.add(mergeSuggested);

		Button removeExtra = new Button();
		removeExtra.setIcon(IconType.MINUS);
		removeExtra.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {

				// remove this object from the pairs list
				Iterator<Pair> iterator = extraSimilarRecordsList.iterator();
				while (iterator.hasNext()) {
					SimilarGRSFRecordWidget.Pair pair = (SimilarGRSFRecordWidget.Pair) iterator
							.next();
					if(pair.getS().equals(s)){
						pair.getW().removeFromParent();
						iterator.remove();
					}
				}
			}
		});

		((ComplexWidget) w).add(vp);
		((ComplexWidget) w).add(removeExtra);
	}

	/**
	 * Get the whole of similar records
	 * @return
	 */
	public List<SimilarGRSFRecord> getSuggestedRecords(){

		if(availableGRSFSimilarRecords == null)
			availableGRSFSimilarRecords = new ArrayList<SimilarGRSFRecord>();


		for (Pair p : extraSimilarRecordsList) {
			availableGRSFSimilarRecords.add(p.getS());
		}

		return availableGRSFSimilarRecords;

	}


}
