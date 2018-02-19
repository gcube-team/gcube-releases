package org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetServiceAsync;
import org.gcube.datacatalogue.grsf_manage_widget.client.events.EnableConfirmButtonEvent;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SimilarGRSFRecord;

import com.github.gwtbootstrap.client.ui.AppendButton;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.github.gwtbootstrap.client.ui.constants.Trigger;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SuggestMerges extends Composite {

	private static SuggestMergesUiBinder uiBinder = GWT
			.create(SuggestMergesUiBinder.class);

	@UiField
	VerticalPanel similarGrsfRecordsSuggestedPanel;

	@UiField
	Button addSimilarRecord;

	private List<Tuple> extraSimilarRecordsList = new ArrayList<Tuple>(0);

	interface SuggestMergesUiBinder extends UiBinder<Widget, SuggestMerges> {
	}

	private GRSFManageWidgetServiceAsync service;

	private HandlerManager eventBus;

	public SuggestMerges(GRSFManageWidgetServiceAsync service, final String acceptedDomain, final HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.service = service;
		this.eventBus = eventBus;

		// manage the "suggest button"
		addSimilarRecord.setText("Add Merge");
		addSimilarRecord.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		addSimilarRecord.setType(ButtonType.LINK);
		addSimilarRecord.getElement().getStyle().setFloat(Float.RIGHT);
		addSimilarRecord.setTitle("Suggest a Similar Record to merge by using its Identifier (UUID)");

		// add handler
		addSimilarRecord.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				SimilarGRSFRecord s = new SimilarGRSFRecord();
				Widget w = buildWidgetForExtraSimilarRecord(s, acceptedDomain, eventBus);
				extraSimilarRecordsList.add(new Tuple(s, w, null));
				similarGrsfRecordsSuggestedPanel.add(w);
			}
		});
	}

	/**
	 * Builds up a widget for suggested similar grsf records. Changes are performed in place with respect to w and s.
	 * @param w the widget
	 * @param s the similar record.
	 * @param eventBus 
	 */
	private Widget buildWidgetForExtraSimilarRecord(final SimilarGRSFRecord s, final String acceptedDomain, final HandlerManager eventBus){

		VerticalPanel main = new VerticalPanel();
		main.getElement().getStyle().setMarginTop(10, Unit.PX);
		main.setWidth("100%");
		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");

		VerticalPanel vpLeft = new VerticalPanel();
		vpLeft.getElement().getStyle().setMarginLeft(15, Unit.PX);
		vpLeft.setWidth("80%");

		HorizontalPanel textBoxIconContainer = new HorizontalPanel();
		textBoxIconContainer.setWidth("100%");
		Paragraph identifier = new Paragraph("Record UUID:");

		// view link
		final Anchor view = new Anchor();
		view.setText("View"); 
		view.setTitle("Click to inspect the record");
		view.setTarget("_blank");
		view.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		view.setVisible(false);

		// a textbox with a validate button on the right side
		AppendButton uuidAndValidateButton = new AppendButton();
		final Tooltip tip = new Tooltip();
		final Button validateUUIDButton = new Button("Validate");
		final PasteAwareTextBox box = new PasteAwareTextBox(validateUUIDButton, tip);
		setupTooltip(tip, box, "", false);
		box.setWidth("512px");
		box.setPlaceholder("Copy and Paste the Identifier (UUID) of the record to merge, then validate");
		validateUUIDButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				validateUUID(box, s, view, validateUUIDButton, acceptedDomain, eventBus, tip);

			}
		});
		uuidAndValidateButton.add(box);
		uuidAndValidateButton.add(validateUUIDButton);
		vpLeft.add(identifier);
		vpLeft.add(uuidAndValidateButton);
		vpLeft.add(view);

		// the right side
		VerticalPanel vpRight = new VerticalPanel();
		vpRight.setWidth("20%");

		Button removeExtra = new Button();
		removeExtra.setText("Remove");
		removeExtra.setTitle("Remove this suggested merge");
		removeExtra.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		removeExtra.setType(ButtonType.LINK);
		removeExtra.getElement().getStyle().setFloat(Float.RIGHT);
		removeExtra.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {

				// remove this object from the pairs list
				Iterator<Tuple> iterator = extraSimilarRecordsList.iterator();
				while (iterator.hasNext()) {
					Tuple pair = (Tuple) iterator
							.next();
					if(pair.getO().equals(s)){
						pair.getW().removeFromParent();
						iterator.remove();
						break;
					}
				}
			}
		});
		vpRight.getElement().getStyle().setFloat(Float.RIGHT);
		vpRight.add(removeExtra);
		hp.add(vpLeft);
		hp.add(vpRight);
		HTML separator = new HTML("<hr style=\"width:100%;\"/>");
		similarGrsfRecordsSuggestedPanel.add(separator);
		main.add(hp);
		main.add(separator);
		return main;
	}

	/**
	 * Validate a UUID (ask at server side if it is ok)
	 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
	 * @param box
	 * @param s
	 * @param icon
	 * @param view
	 * @param eventBus2 
	 * @param tip 
	 */
	protected void validateUUID(final TextBox box, final SimilarGRSFRecord s, final Anchor view, final Button validateUUIDButton, final String acceptedDomain, HandlerManager eventBus2, final Tooltip tip) {

		validateUUIDButton.setText("Validating...");
		validateUUIDButton.setEnabled(false);
		box.setEnabled(false);
		view.setVisible(false);

		final String currentText = box.getText().trim();
		s.setKnowledgeBaseId(null);
		s.setSuggestedMerge(false);

		// else check at server side if it exists
		service.checkIdentifierExistsInDomain(currentText, acceptedDomain, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {

				if(result != null){
					s.setKnowledgeBaseId(currentText);
					s.setSuggestedMerge(true);
					view.setHref(result);
					view.setVisible(true);
					box.setEnabled(false);
					validateUUIDButton.setText("Accepted");
					validateUUIDButton.setType(ButtonType.SUCCESS);
					validateUUIDButton.setEnabled(false);
					eventBus.fireEvent(new EnableConfirmButtonEvent());
					setupTooltip(tip, box, "", false);
				}
				else{
					view.setVisible(false);
					box.setEnabled(true);
					validateUUIDButton.setText("Invalid");
					validateUUIDButton.setType(ButtonType.DANGER);
					validateUUIDButton.setEnabled(true);
					setupTooltip(tip, box, "Invalid", true);
				}


			}

			@Override
			public void onFailure(Throwable caught) {
				box.setEnabled(true);
				view.setVisible(false);
				validateUUIDButton.setText("Invalid");
				validateUUIDButton.setType(ButtonType.DANGER);
				view.setVisible(false);
				box.setEnabled(true);
				setupTooltip(tip, box, caught.getMessage(), true);
			}
		});

	}

	/**
	 * Get the whole list of similar records
	 * @return
	 */
	public List<SimilarGRSFRecord> getSimilarRecords(){
		ArrayList<SimilarGRSFRecord> toReturn = new ArrayList<SimilarGRSFRecord>();

		for (Tuple p : extraSimilarRecordsList) {
			SimilarGRSFRecord similarRecord = ((SimilarGRSFRecord)p.getO());
			if(similarRecord.getKnowledgeBaseId() == null ||  similarRecord.getKnowledgeBaseId().isEmpty())
				continue;
			similarRecord.setSuggestedMerge(true);
			toReturn.add(similarRecord);
		}

		return toReturn;
	}

	private void setupTooltip(Tooltip tooltip, Widget w, String message, boolean show) {
		tooltip.setWidget(w);
		tooltip.setText(message);
		tooltip.setAnimation(true);
		tooltip.setHideDelay(1000);
		tooltip.setPlacement(Placement.TOP);
		tooltip.setTrigger(Trigger.MANUAL);
		tooltip.hide();
		tooltip.reconfigure();

		if(show)
			tooltip.show();
		else
			tooltip.hide();
	}
}
