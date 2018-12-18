package org.gcube.portlets.user.tokengenerator.client;

import org.gcube.portlets.user.tokengenerator.client.events.CloseAccordionEvent;
import org.gcube.portlets.user.tokengenerator.client.ui.subpanels.ApplicationTokens;
import org.gcube.portlets.user.tokengenerator.client.ui.subpanels.QualifiedTokens;
import org.gcube.portlets.user.tokengenerator.client.ui.subpanels.TokenContexPanel;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.github.gwtbootstrap.client.ui.event.ShowEvent;
import com.github.gwtbootstrap.client.ui.event.ShowHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Container of the 3 accordions.
 * @author Massimiliano Assante, ISTI CNR
 * @author Costantino Perciante, ISTI CNR
 */
public class TokenContainer extends Composite{

	private static TokenContainerUiBinder uiBinder = GWT
			.create(TokenContainerUiBinder.class);

	interface TokenContainerUiBinder extends UiBinder<Widget, TokenContainer> {
	}

	@UiField
	AccordionGroup qualifiedTokens;

	@UiField
	AccordionGroup applicationTokens;
	
	@UiField
	Accordion accordionsPanel;
	
	@UiField
	AccordionGroup contextToken;

	private static QualifiedTokens qualifiedTokensPanel;
	private static ApplicationTokens applicationTokensPanel;
	private static TokenContexPanel contextTokenPanel;
	
	private final HandlerManager eventBus = new HandlerManager(null);


	public TokenContainer() {
		initWidget(uiBinder.createAndBindUi(this));
		
		contextTokenPanel = new TokenContexPanel(eventBus);
		
		// load the context one
		contextToken.add(contextTokenPanel);
		
		// when the qualified tokens panel is opened
		qualifiedTokens.addShowHandler(new ShowHandler() {

			@Override
			public void onShow(ShowEvent showEvent) {

				if(qualifiedTokensPanel == null){
					qualifiedTokensPanel = new QualifiedTokens(eventBus);
					qualifiedTokens.add(qualifiedTokensPanel);
				}

			}
		});

		// when the application tokens panel is opened
		applicationTokens.addShowHandler(new ShowHandler() {

			@Override
			public void onShow(ShowEvent showEvent) {

				if(applicationTokensPanel == null){
					applicationTokensPanel = new ApplicationTokens(eventBus);
					applicationTokens.add(applicationTokensPanel);
				}

			}
		});
		
		applicationTokens.addHideHandler(new HideHandler() {
			
			@Override
			public void onHide(HideEvent hideEvent) {
				
				eventBus.fireEvent(new CloseAccordionEvent(applicationTokensPanel));
				
			}
		});
		
		qualifiedTokens.addHideHandler(new HideHandler() {
			
			@Override
			public void onHide(HideEvent hideEvent) {
				
				eventBus.fireEvent(new CloseAccordionEvent(qualifiedTokensPanel));
				
			}
		});
		
		contextToken.addHideHandler(new HideHandler() {
			
			@Override
			public void onHide(HideEvent hideEvent) {
				eventBus.fireEvent(new CloseAccordionEvent(contextTokenPanel));
			}
		});
	}

}
