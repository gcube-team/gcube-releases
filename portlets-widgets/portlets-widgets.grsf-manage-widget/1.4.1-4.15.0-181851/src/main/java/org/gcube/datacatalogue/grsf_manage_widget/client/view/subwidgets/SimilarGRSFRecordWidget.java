package org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets;

import java.util.ArrayList;
import java.util.List;

import org.gcube.datacatalogue.grsf_manage_widget.client.events.EnableConfirmButtonEvent;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SimilarGRSFRecord;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
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
	Button viewMore;

	private List<SimilarGRSFRecord> availableGRSFSimilarRecords;
	private static List<CheckBox> buttonsOrCheckboxesToFreeze = new ArrayList<CheckBox>();
	private List<Widget> toHide = new ArrayList<Widget>();
	private static final int THRESHOLD_SET_HIDDEN = 5;
	private static final String SEE_MORE = "See More";
	private static final String SEE_LESS = "See Less";

	/**
	 * Get widget for available similar grsf records
	 * @param availableGRSFSimilarRecords
	 * @param eventBus 
	 * @param service 
	 */
	public SimilarGRSFRecordWidget(List<SimilarGRSFRecord> availableGRSFSimilarRecords, HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));

		//this.service = service;
		this.availableGRSFSimilarRecords = availableGRSFSimilarRecords;

		if(availableGRSFSimilarRecords != null){

			similarGrsfRecordsPanel.add(new HTML("<hr style=\"width:100%;\"/>"));

			boolean visibleMore = true;
			viewMore.setText(SEE_MORE);
			if(availableGRSFSimilarRecords.size() > THRESHOLD_SET_HIDDEN){
				visibleMore = false;
				viewMore.getElement().getStyle().setFloat(Float.RIGHT);
				viewMore.setType(ButtonType.LINK);
				viewMore.setVisible(true);
				viewMore.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				viewMore.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						event.preventDefault();

						boolean toShow = false;
						if(viewMore.getText().trim().equals(SEE_MORE)){
							viewMore.setText(SEE_LESS);
							toShow = true;
						}
						else{	
							viewMore.setText(SEE_MORE);
							toShow = false;
						}

						for(Widget w: toHide)
							w.setVisible(toShow);

					}
				});
			}

			int index = 0;
			for (final SimilarGRSFRecord similarGRSFRecord : availableGRSFSimilarRecords) {
				Widget widget = buildWidgetForSimilarRecord(similarGRSFRecord, eventBus);
				similarGrsfRecordsPanel.add(widget);
				HTML separator = new HTML("<hr style=\"width:100%;\"/>");
				similarGrsfRecordsPanel.add(separator);
				if(index >= THRESHOLD_SET_HIDDEN){
					widget.setVisible(visibleMore);
					separator.setVisible(visibleMore);
					toHide.add(widget);
					toHide.add(separator);
				}
				index++;
			}

		}

	}

	/**
	 * Builds widget for already present similar GRSF records
	 * @param similarGRSFRecord
	 * @param eventBus 
	 * @param hideMore 
	 * @param index 
	 * @return a Widget (actually a VerticalPanel)
	 */
	public static Widget buildWidgetForSimilarRecord(final SimilarGRSFRecord similarGRSFRecord, final HandlerManager eventBus){

		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("95%");
		VerticalPanel leftPanel = new VerticalPanel();
		leftPanel.setWidth("80%");
		leftPanel.getElement().getStyle().setMarginLeft(20, Unit.PX);
		Paragraph name = new Paragraph("Record Name: " + (similarGRSFRecord.getTitle() != null? similarGRSFRecord.getTitle() : "Unavailable"));
		leftPanel.add(name);
		Paragraph identifier = new Paragraph("UUID: " + 
				similarGRSFRecord.getKnowledgeBaseId());
		leftPanel.add(identifier);

		Paragraph semanticIdentifier = new Paragraph("Semantic Identifier: " + 
				similarGRSFRecord.getSemanticIdentifier());
		leftPanel.add(semanticIdentifier);
		if(similarGRSFRecord.getDescription() != null){
			String shortDescrption = similarGRSFRecord.getDescription().length() > 45 ?
					similarGRSFRecord.getDescription().substring(0, 45) + " ... " :
						similarGRSFRecord.getDescription();
			Paragraph description = new Paragraph(
					"Description: " + shortDescrption);
			description.setTitle("Description: " + similarGRSFRecord.getDescription());
			leftPanel.add(description);
		}

		Anchor view = new Anchor();
		view.setHref(similarGRSFRecord.getUrl());
		view.setText("View"); 
		view.setTitle("Click to inspect the similar record");
		view.setTarget("_blank");
		view.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		leftPanel.add(view);

		VerticalPanel rightPanel = new VerticalPanel();
		rightPanel.setWidth("20%");

		// add merge checkbox
		final CheckBox mergeSuggested = new CheckBox("Merge");
		mergeSuggested.setTitle("Suggest to merge the current record with this similar record");
		mergeSuggested.setValue(false);

		mergeSuggested.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				similarGRSFRecord.setSuggestedMerge(mergeSuggested.getValue());
				eventBus.fireEvent(new EnableConfirmButtonEvent());
			}
		});

		rightPanel.getElement().getStyle().setFloat(Float.RIGHT);
		rightPanel.add(mergeSuggested);
		buttonsOrCheckboxesToFreeze.add(mergeSuggested);
		hp.add(leftPanel);
		hp.add(rightPanel);
		hp.getElement().getStyle().setPadding(10, Unit.PX);
		hp.getElement().getStyle().setMarginBottom(10, Unit.PX);
		return hp;
	}

	/**
	 * Get the whole of similar records
	 * @return
	 */
	public List<SimilarGRSFRecord> getSimilarRecords(){

		return availableGRSFSimilarRecords;

	}

	public void freezeWidget() {

		// freeze the checkboxes
		for (CheckBox cb : buttonsOrCheckboxesToFreeze) {
			cb.setEnabled(false);
		}

	}
}
