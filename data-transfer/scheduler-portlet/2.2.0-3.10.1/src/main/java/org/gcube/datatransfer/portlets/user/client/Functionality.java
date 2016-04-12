package org.gcube.datatransfer.portlets.user.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.datatransfer.portlets.user.client.Common.FolderToRetrieve;
import org.gcube.datatransfer.portlets.user.client.obj.AgentStat;
import org.gcube.datatransfer.portlets.user.client.obj.Uri;
import org.gcube.datatransfer.portlets.user.shared.obj.BaseDto;
import org.gcube.datatransfer.portlets.user.shared.obj.CallingManagementResult;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;
import org.gcube.datatransfer.portlets.user.shared.obj.ManuallyScheduled;
import org.gcube.datatransfer.portlets.user.shared.obj.PeriodicallyScheduled;
import org.gcube.datatransfer.portlets.user.shared.obj.SchedulerObj;
import org.gcube.datatransfer.portlets.user.shared.obj.TransferInfo;
import org.gcube.datatransfer.portlets.user.shared.obj.TypeOfSchedule;

import serp.bytecode.NewArrayInstruction;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.filters.DateFilter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class Functionality extends Common{

	public void initTimers(){
		// ... TIMERS ...
		resizeTimer = new Timer() {
			@Override
			public void run() {
				portlet.reDraw();
			}
		};
		focusTimer = new Timer() {
			@Override
			public void run() {
				foc.setFocus(true);
			}
		};

		getTransferTimer = new Timer() {
			@Override
			public void run() {
				rpCalls.getTransfers();
			//	Info.display("", "Refreshing transfers");
			}
		};
		// auto refresh every 30s
		getTransferRepeatingTimer = new Timer() {
			@Override
			public void run() {
				rpCalls.getTransfers();
		//		Info.display("", "Refreshing transfers");
			}
		};
	}
	
	public void setSomeDefaultUris(){
		Uri temp = new Uri();
		storeForUris = new ListStore<Uri>(uriProp.key());
		temp.setName("WikiPhoto1");
		temp.setURI("http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png");
		temp.setToBeTransferred(true);
		storeForUris.add(temp);
		temp = new Uri();
		temp.setName("WikiPhoto2");
		temp.setURI("http://upload.wikimedia.org/wikipedia/commons/0/0c/Fira_at_Santorini_%28from_north%29.jpg");
		temp.setToBeTransferred(true);
		storeForUris.add(temp);
		temp = new Uri();
		temp.setName("29MbFile.iso");
		temp.setURI("http://ftp.lip6.fr/pub/linux/distributions/scientific/6.0/x86_64/os/images/pxeboot/initrd.img");
		temp.setToBeTransferred(true);
		storeForUris.add(temp);
		temp = new Uri();
		temp.setName("BigFile.iso");
		temp.setURI("http://ftp.lip6.fr/pub/linux/distributions/scientific/6.0/x86_64/iso/SL-60-x86_64-2011-03-03-Everything-DVD2.iso");
		temp.setToBeTransferred(true);
		storeForUris.add(temp);

	}
	
	public void checkIfGoBack(TreeNode<BaseDto> node){
		String value = node.getModel().getName();
		destinationF.setValue(value);
		destinationAnchor.setBodyText(value);

		if (destCombo.isDataStorage()){
			lastSelectedDatastorageFolderName = node.getModel().getName();
			rpCalls.getDatastorageFolder(selectedDataStorageId, node
					.getModel().getName());
		} 
		//	else if (combo1.getCurrentValue().compareTo(SOURCETYPE.Workspace.toString()) == 0) {
		//		lastSelectedFolderId = node.getModel()
		//				.getIdInWorkspace();
		//		neededParent = true;
		//		rpCalls.getWorkspaceFolder(node.getModel().getIdInWorkspace(),
		//				true);
		//	} 
		else if (destCombo.isMongoDBStorage()) {
			lastSelectedMongoDBFolderDestName = node.getModel()
					.getName();
			rpCalls.getMongoDBFolderDest(node.getModel().getName());
		} else if (destCombo.isAgentDest()) {
			destinationF.setValue("."+destinationF.getCurrentValue());
			destinationAnchor.setBodyText("."+destinationF.getCurrentValue());

			lastSelectedAgentFolderDestName = node.getModel().getName();
			rpCalls.getAgentFolderDest(node.getModel().getName());
		}
	}

	public SafeHtml createHeader(int width) {
		String source = "";
		String destination = "";

		if(combo1.isTreeBased()){
			source = "TreeSource";
			sourceAnchor.setBodyText(source);
			toolbarSource.setToolTipConfig(sourceAnchor);
		}
		else if (folderSource != null){
			source = folderSource.getShortname();
			if(sourceAnchor!=null){
				sourceAnchor.setBodyText(folderSource.getName());
				toolbarSource.setToolTipConfig(sourceAnchor);
			}
		}
		if (folderDestination != null){			
			if(destinationAnchor!=null){
				destinationAnchor.setBodyText(folderDestination.getName());
				toolbarDestF.setToolTipConfig(destinationAnchor);
			}
			//Info.display("folderDestination", folderDestination.getName()+"");
		}
		if (destCombo.getCurrentValue() != null) {
			if (destCombo.isMongoDBStorage()) {
				destination = "MongoDB";
			} else if (destCombo.isDataStorage()) {
				destination = selectedDatastorageName;
			} else if (destCombo.isAgentDest()) {
				destination = agentHostname;
			}
			else if (destCombo.isTreeBased()){
				if(selectedDestCollection==null)destination="no collection selected";
				else destination = selectedDestCollection;
			}
		}
		else {
			destination = "Empty destination";
		}		
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("<table align=\"center\" height=\"2px\" width=\""
				+ width
				+ "\" "
				+ "style=\"font-size:4px;font-family:times;color:black;\"><tr>"
				+ "<td align=\"left\" height=\"2px\" width=\""
				+ width
				/ 2
				+ "\">"
				+ source
				+ "</td>"
				+ "<td align=\"left\" height=\"2px\" width=\""
				+ width
				/ 2
				+ "\">" + destination + "</td>" + "</tr></table>");

		return builder.toSafeHtml();
	}

	public void transparentSelectionOfAgent(FolderToRetrieve folderToRetrieve){
		if(this.agentIsSelectedFromStatsPanel==true){
			Info.display("Already selected agent",this.agentHostname);
			if(agentStats!=null && agentStatsTooltip!=null){
				agentStatsTooltip.setBodyText("Selected agent:"+agentHostname);
				agentStats.setToolTipConfig(agentStatsTooltip);
			}
			loadSpecificFolder(folderToRetrieve, this.agentHostname);	
			return;
		}		
		//selection based on statistics ... 
		lastDestComboValue = destCombo.getCurrentValue();
		rpCalls.getAgentStatistics(folderToRetrieve);			
	}

	public void calculateAndSelectBestAgent(FolderToRetrieve folderToRetrieve){
		if (listAgentStats==null || listAgentStats.size()==0){
			Info.display("WARNING", "List of available agents is null");
			return;
		}
		String agentName = listAgentStats.get(0).getEndpoint();
		int max = Integer.valueOf(listAgentStats.get(0).getSuccesful());
		boolean firstTime=true;
		for(AgentStat agentStat : listAgentStats){
			if(firstTime){firstTime=false;continue;}

			int possibleMax=Integer.valueOf(agentStat.getSuccesful());
			if( possibleMax> max){				
				agentName=agentStat.getEndpoint();
				max = possibleMax;
			}			
		}

		if (agentName==null){
			Info.display("WARNING", "agentName==null");
			return;
		}
		agentHostname = agentName;
		
		if(stringOfAgents!=null){
			String[] agentsArray = stringOfAgents.split("\n");
			for (String tmp : agentsArray) {
				// tmp contains: id--name--hostName--port
				String[] partsOfInfo = tmp.split("--");
				if (partsOfInfo[2].compareTo(agentName) == 0) {
					selectedAgentSourcePort = partsOfInfo[3];
				}
			}
			selectedAgentSource = agentName;
		}
				
		Info.display("Selected agent(based on statistics)",this.agentHostname);

		//setting tool tip
		if(agentStats!=null){
			if(agentStatsTooltip==null)agentStatsTooltip=createAnchor("");
			agentStatsTooltip.setBodyText("Selected agent:"+agentHostname);
			agentStats.setToolTipConfig(agentStatsTooltip);
		}

		comboAgent.setValue(agentName);

		if(folderToRetrieve!=null){
			loadSpecificFolder(folderToRetrieve,agentName);			
		}
	}

	public void loadSpecificFolder(FolderToRetrieve folderToRetrieve, String agentName){
		if(folderToRetrieve.equals(FolderToRetrieve.NONE)){
			portlet.redrawEast();
		}
		else if(folderToRetrieve.equals(FolderToRetrieve.MongoDBStorageFolder)){
			rpCalls.getMongoDBFolderDest("/");
		}
		else if(folderToRetrieve.equals(FolderToRetrieve.AgentDestFolder)){
			String[] agentsArray = stringOfAgents.split("\n");
			for (String tmp : agentsArray) {
				// tmp contains: id--name--hostName--port
				String[] partsOfInfo = tmp.split("--");
				if (partsOfInfo[2].compareTo(agentName) == 0) {
					selectedAgentDestinationPort = partsOfInfo[3];
				}
			}
			selectedAgentDestination = agentName;					
			rpCalls.getAgentFolderDest("");
		}		
		else if(folderToRetrieve.equals(FolderToRetrieve.DataStorageFolder)){
			rpCalls.getDatastorageFolder(selectedDataStorageId, "./");
		}	
	}

	/*
	 * designTransferGrid input: Nothing -- returns: Nothing It creates a grid
	 * for the transfers and sets the south panel to this grid
	 */
	public void designTransferGrid() {
		if (callingManagementResultJson != null) {
			callingManagementResult = (CallingManagementResult) CallingManagementResult
					.createSerializer()
					.deSerialize(callingManagementResultJson,
							"org.gcube.datatransfer.portlets.user.shared.obj.CallingManagementResult");
		}

		ColumnConfig<TransferInfo, String> transferId = new ColumnConfig<TransferInfo, String>(
				transferInfoProp.transferId(), 200, "TransferId");
		ColumnConfig<TransferInfo, String> submitter = new ColumnConfig<TransferInfo, String>(
				transferInfoProp.submitter(), 100, "Submitter");
		ColumnConfig<TransferInfo, String> status = new ColumnConfig<TransferInfo, String>(
				transferInfoProp.status(), 100, "Status");
		
		ColumnConfig<TransferInfo, Integer> numOfUpdates = new ColumnConfig<TransferInfo, Integer>(
				transferInfoProp.numOfUpdates(), 50, " # ");

		status.setCell(new AbstractCell<String>() {
			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				String style;
				if (value.compareTo("COMPLETED") == 0)
					style = "style='color: green'";
				else if (value.compareTo("CANCELED") == 0)						
					style = "style='color: #990000'";
				else if (value.compareTo("FAILED") == 0)
					style = "style='color: red'";
				else if (value.compareTo("ONGOING") == 0)
					style = "style='color: navy'";
				else
					style = "style='color: blue'";
				sb.appendHtmlConstant("<span " + style
						+ " qtitle='Change' qtip='" + value + "'>" + value
						+ "</span>");
			}
		});

		ColumnConfig<TransferInfo, String> type = new ColumnConfig<TransferInfo, String>(
				transferInfoProp.typeOfScheduleString(), 250, "Type");
		type.setCell(new AbstractCell<String>() {
			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				// String style="style='color: blue'";

				String newValue = null;
				if (value.startsWith("Periodically")) {
					String test = value;
					String[] tokens = test.split("-");
					newValue = tokens[0] + "<br/>" + tokens[1] + "-"
							+ tokens[2];
				} else
					newValue = value;
				sb.appendHtmlConstant("<span qtitle='Change' qtip='type'>"
						+ newValue + "</span>");
			}
		});

		/*
		 * ColumnConfig<TransferInfo, String> submittedDate = new
		 * ColumnConfig<TransferInfo, String>(transferInfoProp.submittedDate(),
		 * 150, "Submitted Date"); submittedDate.setCell(new
		 * AbstractCell<String>() {
		 * 
		 * @Override public void render(Context context, String value,
		 * SafeHtmlBuilder sb) { //String style="style='color: blue'";
		 * sb.appendHtmlConstant
		 * ("<span qtitle='submittedDate' qtip='submittedDate'>" + value +
		 * "</span>"); } });
		 */

		ColumnConfig<TransferInfo, Date> submittedDate2 = new ColumnConfig<TransferInfo, Date>(
				transferInfoProp.submittedDate2(), 150, "Submitted Date");
		submittedDate2.setCell(new DateCell(
				com.google.gwt.i18n.client.DateTimeFormat
				.getFormat("dd.MM.yy-HH.mm.ss")));

		//progress column
		ColumnConfig<TransferInfo, Double> progressColumn = new ColumnConfig<TransferInfo, Double>(
				transferInfoProp.progress(), 150, "Progress");

		ProgressBarCell progress = new ProgressBarCell(){
			@Override
			public boolean handlesSelection(){
				return true;
			}
		};
		progress.setProgressText("{0}% Complete");
		progress.setWidth(140);
		progressColumn.setCell(progress);

		List<ColumnConfig<TransferInfo, ?>> l = new ArrayList<ColumnConfig<TransferInfo, ?>>();
		l.add(transferId);
		l.add(submitter);
		l.add(status);
		l.add(numOfUpdates);
		l.add(type);
		// l.add(submittedDate);
		l.add(submittedDate2);
		l.add(progressColumn);

		ColumnModel<TransferInfo> cm = new ColumnModel<TransferInfo>(l);

		store = new ListStore<TransferInfo>(transferInfoProp.key());
		StoreSortInfo<TransferInfo> sortInfo = new StoreSortInfo<TransferInfo>(
				transferInfoProp.submittedDate2(), SortDir.DESC);
		store.addSortInfo(sortInfo);

		south.clear();
		Date current = new Date();
		south.setHeadingText("Schedule Details at "
				+ DateTimeFormat.getFormat("HH:mm:ss  EEE, d MMM yyyy").format(
						current) + "  --  Submitter: '"
						+ ResourceName.getCurrentValue() + "'");

		VerticalLayoutContainer vert = new VerticalLayoutContainer();
		vert.setWidth(totalWidth - 30);
		vert.setHeight(panelGeneral.getOffsetHeight() - 328);
		south.setWidth(totalWidth - 25);
		south.setHeight(panelGeneral.getOffsetHeight() - 325);

		grid = new Grid<TransferInfo>(store, cm);
		grid.getView().setAutoExpandColumn(transferId);
		grid.setBorders(false);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setHeight(panelGeneral.getOffsetHeight() - 358);
		grid.setWidth(totalWidth - 30);
		grid.getView().setAutoFill(true);

		// Add a selection model so we can select cells.
		final GridSelectionModel<TransferInfo> selectionModel = new GridSelectionModel<TransferInfo>();
		grid.setSelectionModel(selectionModel);
		grid.addRowDoubleClickHandler(new RowDoubleClickHandler() {
			public void onRowDoubleClick(RowDoubleClickEvent event) {
				dialogBoxGen = createDialogBox(popups.asPopUpOperateTransfer());
				dialogBoxGen.center();
				focusTimer.schedule(200);
			}
		});

		StringFilter<TransferInfo> transferIdFilter = new StringFilter<TransferInfo>(
				transferInfoProp.transferId());
		StringFilter<TransferInfo> submitterFilter = new StringFilter<TransferInfo>(
				transferInfoProp.submitter());
		StringFilter<TransferInfo> statusFilter = new StringFilter<TransferInfo>(
				transferInfoProp.status());

		DateFilter<TransferInfo> submittedDate2Filter = new DateFilter<TransferInfo>(
				transferInfoProp.submittedDate2());

		GridFilters<TransferInfo> filters = new GridFilters<TransferInfo>();
		filters.initPlugin(grid);
		filters.setLocal(true);
		filters.addFilter(transferIdFilter);
		filters.addFilter(submitterFilter);
		filters.addFilter(statusFilter);
		filters.addFilter(submittedDate2Filter);

		vert.add(grid);
		south.add(vert);
		// button1
		if(agentStats==null)agentStats= new ToolButton("view agent statistics");
		if(agentStatsTooltip==null)agentStatsTooltip=createAnchor("...");
		agentStats.setToolTipConfig(agentStatsTooltip);

		agentStats.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				//show statistics
				dialogBoxGen = createDialogBox(popups.asPopUpAgentStats());
				dialogBoxGen.center();
				focusTimer.schedule(200);
			}
		});
		agentStats.setWidth("200px");
		agentStats.getElement().setInnerText("view agent statistics");
		agentStats.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		agentStats.getElement().getStyle().setBorderWidth(1, Unit.PX);
		agentStats.getElement().getStyle().setBorderColor("black");

		// button2
		ToolButton refresh = new ToolButton("refresh");
		refresh.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				rpCalls.getTransfers();
			}
		});
		refresh.setWidth("60px");
		refresh.getElement().setInnerText("refresh");
		refresh.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		refresh.getElement().getStyle().setBorderWidth(1, Unit.PX);
		refresh.getElement().getStyle().setBorderColor("black");

		// adding buttons
		if (south.getHeader().getToolCount() < 1) {
			south.getHeader().addTool(refresh);
			south.getHeader().addTool(agentStats);
		}

		if (this.callingManagementResult != null) {
			List<TransferInfo> transfers = this.callingManagementResult
					.getAllTheTransfersInDB();
			for (TransferInfo tmp : transfers) {
				// we need to add the date field because so far the date it's
				// only in a string format
				String stringDate = tmp.getSubmittedDate();
				if (stringDate == null || stringDate.compareTo("")==0) continue;
				
				String format = "dd.MM.yy-HH.mm.ss";
				Date date = null;
				String[] parts=stringDate.split("\\.");
				if(parts.length<5)stringDate=stringDate+".00";
				
				date = DateTimeFormat.getFormat(format).parse(stringDate);
				tmp.setSubmittedDate2(date);
				// we store the transfer info
				store.add(tmp);
			}
		}
	}
	public void replaceSubmitterInHeader(){
		Date current = new Date();
		south.setHeadingText("Schedule Details at "
				+ DateTimeFormat.getFormat("HH:mm:ss  EEE, d MMM yyyy").format(
						current) + "  --  Submitter: '"
						+ ResourceName.getCurrentValue() + "'");		
	}
	
	public void loadTheTransfers(){
		if (callingManagementResultJson != null) {
			callingManagementResult = (CallingManagementResult) CallingManagementResult
					.createSerializer()
					.deSerialize(callingManagementResultJson,
							"org.gcube.datatransfer.portlets.user.shared.obj.CallingManagementResult");
		}
		
		if (this.callingManagementResult != null) {
			List<TransferInfo> transfers = this.callingManagementResult
					.getAllTheTransfersInDB();
			for (TransferInfo tmp : transfers) {
				// we need to add the date field because so far the date it's
				// only in a string format
				String stringDate = tmp.getSubmittedDate();
				if (stringDate == null || stringDate.compareTo("")==0) continue;
				String format = "dd.MM.yy-HH.mm.ss";
				Date date = null;
				String[] parts=stringDate.split("\\.");
				if(parts.length<5)stringDate=stringDate+".00";
				
				date = DateTimeFormat.getFormat(format).parse(stringDate);
				tmp.setSubmittedDate2(date);				
			}
			// we store the transfer info
			replaceSubmitterInHeader();
			store.replaceAll(transfers);
			store.commitChanges();
		}
	}

	/*
	 * checkFields input: Nothing -- returns: Boolean Depends on the command
	 * that the user has selected it checks the appropriate fields. It returns
	 * true when user forgot to fill/select sth. In other case returns false
	 */
	public boolean checkFields() {
		// check for the command combo box
		if (commands == null) {
			printMsgInDialogBox("You should choose a Command !");
			return true;
		}
		if (commands.getCurrentValue() == null) {
			printMsgInDialogBox("You should choose a Command !");
			return true;
		}

		// check for resource name and scope
		if (ResourceName.getCurrentValue() == null) {
			printMsgInDialogBox("You should have a Resource Name !");
			return true;
		}
		if (scope.getCurrentValue() == null) {
			printMsgInDialogBox("You should have a Scope !");
			return true;
		}

		if (commands.getCurrentValue().compareTo("monitor") == 0
				|| commands.getCurrentValue().compareTo("get outcomes") == 0
				|| commands.getCurrentValue().compareTo("cancel") == 0) {
			if (transferId.getCurrentValue() == null) {
				printMsgInDialogBox("You should have a transfer id !");
				return true;
			}
			return false;
		}

		if (commands.getCurrentValue().compareTo("schedule") != 0)
			return false;

		// check if we have a source type and if it's URI or Workspace we check
		// if we have agent ..
		if (this.agentHostname == null) {
			printMsgInDialogBox("You should have an agent !");
			return true;
		}

		// in case of workspace items check if we have the credentials
		if (combo1.isWorkspace()) {
			if (this.passWorkspace == null) {
				printMsgInDialogBox("You should fill the password for the workspace !");
				return true;
			} else if (this.passWorkspace.getCurrentValue() == null) {
				printMsgInDialogBox("You should fill the password for the workspace !");
				return true;
			}
		}
		// check if we have a destination
		if (this.destCombo == null) {
			printMsgInDialogBox("You should have a destination !");
			return true;
		} else if (this.destCombo.getCurrentValue() == null) {
			printMsgInDialogBox("You should have a destination !");
			return true;
		}

		// check if we have a destination folderSource
		/*
		 * if(this.destinationF==null){printMsgInDialogBox(
		 * "You should have a destination folderSource !");return true;} else
		 * if(this.destinationF.getCurrentValue()==null){printMsgInDialogBox(
		 * "You should have a destination folderSource !");return true;}
		 */
		if (this.destCombo.isMongoDBStorage()) {
			// check if we have service name
			if (this.smServiceName == null) {
				printMsgInDialogBox("You should have a Service Name !");
				return true;
			} else if (this.smServiceName.getCurrentValue() == null) {
				printMsgInDialogBox("You should have a Service Name !");
				return true;
			}
			// service class
			if (this.smServiceClass == null) {
				printMsgInDialogBox("You should have a Service Class !");
				return true;
			} else if (this.smServiceClass.getCurrentValue() == null) {
				printMsgInDialogBox("You should have a Service Class !");
				return true;
			}
			// owner
			if (this.smOwner == null) {
				printMsgInDialogBox("You should have an Owner !");
				return true;
			} else if (this.smOwner.getCurrentValue() == null) {
				printMsgInDialogBox("You should have an Owner !");
				return true;
			}
			// access type
			if (this.smAccessType == null) {
				printMsgInDialogBox("You should have an Access Type !");
				return true;
			} else if (this.smAccessType.getCurrentValue() == null) {
				printMsgInDialogBox("You should have an Access Type !");
				return true;
			}
		} else if (this.destCombo.isDataStorage()) {
			if (this.selectedDataStorageId == null) {
				printMsgInDialogBox("You should have a Remote Node !");
				return true;
			}
		}

		// check for the typeOfSchedule combo box
		if (typeOfSchedule == null) {
			printMsgInDialogBox("You should have a type of Schedule !");
			return true;
		}
		if (typeOfSchedule.getCurrentValue() == null) {
			printMsgInDialogBox("You should have a type of Schedule !");
			return true;
		}

		// then our check depends on which type of schedule we have
		if (typeOfSchedule.getCurrentValue().compareTo("manually scheduled") == 0) {
			if (date.getCurrentValue() == null) {
				printMsgInDialogBox("You should have a Schedule Date !");
				return true;
			}
			if (time.getCurrentValue() == null) {
				printMsgInDialogBox("You should have a Schedule Time !");
				return true;
			}
		} else if (typeOfSchedule.getCurrentValue().compareTo(
				"periodically scheduled") == 0) {
			if (date.getCurrentValue() == null) {
				printMsgInDialogBox("You should have a Schedule Start Date !");
				return true;
			}
			if (time.getCurrentValue() == null) {
				printMsgInDialogBox("You should have a Schedule -stStart Time !");
				return true;
			}
			if (frequency.getCurrentValue() == null) {
				printMsgInDialogBox("You should have a frequency !");
				return true;
			}
		}
		return false;
	}

	public boolean checkMongoDBFields() {
		// check if we have service name
		if (this.smServiceName == null) {
			Info.display("Warning", "You should have a Service Name !");
			return true;
		} else if (this.smServiceName.getCurrentValue() == null) {
			Info.display("Warning", "You should have a Service Name !");
			return true;
		}
		// service class
		if (this.smServiceClass == null) {
			Info.display("Warning", "You should have a Service Class !");
			return true;
		} else if (this.smServiceClass.getCurrentValue() == null) {
			Info.display("Warning", "You should have a Service Class !");
			return true;
		}
		// owner
		if (this.smOwner == null) {
			Info.display("Warning", "You should have an Owner !");
			return true;
		} else if (this.smOwner.getCurrentValue() == null) {
			Info.display("Warning", "You should have an Owner !");
			return true;
		}
		// access type
		if (this.smAccessType == null) {
			Info.display("Warning", "You should have an Access Type !");
			return true;
		} else if (this.smAccessType.getCurrentValue() == null) {
			Info.display("Warning", "You should have an Access Type !");
			return true;
		}

		return false;
	}

	public boolean checkMongoDBSourceFields() {
		// check if we have service name
		if (this.smServiceNameSource == null) {
			Info.display("Warning", "You should have a Service Name !");
			return true;
		} else if (this.smServiceNameSource.getCurrentValue() == null) {
			Info.display("Warning", "You should have a Service Name !");
			return true;
		}
		// service class
		if (this.smServiceClassSource == null) {
			Info.display("Warning", "You should have a Service Class !");
			return true;
		} else if (this.smServiceClassSource.getCurrentValue() == null) {
			Info.display("Warning", "You should have a Service Class !");
			return true;
		}
		// owner
		if (this.smOwnerSource == null) {
			Info.display("Warning", "You should have an Owner !");
			return true;
		} else if (this.smOwnerSource.getCurrentValue() == null) {
			Info.display("Warning", "You should have an Owner !");
			return true;
		}
		// access type
		if (this.smAccessTypeSource == null) {
			Info.display("Warning", "You should have an Access Type !");
			return true;
		} else if (this.smAccessTypeSource.getCurrentValue() == null) {
			Info.display("Warning", "You should have an Access Type !");
			return true;
		}

		return false;
	}

	/*
	 * schedule input: SchedulerObj -- returns: SchedulerObj It fills the
	 * scheduler objects with all the appropriate info depends of the type of
	 * transfer.
	 */
	public SchedulerObj fillSchedulerObj(SchedulerObj scheduleObj) {
		scheduleObj.setSubmitter(this.ResourceName.getCurrentValue());
		scheduleObj.setScope(this.scope.getCurrentValue());
		scheduleObj.setAgentHostname(agentHostname);
		scheduleObj.setUnzipFile(unzip.getValue());
		scheduleObj.setOverwrite(overwrite.getValue());
		scheduleObj.setSyncOp(false);
		//Info.display("unzip.getValue()", unzip.getValue().toString());
		if (this.destinationF == null)
			scheduleObj.setDestinationFolder("/");
		else if (this.destinationF.getCurrentValue() == null)
			scheduleObj.setDestinationFolder("/");
		else
			scheduleObj.setDestinationFolder(destinationF.getCurrentValue());

		DateTimeFormat formatter = DateTimeFormat.getFormat("dd.MM.yy-HH.mm.ss");
		scheduleObj.setSubmittedDate(formatter.format(new Date()));

		if (combo1.isWorkspace()) {
			inputUrls = new ArrayList<String>();
			for (BaseDto tmp : this.toBeTransferredStore.getAll()) {
				if (tmp.getName().compareTo("") == 0)
					continue;
				if (tmp.getChildren() != null) {
					if (tmp.getChildren().size() == 1
							&& tmp.getChildren().get(0).getData().getName()
							.compareTo("") == 0)
						continue;
				}
				// inputUrls.add(tmp.getLink());
				inputUrls.add(tmp.getName());
			}
			scheduleObj.setInputUrls(inputUrls);
			scheduleObj.setTypeOfTransfer("FileBasedTransfer");
			scheduleObj.setPass(this.passWorkspace.getCurrentValue());
			scheduleObj.setSourceType("Workspace");
		} else if (combo1.isDatasource()) {
			if (selectedDataSourceId == null) {
				this.printMsgInDialogBox("You have not selected DataSource !");
				return null;
			}
			scheduleObj.setDataSourceId(selectedDataSourceId);
			inputUrls = new ArrayList<String>();
			for (BaseDto tmp : this.toBeTransferredStore.getAll()) {
				if (tmp.getName().compareTo("") == 0)
					continue;
				if (tmp.getChildren() != null) {
					if (tmp.getChildren().size() == 1
							&& tmp.getChildren().get(0).getData().getName()
							.compareTo("") == 0)
						continue;
				}
				inputUrls.add(tmp.getName());
			}
			scheduleObj.setInputUrls(inputUrls);
			scheduleObj.setTypeOfTransfer("FileBasedTransfer");
			scheduleObj.setSourceType("DataSource");
		} else if (combo1.isURI()) {
			inputUrls = new ArrayList<String>();
			for (BaseDto tmp : this.toBeTransferredStore.getAll()) {
				if (tmp.getName().compareTo("") == 0)
					continue;
				if (tmp.getChildren() != null) {
					if (tmp.getChildren().size() == 1
							&& tmp.getChildren().get(0).getData().getName()
							.compareTo("") == 0)
						continue;
				}
				inputUrls.add(tmp.getName());
			}
			scheduleObj.setInputUrls(inputUrls);
			scheduleObj.setTypeOfTransfer("FileBasedTransfer");
			scheduleObj.setSourceType("URI");
		} else if (combo1.isMongoDB()) {
			inputUrls = new ArrayList<String>();
			for (BaseDto tmp : this.toBeTransferredStore.getAll()) {
				if (tmp.getName().compareTo("") == 0)
					continue;
				if (tmp.getChildren() != null) {
					if (tmp.getChildren().size() == 1
							&& tmp.getChildren().get(0).getData().getName()
							.compareTo("") == 0)
						continue;
				}
				String str = tmp.getLink();
				if (!str.startsWith("smp://")) {
					str = str.replaceFirst("smp:/", "smp://");
				}
				inputUrls.add(str);

			}
			scheduleObj.setInputUrls(inputUrls);
			scheduleObj.setTypeOfTransfer("FileBasedTransfer");
			scheduleObj.setSourceType("MongoDB");
		} else if (combo1.isAgentSource()) {
			inputUrls = new ArrayList<String>();
			for (BaseDto tmp : this.toBeTransferredStore.getAll()) {
				if (tmp.getName().compareTo("") == 0)
					continue;
				if (tmp.getChildren() != null) {
					if (tmp.getChildren().size() == 1
							&& tmp.getChildren().get(0).getData().getName()
							.compareTo("") == 0)
						continue;
				}
				String str = tmp.getLink();
				inputUrls.add(str);
			}
			scheduleObj.setInputUrls(inputUrls);
			scheduleObj.setTypeOfTransfer("FileBasedTransfer");
			scheduleObj.setSourceType("AgentSource");
		}
		 else if (combo1.isTreeBased()) {
				inputUrls = new ArrayList<String>();
				for (BaseDto tmp : this.toBeTransferredStore.getAll()) {
					if (tmp.getName().compareTo("") == 0)
						continue;
					if (tmp.getChildren() != null) {
						if (tmp.getChildren().size() == 1
								&& tmp.getChildren().get(0).getData().getName()
								.compareTo("") == 0)
							continue;
					}
					String idAndCardinality = tmp.getLink();
					String[] parts=idAndCardinality.split("--");
					if(parts==null || parts.length<2){
						Info.display("not valid source ","dest tree source has no or less than expected info");
						return null;
					}
					scheduleObj.setDataSourceId(parts[0]);//keep only one source id
					break;
				}
				//scheduleObj.setInputUrls(inputUrls);
				scheduleObj.setTypeOfTransfer("TreeBasedTransfer");
				scheduleObj.setSourceType("");
		}


		// storage type
		if (destCombo.isAgentDest()) { 
			// for the agent's node
			scheduleObj.setStorageType("LocalGHN");
		} else if (destCombo.isMongoDBStorage()) { 
			// for mongoDB and storage Manager
			scheduleObj.setStorageType("StorageManager");
			scheduleObj.setServiceClass(smServiceClass.getCurrentValue());
			scheduleObj.setServiceName(smServiceName.getCurrentValue());
			scheduleObj.setAccessType(smAccessType.getCurrentValue());
			scheduleObj.setOwner(smOwner.getCurrentValue());
		} else if (destCombo.isDataStorage()) { 
			// for datastorages
			if (selectedDataStorageId == null) {
				this.printMsgInDialogBox("You have not selected DataStorage !");
				return null;
			}
			scheduleObj.setStorageType("DataStorage");
			scheduleObj.setDataStorageId(selectedDataStorageId);
		}
		else if (destCombo.isTreeBased()) { 
			// for trees
			if (selectedDestCollection == null) {
				this.printMsgInDialogBox("You have not selected Dest.Collection !");
				return null;
			}
			scheduleObj.setStorageType("");
			scheduleObj.setDataStorageId(selectedDestCollection);
		}

		TypeOfSchedule typeOfScheduleObj = new TypeOfSchedule();
		if (this.typeOfSchedule.getCurrentValue().compareTo("direct") == 0) {
			typeOfScheduleObj.setDirectedScheduled(true);
		} else if (this.typeOfSchedule.getCurrentValue().compareTo(
				"manually scheduled") == 0) {
			ManuallyScheduled manuallyScheduled = new ManuallyScheduled();
			// DateTimeFormat formatter =
			// DateTimeFormat.getFormat("dd.MM.yy-HH.mm");
			DateTimeFormat tmpFormatter1 = DateTimeFormat.getFormat("dd.MM.yy");
			String day = tmpFormatter1.format(this.date.getCurrentValue());
			DateTimeFormat tmpFormatter2 = DateTimeFormat.getFormat("HH.mm");
			String time = tmpFormatter2.format(this.time.getCurrentValue());
			// the string below represent the date in a format: "dd.MM.yy-HH.mm"
			String instanceString = day.concat("-").concat(time);

			manuallyScheduled.setInstanceString(instanceString);
			typeOfScheduleObj.setManuallyScheduled(manuallyScheduled);
		} else if (this.typeOfSchedule.getCurrentValue().compareTo(
				"periodically scheduled") == 0) {
			PeriodicallyScheduled periodicallyScheduled = new PeriodicallyScheduled();
			periodicallyScheduled
			.setFrequency(this.frequency.getCurrentValue());

			// DateTimeFormat formatter =
			// DateTimeFormat.getFormat("dd.MM.yy-HH.mm");
			DateTimeFormat tmpFormatter1 = DateTimeFormat.getFormat("dd.MM.yy");
			String day = tmpFormatter1.format(this.date.getCurrentValue());
			DateTimeFormat tmpFormatter2 = DateTimeFormat.getFormat("HH.mm");
			String time = tmpFormatter2.format(this.time.getCurrentValue());
			// the string below represent the date in a format: "dd.MM.yy-HH.mm"
			String startInstanceString = day.concat("-").concat(time);

			periodicallyScheduled.setStartInstanceString(startInstanceString);
			typeOfScheduleObj.setPeriodicallyScheduled(periodicallyScheduled);
		}
		scheduleObj.setTypeOfSchedule(typeOfScheduleObj);

		return scheduleObj;
	}


	/*
	 * addTheGoBackOption input: Nothing -- returns: FolderDto It adds the 'go
	 * back' folderSource in the source tree so that the user can browse back.
	 */
	public FolderDto addTheGoBackOption(boolean isForSource) {
		FolderDto empty = makeFolder("");
		String parentName = "";
		String folderName=null;
		
		
		if(isForSource)	{
			if(combo1.isTreeBased())return folderSource;
			folderName = folderSource.getName().replaceFirst(".//", "./");
			parentName = getParentName(folderName,true);
		}
		else{
			if(destCombo.isTreeBased())return folderDestination;
			folderName = folderDestination.getName().replaceFirst(".//", "./");
			parentName = getParentName(folderName,false);
		}

		// this.printMsgInDialogBox("folderName="+folderName+"\n"+"parentName="+parentName);
		// it means in any case of folderSource there is no upper level
		if (parentName == null){
			if(isForSource) return folderSource;
			else return folderDestination;
		}

		FolderDto rootFolder = makeFolder(folderName); // parent
		FolderDto upFolder = makeFolder(parentName);
		upFolder.addChild(empty);
		upFolder.setShortname("<< Back");

		if (isForSource && combo1.isWorkspace()) {
			upFolder.setIdInWorkspace(folderSource.getParentIdInWorkspace());
		}
		//else if ((!isForSource) && destCombo.getCurrentValue().compareTo("Workspace") == 0) {
		//	upFolder.setIdInWorkspace(folderDestination.getParentIdInWorkspace());
		//}
		List<FolderDto> children = new ArrayList<FolderDto>();
		children.add(upFolder); // child-go back
		if (isForSource){
			for (FolderDto tmp : folderSource.getChildren())children.add(tmp); // rest of children
		}
		else {
			for (FolderDto tmp : folderDestination.getChildren())children.add(tmp); // rest of children
		}
		rootFolder.setChildren(children);
					return rootFolder;
	}

	/*
	 * getParentName input: String with the name -- returns: String It returns
	 * the parent name of this folderSource(name as an input). If it is about
	 * workspace and the folderSource has a parent it returns just an empty string ''
	 */
	public String getParentName(String name, boolean isForSource) {
		if(isForSource){
			if (combo1.isDatasource()) {
				if (selectedDatasourcePath == null
						|| name.compareTo(selectedDatasourcePath) == 0)
					return null;
			} else if (combo1.isWorkspace()) {
				if (idWorkspaceRoot != null)
					if (idWorkspaceRoot.compareTo(folderSource.getIdInWorkspace()) != 0)
						return "";
					else
						return null;
			} else if (combo1.isMongoDB()) {
				if (selectedMongoDBSourcePath == null
						|| name.compareTo(selectedMongoDBSourcePath) == 0)
					return null;
			} else if (combo1.isAgentSource()) {
				if (selectedAgentSourcePath == null
						|| name.compareTo(selectedAgentSourcePath) == 0)
					return null;
			}
		}
		else{
			if (destCombo.isDataStorage()) {
				if (selectedDatastoragePath == null
						|| name.compareTo(selectedDatastoragePath) == 0)
					return null;
			} 
			//	else if (destCombo.getCurrentValue().compareTo("Workspace") == 0) {
			//  	if (idWorkspaceRootDest != null)
			//			if (idWorkspaceRootDest.compareTo(folderDestination.getIdInWorkspace()) != 0)
			//				return "";
			//			else
			//				return null;
			//	} 
			else if (destCombo.isMongoDBStorage()) {
				if (selectedMongoDBDestinationPath == null
						|| name.compareTo(selectedMongoDBDestinationPath) == 0)
					return null;
			} else if (destCombo.isAgentDest()) {
				if (selectedAgentDestinationPath == null
						|| name.compareTo(selectedAgentDestinationPath) == 0)
					return null;
			}
		}
		// the following is only for the data source
		String folderName = name;
		String upFolder = "";
		String[] partsOfName = folderName.split("/");

		for (int i = 0; i < partsOfName.length - 1; i++) {
			upFolder = upFolder + partsOfName[i] + "/";
		}
		return upFolder;
	}

	/*
	 * makeFolder input: String with the name -- returns: FolderDto It creates a
	 * FolderDto
	 */
	public FolderDto makeFolder(String name) {
		FolderDto theReturn = new FolderDto(++autoId, name);
		theReturn.setChildren((List<FolderDto>) new ArrayList<FolderDto>());
		return theReturn;
	}

	/*
	 * printFolder input: FolderDto object and the indent -- returns: String
	 * Prints a FolderDto folderSource
	 */
	void printFolder(FolderDto folderSource, int indent) {
		if (indent == 0)
			printFolderString = "";
		for (int i = 0; i < indent; i++)
			printFolderString = printFolderString.concat("\t");
		printFolderString = printFolderString.concat("folderSource: name="
				+ folderSource.getName() + " - id=" + folderSource.getId() + "\n");

		List<FolderDto> tmpListOfChildren = folderSource.getChildren();
		if (tmpListOfChildren != null) {
			for (FolderDto tmp : tmpListOfChildren) { // first the files
				if (tmp.getChildren().size() <= 0) {
					if (tmp.getName().compareTo("") == 0)
						continue;
					for (int i = 0; i < indent; i++)
						printFolderString = printFolderString.concat("\t");
					printFolderString = printFolderString.concat("file : id="
							+ tmp.getId() + " - name=" + tmp.getName() + "\n");
				}
			}
			for (FolderDto tmp : tmpListOfChildren) { // then the folders
				if (tmp.getChildren().size() > 0) {
					printFolder(tmp, indent + 1);
				}
			}
		}
		if (indent == 0) {
			this.printMsgInDialogBox(printFolderString);
		}
	}

	/*
	 * createAgentStats input: Nothing -- returns: Nothing It creates the
	 * agentStats objects
	 */
	public boolean createAgentStats() {
		if (this.stringOfAgents == null) {
			Info.display("Warning", "Agent string is null");
			return false;
		} else if (this.stringOfAgentStats == null) {
			Info.display("Warning", "AgentStats string is null");
			return false;
		}

		// agentsParts structure: id--name--hostName--port\n
		String[] agents = this.stringOfAgents.split("\n");
		// structure: agentIdOfIS--ongoing--failed--succeeded--canceled--total\n
		String[] statistics = this.stringOfAgentStats.split("\n");

		List<AgentStat> tmpList = new ArrayList<AgentStat>();
		for (String agent : agents) {
			String[] partsOfAgent = agent.split("--");
			String id = partsOfAgent[0];
			String endpoint = partsOfAgent[2];
			for (String stats : statistics) {
				String[] partsOfStats = stats.split("--");
				if (partsOfStats.length < 6)
					continue;

				if (partsOfStats[0].compareTo(id) == 0) {
					AgentStat agentStat = new AgentStat();
					agentStat.setId(id);
					agentStat.setEndpoint(endpoint);
					agentStat.setOngoing(partsOfStats[1]);
					agentStat.setFailed(partsOfStats[2]);
					agentStat.setSuccesful(partsOfStats[3]);
					agentStat.setCanceled(partsOfStats[4]);
					agentStat.setTotal(partsOfStats[5]);
					tmpList.add(agentStat);
				}
			}
		}
		if (tmpList.size() > 0)
			listAgentStats = tmpList;

		if (this.listAgentStats == null)
			return false;
		else return true;

	}
	
	/*
	 * setDialogBoxForMessages input: Nothing -- returns: Nothing It initializes
	 * the 'dialogBoxGen' for showing messages
	 */
	public void setDialogBoxForMessages() {
		dialogBoxGen = new DialogBox();
		dialogBoxGen.setAnimationEnabled(true);
		dialogBoxGen.getElement().getStyle().setZIndex(100);
		dialogBoxGen.setText("[+]");
	}

	/*
	 * printMsgInDialogBox input: String with the message -- returns: Nothing It
	 * shows the 'dialogBoxGen' which contains the input string message
	 */
	public void printMsgInDialogBox(String message) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendEscapedLines(message);
		FramedPanel panel = new FramedPanel();
		panel.setHeadingText("");
		panel.setWidth(300);
		panel.setBodyStyle("background: none; padding: 5px");
		HTML html = new HTML();
		html.setHTML(builder.toSafeHtml());
		panel.add(html);
		panel.setButtonAlign(BoxLayoutPack.START);
		TextButton closeButton = new TextButton("Close");
		closeButton.addSelectHandler(new SelectHandler() {
			public void onSelect(SelectEvent event) {
				dialogBoxGen.hide();
			}
		});

		// adding the button
		panel.addButton(closeButton);
		// key handlers -------------
		foc = new FocusPanel();
		foc.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
						|| event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					dialogBoxGen.hide();
				}
			}
		});
		foc.add(panel);
		// --------------

		dialogBoxGen.setWidget(foc);
		dialogBoxGen.center();
		if (focusTimer == null) {
			Timer focusTimer = new Timer() {
				@Override
				public void run() {
					foc.setFocus(true);
				}
			};
			focusTimer.schedule(200);
		}
	}

	public void popupDestTrees(){
		rpCalls.getTreeWriteSources(true);
	}
	/*
	 * createDialogBox input: Widget -- returns: DialogBox It hides the
	 * 'dialogBoxGen' and returns a new DialogBox containing the input widget
	 */
	public DialogBox createDialogBox(Widget widg) {
		dialogBoxGen.hide(); //
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("[+]");
		dialogBox.setAnimationEnabled(true);
		dialogBox.getElement().getStyle().setZIndex(50);
		dialogBox.setWidget(widg);
		return dialogBox;
	}

	/*
	 * createAnchor input: String with the message -- returns: ToolTipConfig It
	 * creates a new anchor which contains the input string message
	 */
	public ToolTipConfig createAnchor(String message) {
		ToolTipConfig config = new ToolTipConfig();
		config.setBodyText(message);
		// config.setMouseOffset(new int[]{0,0});
		// config.setAnchor(Side.LEFT);
		// config.setCloseable(true);
		config.setTrackMouse(true);
		return config;
	}

	public PopupPanel createLoadingIcon() {
		PopupPanel loadingIcon = new PopupPanel();
		loadingIcon.setStyleName("imagePop");
		loadingIcon.getElement().getStyle().setBorderWidth(0, Unit.PX);
		return loadingIcon;
	}
	public void startLoadingIcon(Widget sender, PopupPanel loadingIcon) {
		int width, height;
		if (sender == null)
			return;
		height = sender.getOffsetHeight();
		width = sender.getOffsetWidth();
		loadingIcon.setPopupPosition(sender.getAbsoluteLeft() + width
				/ 2 - 20, sender.getAbsoluteTop() + height / 2 - 20);
		loadingIcon.show();
	}
	public void stopLoadingIcon(PopupPanel loadingIcon) {
		loadingIcon.hide();
		//sometimes it is blocked .. we hide again 
		if(loadingIcon.isShowing())loadingIcon.hide();
		loadingIcon.clear();
	}
}
