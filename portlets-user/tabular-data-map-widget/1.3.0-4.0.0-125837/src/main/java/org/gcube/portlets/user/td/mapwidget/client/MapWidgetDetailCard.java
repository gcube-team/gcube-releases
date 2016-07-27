/**
 * 
 */
package org.gcube.portlets.user.td.mapwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.licenses.LicenceData;
import org.gcube.portlets.user.td.gwtservice.shared.map.MapCreationSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class MapWidgetDetailCard extends WizardCard {
	protected DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");

	protected final String TABLEDETAILPANELWIDTH = "100%";
	protected final String TABLEDETAILPANELHEIGHT = "100%";
	protected final String FORMWIDTH = "538px";

	protected MapCreationSession mapCreationSession;
	protected MapWidgetDetailCard thisCard;

	protected VerticalLayoutContainer p;
	protected VerticalPanel tableDetailPanel;

	private TextField name;
	private TextField userName;
	private TextArea metaAbstract;
	private TextArea metaPurpose;
	private TextArea metaCredits;
	private TextField metaKeywords;

	protected ListLoader<ListLoadConfig, ListLoadResult<LicenceData>> loader;
	protected ComboBox<LicenceData> comboLicences;

	public MapWidgetDetailCard(final MapCreationSession mapCreationSession) {
		super("Map Detail", "");

		this.mapCreationSession = mapCreationSession;
		thisCard = this;

		tableDetailPanel = new VerticalPanel();

		tableDetailPanel.setSpacing(4);
		tableDetailPanel.setWidth(TABLEDETAILPANELWIDTH);
		tableDetailPanel.setHeight(TABLEDETAILPANELHEIGHT);

		FramedPanel form = new FramedPanel();
		form.setHeadingText("Details");
		form.setWidth(FORMWIDTH);

		FieldSet fieldSet = new FieldSet();
		fieldSet.setHeadingText("Information");
		fieldSet.setCollapsible(false);
		form.add(fieldSet);

		p = new VerticalLayoutContainer();
		fieldSet.add(p);

		name = new TextField();
		name.setAllowBlank(false);
		name.setEmptyText("Enter a name...");
		name.setValue("");
		p.add(new FieldLabel(name, "Name"), new VerticalLayoutData(1, -1));

		userName = new TextField();
		userName.setAllowBlank(false);
		userName.setEmptyText("Enter a user name...");
		userName.setValue(mapCreationSession.getUsername());
		p.add(new FieldLabel(userName, "User Name"), new VerticalLayoutData(1,
				-1));
		userName.setReadOnly(true);
		
		metaAbstract = new TextArea();
		metaAbstract.setAllowBlank(false);
		metaAbstract.setEmptyText("Enter a abstract...");
		metaAbstract.setValue("");
		p.add(new FieldLabel(metaAbstract, "Abstract"), new VerticalLayoutData(
				1, -1));

		metaPurpose = new TextArea();
		metaPurpose.setAllowBlank(false);
		metaPurpose.setEmptyText("Enter a purpose...");
		metaPurpose.setValue("");
		p.add(new FieldLabel(metaPurpose, "Purpose"), new VerticalLayoutData(1,
				-1));

		metaCredits = new TextArea();
		metaCredits.setAllowBlank(false);
		metaCredits.setEmptyText("Enter credits...");
		metaCredits.setValue("");
		p.add(new FieldLabel(metaCredits, "Credits"), new VerticalLayoutData(1,
				-1));

		metaKeywords = new TextField();
		metaKeywords.setAllowBlank(false);
		metaKeywords.setEmptyText("Enter keywords...");
		metaKeywords.setValue("");
		p.add(new FieldLabel(metaKeywords, "Keywords"), new VerticalLayoutData(
				1, -1));

		tableDetailPanel.add(form);
		
		setContent(tableDetailPanel);
		
		retrieveInfo();
		
	}

	/*
	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<LicenceData>> callback) {
		TDGWTServiceAsync.INSTANCE
				.getLicences(new AsyncCallback<ArrayList<LicenceData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("load combo failure:"
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error",
									"Error retrieving licences.");
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<LicenceData> result) {
						Log.trace("loaded " + result.size() + " ColumnData");
						callback.onSuccess(new ListLoadResultBean<LicenceData>(
								result));

					}

				});

	}
	*/
	protected void updateInfo(TabResource tabResource){
		name.setValue(tabResource.getName());
		metaAbstract.setValue(tabResource.getDescription());
		forceLayout();
	}
	
	protected void retrieveInfo() {
		
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(mapCreationSession.getTrId(), new AsyncCallback<TabResource>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error("Error: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error",
										caught.getLocalizedMessage());
							} else {
								Log.error("Error retrieving tabular resource information:"
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error",
										"Error retrieving tabular resource information");
							}
						}
						
					}

					public void onSuccess(TabResource result) {
						Log.debug("Retrieved: "+result);
						updateInfo(result);
					}

				});

	}

	
	
	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				checkData();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove CSVTableDetailCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setEnableNextButton(true);
		getWizardWindow().setEnableBackButton(true);

	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		AlertMessageBox d;
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(true);

			}
		};

		if (name.getValue() == null || name.getValue().isEmpty()
				|| !name.isValid()) {
			d = new AlertMessageBox("Attention!", "Fill in name field");
			d.addHideHandler(hideHandler);
			d.show();
		} else {
			if (userName.getValue() == null || userName.getValue().isEmpty()
					|| !userName.isValid()) {
				d = new AlertMessageBox("Attention!", "Fill in user name field");
				d.addHideHandler(hideHandler);
				d.show();
				
			} else {
				if (metaAbstract.getValue() == null || metaAbstract.getValue().isEmpty()
						|| !metaAbstract.isValid()) {
					d = new AlertMessageBox("Attention!", "Fill in abstract field");
					d.addHideHandler(hideHandler);
					d.show();
					
				} else {
					if (metaPurpose.getValue() == null || metaPurpose.getValue().isEmpty()
							|| !metaPurpose.isValid()) {
						d = new AlertMessageBox("Attention!", "Fill in purpose field");
						d.addHideHandler(hideHandler);
						d.show();
						
					} else {
						if (metaCredits.getValue() == null || metaCredits.getValue().isEmpty()
								|| !metaCredits.isValid()) {
							d = new AlertMessageBox("Attention!", "Fill in credits field");
							d.addHideHandler(hideHandler);
							d.show();
							
						} else {
							if (metaKeywords.getValue() == null || metaKeywords.getValue().isEmpty()
									|| !metaKeywords.isValid()) {
								d = new AlertMessageBox("Attention!", "Fill in keywords field");
								d.addHideHandler(hideHandler);
								d.show();
								
							} else {
								name.setReadOnly(true);
								userName.setReadOnly(true);
								metaAbstract.setReadOnly(true);
								metaPurpose.setReadOnly(true);
								metaCredits.setReadOnly(true);
								metaKeywords.setReadOnly(true);

								goNext();
							}						}					}

				}
			}

		}
	}

	protected void goNext() {
		try {
		
			mapCreationSession.setName(name.getCurrentValue());
			mapCreationSession.setUsername(userName.getCurrentValue());
			mapCreationSession.setMetaAbstract(metaAbstract.getCurrentValue());
			mapCreationSession.setMetaPurpose(metaPurpose.getCurrentValue());
			mapCreationSession.setMetaCredits(metaCredits.getCurrentValue());
			ArrayList<String> keywordsList=new ArrayList<String>();
			String keys=metaKeywords.getCurrentValue();
			int separator=keys.indexOf(",");
			while(separator!=-1){
				String keyNew=keys.substring(0, separator);
				keywordsList.add(keyNew);
				keys=keys.substring(separator+1);
				separator=keys.indexOf(",");
			}
			keywordsList.add(keys);
			
			mapCreationSession.setMetaKeywords(keywordsList);
			MapWidgetOperationInProgressCard mapWidgetOperationInProgressCard = new MapWidgetOperationInProgressCard(
					mapCreationSession);
			getWizardWindow().addCard(mapWidgetOperationInProgressCard);
			Log.info("NextCard MapWidgetOperationInProgressCard");
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
			e.printStackTrace();
			
			name.setReadOnly(false);
			userName.setReadOnly(true);
			metaAbstract.setReadOnly(false);
			metaPurpose.setReadOnly(false);
			metaCredits.setReadOnly(false);
			metaKeywords.setReadOnly(false);
			
			getWizardWindow().setEnableNextButton(true);
			getWizardWindow().setEnableBackButton(true);
			
			UtilsGXT3.alert("Error", e.getLocalizedMessage());
			
		}
	}

}
