package org.gcube.portlets.admin.searchmanagerportlet.gwt.client.widgets;

import java.util.ArrayList;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.FieldsListPanel;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.SearchManager;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.FieldInfoBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.PresentableFieldInfoBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.SMConstants;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.SearchableFieldInfoBean;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FieldFormPanel extends Composite {

	protected VerticalPanel mainPanel = new VerticalPanel();
	private CaptionPanel captionPanel = new CaptionPanel();
	private HorizontalPanel hostSPPanel = new HorizontalPanel();
	private HorizontalPanel fPanel = new HorizontalPanel();

	private TextBox fieldLabel = new TextBox();
	private TextArea fieldDesc = new TextArea();	
	protected ListBox searchableFields = new ListBox();
	protected ListBox presentableFields = new ListBox();

	private HorizontalPanel labelP = new HorizontalPanel();
	private HorizontalPanel descP = new HorizontalPanel();
	private DockPanel searchableIP = new DockPanel();
	private DockPanel presentableIP = new DockPanel();

	private Button addSearchableBtn = new Button();
	private Button addPresentableBtn = new Button();

	// These 2 panels will display the current selected searchable/presentable field
	private VerticalPanel sPropertiesP = new VerticalPanel();
	private VerticalPanel pPropertiesP = new VerticalPanel();

	private Button saveBtn = new Button("Save");

	protected static HTML warningMsg = new HTML("<span style=\"color: darkred\">" +
	"WARNING: Your changes are temporary saved. Please click on the <b>Save</b> button to save your changes</span>");

	protected static HTML noSearchableMsg = new HTML("<span style=\"color: darkblue\">" +
			"No Searchable fields available", true);

	protected static HTML noPresentableMsg = new HTML("<span style=\"color: darkblue\">" +
			"No presentable fields available.", true); 
	
	protected static HTML requiredFieldsMsg = new HTML("<span  style=\"color: #bbbbbb;font-size: small;\">" +
			"Fields with * are mandatory", true); 

	public FieldFormPanel(final FieldInfoBean fBean, final FieldsListPanel fieldsPanel) {

		Log.trace("Creating a new form panel");
		mainPanel.setWidth("100%");
		mainPanel.setSpacing(8);
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		fPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		fPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		captionPanel.setWidth("97%");
		
		sPropertiesP.setWidth("100%");
		pPropertiesP.setWidth("100%");
		searchableIP.setWidth("100%");
		presentableIP.setWidth("100%");

		addSearchableBtn.setStyleName("addSPButton");
		addSearchableBtn.setTitle("Create a new searchable field info");
		addPresentableBtn.setStyleName("addPPButton");
		addPresentableBtn.setTitle("Create a new presentable field info");

		hostSPPanel.setSpacing(12);

		labelP.setSpacing(8);
		labelP.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		descP.setSpacing(8);
		descP.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		searchableIP.setSpacing(8);
		searchableIP.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		presentableIP.setSpacing(8);
		presentableIP.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);


		labelP.add(new Label("* Name"));
		labelP.add(fieldLabel);
		descP.add(new Label("Description"));
		descP.add(fieldDesc);
		searchableFields.setVisible(false);
		presentableFields.setVisible(false);
		searchableIP.add(addSearchableBtn, DockPanel.EAST);
		searchableIP.add(searchableFields, DockPanel.NORTH);
		searchableIP.add(sPropertiesP, DockPanel.CENTER);
		presentableIP.add(addPresentableBtn, DockPanel.EAST);
		presentableIP.add(presentableFields, DockPanel.NORTH);
		presentableIP.add(pPropertiesP, DockPanel.CENTER);

		hostSPPanel.add(searchableIP);
		hostSPPanel.add(presentableIP);

		// If this is an existing FieldInfo and not a new one
		if (fBean != null && fBean.getID() != null) {
			captionPanel.setCaptionHTML("<span style=\"color: darkblue\">" +
					"FIELD: " + fBean.getLabel() + " (" + fBean.getID() + ")");
			fieldDesc.setText((fBean.getDescription() != null) ? fBean.getDescription() : "");
			fieldLabel.setText(fBean.getLabel());

			// for all the searchable fields, add the collection ID to the drop down list and the ID as value
			if (fBean.getSearchableFields() != null && fBean.getSearchableFields().size() > 0) {
				Log.trace("searchables exist...");

				for (SearchableFieldInfoBean sBean : fBean.getSearchableFields()) {
					if (sBean.getID() != null) {
						//searchableFields.addItem(sBean.getCollectionID(), sBean.getID());
						searchableFields.addItem(sBean.getCollectionName(), sBean.getID());
						Log.trace("searchable added to the list");
					}
				}
				if (searchableFields.getItemCount() > 0) {
					searchableFields.setVisible(true);
					// preselect the first added item. This is random!
					searchableFields.setSelectedIndex(0);
					// Display the first item's properties
					final SearchableFieldInfoBean currentSBean = findSearchableBeanByID(searchableFields.getValue(0), fBean, true);
					SearchableInfoPanel sp = new SearchableInfoPanel(currentSBean, fBean, FieldFormPanel.this);
					sPropertiesP.clear();
					sPropertiesP.add(sp);

					/*
					 * This handler refers to the existing searchable fields that have been returned as part of the
					 * FieldInfo. This means that they do have an ID.
					 */
					sp.setDeleteBtnHandler(new ClickHandler() {

						public void onClick(ClickEvent event) {
							boolean confirmed = Window.confirm("Do you really want to delete the Searchable field?");
							if (confirmed) {
								String id = searchableFields.getValue(0);
								removeSearchable(id, 0, fBean);
							}
						}
					});
				}
			}
			// There are not searchable for now..... Display an info msg
			else {
				sPropertiesP.clear();
				sPropertiesP.add(noSearchableMsg);
			}

			// for all the presentable fields add the collection ID to the drop down list and the ID as value
			if (fBean.getPresentableFields() != null && fBean.getPresentableFields().size() > 0) {
				Log.trace("Presentables exist");
				for (PresentableFieldInfoBean pBean : fBean.getPresentableFields()) {
					if (pBean.getID() != null) {
						this.presentableFields.addItem(pBean.getCollectionName(), pBean.getID());
						Log.trace("presentable added to the list");
					}
				}
				if (presentableFields.getItemCount() > 0) {
					this.presentableFields.setVisible(true);
					// preselect the first added item. This is random!
					this.presentableFields.setSelectedIndex(0);
					// Display the first item's properties
					final PresentableFieldInfoBean currentPBean = findPresentableBeanByID(this.presentableFields.getValue(0), fBean, true);
					PresentableInfoPanel pp = new PresentableInfoPanel(currentPBean, fBean, FieldFormPanel.this);
					pPropertiesP.clear();
					pPropertiesP.add(pp);

					/*
					 * This handler refers to the existing presentable fields that have been returned as part of the
					 * FieldInfo. This means that they do have an ID.
					 */
					pp.setDeleteBtnHandler(new ClickHandler() {

						public void onClick(ClickEvent event) {
							boolean confirmed = Window.confirm("Do you really want to delete the Presentable field?");
							if (confirmed) {
								String id = presentableFields.getValue(0);
								removePresentable(id, 0, fBean);
							}
						}
					});
				}
			}
			else {
				pPropertiesP.clear();
				pPropertiesP.add(noPresentableMsg);
			}
		}
		// The form represents a new Field
		else {
			captionPanel.setCaptionHTML("<span style=\"color: darkblue\">" + "New Field");
			sPropertiesP.clear();
			sPropertiesP.add(noSearchableMsg);
			pPropertiesP.clear();
			pPropertiesP.add(noPresentableMsg);
		}

		fPanel.add(labelP);
		fPanel.add(descP);
		mainPanel.add(fPanel);
		mainPanel.add(hostSPPanel);
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.add(saveBtn);
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		mainPanel.add(requiredFieldsMsg);
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		captionPanel.add(mainPanel);	
		
		initWidget(captionPanel);

		saveBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you want to save field's changes?");
				if (confirmed) {
					// validate the form before sending the request to the server
					boolean isValidated = validateForm();
					if (isValidated) {
						boolean isUpdated = false;
						if (fBean.getID() != null)
							isUpdated = true;

						fBean.setLabel(fieldLabel.getText());
						if (!fieldDesc.getText().trim().isEmpty())
							fBean.setDescription(fieldDesc.getText());

						AsyncCallback<FieldInfoBean> updateFieldInfoCallback = new AsyncCallback<FieldInfoBean>() {

							public void onFailure(Throwable caught) {
								SearchManager.hideLoading();
								SearchManager.displayErrorWindow("Failed to save the field. Please try again", caught);
							}

							public void onSuccess(FieldInfoBean result) {
								SearchManager.hideLoading();
								mainPanel.remove(warningMsg);

								// Update the tree. Add the new item
								if (fBean.getID() == null) {
									fieldsPanel.addItemToTree(result, true);
									captionPanel.setCaptionHTML("<span style=\"color: darkblue\">" +
											"FIELD: " + result.getLabel() + " (" + result.getID() + ")");
								}
								// Update the info of the existing  tree item
								else {
									fieldsPanel.updateExistingFieldItem(result);
								}
								// TODO: check the delay of this It was executed now it is commented just for test
								//fieldsPanel.refreshTreeInfo();
							}
						};SearchManager.smService.createField(fBean, isUpdated, updateFieldInfoCallback);
						SearchManager.showLoading();
					}
					else {
						SearchManager.showInfoPopup("Field name cannot be empty.");
						fieldLabel.setFocus(true);
					}
				}
			}
		});


		searchableFields.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				final int selectedIndex = ((ListBox)event.getSource()).getSelectedIndex();
				final String id = ((ListBox)event.getSource()).getValue(selectedIndex);
				SearchableFieldInfoBean currentSBean;
				if (id.startsWith(SMConstants.TEMPIDOFFSET))
					currentSBean = findSearchableBeanByID(id, fBean, false);
				else 
					currentSBean = findSearchableBeanByID(id, fBean, true);
				SearchableInfoPanel sp = new SearchableInfoPanel(currentSBean, fBean, FieldFormPanel.this);
				sPropertiesP.clear();
				sPropertiesP.add(sp);

				sp.setDeleteBtnHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						boolean confirmed = Window.confirm("Do you really want to delete the Searchable field?");
						if (confirmed)
							removeSearchable(id, selectedIndex, fBean);
					}
				});
			}
		});

		presentableFields.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				final int selectedIndex = ((ListBox)event.getSource()).getSelectedIndex();
				final String id = ((ListBox)event.getSource()).getValue(selectedIndex);
				PresentableFieldInfoBean currentPBean;
				if (id.startsWith(SMConstants.TEMPIDOFFSET))
					currentPBean = findPresentableBeanByID(id, fBean, false);
				else 
					currentPBean = findPresentableBeanByID(id, fBean, true);
				PresentableInfoPanel pp = new PresentableInfoPanel(currentPBean, fBean, FieldFormPanel.this);
				pPropertiesP.clear();
				pPropertiesP.add(pp);

				/*
				 * In this case we check the source of the presentable field. If it is not yet stored
				 * we use the tempID, otherwise we use the ID 
				 */
				pp.setDeleteBtnHandler(new ClickHandler() {

					public void onClick(ClickEvent event) {
						boolean confirmed = Window.confirm("Do you really want to delete the Presentable field?");
						if (confirmed)
							removePresentable(id, selectedIndex, fBean);
					}
				});	
			}
		});

		addSearchableBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				addNewSearchable(fBean);
			}
		});

		addPresentableBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				addNewPresentable(fBean);
			}
		});

	}

	/**************************************************************************************************************************************************
	 * 
	 * 												
	 **************************************************************************************************************************************************/

	private void addNewSearchable(final FieldInfoBean fBean) {
		SearchableFieldInfoBean newSBean = new SearchableFieldInfoBean();
		//fBean.addSearchableField(newSBean);
		SearchableInfoPanel sp = new SearchableInfoPanel(newSBean, fBean, FieldFormPanel.this);
		sPropertiesP.clear();
		sPropertiesP.add(sp);
		
		sp.setDeleteBtnHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you really want to delete the Searchable field?");
				if (confirmed) {
					int selectedIndex = searchableFields.getSelectedIndex();
					String id = searchableFields.getValue(selectedIndex);
					removeSearchable(id, selectedIndex, fBean);
				}
			}
		});
	}
	
	private void addNewPresentable(final FieldInfoBean fBean) {
		PresentableFieldInfoBean newPBean = new PresentableFieldInfoBean();
	//	fBean.addPresentableField(newPBean);
		pPropertiesP.clear();
		PresentableInfoPanel pp = new PresentableInfoPanel(newPBean, fBean, FieldFormPanel.this);
		pPropertiesP.add(pp);	
		
		pp.setDeleteBtnHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you really want to delete the Presentable field?");
				if (confirmed) {
					int selectedIndex = presentableFields.getSelectedIndex();
					String id = presentableFields.getValue(selectedIndex);
					removePresentable(id, selectedIndex, fBean);
				}
			}
		});
	}
	
	private void removePresentable(String id, int index, FieldInfoBean fBean) {
		presentableFields.removeItem(index);
		if (presentableFields.getItemCount() > 0) {
			presentableFields.setSelectedIndex(0);
			String selID = presentableFields.getValue(0);
			PresentableFieldInfoBean currentPBean;
			if (selID.startsWith(SMConstants.TEMPIDOFFSET))
				currentPBean = findPresentableBeanByID(selID, fBean, false);
			else 
				currentPBean = findPresentableBeanByID(selID, fBean, true);
			addExistingPresentable(currentPBean, fBean);
		}
		else {
			presentableFields.setVisible(false);
			pPropertiesP.clear();
			pPropertiesP.add(noPresentableMsg);
		}

		ArrayList<PresentableFieldInfoBean> pFields = fBean.getPresentableFields();
		int i = 0;
		for (PresentableFieldInfoBean pb : pFields) {
			if (id.startsWith(SMConstants.TEMPIDOFFSET)) {
				if (pb.getTempID() != null && pb.getTempID().trim().equals(id.trim())) {
					pFields.remove(i);
					break;
				}
			}
			else if (pb.getID() != null && pb.getID().trim().equals(id.trim())) {
				pFields.remove(i);
				break;
			}
			i++;
		}
		mainPanel.insert(warningMsg, 0);
	}
	
	private void removeSearchable(String id, int index, FieldInfoBean fBean) {
		searchableFields.removeItem(index);
		if (searchableFields.getItemCount() > 0) {
			searchableFields.setSelectedIndex(0);
			String selID = searchableFields.getValue(0);
			SearchableFieldInfoBean currentSBean;
			if (selID.startsWith(SMConstants.TEMPIDOFFSET))
				currentSBean = findSearchableBeanByID(selID, fBean, false);
			else 
				currentSBean = findSearchableBeanByID(selID, fBean, true);
			addExistingSearchable(currentSBean, fBean);
		}	
		else {
			searchableFields.setVisible(false);
			sPropertiesP.clear();
			sPropertiesP.add(noSearchableMsg);
		}
		ArrayList<SearchableFieldInfoBean> sFields = fBean.getSearchableFields();
		int i = 0;
		for (SearchableFieldInfoBean sb : sFields) {
			if (id.startsWith(SMConstants.TEMPIDOFFSET)) {
				if (sb.getTempID() != null && sb.getTempID().trim().equals(id.trim())) {
					sFields.remove(i);
					break;
				}
			}
			else if (sb.getID() != null && sb.getID().trim().equals(id.trim())) {
				sFields.remove(i);
				break;
			}
			i++;
		}
		mainPanel.insert(warningMsg, 0);
}
	
	
	private void addExistingSearchable(SearchableFieldInfoBean sBean, final FieldInfoBean fBean) {
		SearchableInfoPanel sp = new SearchableInfoPanel(sBean, fBean, FieldFormPanel.this);
		sPropertiesP.clear();
		sPropertiesP.add(sp);
		
		sp.setDeleteBtnHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you really want to delete the Searchable field?");
				if (confirmed) {
					int selectedIndex = searchableFields.getSelectedIndex();
					String id = searchableFields.getValue(selectedIndex);
					removeSearchable(id, selectedIndex, fBean);
				}
			}
		});
	}
	
	private void addExistingPresentable(PresentableFieldInfoBean pBean, final FieldInfoBean fBean) {
		PresentableInfoPanel pp = new PresentableInfoPanel(pBean, fBean, FieldFormPanel.this);
		pPropertiesP.clear();
		pPropertiesP.add(pp);	
		
		pp.setDeleteBtnHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you really want to delete the Presentable field?");
				if (confirmed) {
					int selectedIndex = presentableFields.getSelectedIndex();
					String id = presentableFields.getValue(selectedIndex);
					removePresentable(id, selectedIndex, fBean);
				}
			}
		});
	}
	
	private void setSearchablePanelDeleteHandler(SearchableInfoPanel sp, final FieldInfoBean fBean) {
		sp.setDeleteBtnHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				boolean confirmed = Window.confirm("Do you really want to delete the Searchable field?");
				if (confirmed) {
					int selectedIndex = searchableFields.getSelectedIndex();
					String id = searchableFields.getValue(selectedIndex);
					removeSearchable(id, selectedIndex, fBean);
				}
			}
		});
	}

	private SearchableFieldInfoBean findSearchableBeanByID(String ID, FieldInfoBean fBean, boolean useSID) {
		if (fBean != null) {
			ArrayList<SearchableFieldInfoBean> tmp = fBean.getSearchableFields();
			if (tmp != null && tmp.size() > 0) {
				for (SearchableFieldInfoBean s : tmp) {
					if (useSID) {
						if (s.getID() != null) {
							if (s.getID().trim().equals(ID.trim()))
								return s;
						}
					}
					else {
						if (s.getTempID() != null) {
							if (s.getTempID().trim().equals(ID.trim())) {
								return s;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private PresentableFieldInfoBean findPresentableBeanByID(String ID, FieldInfoBean fBean, boolean useSID) {
		if (fBean != null) {
			ArrayList<PresentableFieldInfoBean> tmp = fBean.getPresentableFields();
			if (tmp != null && tmp.size() > 0) {
				for (PresentableFieldInfoBean s : tmp) {
					if (useSID) {
						if (s.getID().trim().equals(ID.trim()))
							return s;
					}
					else {
						if (s.getTempID() != null) {
							if (s.getTempID().trim().equals(ID.trim())) {
								return s;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private boolean validateForm() {
		if (fieldLabel.getText().trim().isEmpty())
			return false;
		else
			return true;
	}
}
