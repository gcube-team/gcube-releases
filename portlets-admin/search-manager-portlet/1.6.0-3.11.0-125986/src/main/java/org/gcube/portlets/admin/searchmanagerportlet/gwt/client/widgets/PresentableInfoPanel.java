package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.SearchManager;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.CollectionInfoBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.FieldInfoBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.PresentableFieldInfoBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.SMConstants;

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

public class PresentableInfoPanel extends Composite{

	private SuggestBox collectionID;
	private MultiWordSuggestOracle slOracle = new MultiWordSuggestOracle();
	private SuggestBox sourceLocator = new SuggestBox(slOracle);
	private TextBox queryInfo = new TextBox();
	private TextBox presentationInfo = new TextBox();
	//private CheckBox hasProjection = new CheckBox("Has projection");
	private CheckBox isSortable = new CheckBox("Is sortable");

	private HorizontalPanel collP = new HorizontalPanel();
	private HorizontalPanel sourceLP = new HorizontalPanel();
	private HorizontalPanel queryIP = new HorizontalPanel();
	private HorizontalPanel presIP = new HorizontalPanel();

	private VerticalPanel mainPanel = new VerticalPanel();
	private VerticalPanel formPanel = new VerticalPanel();
	private CaptionPanel captionP = new CaptionPanel("Presentation Information");

	private HorizontalPanel hostBtnPanel = new HorizontalPanel();
	private Button saveBtn = new Button("Add");
	private Button deleteBtn = new Button("Delete");

	private String savePresentableMsg = "The new Presentable field has been created";

	public PresentableInfoPanel(final PresentableFieldInfoBean pBean, final FieldInfoBean fBean, final FieldFormPanel referenceWidget) {

		if (fBean.getAvailablePresentableCollectionsIDs() != null && fBean.getAvailablePresentableCollectionsIDs().size() > 0) {
			MultiWordSuggestOracle colIDOracle = new MultiWordSuggestOracle();
			ArrayList<String> defaultSugList = new ArrayList<String>();
			for (CollectionInfoBean cBean : fBean.getAvailablePresentableCollectionsIDs()) {
				String c = cBean.getName() + " (" + cBean.getID() + ")";
				colIDOracle.add(c);
				defaultSugList.add(c);
			}
			//colIDOracle.addAll(fBean.getAvailablePresentableCollectionsIDs());
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
					};SearchManager.smService.getIndexLocatorList(fBean.getID(), realColID, SMConstants.PRESENTABLE, getAvailablaLocatorsCallbak);
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
		queryIP.setSpacing(4);
		queryIP.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		presIP.setSpacing(4);
		presIP.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		collP.add(new Label("* Collection ID"));
		collP.add(collectionID);
		sourceLP.add(new Label("* Source Locator (IndexID)"));
		sourceLP.add(sourceLocator);
		queryIP.add(new Label("Query Expression"));
		queryIP.add(queryInfo);
		presIP.add(new Label("Presentation Information"));
		presIP.add(presentationInfo);


		formPanel.add(collP);
		formPanel.add(sourceLP);
		formPanel.add(queryIP);
		formPanel.add(presIP);
		//	formPanel.add(hasProjection);
		formPanel.add(isSortable);
		formPanel.add(hostBtnPanel);

		captionP.setWidth("100%");
		captionP.add(formPanel);
		mainPanel.add(captionP);

		// if this represents an existing presentable field info then set the existing values to the fields
		if (pBean != null && (pBean.getID() != null || pBean.getTempID() != null)) {
			saveBtn.setText("Update");
			deleteBtn.setVisible(true);
			savePresentableMsg = "The Presentable field has been updated";

			//this.collectionID.setText(pBean.getCollectionID());
			this.collectionID.setText(pBean.getCollectionName() + " (" + pBean.getCollectionID() + ")");
			//this.hasProjection.setValue(pBean.isProjection());
			this.isSortable.setValue(pBean.isSortable());
			//TODO change this

			if (pBean.getPresentationInfo() != null) {
				Iterator<String> it = pBean.getPresentationInfo().iterator();
				String pInfo = "";
				while (it.hasNext()) {
					String p = it.next();
					pInfo = pInfo + p + ", ";
				}
				pInfo = pInfo.substring(0, pInfo.length()-2);

				this.presentationInfo.setText(pInfo);
			}
			else
				this.presentationInfo.setText("");
			
			this.queryInfo.setText(pBean.getQueryExpression());
			this.sourceLocator.setText(pBean.getSourceLocator());
		}

		initWidget(mainPanel);

		// Add click handler for the save button
		saveBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				boolean isValidated = validateForm();
				if (isValidated) {
					//TODO this is new
					fBean.addPresentableField(pBean);
					pBean.setCollectionID(getCollectionIDFromDesc(collectionID.getText()));
					pBean.setCollectionName(collectionID.getText().substring(0, collectionID.getText().lastIndexOf("(")-1));
					pBean.setSourceLocator(sourceLocator.getText());
					pBean.setSortable(isSortable.getValue());
					//	pBean.setProjection(hasProjection.getValue());
					if (!queryInfo.getText().trim().isEmpty())
						pBean.setQueryExpression(queryInfo.getText());

					String pInfo = presentationInfo.getText().trim();
					if (!pInfo.isEmpty()) {
						String[] pInfoArray = pInfo.split(",");
						Set<String> pInfoSet = new HashSet<String>();
						for (String p : pInfoArray) {
							pInfoSet.add(p);
						}
						pBean.setPresentationInfo(pInfoSet);
					}
					else
						pBean.setPresentationInfo(null);


					// set the temporary ID so that it can be found later from the client if it still has not get a valid ID
					if (pBean.getID() == null) {
						pBean.setTempID(collectionID.getText().trim()+ "_" + sourceLocator.getText().trim());
						referenceWidget.presentableFields.addItem(pBean.getCollectionName(), pBean.getTempID());
						referenceWidget.presentableFields.setSelectedIndex(referenceWidget.presentableFields.getItemCount()-1);
					}
					else {
						referenceWidget.presentableFields.setItemText(referenceWidget.presentableFields.getSelectedIndex(), pBean.getCollectionName());
					}

					if (!referenceWidget.presentableFields.isVisible())
						referenceWidget.presentableFields.setVisible(true);


					deleteBtn.setVisible(true);
					saveBtn.setText("Update");
					referenceWidget.mainPanel.insert(FieldFormPanel.warningMsg, 0);
					SearchManager.showInfoPopup(savePresentableMsg);
					savePresentableMsg = "The Presentable field has been updated";
				}
				else
					SearchManager.showInfoPopup("Required fields cannot be empty. Fill in the empty fields and then click on the 'Add' button");
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
