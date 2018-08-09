/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel.result;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.td.taskswidget.client.resources.Resources;
import org.gcube.portlets.user.td.taskswidget.client.util.GwtDataFormatter;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobModel.ColumnConfigTdJobModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdJobStatusType;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdOperationModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 25, 2013
 *
 */
public class FlexTableJob extends FlexTable{
	
	
	private List<ColumnConfigTdJobModel> columsConfigs;
	
	private HashMap<String, Integer> rowIndexJob = new HashMap<String, Integer>();

	/**
	 * 
	 */
	public FlexTableJob(TdJobModel.ColumnConfigTdJobModel... columns) {
		
		if(columns==null)
			return;
		
		columsConfigs = Arrays.asList(columns);
		initTable();
	}

	/**
	 * 
	 */
	private void initTable() {
		setCellPadding(10);
		setCellSpacing(10);
		setStyleName("job-table");
		
		for (int i = 0; i < columsConfigs.size(); i++)
			setWidget(0, i, new Label(columsConfigs.get(i).getLabel()));
	}

	/**
	 * @param jobModel
	 * @param jobsBar
	 */
	public void updateStatus(TdJobModel jobModel) {
		
		if(jobModel==null)
			return;
		
		System.out.println("Update Status for "+jobModel);
		Integer rowIndex = rowIndexJob.get(jobModel.getJobIdentifier());

		if(rowIndex==null){
			Integer index = new Integer(this.getRowCount());
			rowIndexJob.put(jobModel.getJobIdentifier(), index);
			update(index.intValue(), jobModel);
			
		}else{
			
			update(rowIndex.intValue(), jobModel);

		}
		
	}
	
	private void update(int rowIndex,TdJobModel jobModel){
		
		for (int i = 0; i < columsConfigs.size(); i++) {
//			setWidget(rowIndex, i, new Label(columsConfigs.get(i)+""));
			
			if(columsConfigs.get(i).equals(ColumnConfigTdJobModel.StatusIcon)){
				Image img = getProgressStatusView(jobModel);
				
				if(img!=null){
					
					if(jobModel.getStatus()!=null){
						//IF JOB STATUS IS RUNNING SHOW HUMAN READABLE STATUS IF THIS IS NOT EMPTY
						if(jobModel.getStatus().equals(TdJobStatusType.RUNNING)){
							String humanStatus = jobModel.getHumanReadableStatus();
							if(humanStatus!=null && !humanStatus.isEmpty()){
								img.setTitle(humanStatus); //set tooltip
							}
							else
								img.setTitle(jobModel.getStatus().toString()); //set tooltip
						}else
							img.setTitle(jobModel.getStatus().toString()); //set tooltip
					}
					
					setWidget(rowIndex, i, img);
				}
//				getCellFormatter().setWidth(rowIndex, i, "100px");
			}
			else if(columsConfigs.get(i).equals(ColumnConfigTdJobModel.Time)){
				Image time = setTime(jobModel.getStartTime(), jobModel.getEndTime());
				setWidget(rowIndex, i,time);
			}
			else if(columsConfigs.get(i).equals(ColumnConfigTdJobModel.Progress)){
				float percentage = jobModel.getProgressPercentage();
				percentage = percentage*100;
				setWidget(rowIndex, i, new Label(GwtDataFormatter.fmtToInt(percentage)+"%"));
			}else if(columsConfigs.get(i).equals(ColumnConfigTdJobModel.Type)){
				if(jobModel.getOpdModel()!=null){
					TdOperationModel type = jobModel.getOpdModel();
					
					if(type!=null){
						Label lb = new Label(type.getName().toString());
						String descr = jobModel.getOpdModel().getDescription()!=null?jobModel.getOpdModel().getDescription():type.toString();
						lb.setToolTip(descr);
						setWidget(rowIndex, i, lb);
					}
				}
			}else if(columsConfigs.get(i).equals(ColumnConfigTdJobModel.OperationInfo)){
				
					TdOperationModel type = jobModel.getOpdModel();
					String operationId = type!=null? type.getOperationId():"";
					setWidget(rowIndex, i, setDialogJobInfoDescription(operationId, jobModel));

			}else if(columsConfigs.get(i).equals(ColumnConfigTdJobModel.ValidationJobs)){
				
				final List<TdJobModel> validationJob = jobModel.getListValidationJobModel();
				
				if(validationJob==null)
					return;
				
				final Image validationImg = new Image(Resources.INSTANCE.validating());
				
				validationImg.addMouseOverHandler(new MouseOverHandler() {
					
					@Override
					public void onMouseOver(MouseOverEvent event) {
						validationImg.getElement().getStyle().setCursor(Cursor.POINTER); 
						
					}
				});
				
				validationImg.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent arg0) {
						final Dialog dialog = new Dialog();
//						dialog.setLayout(new FitLayout());
						dialog.setScrollMode(Scroll.AUTOY);
						
						dialog.setHeight(170);
						
//						dialog.setWidth(300);
//						FlexTableJob flex = new FlexTableJob(TdJobModel.ColumnConfigTdJobModel.Progress, TdJobModel.ColumnConfigTdJobModel.StatusIcon);
						
						FlexTableJob flex = new FlexTableJob(TdJobModel.ColumnConfigTdJobModel.OperationInfo, TdJobModel.ColumnConfigTdJobModel.Progress, TdJobModel.ColumnConfigTdJobModel.StatusIcon);
						dialog.setWidth(380);
						
						LayoutContainer lc = new LayoutContainer();
						lc.mask("Loading");
						for (TdJobModel tdJobModel : validationJob) {
							flex.updateStatus(tdJobModel);
							lc.unmask();
						}
						
						dialog.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

							@Override
							public void componentSelected(ButtonEvent ce) {
								dialog.hide();
							}

						});
						
						dialog.setHeading("Validation Jobs");
						lc.add(flex);
						dialog.add(lc);
						dialog.setHideOnButtonClick(true);
						dialog.show();
					}
				});
				
				setWidget(rowIndex, i, validationImg);
			
			}else
				setWidget(rowIndex, i, new Label(jobModel.get(columsConfigs.get(i).getId())+""));
			
		}
	}
	


	/**
	 * @param startTime
	 * @param endTime
	 */
	private Image setTime(final Date startTime, final Date endTime) {
		final Image time = new Image(Resources.INSTANCE.clock());
		time.setTitle("Show job time");
		
		time.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				time.getElement().getStyle().setCursor(Cursor.POINTER); 
				
			}
		});
		
		time.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			
				MessageBox.info("Job Time:", "Start Time: "+GwtDataFormatter.getDateFormat(startTime) +"<br/> End Time: "+GwtDataFormatter.getDateFormat(endTime), null);
			}
		});
		
		return time;
	}
	
	
	/**
	 * @param type 
	 * @param startTime
	 * @param endTime
	 */
	private Image setDialogJobInfoDescription(final String operationId, TdJobModel jobModel) {
		final Image decrImage = new Image(Resources.INSTANCE.info());
		decrImage.setTitle("Show Job Information");
		
		TdOperationModel jom = jobModel.getOpdModel();
		String descr = "<div style=\"padding:10px 10px 10px 10px\"><b style=\"font-size:12px\">Operation Description:</b><br/><br/>"+jobModel.getDescription()+"<br/><br/><br/>";
		
		if(jom!=null){
			descr+= "<div class=\"moz-rounded-corners\"><b style=\"font-size:12px\">About \""+jom.getName()+"\"</b><br/><br/>";
			descr+=jom.getDescription()+"</div>";
		}
		
		/*if(jom!=null){
			descr+= "<div class=\"moz-rounded-corners\"><b style=\"font-size:12px\">About this Operation</b><br/><br/>";
			descr+= "<b>Name: </b>"+jom.getName()+"<br/><br/>";
			descr+="<b>Description: </b>"+jom.getDescription()+"</div>";
		}*/
		
		descr+="</div>";
		
		final String decription = descr;
		decrImage.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				decrImage.getElement().getStyle().setCursor(Cursor.POINTER); 
				
			}
		});
		
		decrImage.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {

//				MessageBox.info("Job - Operation Id: "+operationId, decription, null);
				Dialog dialog = new Dialog();
				dialog.setStyleAttribute("background-color", "#FAFAFA");
				dialog.setSize(380, 180);
				dialog.setLayout(new FitLayout());
				dialog.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.info()));
				dialog.setModal(true);
				dialog.setHeading("Job Information - Operation Id: "+operationId);
				dialog.setButtons(Dialog.OK);
				dialog.setHideOnButtonClick(true);
				dialog.setButtonAlign(HorizontalAlignment.CENTER);
//				dialog.setSize(200, 150);
				
				ContentPanel cp = new ContentPanel();
				cp.setHeaderVisible(false);
				cp.setFrame(false);
				cp.setScrollMode(Scroll.AUTOY);
				cp.add(new Html(decription));
				dialog.add(cp);
				dialog.show();
			}
		});
		
		return decrImage;
	}
	
	public Image getProgressStatusView(TdJobModel jobModel){
		
		switch (jobModel.getStatus()) {
			case INITIALIZING:return new Image(Resources.INSTANCE.initializing());
//			case FALLBACK: return new Image(Resources.INSTANCE.attention());
			case COMPLETED: return new Image(Resources.INSTANCE.success());
			case RUNNING: return new Image(Resources.INSTANCE.working());
			case FAILED: return new Image(Resources.INSTANCE.failicon());
			case STATUS_UNKNOWN: return new Image(Resources.INSTANCE.unknown());
			case PENDING: return new Image(Resources.INSTANCE.pending());
			case VALIDATING: return new Image(Resources.INSTANCE.validating());
			default: return null;
		}
	}
	
	public void reset(){
		this.removeAllRows();
		initTable();
	}

}
