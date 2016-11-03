package org.gcube.application.aquamaps.aquamapsportlet.client.filters;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;

import com.allen_sauer.gwt.log.client.Log;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.KeyListener;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.menu.Adapter;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.CheckItem;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.MenuItem;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.menu.event.CheckItemListenerAdapter;

public abstract class SubmittedFiltersMenu extends Menu {

	
	private final SubmittedFiltersMenu instance = this; 
	
	
	JobSelection jobPopup=new JobSelection() {
		
		@Override
		public void setParameter(String paramName, String paramValue) {
			instance.setParameter(paramName, paramValue);
			jobPopup.hide();
		}
		
		public void close() {hide();};
	};
	
	
	public SubmittedFiltersMenu() {
		
		Menu showMenu=new Menu();
		CheckItem showAquaMapsObject=new CheckItem();
		showAquaMapsObject.setText("AquaMaps objects");
		showAquaMapsObject.setChecked(true);
		showAquaMapsObject.setGroup(Tags.submittedShowAquaMaps);
		showAquaMapsObject.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					setParameter(Tags.submittedShowAquaMaps,"true");
			}
		});
		showMenu.addItem(showAquaMapsObject);
		
		CheckItem showJobs=new CheckItem();
		showJobs.setText("Jobs");
		showJobs.setChecked(false);
		showJobs.setGroup(Tags.submittedShowAquaMaps);
		showJobs.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					setParameter(Tags.submittedShowAquaMaps,"false");
			}
		});
		showMenu.addItem(showJobs);
		
		MenuItem showMenuItem = new MenuItem("Show", showMenu); 
		this.addItem(showMenuItem);
		
		Menu typeMenu=new Menu();
		CheckItem allType=new CheckItem();
		allType.setText("All");
		allType.setChecked(true);
		allType.setGroup(Tags.submittedObjectType);
		allType.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					deleteParameter(Tags.submittedObjectType);
			}
		});
		typeMenu.addItem(allType);		
		
		CheckItem biodivType=new CheckItem();
		biodivType.setText("Biodiversity");
		biodivType.setChecked(false);
		biodivType.setGroup(Tags.submittedObjectType);
		biodivType.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					setParameter(Tags.submittedObjectType,ClientObjectType.Biodiversity.toString());
			}
		});
		typeMenu.addItem(biodivType);
		
		CheckItem distrType=new CheckItem();
		distrType.setText("Species Distribution");
		distrType.setChecked(false);
		distrType.setGroup(Tags.submittedObjectType);
		distrType.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					setParameter(Tags.submittedObjectType,ClientObjectType.SpeciesDistribution.toString());
			}
		});
		typeMenu.addItem(distrType);
		
		MenuItem typeItem = new MenuItem("Filter by Type", typeMenu); 
		this.addItem(typeItem);
		
		
		Menu statusMenu=new Menu();
		CheckItem allStatus=new CheckItem();
		allStatus.setText("All");
		allStatus.setChecked(true);
		allStatus.setGroup(Tags.submittedObjectStatus);
		allStatus.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					deleteParameter(Tags.submittedObjectStatus);
			}
		});
		statusMenu.addItem(allStatus);		
		
		CheckItem pendingStatus=new CheckItem();
		pendingStatus.setText("Pending");
		pendingStatus.setChecked(false);
		pendingStatus.setGroup(Tags.submittedObjectStatus);
		pendingStatus.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					setParameter(Tags.submittedObjectStatus,"Pending");
			}
		});
		statusMenu.addItem(pendingStatus);
		
		CheckItem generatingStatus=new CheckItem();
		generatingStatus.setText("Generating");
		generatingStatus.setChecked(false);
		generatingStatus.setGroup(Tags.submittedObjectStatus);
		generatingStatus.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					setParameter(Tags.submittedObjectStatus,"Generating");
			}
		});		
		statusMenu.addItem(generatingStatus);
		
		CheckItem simulatingStatus=new CheckItem();
		simulatingStatus.setText("Simulating");
		simulatingStatus.setChecked(false);
		simulatingStatus.setGroup(Tags.submittedObjectStatus);
		simulatingStatus.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					setParameter(Tags.submittedObjectStatus,"Simulating");
			}
		});
		statusMenu.addItem(simulatingStatus);
		
		CheckItem publishingStatus=new CheckItem();
		publishingStatus.setText("Publishing");
		publishingStatus.setChecked(false);
		publishingStatus.setGroup(Tags.submittedObjectStatus);
		publishingStatus.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					setParameter(Tags.submittedObjectStatus,"Publishing");
			}
		});
		statusMenu.addItem(publishingStatus);
		
		CheckItem completedStatus=new CheckItem();
		completedStatus.setText("Completed");
		completedStatus.setChecked(false);
		completedStatus.setGroup(Tags.submittedObjectStatus);
		completedStatus.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					setParameter(Tags.submittedObjectStatus,"Completed");
			}
		});
		statusMenu.addItem(completedStatus);
		
		CheckItem errorStatus=new CheckItem();
		errorStatus.setText("Error");
		errorStatus.setChecked(false);
		errorStatus.setGroup(Tags.submittedObjectStatus);
		errorStatus.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					setParameter(Tags.submittedObjectStatus,"Error");
			}
		});
		statusMenu.addItem(errorStatus);
		
		MenuItem statusMenuItem = new MenuItem("Filter by Status", statusMenu); 
		this.addItem(statusMenuItem);
		
		
		Menu jobIdMenu=new Menu();
		final CheckItem allJobs=new CheckItem();
		allJobs.setText("All");
		allJobs.setChecked(true);	
		allJobs.setGroup(Tags.submittedJobId);
		allJobs.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked)
					deleteParameter(Tags.submittedJobId);
			}
		});
		jobIdMenu.addItem(allJobs);
		
		
		
		CheckItem selectJob=new CheckItem();
		selectJob.setText("Select job");
		selectJob.setChecked(true);
		selectJob.setGroup(Tags.submittedJobId);
		selectJob.addListener(new BaseItemListenerAdapter(){			
			@Override
			public void onClick(BaseItem item, EventObject e) {
				jobPopup.show();
				jobPopup.grid.getStore().reload();
			}
		});
		jobIdMenu.addItem(selectJob);
		
//		JobComboBox jobCombo=new JobComboBox();
//		jobCombo.addListener(new ComboBoxListenerAdapter(){
//			public void onSelect(ComboBox comboBox, Record record, int index) {
//				Log.debug("entro in on select");
//				String jobId=record.getAsString("searchId");
//				allJobs.setChecked(false);
//				setParameter(Tags.submittedJobId, jobId);
//			}
//		});
//		Adapter jobChooser=new Adapter(jobCombo);
//		jobIdMenu.addItem(jobChooser);
		MenuItem jobIdMenuItem = new MenuItem("Filter by job", jobIdMenu); 
		this.addItem(jobIdMenuItem);
		setParameter(Tags.submittedShowAquaMaps,"true");
		
		
		Menu titleMenu=new Menu();
		final CheckItem filteredTitle=new CheckItem();
		final CheckItem allTitle=new CheckItem();
		allTitle.setText("All");
		allTitle.setChecked(true);
		allTitle.setGroup(Tags.submittedFilterByTitle);
		allTitle.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked){
					deleteParameter(Tags.submittedFilterByTitle);
					filteredTitle.setText("Title");
				}
			}
		});
		
		

		filteredTitle.setText("Title");
		filteredTitle.setChecked(true);
		filteredTitle.setGroup(Tags.submittedFilterByTitle);
		
			
		filteredTitle.addListener(new CheckItemListenerAdapter(){
			@Override
			public void onCheckChange(CheckItem item, boolean checked) {
				if(checked){
					MessageBox.prompt("Title", "Please enter title:",  
							new MessageBox.PromptCallback() {  
						public void execute(String btnID, String text) { 
							if(btnID.equalsIgnoreCase("OK")){	
								setParameter(Tags.submittedFilterByTitle, text);
								filteredTitle.setText("Title: "+text);
							}else {
								filteredTitle.setChecked(false);
								allTitle.setChecked(true);
							}
						}
					});
				}
					
			}
		});
		
		titleMenu.addItem(allTitle);
		titleMenu.addItem(filteredTitle);
		MenuItem titleMenuItem = new MenuItem("Filter by Title", titleMenu); 
		this.addItem(titleMenuItem);
	}
	
	public abstract void deleteParameter(String paramName);
	public abstract void setParameter(String paramName,String paramValue);
	
}
