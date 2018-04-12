package org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.datacatalogue.common.enums.Product_Type;
import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetServiceAsync;
import org.gcube.datacatalogue.grsf_manage_widget.client.events.EnableConfirmButtonEvent;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ConnectedBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;

import com.github.gwtbootstrap.client.ui.AppendButton;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
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

/**
 * Connections panel
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ConnectToWidget extends Composite{

	private static ConnectToWidgetUiBinder uiBinder = GWT
			.create(ConnectToWidgetUiBinder.class);

	interface ConnectToWidgetUiBinder extends UiBinder<Widget, ConnectToWidget> {
	}

	@UiField
	VerticalPanel connectPanel;

	@UiField
	Button suggestRecord;

	private List<Tuple> connectList = new ArrayList<Tuple>(0); // for the suggested ones
	private List<ConnectedBean> currentlyConnected; // they can be "unconnected" or "removed"
	private List<ConnectedBean> suggestedByKnowledgeBase; // they can be "connected"
	private GRSFManageWidgetServiceAsync service;
	private static List<CheckBox> buttonsOrCheckboxesToFreeze = new ArrayList<CheckBox>();
	private HandlerManager eventBus;

	public ConnectToWidget(final ManageProductBean bean, GRSFManageWidgetServiceAsync service, final HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));

		this.service = service;
		this.eventBus = eventBus;

		// get already connected beans, and suggested ones
		currentlyConnected = bean.getCurrentConnections();

		if(currentlyConnected != null){
			connectPanel.add(new HTML("<hr style=\"width:100%;\"/>"));
			for (ConnectedBean connected : currentlyConnected) {
				Widget widget = buildWidgetForConnected(connected, false, eventBus);
				connectPanel.add(widget);
				connectPanel.add(new HTML("<hr style=\"width:100%;\"/>"));
			}
		}

		// add the one suggested by the knowledge base, they can be just "connected"
		suggestedByKnowledgeBase = bean.getSuggestedByKnowledgeBaseConnections();

		if(suggestedByKnowledgeBase != null){
			for (ConnectedBean connected : suggestedByKnowledgeBase) {
				Widget widget = buildWidgetForConnected(connected, true, eventBus);
				connectPanel.add(widget);
				connectPanel.add(new HTML("<hr style=\"width:100%;\"/>"));
			}
		}

		// manage the button for manual suggestion
		final String acceptedDomain = bean.getDomain().equalsIgnoreCase(Product_Type.STOCK.getOrigName()) ? 
				Product_Type.FISHERY.getOrigName()  : Product_Type.STOCK.getOrigName(); 
				suggestRecord.setTitle("Connect this " + bean.getDomain() + " record to a " + acceptedDomain + " record ");
				suggestRecord.setText("Add Connection");
				suggestRecord.setType(ButtonType.LINK);
				suggestRecord.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				suggestRecord.getElement().getStyle().setFloat(Float.RIGHT);

				// add handler
				suggestRecord.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent arg0) {

						ConnectedBean cb = new ConnectedBean();
						Tuple t = buildWidgetConnect(cb, acceptedDomain, eventBus);
						connectList.add(t);
						connectPanel.add(t.getW());

					}
				});

	}

	/**
	 * 
	 * @param similarGRSFRecord
	 * @return
	 */
	public static Widget buildWidgetForConnected(final ConnectedBean connected, boolean suggestedByKb, final HandlerManager eventBus){
		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("95%");
		VerticalPanel leftPanel = new VerticalPanel();
		leftPanel.setWidth("80%");
		leftPanel.getElement().getStyle().setMarginLeft(20, Unit.PX);
		Paragraph name = new Paragraph("Record Name: " + (connected.getTitle() != null? connected.getTitle() : "Unavailable"));
		leftPanel.add(name);
		
		Paragraph identifier = new Paragraph("UUID: " + 
				connected.getKnowledgeBaseId());
		leftPanel.add(identifier);

		Paragraph semanticIdentifier = new Paragraph("Semantic Identifier: " + 
				connected.getSemanticIdentifier());
		leftPanel.add(semanticIdentifier);

		Anchor view = new Anchor();
		view.setHref(connected.getUrl());
		view.setText("View"); 
		view.setTitle("Click to view the similar record");
		view.setTarget("_blank");
		view.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		leftPanel.add(view);

		VerticalPanel rightPanel = new VerticalPanel();
		rightPanel.setWidth("20%");
		rightPanel.getElement().getStyle().setFloat(Float.RIGHT);

		if(!suggestedByKb){
			final CheckBox removeExtra = new CheckBox("Remove");
			removeExtra.setTitle("Remove this connection");
			removeExtra.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent arg0) {
					connected.setRemove(removeExtra.getValue());
					eventBus.fireEvent(new EnableConfirmButtonEvent());
				}
			});
			rightPanel.add(removeExtra);
			// append buttons and checkboxes that could be frozen
			buttonsOrCheckboxesToFreeze.add(removeExtra);
		}

		final CheckBox connect = new CheckBox("Connect");
		connect.setValue(!suggestedByKb); // automatically check/uncheck the value it based on the suggestion
		connect.setTitle("Connect this record");
		connect.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				connected.setConnect(connect.getValue());
				eventBus.fireEvent(new EnableConfirmButtonEvent());
			}
		});
		rightPanel.add(connect);
		hp.add(leftPanel);
		hp.add(rightPanel);
		hp.getElement().getStyle().setPadding(10, Unit.PX);
		hp.getElement().getStyle().setMarginBottom(10, Unit.PX);
		
		// append buttons and checkboxes that could be frozen
		buttonsOrCheckboxesToFreeze.add(connect);
		
		return hp;
	}

	/**
	 * Builds up a widget for connecting records.
	 * @param w the widget
	 * @param cb the connectBean.
	 * @param acceptedDomain 
	 */
	private Tuple buildWidgetConnect(final ConnectedBean cb, final String acceptedDomain, HandlerManager eventBus){

		VerticalPanel main = new VerticalPanel();
		HorizontalPanel hp = new HorizontalPanel();
		main.setWidth("100%");
		hp.setWidth("100%");

		VerticalPanel vpLeft = new VerticalPanel();
		vpLeft.getElement().getStyle().setMarginLeft(15, Unit.PX);
		vpLeft.setWidth("80%");
		Paragraph identifier = new Paragraph("UUID:");

		// view link
		final Anchor view = new Anchor();
		view.setText("View"); 
		view.setTitle("Click to inspect the record");
		view.setTarget("_blank");
		view.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		view.setVisible(false);

		// a textbox with a validate button on the right side
		AppendButton uuidAndValidateButton = new AppendButton();
		final Button validateUUIDButton = new Button("Validate");
		final Tooltip tip = new Tooltip();
		validateUUIDButton.setEnabled(false);
		final PasteAwareTextBox box = new PasteAwareTextBox(validateUUIDButton, tip);
		setupTooltip(tip, box, "", false);
		box.setWidth("512px");
		box.setPlaceholder("Copy and Paste the Identifier (UUID) of the record to connect here, then validate");
		validateUUIDButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				validateUUID(box, cb, view, validateUUIDButton, acceptedDomain, tip);

			}
		});
		uuidAndValidateButton.add(box);
		uuidAndValidateButton.add(validateUUIDButton);
		vpLeft.add(identifier);
		vpLeft.add(uuidAndValidateButton);
		vpLeft.add(view);

		VerticalPanel vpRight = new VerticalPanel();
		vpRight.setWidth("20%");

		Button removeExtra = new Button();
		removeExtra.setText("Remove");
		removeExtra.setType(ButtonType.LINK);
		removeExtra.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		removeExtra.setTitle("Remove this Connection");
		removeExtra.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {

				// remove this object from the pairs list
				Iterator<Tuple> iterator = connectList.iterator();
				while (iterator.hasNext()) {
					Tuple pair = (Tuple) iterator
							.next();
					if(pair.getO().equals(cb)){
						pair.getW().removeFromParent();
						iterator.remove();
					}
				}
			}
		});
		vpRight.add(removeExtra);
		hp.add(vpLeft);
		hp.add(vpRight);
		HTML separator = new HTML("<hr style=\"width:100%;\"/>");
		connectPanel.add(separator);
		main.add(hp);
		main.add(separator);
		return new Tuple(cb, main, box);
	}

	protected void validateUUID(final TextBox box, final ConnectedBean c, final Anchor view, final Button validateUUIDButton, final String acceptedDomain, final Tooltip tip) {

		validateUUIDButton.setText("Validating...");
		validateUUIDButton.setEnabled(false);
		box.setEnabled(false);
		view.setVisible(false);

		final String currentText = box.getText().trim();
		c.setKnowledgeBaseId(null);
		c.setConnect(false);

		// else check at server side if it exists
		service.checkIdentifierExistsInDomain(currentText, acceptedDomain, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {

				if(result != null){
					c.setKnowledgeBaseId(currentText);
					c.setConnect(true);
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
	 * Get the list of records to connect with this one
	 * @return
	 */
	public List<ConnectedBean> getConnectList() {

		List<ConnectedBean> toReturn = new ArrayList<>();

		if(currentlyConnected != null)
			toReturn.addAll(currentlyConnected);

		if(suggestedByKnowledgeBase != null)
			toReturn.addAll(suggestedByKnowledgeBase);

		for (Tuple p : connectList) {
			ConnectedBean potentialConnection = (ConnectedBean) p.getO();
			String suggestedIdentifier = potentialConnection.getKnowledgeBaseId();
			if(suggestedIdentifier == null || suggestedIdentifier.isEmpty())
				continue;
			else
				toReturn.add(potentialConnection);
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
	
	public void freezeWidget(){
		
		suggestRecord.setEnabled(false);
		
		// freeze the checkboxes
		for (CheckBox cb : buttonsOrCheckboxesToFreeze) {
			cb.setEnabled(false);
		}
	}

}
