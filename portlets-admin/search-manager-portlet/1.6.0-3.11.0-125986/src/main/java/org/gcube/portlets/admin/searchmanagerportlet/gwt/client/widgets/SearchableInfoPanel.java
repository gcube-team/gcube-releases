package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.SearchManager;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.CollectionInfoBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.FieldInfoBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.SMConstants;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.SearchableFieldInfoBean;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchableInfoPanel extends Composite {

	private SuggestBox collectionID;
	private MultiWordSuggestOracle slOracle = new MultiWordSuggestOracle();
	private SuggestBox sourceLocator = new SuggestBox(slOracle);
	private TextBox indexCap = new TextBox();
	private TextBox indexQL = new TextBox();
	private CheckBox isSortable = new CheckBox("Is sortable");

	private HorizontalPanel collP = new HorizontalPanel();
	private HorizontalPanel sourceLP = new HorizontalPanel();
	private HorizontalPanel indexCapP = new HorizontalPanel();
	private HorizontalPanel indexQLP = new HorizontalPanel();

	private VerticalPanel mainPanel = new VerticalPanel();
	private VerticalPanel formPanel = new VerticalPanel();
	private CaptionPanel captionP = new CaptionPanel("Searchable Information");

	private HorizontalPanel hostBtnPanel = new HorizontalPanel();
	private Button saveBtn = new Button("Add");
	private Button deleteBtn = new Button("Delete");
	
	private String saveSearchableMsg = "The new Searchable field has been created";

	public SearchableInfoPanel(final SearchableFieldInfoBean sBean, final FieldInfoBean fBean, final FieldFormPanel referenceWidget) {

		if (fBean.getAvailableSearchableCollectionsIDs() != null && fBean.getAvailableSearchableCollectionsIDs().size() > 0) {
			MultiWordSuggestOracle colIDOracle = new MultiWordSuggestOracle();
			ArrayList<String> defaultSugList = new ArrayList<String>();
			for (CollectionInfoBean cBean : fBean.getAvailableSearchableCollectionsIDs()) {
				String c = cBean.getName() + " (" + cBean.getID() + ")";
				colIDOracle.add(c);
				defaultSugList.add(c);
			}
			//colIDOracle.addAll(fBean.getAvailableSearchableCollectionsIDs());
			colIDOracle.setDefaultSuggestionsFromText(defaultSugList);
			collectionID = new SuggestBox(colIDOracle);

			collectionID.getTextBox().addFocusHandler(new FocusHandler() {
				public void onFocus(FocusEvent event) {
					collectionID.showSuggestionList();			
				}
			});	     
		}
		else
			collectionID = new SuggestBox();


		sourceLocator.getTextBox().addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				if (!collectionID.getText().trim().isEmpty()) {
					String realColID = getCollectionIDFromDesc(collectionID.getText());
					AsyncCallback<ArrayList<String>> getAvailablaLocatorsCallbak = new AsyncCallback<ArrayList<String>>() {

						public void onFailure(Throwable caught) {

						}

						public void onSuccess(ArrayList<String> result) {
							slOracle.clear();
							slOracle.setDefaultSuggestionsFromText(result);
							slOracle.addAll(result);	
							sourceLocator.showSuggestionList();
						}
					};SearchManager.smService.getIndexLocatorList(fBean.getID(), realColID, SMConstants.SEARCHABLE, getAvailablaLocatorsCallbak);
				}
			}
		});

		indexCap.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				if (!sourceLocator.getText().trim().isEmpty() && indexCap.getText().trim().isEmpty()) {
					AsyncCallback<Set<String>> getAvailableCapsCallbak = new AsyncCallback<Set<String>>() {

						public void onFailure(Throwable caught) {

						}

						public void onSuccess(Set<String> result) {
							if (result != null && indexCap.getText().trim().isEmpty()) {
								Iterator<String> it = result.iterator();
								String capabilities = "";
								while (it.hasNext()) {
									String cap = it.next();
									capabilities = capabilities + cap + ", ";
								}
								capabilities = capabilities.substring(0, capabilities.length()-2);

								indexCap.setText(capabilities);
							}
						}
					};SearchManager.smService.getIndexCapabilities(sourceLocator.getText().trim(), getAvailableCapsCallbak);
				}
			}
		});

		formPanel.setSpacing(5);
		formPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		formPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		hostBtnPanel.setSpacing(5);
		hostBtnPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hostBtnPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hostBtnPanel.add(saveBtn);

		// It will be visible only when editing existing fields
		deleteBtn.setVisible(false);
		hostBtnPanel.add(deleteBtn);

		collP.setSpacing(4);
		collP.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		sourceLP.setSpacing(4);
		sourceLP.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		indexCapP.setSpacing(4);
		indexCapP.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		indexQLP.setSpacing(4);
		indexQLP.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		collP.add(new Label("* Collection ID"));
		collP.add(collectionID);
		sourceLP.add(new Label("* Source Locator (IndexID)"));
		sourceLP.add(sourceLocator);
		indexCapP.add(new Label("Index Capabilities"));
		indexCapP.add(indexCap);
		indexQLP.add(new Label("Index Query Expression"));
		indexQLP.add(indexQL);
		indexCap.setTitle("Add the capabilities of this searchable seperated by comma");

		formPanel.add(collP);
		formPanel.add(sourceLP);
		formPanel.add(indexCapP);
		formPanel.add(indexQLP);
		formPanel.add(isSortable);
		formPanel.add(hostBtnPanel);

		captionP.setWidth("100%");
		captionP.add(formPanel);
		mainPanel.add(captionP);

		// if this represents an existing searchable field info then set the existing values to the fields
		if (sBean != null && (sBean.getID() != null || sBean.getTempID() != null)) {
			saveBtn.setText("Update");
			deleteBtn.setVisible(true);
			saveSearchableMsg = "The Searchable field has been updated";

			//TODO this.collectionID.setText(sBean.getCollectionID());
			this.collectionID.setText(sBean.getCollectionName() + " (" + sBean.getCollectionID() + ")");
			sourceLocator.setText(sBean.getSourceLocator());
			this.indexQL.setText(sBean.getIndexQueryLanguage());
			this.isSortable.setValue(sBean.isSortable());

			if (sBean.getIndexCapabilities() != null && sBean.getIndexCapabilities().size() > 0) {
				String tmp = "";
				for (String c : sBean.getIndexCapabilities()) {
					tmp += c + ", ";
				}
				tmp = tmp.substring(0, tmp.length()-2);
				this.indexCap.setText(tmp);
			}
		}

		initWidget(mainPanel);

		// Add click handler for the save button
		saveBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				boolean isValidated = validateForm();
				if (isValidated) {
					//TODO this is new
					fBean.addSearchableField(sBean);
					sBean.setCollectionID(getCollectionIDFromDesc(collectionID.getText()));
					sBean.setCollectionName(collectionID.getText().substring(0, collectionID.getText().lastIndexOf("(")-1));
					sBean.setSortable(isSortable.getValue());
					sBean.setSourceLocator(sourceLocator.getText().trim());
					if(!indexQL.getText().trim().isEmpty())
						sBean.setIndexQueryLanguage(indexQL.getText().trim());

				
					String caps = indexCap.getText().trim();
					if (!caps.isEmpty()) {
						String[] capsArray = caps.split(",");
						ArrayList<String> cBean = new ArrayList<String>();
						for (String c : capsArray) {
							Log.debug("Cap -> " + c);
							cBean.add(c.trim());
						}
						sBean.setIndexCapabilities(cBean);
					}
					else
						sBean.setIndexCapabilities(null);

					/* Update the referenced Field for the new searchable
					 * Assign as temporary ID to the ddl the indexID */
					// set the temporary ID so that it can be found later from the client if it still has not got a valid ID 
					if (sBean.getID() == null) {
						sBean.setTempID(collectionID.getText().trim()+ "_" + sourceLocator.getText().trim());
						//TODO changed to name instead of ID
						referenceWidget.searchableFields.addItem(sBean.getCollectionName(), sBean.getTempID());
						referenceWidget.searchableFields.setSelectedIndex(referenceWidget.searchableFields.getItemCount()-1);
					}
					else {
						// TODO changed to name
						referenceWidget.searchableFields.setItemText(referenceWidget.searchableFields.getSelectedIndex(), sBean.getCollectionName());
					}
					if (!referenceWidget.searchableFields.isVisible())
						referenceWidget.searchableFields.setVisible(true);
					
					deleteBtn.setVisible(true);
					saveBtn.setText("Update");
					referenceWidget.mainPanel.insert(FieldFormPanel.warningMsg, 0);
					SearchManager.showInfoPopup(saveSearchableMsg);
					saveSearchableMsg = "The Searchable field has been updated";
				}
				else
					SearchManager.showInfoPopup("Required fields cannot be empty. Fill in the empty fields and then click on the 'Add/Update' button");
			}
		});
	}

	protected void setDeleteBtnHandler(ClickHandler handler) {
		deleteBtn.addClickHandler(handler);
	}

	private boolean validateForm() {
		if (collectionID.getText().trim().isEmpty() || sourceLocator.getText().trim().isEmpty())
			return false;
		else
			return true;
	}
	
	private String getCollectionIDFromDesc(String desc) {
		if (desc.contains("("))
			return desc.trim().substring(desc.lastIndexOf("(")+1, desc.length()-1);
		return desc;
	}
}
