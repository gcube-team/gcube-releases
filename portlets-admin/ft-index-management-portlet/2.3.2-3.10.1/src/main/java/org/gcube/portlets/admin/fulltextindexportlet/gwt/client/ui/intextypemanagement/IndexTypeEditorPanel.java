package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.intextypemanagement;

import java.util.Map;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.FTIndexManagementPortlet;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.FieldBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.IndexTypeBean;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class IndexTypeEditorPanel extends Composite {

	private ListBox indexTypesList;
	private int selectedIdxType;
	private Button refreshIdxTypesBtn;
	private Button addIdxTypeBtn;
	private Button copyIdxTypeBtn;
	private Button deleteIdxTypeBtn;
	private TextBox idxTypeNameBox;
	private TextBox idxTypeDescBox;
	private Widget idxTypeDetailsPanel;
	private FlexTable indexTypeTable;
	private Label successLabel;
	private Label errorLabel;
	private Map<String, IndexTypeBean> idxTypeResources;
	private Button saveIdxTypeButton;
	
	public IndexTypeEditorPanel() {
		selectedIdxType = -1;
		idxTypeResources = null;
		
		Grid hp = new Grid(1, 3);
		hp.setWidget(0, 0, createIndexTypeListPanel());
		hp.getCellFormatter().setWidth(0, 0, "50px");
		hp.setWidget(0, 1, new HTML("<div style=\"width: 2px; height: 400px; border-right: 1px solid rgb(250,250,250); border-left: 1px solid rgb(100,100,100);\"> </div>"));
		hp.getCellFormatter().setWidth(0, 1, "2px");
		idxTypeDetailsPanel = createIndexTypeDetailsPanel();
		hp.setWidget(0, 2, idxTypeDetailsPanel);
		hp.setCellPadding(10);
		
		initWidget(hp);
		
		populateIndexTypeList();
		idxTypeListSelectionChange();
	}
	
	private void populateIndexTypeList() {
		AsyncCallback<Map<String,IndexTypeBean>> callback = new AsyncCallback<Map<String,IndexTypeBean>>() {

			public void onSuccess(Map<String, IndexTypeBean> arg0) {
				indexTypesList.clear();
				idxTypeResources = arg0;
				for (Map.Entry<String, IndexTypeBean> res : arg0.entrySet()) {
					indexTypesList.addItem(res.getValue().getIndexTypeName(), res.getKey());
				}
				indexTypesList.setSelectedIndex(-1);
				selectedIdxType = -1;
				idxTypeListSelectionChange();
			}

			public void onFailure(Throwable arg0) {
				Window.alert("Failed to retrieve the list of available indexTypes:\n" + arg0.toString());
			}
		};
		FTIndexManagementPortlet.mgmtService.getAllIndexTypes(callback);
	}
	
	private void idxTypeListSelectionChange() {
		if (selectedIdxType == -1) {
			deleteIdxTypeBtn.setEnabled(false);
			copyIdxTypeBtn.setEnabled(false);
			idxTypeDetailsPanel.setVisible(false);
		}
		else {
			deleteIdxTypeBtn.setEnabled(true);
			copyIdxTypeBtn.setEnabled(true);
			
			IndexTypeBean selectedIndexType = idxTypeResources.get(indexTypesList.getValue(selectedIdxType));
			updateIndexTypeDetails(selectedIndexType);
			idxTypeDetailsPanel.setVisible(true);
		}
	}
	
	private Widget createIndexTypeListPanel() {
		VerticalPanel idxTypesListPanel = new VerticalPanel();
		idxTypesListPanel.add(new Label("Available indexTypes:"));
		indexTypesList = new ListBox();
		indexTypesList.setVisibleItemCount(15);
		indexTypesList.addChangeListener(new ChangeListener() {
			/* (non-Javadoc)
			 * @see com.google.gwt.user.client.ui.ChangeListener#onChange(com.google.gwt.user.client.ui.Widget)
			 */
			public void onChange(Widget arg0) {
				selectedIdxType = indexTypesList.getSelectedIndex();
				idxTypeListSelectionChange();
			}
		});
		indexTypesList.setWidth("200px");
		idxTypesListPanel.add(indexTypesList);

		/* Create the "refresh" button */
		refreshIdxTypesBtn = new Button("Refresh List", new ClickListener() {
			/* (non-Javadoc)
			 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
			 */
			public void onClick(Widget arg0) {
				//populateIndexTypeList();
			}
		});
		refreshIdxTypesBtn.setWidth("200px");
		idxTypesListPanel.add(refreshIdxTypesBtn);
		
		/* Create the "new" button */
		addIdxTypeBtn = new Button("New indexType", new ClickListener() {
			/* (non-Javadoc)
			 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
			 */
			public void onClick(Widget arg0) {
				IndexTypeBean newIdxType = new IndexTypeBean();
				newIdxType.setIndexTypeDesc("");
				newIdxType.setIndexTypeName("");
				newIdxType.setIndexTypeFields(new FieldBean[0]);
				newIdxType.setResourceID(null);
				
				updateIndexTypeDetails(newIdxType);
				idxTypeDetailsPanel.setVisible(true);
			}
		});
		addIdxTypeBtn.setWidth("200px");
		idxTypesListPanel.add(addIdxTypeBtn);

		/* Create the "Delete" button */
		deleteIdxTypeBtn = new Button("Delete indexType", new ClickListener() {
			/* (non-Javadoc)
			 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
			 */
			public void onClick(Widget arg0) {
				if (Window.confirm("Are you sure you want to delete the indexType '" + idxTypeNameBox +"'?")) {
					
					AsyncCallback<Void> callback = new AsyncCallback<Void>() {
	                	
	                	public void onSuccess(Void arg0) {
	                		//populateIndexTypeList();
	                	}
	                	
	                	public void onFailure(Throwable arg0) {
	                		Window.alert("Failed to delete the indexType:\n" + arg0);
	                	}
	                };
	
					IndexTypeBean selectedIndexType = idxTypeResources.get(indexTypesList.getValue(selectedIdxType));
	                FTIndexManagementPortlet.mgmtService.deleteIndexType(selectedIndexType, callback);
				}
			}
		});
		deleteIdxTypeBtn.setWidth("200px");
		idxTypesListPanel.add(deleteIdxTypeBtn);

		/* Create the "copy button" */
		copyIdxTypeBtn = new Button("Copy indexType", new ClickListener() {
			/* (non-Javadoc)
			 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
			 */
			public void onClick(Widget arg0) {
				IndexTypeBean selectedIndexType = idxTypeResources.get(indexTypesList.getValue(selectedIdxType));
				IndexTypeBean newIdxType = new IndexTypeBean();
				newIdxType.setIndexTypeDesc(selectedIndexType.getIndexTypeDesc());
				newIdxType.setIndexTypeName("Copy of " + selectedIndexType.getIndexTypeName());
				FieldBean[] otherIdxTypeFields = selectedIndexType.getIndexTypeFields();
				FieldBean[] idxTypeFields = new FieldBean[otherIdxTypeFields.length];
				for (int i=0; i<otherIdxTypeFields.length; i++) {
					FieldBean fb = new FieldBean();
					fb.setBoost(otherIdxTypeFields[i].getBoost());
					fb.setIndex(otherIdxTypeFields[i].getIndex());
					fb.setName(otherIdxTypeFields[i].getName());
					fb.setReturned(otherIdxTypeFields[i].getReturned());
					fb.setSort(otherIdxTypeFields[i].getSort());
					fb.setStore(otherIdxTypeFields[i].getStore());
					fb.setTokenize(otherIdxTypeFields[i].getTokenize());
					idxTypeFields[i] = fb;
				}
				newIdxType.setIndexTypeFields(idxTypeFields);
				newIdxType.setResourceID(null);
				
				updateIndexTypeDetails(newIdxType);
				idxTypeDetailsPanel.setVisible(true);
			}
		});
		copyIdxTypeBtn.setWidth("200px");
		idxTypesListPanel.add(copyIdxTypeBtn);
		
		return idxTypesListPanel;
	}
	
	private Widget createIndexTypeDetailsPanel() {
		VerticalPanel idxDetailsPanel = new VerticalPanel();
		idxDetailsPanel.setWidth("100%");
		
		Grid hp = new Grid(2, 2);
		hp.setWidget(0, 0, new Label("IndexType name:"));
		idxTypeNameBox = new TextBox();
		idxTypeNameBox.setVisibleLength(20);
		hp.setWidget(0, 1, idxTypeNameBox);
		hp.setWidget(1, 0, new Label("IndexType description:"));
		idxTypeDescBox = new TextBox();
		idxTypeDescBox.setVisibleLength(50);
		hp.setWidget(1, 1, idxTypeDescBox);
		hp.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
		hp.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_MIDDLE);
		idxDetailsPanel.add(hp);
		
		indexTypeTable = new FlexTable();
        indexTypeTable.setCellSpacing(0);
        indexTypeTable.setCellPadding(2);

        saveIdxTypeButton = new Button("Save", new ClickListener() {
        	/* (non-Javadoc)
        	 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
        	 */
        	public void onClick(Widget arg0) {
        		
        		/* Check if name is valid */
        		String idxTypeName = idxTypeNameBox.getText();
        		if (!idxTypeName.startsWith("IndexType_ft_")) {
        			Window.alert("The indexType name must start with the 'IndexType_ft_' prefix.");
        			return;
        		}
        		if (selectedIdxType == -1) {
	        		for (IndexTypeBean itb : idxTypeResources.values()) {
	        			if (idxTypeName.equalsIgnoreCase(itb.getIndexTypeName())) {
	        				Window.alert("An indexType with the same name already exists. Please choose a different name.");
	        				break;
	        			}
	        		}
        		}
        		else {
        			IndexTypeBean tmp = idxTypeResources.get(indexTypesList.getValue(selectedIdxType));
	        		for (IndexTypeBean itb : idxTypeResources.values()) {
	        			if (!(tmp.getResourceID().equals(itb.getResourceID())) && idxTypeName.equalsIgnoreCase(itb.getIndexTypeName())) {
	        				Window.alert("An indexType with the same name already exists. Please choose a different name.");
	        				break;
	        			}
	        		}        			
        		}
        		
        		/* Construct the indexTypeBean */
        		FieldBean[] fields = new FieldBean[indexTypeTable.getRowCount() - 1];
                for (int i = 1; i < indexTypeTable.getRowCount(); i++) {
                    FieldBean field = new FieldBean();
                    field.setName(((TextBox) indexTypeTable.getWidget(i, 0)).getText());
                    field.setIndex(((CheckBox) indexTypeTable.getWidget(i, 1)).isChecked());
                    field.setStore(((CheckBox) indexTypeTable.getWidget(i, 2)).isChecked());
                    field.setReturned(((CheckBox) indexTypeTable.getWidget(i, 3)).isChecked());
                    field.setBoost(((TextBox) indexTypeTable.getWidget(i, 4)).getText());
                    field.setTokenize(((CheckBox) indexTypeTable.getWidget(i, 5)).isChecked());
                    fields[i-1] = field;
                }

                IndexTypeBean selectedIndexType = null;
                if (selectedIdxType != -1) {
	                selectedIndexType = idxTypeResources.get(indexTypesList.getValue(selectedIdxType));
                }
                else {
                	selectedIndexType = new IndexTypeBean();
                	selectedIndexType.setResourceID(null);
                }
                selectedIndexType.setIndexTypeFields(fields);
                selectedIndexType.setIndexTypeName(idxTypeNameBox.getText());
                selectedIndexType.setIndexTypeDesc(idxTypeDescBox.getText());
                
                final IndexTypeBean indexTypeObj = selectedIndexType;
                
                AsyncCallback<String> callback = new AsyncCallback<String>() {
                	/* (non-Javadoc)
                	 * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
                	 */
                	public void onSuccess(String arg0) {
                		Window.alert("The indexType has been saved successfully.");
                		
                		if (indexTypeObj.getResourceID() == null) {
                			indexTypesList.addItem(indexTypeObj.getIndexTypeName(), arg0);
                			selectedIdxType = indexTypesList.getItemCount() - 1;
                			indexTypesList.setSelectedIndex(selectedIdxType);
                			idxTypeListSelectionChange();
                		}
                		else {
                			indexTypesList.setItemText(selectedIdxType, indexTypeObj.getIndexTypeName());
                		}
                		
                		indexTypeObj.setResourceID(arg0);
                		idxTypeResources.put(arg0, indexTypeObj);
                	}
                	
                	/* (non-Javadoc)
                	 * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
                	 */
                	public void onFailure(Throwable arg0) {
                		Window.alert("Failed to save the indexType:\n" + arg0);
                	}
                };
                FTIndexManagementPortlet.mgmtService.saveIndexType(selectedIndexType, callback);
        	}
        });

        VerticalPanel vp = new VerticalPanel();
        vp.setWidth("100%");
        vp.add(indexTypeTable);
        Button addFieldBtn = new Button("Add new field");
        addFieldBtn.addClickListener(new ClickListener() {
        	/* (non-Javadoc)
        	 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
        	 */
        	public void onClick(Widget arg0) {
        		addIndexFieldRow("New field", true, true, true, "1.0", true);
        	}
        });
        vp.add(addFieldBtn);
        ScrollPanel sp = new ScrollPanel(vp);
        sp.setWidth("100%");
        sp.setHeight("350px");
        idxDetailsPanel.add(sp);
        idxDetailsPanel.add(saveIdxTypeButton);
        
        successLabel = new Label();
        successLabel.setVisible(false);
        successLabel.addStyleName("diligent-success");
        errorLabel = new Label();
        errorLabel.setVisible(false);
        errorLabel.addStyleName("diligent-error");
        idxDetailsPanel.add(successLabel);
        idxDetailsPanel.add(errorLabel);

        indexTypeTable.setWidth("100%");
        
		return idxDetailsPanel;
	}
	
	private void updateIndexTypeDetails(IndexTypeBean idxType) {
		idxTypeNameBox.setText(idxType.getIndexTypeName());
		idxTypeDescBox.setText(idxType.getIndexTypeDesc());
		
		FieldBean[] indexTypeFields = idxType.getIndexTypeFields();
        indexTypeTable.clear();
        indexTypeTable.setWidget(0, 0, new Label("name"));
        indexTypeTable.getCellFormatter().addStyleName(0, 0,
                "diligent-indexType-header");
        indexTypeTable.setWidget(0, 1, new Label("index"));
        indexTypeTable.getCellFormatter().addStyleName(0, 1,
                "diligent-indexType-header");
        indexTypeTable.setWidget(0, 2, new Label("store"));
        indexTypeTable.getCellFormatter().addStyleName(0, 2,
                "diligent-indexType-header");
        indexTypeTable.setWidget(0, 3, new Label("return"));
        indexTypeTable.getCellFormatter().addStyleName(0, 3,
                "diligent-indexType-header");
        indexTypeTable.setWidget(0, 4, new Label("boost"));
        indexTypeTable.getCellFormatter().addStyleName(0, 4,
                "diligent-indexType-header");
        indexTypeTable.setWidget(0, 5, new Label("tokenize"));
        indexTypeTable.getCellFormatter().addStyleName(0, 5,
                "diligent-indexType-header");
        // indexTypeTable.getRowFormatter().addStyleName(0,
        // "diligent-indexType-header");
        
        /* Remove old rows */
        int numRows = indexTypeTable.getRowCount();
        for (int i=numRows-1; i>0; i--)
        	indexTypeTable.removeRow(i);
        
        /* Add new rows */
        for (int i = 0; i < indexTypeFields.length; i++) {
        	addIndexFieldRow(indexTypeFields[i].getName(), indexTypeFields[i].getIndex(), indexTypeFields[i].getStore(), indexTypeFields[i].getReturned(), indexTypeFields[i].getBoost(), indexTypeFields[i].getTokenize());
        }

        saveIdxTypeButton.setEnabled(true);
	}
	
	private void addIndexFieldRow(String name, boolean bIndex, boolean bStore, boolean bReturn, String boost, boolean bTokenize) {
		int z = indexTypeTable.getRowCount();
		
		TextBox tbFieldName = new TextBox();
    	tbFieldName.setText(name);
        indexTypeTable.setWidget(z, 0, tbFieldName);
        indexTypeTable.getCellFormatter().addStyleName(z, 0, "diligent-indexType");

        CheckBox indexed = new CheckBox();
        indexed.setChecked(bIndex);
        //indexed.setEnabled(false);
        indexTypeTable.setWidget(z, 1, indexed);
        indexTypeTable.getCellFormatter().addStyleName(z, 1, "diligent-indexType");

        CheckBox stored = new CheckBox();
        stored.setChecked(bStore);
        //stored.setEnabled(false);
        indexTypeTable.setWidget(z, 2, stored);
        indexTypeTable.getCellFormatter().addStyleName(z, 2, "diligent-indexType");

        CheckBox returned = new CheckBox();
        returned.setChecked(bReturn);
        indexTypeTable.setWidget(z, 3, returned);
        indexTypeTable.getCellFormatter().addStyleName(z, 3, "diligent-indexType");

        TextBox tbBoost = new TextBox();
        tbBoost.setText(boost);
        tbBoost.setVisibleLength(3);
        // boost.setMaxLength(1);
        indexTypeTable.setWidget(z, 4, tbBoost);
        indexTypeTable.getCellFormatter().addStyleName(z, 4, "diligent-indexType");

        CheckBox tokenized = new CheckBox();
        tokenized.setChecked(bTokenize);
        //tokenized.setEnabled(false);
        indexTypeTable.setWidget(z, 5, tokenized);
        indexTypeTable.getCellFormatter().addStyleName(z, 5, "diligent-indexType");

        final HTML deleteField = new HTML("<div rowNum=\"" + z + "\" class=\"diligent-indexTypeField-delete\"> </div>");
        deleteField.addClickListener(new ClickListener() {
        	/* (non-Javadoc)
        	 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
        	 */
        	public void onClick(Widget arg0) {
        		int rowNum = Integer.valueOf(deleteField.getElement().getElementsByTagName("div").getItem(0).getAttribute("rowNum"));
        		indexTypeTable.removeRow(rowNum);
        		for (int j=rowNum; j<indexTypeTable.getRowCount(); j++)
        			indexTypeTable.getWidget(j, 6).getElement().getElementsByTagName("div").getItem(0).setAttribute("rowNum", String.valueOf(j));
        	}
        });
        indexTypeTable.setWidget(z, 6, deleteField);
        indexTypeTable.getCellFormatter().addStyleName(z, 5, "diligent-indexType");
        
        // indexTypeTable.getRowFormatter().addStyleName(i+1,
        // "diligent-indexType");
	}
}
