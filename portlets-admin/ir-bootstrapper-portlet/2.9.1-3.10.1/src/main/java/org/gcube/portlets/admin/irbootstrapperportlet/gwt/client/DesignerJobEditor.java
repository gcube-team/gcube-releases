package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.AssignUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete.JobAutoCompleteData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete.JobAutoCompleteEntryDesc;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete.ObjectDesc;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete.ObjectNameAndPtr;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobAttributesChangeListener;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobListUpdatedListener;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobsDesignerNavigatorListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StoreTraversalCallback;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.EditorGridPanel;
import com.gwtext.client.widgets.grid.GridEditor;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridCellListenerAdapter;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtext.client.widgets.layout.VerticalLayout;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class DesignerJobEditor extends Composite implements JobListUpdatedListener, JobsDesignerNavigatorListener {

	private ArrayReader reader;
	private Store store;
	private RecordDef recordDef;
	private JobUIElement jobToEdit;
	private TextField jobNameField;
	private ComboBox jobTypeField;
	private ComboBox jobExtendsField;
	private RecordDef jobTypesRecordDef;
	private RecordDef jobNamesRecordDef;
	private Store jobTypesStore;
	private Store jobNamesAndTypesStore;
	private Button saveButton;
	private boolean hasMadeChanges;
	private JobAttributesChangeListener changeListener;
	private ComboBox assignToField;
	private Store assignToStore;
	private RecordDef assignToRecordDef;
	private ComboBox assignFromField;
	private Store assignFromStore;
	private RecordDef assignFromRecordDef;
	
	private JobAutoCompleteData autoCompleteData;
	private int selectedAssignment;
	
	private static final String USERINPUT_ASSIGNMENT = "%userInput";
	
	public DesignerJobEditor(IRBootstrapperPortletG portlet) {
		
		jobToEdit = null;
		autoCompleteData = null;
		hasMadeChanges = false;
		changeListener = null;
		selectedAssignment = -1;
		
		/* Create the job types and job names fields, recorddefs and stores */
		jobTypesRecordDef = new RecordDef(new FieldDef[] { new StringFieldDef("jobtype") });
		jobNamesRecordDef = new RecordDef(new FieldDef[] { new StringFieldDef("jobname"), new StringFieldDef("jobtype") });
		jobTypesStore = new Store(new ArrayReader(jobTypesRecordDef));
		jobNamesAndTypesStore = new Store(new ArrayReader(jobNamesRecordDef));
		jobTypeField = new ComboBox("Job type", "jobtype", 400);
		jobTypeField.setListWidth(400);
		jobTypeField.setStore(jobTypesStore);
		jobTypeField.setDisplayField("jobtype");
		jobTypeField.setMode(ComboBox.LOCAL);
		jobTypeField.setReadOnly(true);
		jobTypeField.setForceSelection(true);
		jobTypeField.setTriggerAction(ComboBox.ALL);
		jobExtendsField = new ComboBox("Extends", "jobextends", 400);
		jobExtendsField.setListWidth(400);
		jobExtendsField.setStore(jobNamesAndTypesStore);
		jobExtendsField.setDisplayField("jobname");
		jobExtendsField.setMode(ComboBox.LOCAL);
		jobExtendsField.setReadOnly(true);
		jobExtendsField.setForceSelection(true);
		jobExtendsField.setTriggerAction(ComboBox.ALL);
		jobExtendsField.setLinked(true);
		jobNameField = new TextField("Job name", "jobname", 400);
		
		jobTypeField.addListener(new ComboBoxListenerAdapter() {  
			public void onSelect(ComboBox comboBox, Record record, int index) {
				jobNamesAndTypesStore.filterBy(new StoreTraversalCallback() {
					public boolean execute(Record record) {
						String jobType = record.getAsString("jobtype");
						String jobType2 = jobTypeField.getValue();
						if (jobType.equals("*") || jobType.equals(jobType2))
							return true;
						return false;
					}
				});
				jobExtendsField.setValue("(none)");
				populateCombosWithAutoCompleteData(jobTypeField.getValue());
		    }
		});  

		/* Create the init assignments recorddef */
		recordDef = new RecordDef(  
			new FieldDef[]{  
					new StringFieldDef("to"),
					new StringFieldDef("from"),
					//
					new StringFieldDef("label"),
					new BooleanFieldDef("userinput")
			}
		);
		this.reader = new ArrayReader(recordDef);

		assignToRecordDef = new RecordDef(new FieldDef[] { new StringFieldDef("assignField") });
		assignToStore = new Store(new ArrayReader(assignToRecordDef));
		assignToField = new ComboBox();
		assignToField.setTriggerAction(ComboBox.ALL);
		assignToField.setStore(assignToStore);
		assignToField.setDisplayField("assignField");
		assignToField.setMode(ComboBox.LOCAL);
		assignToField.setListWidth(600);
		assignToField.setTypeAhead(true);
		assignToField.setMinChars(1);
		
		assignFromRecordDef = new RecordDef(new FieldDef[] { new StringFieldDef("assignField") });
		assignFromStore = new Store(new ArrayReader(assignFromRecordDef));
		assignFromField = new ComboBox();
		assignFromField.setTriggerAction(ComboBox.ALL);
		//assignFromField.setStore(assignToStore);
		assignFromField.setStore(assignFromStore);
		assignFromField.setDisplayField("assignField");
		assignFromField.setMode(ComboBox.LOCAL);
		assignFromField.setListWidth(600);
		assignFromField.setTypeAhead(true);
		assignFromField.setMinChars(1);

	
		
		ColumnConfig toCol = new ColumnConfig("Assign to", "to", 350, false, null, "to");
		toCol.setEditor(new GridEditor(assignToField));  

		ColumnConfig fromCol = new ColumnConfig("Value", "from", 350, false, null, "from");
		fromCol.setEditor(new GridEditor(assignFromField));
		
		//TODO
		ColumnConfig labelCol = new ColumnConfig("Label", "label", 400, false, null, "label");
		labelCol.setEditor(new GridEditor(new TextField()));
		
		ColumnConfig userInputCol = new ColumnConfig("User Input?", "userinput", 55);
		userInputCol.setRenderer(new Renderer() {  
	            public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex,  
	                                            int colNum, Store store) {  
	                boolean checked = ((Boolean) value).booleanValue();  
	                return "<img class=\"checkbox\" src=\"" + GWT.getModuleBaseURL() + "../images/" +  
	                                    (checked ? "checked.gif" : "unchecked.gif") + "\"/>";  
	            }
	        });  

		//ColumnConfig[] columnConfigs = { toCol, fromCol };
		ColumnConfig[] columnConfigs = { userInputCol, toCol, fromCol, labelCol};
		ColumnModel columnModel = new ColumnModel(columnConfigs);  
		columnModel.setDefaultSortable(false);  

		Panel p = new Panel();
		p.setBorder(false);
		p.setLayout(new VerticalLayout());
		EditorGridPanel grid = new EditorGridPanel();
		grid.setColumnModel(columnModel);
		grid.setWidth(1150);
		//grid.setHeight(200);
		grid.setAutoHeight(true);
		grid.setTitle("Job initialization assignments");
		grid.setFrame(true);
		grid.setClicksToEdit(1);
		grid.setAutoScroll(true);
		grid.addGridRowListener(new GridRowListenerAdapter() {
			@Override
			public void onRowClick(GridPanel grid, int rowIndex, EventObject e) {
				selectedAssignment = rowIndex;
			}
		});
		grid.addTool(new Tool(Tool.PLUS, new Function() {  
			public void execute() {
				//store.add(recordDef.createRecord(new String[2]));
				store.add(recordDef.createRecord(new String[4]));
			}  
		}, "Add new assignment"));
		grid.addTool(new Tool(Tool.MINUS, new Function() {  
			public void execute() {
				if (selectedAssignment != -1) {
					store.remove(store.getAt(selectedAssignment));
					selectedAssignment = -1;
				}
			}  
		}, "Delete selected assignment"));
		
		 grid.addGridCellListener(new GridCellListenerAdapter() {  
			 public void onCellClick(GridPanel grid, int rowIndex, int colIndex, EventObject e) {  
	                if (grid.getColumnModel().getDataIndex(colIndex).equals("label")) {  
	                    Record record = grid.getStore().getAt(rowIndex);
	                    String from = record.getAsString("from");
	                    //Log.debug(from);
	                    if (!from.trim().equals(USERINPUT_ASSIGNMENT))
	                    	MessageBox.alert("The label is not needed for that type of assignment.");
	                }  
	            }  
	        });  
		
		/* create the data store */
		store = new Store(reader);
		grid.setStore(store);

		/* Create the "save" button and add it to the panel */
		saveButton = new Button("Save", new ButtonListenerAdapter() {  
			public void onClick(Button button, EventObject e) {
				
				if (jobTypeField.getValue() == null) {
					MessageBox.alert("A job type must be selected for this job.");
					return;
				}
				
				/* Keep the original and current job attribute values */
				final String originalJobName = jobToEdit.getName();
				final String originalJobType = jobToEdit.getJobTypeName();
				final String originalJobExtends = jobToEdit.getJobExtends();
				final String currentJobName = jobNameField.getValueAsString();
				final String currentJobType = jobTypeField.getValue();
				final String currentJobExtends = jobExtendsField.getValue();

				/* Find out if the job attributes have changed */
            	final boolean nameChanged = !originalJobName.equals(currentJobName);
            	final boolean typeChanged = !originalJobType.equals(currentJobType);
            	final boolean extendsChanged = !originalJobExtends.equals(currentJobExtends);

            	/* If the name has changed, or the active job is a new job, check if the new name is the same as an existing name */
            	if (nameChanged || jobToEdit.getUID()==null) {
            		if (jobNamesAndTypesStore.query("jobname", currentJobName).length > 0) {
            			MessageBox.alert("A job with the same name already exists. Please choose another name.");
            			jobNameField.focus(true);
            			return;
            		}
            	}
            	
            	/* If the name or the "job extends" has changed, check if the job name is the same as "job extends" */
            	if (nameChanged || extendsChanged) {
            		if (currentJobName.equals(currentJobExtends)) {
            			MessageBox.alert("A job cannot extend itself! Please correct the error.");
            			jobNameField.focus(true);
            			return;
            		}
            	}
            	
            	
				AsyncCallback<String> callback = new AsyncCallback<String>() {
		            /**
		             * {@inheritDoc}
		             */
		            public void onSuccess(String result) {
		            	
		            	if ((jobToEdit.getUID()==null) && (nameChanged || typeChanged))
		            		MessageBox.alert("You have changed the job name and/or type. Other jobs that extend this job (if any) will now have outdated information (invalid references to this job). Make sure you fix this before trying to run such jobs.");
		            	
		            	hasMadeChanges = true;

		            	if (jobToEdit.getUID() == null) { /* New job */
		            		jobToEdit.setUID(result);
		            		
		            		/* Notify the registered listener that a new job has been created */
			            	if (changeListener != null) {
			            		changeListener.newJobCreated(jobToEdit);
			            	}
			            	
			            	setJobToProcess(jobToEdit);
		            	}
		            	else { /* Existing job */
			            	if (changeListener != null) {
				            	/* If the job name has changed, notify the registered listener */
				            	if (nameChanged)
			            			changeListener.jobNameChanged(jobToEdit, originalJobName, currentJobName);
		
				            	/* If the job type has changed, notify the registered listener */
				            	if (typeChanged) {
			            			changeListener.jobTypeChanged(jobToEdit, originalJobType, currentJobType);		            			
				            	}
			            	}
	
			            	if (nameChanged || typeChanged)
			            		setJobToProcess(jobToEdit);
		            	}
		            }

		            /**
		             * {@inheritDoc}
		             */
		            public void onFailure(Throwable caught) {
		            	Window.alert("Failed to save job." + caught);
		            }
		        };			
				
		        // construct list of initial assignments
		        List<AssignUIElement> assignList = new LinkedList<AssignUIElement>();
		        for (Record r : store.getRecords()) {
		        	String from  = r.getAsString("from");
		        	String to  = r.getAsString("to");
		        	String label = r.getAsString("label");
		        	if (to.length()>0 && from.length()>0) {
			        	AssignUIElement aue = new AssignUIElement();
			        	aue.setAssignTo(to);
			        	aue.setAssignFrom(from);
			        	//TODO
			        	if (from.trim().equalsIgnoreCase(USERINPUT_ASSIGNMENT)) {
			        		aue.setRequiresUserInput(true);
			        		aue.setUserInputLabel(label);
			        	}
			        	assignList.add(aue);
		        	}
		        }
		        
		        jobToEdit.setName(currentJobName);
		        jobToEdit.setJobTypeName(currentJobType);
		        jobToEdit.setJobExtends(currentJobExtends.equals("(none)") ? null : currentJobExtends);
		        jobToEdit.setInitAssignments(assignList);
				IRBootstrapperPortletG.bootstrapperService.saveJob(jobToEdit, callback);
			}			
		});
		
		/* Create the job attributes form and add it to the panel */
		Panel jobAttrsPanel = new Panel("Job Attributes");
		FieldSet fieldSet = new FieldSet();  
		fieldSet.setLabelWidth(90);  
		fieldSet.setTitle("");  
		fieldSet.setAutoHeight(true);  
		fieldSet.setBorder(false);
		fieldSet.add(jobNameField);
		fieldSet.add(jobTypeField);
		fieldSet.add(jobExtendsField);
		jobAttrsPanel.add(fieldSet);
		jobAttrsPanel.setWidth(1100);
		p.add(jobAttrsPanel);
		
		/* add the initialization assignments editor grid to the panel */
		p.add(grid);
		p.add(saveButton);
		p.setAutoScroll(true);
		p.setVisible(false);
		
		initWidget(p);
	}
	
	public void initialize(JobAutoCompleteData autoCompleteData) {
		this.autoCompleteData = autoCompleteData;
	}
	
	public void setJobToProcess(final JobUIElement job) {
		store.removeAll();
		
		if (job != null) {
			/* Copy the job attributes to the UI fields */
			jobNameField.setValue(job.getName());
			jobTypeField.setValue(job.getJobTypeName());

			if (job.getJobTypeName() != null) {
				jobNamesAndTypesStore.filterBy(new StoreTraversalCallback() {
					public boolean execute(Record record) {
						String jobType = record.getAsString("jobtype");
						String jobType2 = job.getJobTypeName();
						if (jobType.equals("*") || jobType.equals(jobType2))
							return true;
						return false;
					}
				});
			}
			jobExtendsField.setValue(job.getJobExtends()!=null ? job.getJobExtends() : "(none)");
			
			/* Copy the job initialization assignments to the UI fields */
			for (AssignUIElement assignment : job.getInitAssignments()) {
				//TODO
				Record record;
				if (assignment.requiresUserInput()) {
					record = recordDef.createRecord(new Object[] { assignment.getAssignTo(), assignment.getAssignFrom(), assignment.getUserInputLabel(), assignment.requiresUserInput() });
				}
				else
					record = recordDef.createRecord(new Object[] { assignment.getAssignTo(), assignment.getAssignFrom(), "", Boolean.FALSE });
				store.add(record);
			}	
			
			/* Populate the initial assignments editor combo with all the possible autocomplete values */
			populateCombosWithAutoCompleteData(job.getJobTypeName());
			
			saveButton.enable();
			this.getWidget().setVisible(true);
		}
		else {
			saveButton.disable();
			this.getWidget().setVisible(false);
		}
		
		jobToEdit = job;
	}

	/**
	 * Populates the two combo boxes in the initial assignments editor pane with all the possible
	 * autocomplete values for the given job type name.
	 * @param jobTypeName
	 */
	private void populateCombosWithAutoCompleteData(String jobTypeName) {
		assignToStore.removeAll();
		assignFromStore.removeAll();
		
		if (autoCompleteData!=null && jobTypeName!=null) {
			List<JobAutoCompleteEntryDesc> autoCompEntries = autoCompleteData.getAutoCompleteEntries();
			for (JobAutoCompleteEntryDesc entry : autoCompEntries) {					
				if (entry.getJobTypeName().equals(jobTypeName)) {
					String base = "%" + jobTypeName;
					Map<String, ObjectDesc> objectStore = autoCompleteData.getAutoCompleteObjectStore();

					LinkedList<Object[]> queue = new LinkedList<Object[]>();
					queue.add(new Object[] { objectStore.get(entry.getRootObjectDescID()), base});
					while (queue.size() > 0) {
						Object[] c = queue.remove(0);
						ObjectDesc currObj = (ObjectDesc) c[0];
						String currPath = (String) c[1];
						
						List<ObjectNameAndPtr> children = currObj.getChildren();
						if (children.size() > 0) {
							int i = 0;
							for (ObjectNameAndPtr onap : children) {
								String newPath = currPath + "." + onap.getName();
								assignToStore.add(assignToRecordDef.createRecord(new Object[] { newPath }));
								//TODO is this correct??? i have changed it to assignFromRecordDef
								assignFromStore.add(assignFromRecordDef.createRecord(new Object[] { newPath }));
								if (onap.getIDPtr() != null) {
									queue.add(i, new Object[] { objectStore.get(onap.getIDPtr()), newPath});
									i++;
								}
							}
						}
					}
					break;
				}
			}
		}	
		//TODO
		assignFromStore.add(assignFromRecordDef.createRecord(new Object[] { USERINPUT_ASSIGNMENT }));
	}
	
	public boolean hasMadeAnyChangesToJobConfiguration() {
		return hasMadeChanges;
	}
	
	public void registerJobAttributesChangeListener(JobAttributesChangeListener listener) {
		this.changeListener = listener;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobsInfoChangeListener#onJobsInfoLoaded(java.util.List, java.util.List)
	 */
	public void onJobsInfoLoaded(List<String> availableJobTypeNames, List<String[]> availableJobNames) {
		jobTypesStore.removeAll();
		jobNamesAndTypesStore.removeAll();

		for (String jobType : availableJobTypeNames) {
			jobTypesStore.add(jobTypesRecordDef.createRecord(new Object[] { jobType }));
		}
		for (String[] jobNameAndType : availableJobNames) {
			jobNamesAndTypesStore.add(jobNamesRecordDef.createRecord(new Object[] { jobNameAndType[0], jobNameAndType[1] }));
		}
		this.getWidget().setVisible(false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobsDesignerNavigatorListener#onJobSelected(org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement)
	 */
	public void onJobSelected(JobUIElement job) {
		this.setJobToProcess(job);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobsDesignerNavigatorListener#beforeJobSelected(org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement)
	 */
	public boolean beforeJobSelected(JobUIElement job) {
		if (jobToEdit==null || jobToEdit.getUID()!=null)
			return true;
			
		return com.google.gwt.user.client.Window.confirm("The job you are currently editing is new (has never been saved). If you switch to another job, the current job will be lost. Are you sure you want to continue?");
	}
}
