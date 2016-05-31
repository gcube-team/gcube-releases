package org.gcube.portlets.admin.searchmanagerportlet.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class AnnotationsTable extends Composite {
	
	Button saveBtn =  new Button("Save");
	final MultiSelectionModel<String> selectionModel = new MultiSelectionModel<String>();
	List<String> annotations;
	String currentFieldID;
	private VerticalPanel vp = new VerticalPanel();
	final CellTable<String> table = new CellTable<String>();

	public AnnotationsTable(final String fieldID, final List<String> annotations) {
		this.annotations = annotations;
		this.currentFieldID = fieldID;
		vp.setSpacing(6);

		
		// Display 10 rows in one page
		table.setPageSize(10);
		

		// Add a selection model so we can select cells.	
		table.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<String> createCheckboxManager());

		Column<String, Boolean> checkColumn = new Column<String, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(String object) {
				// Get the value from the selection model.
				return selectionModel.isSelected(object);
			}
		};
		table.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
		table.setColumnWidth(checkColumn, 40, Unit.PX);

		TextColumn<String> ann = new TextColumn<String>() {
			@Override
			public String getValue(String object) {
				return object;
			}
		};
		table.addColumn(ann, "Semantic Annotation");

		AsyncDataProvider<String> provider = new AsyncDataProvider<String>() {
			@Override
			protected void onRangeChanged(HasData<String> display) {
				int start = display.getVisibleRange().getStart();
				int end = start + display.getVisibleRange().getLength();
				end = end >= annotations.size() ? annotations.size() : end;
				List<String> sub = annotations.subList(start, end);
				updateRowData(start, sub);
			}
		};
		provider.addDataDisplay(table);
		provider.updateRowCount(annotations.size(), true);

		SimplePager.Resources resources = GWT.create(SimplePager.Resources.class); 
		SimplePager pager = new SimplePager(TextLocation.CENTER, resources , false, 1000, true); 
		
		//SimplePager pager = new SimplePager();
		pager.setRangeLimited(true);
		pager.setDisplay(table);
		
		saveBtn.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				
				AsyncCallback<Void> saveFieldAnnotationCallback = new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						saveBtn.setEnabled(true);
						SearchManager.hideLoading();
						SearchManager.showInfoPopup("Failed to save the annotations for the selected Field. Please try again.");
						
					}

					public void onSuccess(Void result) {
						saveBtn.setEnabled(true);
						SearchManager.hideLoading();
						
					}
				};SearchManager.smService.saveAnnotations(currentFieldID, getSelectedAnnotations(), getNonSelectedAnnotations(), saveFieldAnnotationCallback);
				saveBtn.setEnabled(false);
				SearchManager.showLoading();
				
			}
		});
		
		vp.add(table);
		vp.add(pager);
		vp.add(saveBtn);
		vp.setCellHorizontalAlignment(saveBtn, HasHorizontalAlignment.ALIGN_RIGHT);

		initWidget(vp);
	}
	
	public void selectAnnotationsOfField(String fieldID, ArrayList<String> fieldsAnnotations) {
		currentFieldID = fieldID;
		selectionModel.clear();
		for (String c : fieldsAnnotations) {	
			selectionModel.setSelected(c, true);
		}
	}
	
	
	public ArrayList<String> getSelectedAnnotations() {
		ArrayList<String> selectedAnnotations = new ArrayList<String>();
		for (String c : annotations) {
			if (selectionModel.isSelected(c)) {
				selectedAnnotations.add(c);
			}
		}
		return selectedAnnotations;
		}
	
	public ArrayList<String> getNonSelectedAnnotations() {
		ArrayList<String> nonSelectedAnnotations = new ArrayList<String>();
		for (String c : annotations) {
			if (!selectionModel.isSelected(c)) {
				nonSelectedAnnotations.add(c);
			}
		}
		return nonSelectedAnnotations;
		}
}
