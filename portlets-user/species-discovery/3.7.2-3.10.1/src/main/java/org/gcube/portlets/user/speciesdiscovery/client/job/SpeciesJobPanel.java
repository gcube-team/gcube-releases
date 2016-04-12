package org.gcube.portlets.user.speciesdiscovery.client.job;

import org.gcube.portlets.user.speciesdiscovery.client.job.occurrence.OccurrenceJobSpeciesPanel;
import org.gcube.portlets.user.speciesdiscovery.client.job.taxonomy.TaxonomyJobSpeciesPanel;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.util.RenderTextFieldUtil;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class SpeciesJobPanel  extends ContentPanel{

	protected static final String EMPTY = "Empty";
	protected static final String CURRENT_QUERY = "Current query: ";
	
	
	private ToolBar toolbar = new ToolBar();
	private TaxonomyJobSpeciesPanel taxonomyJobPanelInstance;
	private OccurrenceJobSpeciesPanel occurrenceJobPanelInstance;
	private Button btnTaxonomyJobs;
	private Button btnOccurrenceJobs;
	private int jobOccurrenceCount = 0;
	private int jobTaxonomyCount = 0;
	
	private TextField<String> currentQuery;
	
	public SpeciesJobPanel(EventBus eventBus) {
		this.setTopComponent(toolbar);
		setLayout(new FitLayout());
		setHeaderVisible(false);
		
		taxonomyJobPanelInstance = TaxonomyJobSpeciesPanel.getInstance(eventBus);
		occurrenceJobPanelInstance = OccurrenceJobSpeciesPanel.getInstance(eventBus);
		
		createOccurrencesToolBar();
		
		toolbar.add(new SeparatorToolItem());
		
		createTaxonomyToolBar();
		
		
		Text txtLastQuery = new Text(CURRENT_QUERY);
		txtLastQuery.setStyleAttribute("padding-right", "2px");
		txtLastQuery.setStyleAttribute("color", "gray");
		
		currentQuery = new TextField<String>();
		
		currentQuery.setReadOnly(true);
		currentQuery.setWidth(400);
		currentQuery.setValue(EMPTY);
		toolbar.add(new FillToolItem());
		toolbar.add(txtLastQuery);
		toolbar.add(currentQuery);
		
		RenderTextFieldUtil.setTextFieldAttr(currentQuery, "background", "none");
//		RenderTextFieldUtil.setTextFieldAttr(lastQuery, "text-align", "right");
	}
	
	private void createOccurrencesToolBar(){
		
		btnTaxonomyJobs = new Button("Species Taxonomy Jobs");
		btnTaxonomyJobs.setScale(ButtonScale.MEDIUM);
		setIconTaxonomyByCounter(0);
		
		btnTaxonomyJobs.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				resetTaxonoyCouter();
				taxonomyJobPanelInstance.getSpeciesJobWindow().show();
			}
			
		});
		toolbar.add(btnTaxonomyJobs);
	
	}
	
	private void resetTaxonoyCouter(){
		jobTaxonomyCount = 0;
		setIconTaxonomyByCounter(0);
	}
	
	private void createTaxonomyToolBar(){

		btnOccurrenceJobs = new Button("Species Occurrence Jobs");
		btnOccurrenceJobs.setScale(ButtonScale.MEDIUM);
		setIconOccurrenceByCounter(0);
		
		btnOccurrenceJobs.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				resetOccurrenceCounter();
				occurrenceJobPanelInstance.getSpeciesJobWindow().show();
			}
			
		});
		toolbar.add(btnOccurrenceJobs);
	}
	
	public void setLastQuery(String query){
		this.currentQuery.setValue(query);
	}
	
	public void setLastQueryAsEmpty(){
		this.currentQuery.setValue(EMPTY);
	}
	
	private void resetOccurrenceCounter(){
		jobOccurrenceCount = 0;
		setIconOccurrenceByCounter(0);
	}
	
	public void setIconOccurrenceByCounter(int count){
		this.jobOccurrenceCount += count;
		
		switch (jobOccurrenceCount) {
		
		case 0:
			
			btnOccurrenceJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getBluePlace()));
			
			break;
		case 1:
			
			btnOccurrenceJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getBluePlace1()));
			
			break;
		case 2:
			
			btnOccurrenceJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getBluePlace2()));
			break;
		case 3:
					
			btnOccurrenceJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getBluePlace3()));
			break;
		case 4:
			
			btnOccurrenceJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getBluePlace4()));
			break;
		default:
			btnOccurrenceJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getBluePlace4More()));
		}
		
		
		toolbar.layout();
	}
	
	
	public void setIconTaxonomyByCounter(int count){
		this.jobTaxonomyCount += count;
		
		switch (jobTaxonomyCount) {
		
		case 0:
			
			btnTaxonomyJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getTaxonomy()));
			
			break;
		case 1:
			
			btnTaxonomyJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getTaxonomy1()));
			
			break;
		case 2:
			
			btnTaxonomyJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getTaxonomy2()));
			break;
		case 3:
					
			btnTaxonomyJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getTaxonomy3()));
			break;
		case 4:
			
			btnTaxonomyJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getTaxonomy4()));
			break;
		default:
			btnTaxonomyJobs.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getTaxonomy4More()));
		}
		
		
		toolbar.layout();
	}

}
