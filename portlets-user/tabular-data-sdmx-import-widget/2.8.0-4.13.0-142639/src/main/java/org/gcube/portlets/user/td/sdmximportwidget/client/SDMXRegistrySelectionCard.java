/**
 * 
 */
package org.gcube.portlets.user.td.sdmximportwidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.source.SDMXRegistrySource;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SDMXRegistrySelectionCard extends WizardCard {

	protected SDMXImportSession importSession;
	protected SDMXRegistrySelectionCard thisCard;

	final TextField url = new TextField();

	public SDMXRegistrySelectionCard(final SDMXImportSession importSession) {
		super("SDMX Registry selection", "");

		this.importSession = importSession;
		thisCard = this;

		VerticalPanel registrySelectionPanel = new VerticalPanel();
		registrySelectionPanel.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSources());

		Radio radioStandardRegistry = new Radio();

		radioStandardRegistry
				.setBoxLabel("<p style='display:inline-table;'><b>Internal SDMX Registry</b>"
						+ "<br>Select this if you want use the Internal Registry</p>");
		radioStandardRegistry.setValue(true);
		radioStandardRegistry.setName("Default");
		radioStandardRegistry.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());

		Radio radioUrlRegistry = new Radio();
		radioUrlRegistry
				.setBoxLabel("<p style='display:inline-table;'><b>Another SDMX Registry</b><br><SPAN id='SDMXRegistryUrl'></SPAN></p>");
		radioUrlRegistry.setName("Url");
		radioUrlRegistry.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());
		radioUrlRegistry.disable();

		url.setName("sdmxRegistryUrlInpuntField");
		url.setId("sdmxRegistryUrlInputFieldId");
		url.setStylePrimaryName(res.wizardCSS()
				.getSDMXRegistryUrlInputStyle());

		final VerticalLayoutContainer vcontainer = new VerticalLayoutContainer();
		vcontainer.add(new FieldLabel(url, "URL"), new VerticalLayoutData(-1,
				-1));
		vcontainer.setStylePrimaryName(res.wizardCSS()
				.getSDMXRegistryUrlStyle());
		vcontainer.setVisible(false);

		NodeList<Element> nodel = radioUrlRegistry.getElement()
				.getElementsByTagName("SPAN");
		Element span = nodel.getItem(0);
		span.appendChild(vcontainer.getElement());

		registrySelectionPanel.add(radioStandardRegistry);
		registrySelectionPanel.add(radioUrlRegistry);

		// we can set name on radios or use toggle group
		ToggleGroup toggle = new ToggleGroup();
		toggle.add(radioStandardRegistry);
		toggle.add(radioUrlRegistry);

		toggle.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {

			
			public void onValueChange(ValueChangeEvent<HasValue<Boolean>> event) {
				try {
					ToggleGroup group = (ToggleGroup) event.getSource();
					Radio radio = (Radio) group.getValue();
					Log.info("Registry Selected:" + radio.getName());
					if (radio.getName().compareTo("Url") == 0) {
						vcontainer.setVisible(true);
						NodeList<Element> nodel = radio.getElement()
								.getElementsByTagName("SPAN");
						Element span = nodel.getItem(0);
						span.appendChild(vcontainer.getElement());

						Log.info("SDMXRegistry Url");
						Log.info("Input value: " + url.getCurrentValue());
						SDMXRegistrySource r = ((SDMXRegistrySource) importSession
								.getSource());
						r.setUrl(null);
						Log.info("" + importSession.getSource());
					} else {
						vcontainer.setVisible(false);
						Log.info("SDMXRegistry Default");
						SDMXRegistrySource r = ((SDMXRegistrySource) importSession
								.getSource());
						r.setUrl(null);
						Log.info("Input value: " + url.getCurrentValue());
						Log.info("" + importSession.getSource());
					}
				} catch (Exception e) {
					Log.error("ToggleGroup: onValueChange "
							+ e.getLocalizedMessage());
				}

			}
		});

		setContent(registrySelectionPanel);

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {
			public void execute() {
				try {
					Log.info("Input value: " + url.getCurrentValue());
					if (importSession.getSource() instanceof SDMXRegistrySource) {
						((SDMXRegistrySource) importSession.getSource())
								.setUrl(url.getCurrentValue());

						TDGWTServiceAsync.INSTANCE.setSDMXRegistrySource(
								((SDMXRegistrySource)importSession.getSource()), new AsyncCallback<Void>() {

									public void onFailure(Throwable caught) {
										if (caught instanceof TDGWTSessionExpiredException) {
											getEventBus()
													.fireEvent(
															new SessionExpiredEvent(
																	SessionExpiredType.EXPIREDONSERVER));
										} else {
										
										Log.error("SDMXImportSession do not stored "
												+ caught.getLocalizedMessage());
										}
									}

									public void onSuccess(Void result) {
										Log.info("SDMXRegistrySource stored");
									}
								});

						if (importSession.getSDMXDocument().getId()
								.compareTo("codelist") == 0) {
							SDMXCodelistSelectionCard sdmxCodelistSelectionCard = new SDMXCodelistSelectionCard(
									importSession);
							getWizardWindow()
									.addCard(sdmxCodelistSelectionCard);
							Log.info("NextCard SDMXCodelistSelectionCard");
							getWizardWindow().nextCard();
						} else {
							if (importSession.getSDMXDocument().getId()
									.compareTo("dataset") == 0) {
								SDMXDatasetSelectionCard sdmxDatasetSelectionCard = new SDMXDatasetSelectionCard(
										importSession);
								getWizardWindow().addCard(
										sdmxDatasetSelectionCard);
								Log.info("NextCard SDMXDatasetSelectionCard");
								getWizardWindow().nextCard();
							} else {

							}
						}
					} else {
						Log.error("There is a problem in source selection.Expected SDMXRegistrySource, and found"
								+ importSession.getSource());
					}

				} catch (Exception e) {
					Log.error("sayNextCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove SDMXRegistrySelectionCard");

				} catch (Exception e) {
					Log.error("sayNextCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);

	}

}
