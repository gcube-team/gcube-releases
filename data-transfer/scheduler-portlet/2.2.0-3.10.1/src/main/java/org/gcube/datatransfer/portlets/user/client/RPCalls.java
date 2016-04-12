package org.gcube.datatransfer.portlets.user.client;

import java.util.ArrayList;

import org.gcube.datatransfer.portlets.user.client.Common.DESTTYPE;
import org.gcube.datatransfer.portlets.user.client.Common.FolderToRetrieve;
import org.gcube.datatransfer.portlets.user.client.obj.Outcomes;
import org.gcube.datatransfer.portlets.user.client.obj.TreeOutcomes;
import org.gcube.datatransfer.portlets.user.shared.SchedulerServiceAsync;
import org.gcube.datatransfer.portlets.user.shared.obj.BaseDto;
import org.gcube.datatransfer.portlets.user.shared.obj.CallingSchedulerResult;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;
import org.gcube.datatransfer.portlets.user.shared.obj.SchedulerObj;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class RPCalls extends Common{

	public void createNewTreeSource(){
		if(newTreeSourceField==null || newTreeSourceField.getCurrentValue()==null){
			Info.display("","Sourcefield is null");
			return ;
		}		
		schedulerService.createNewTreeSource(selectedAgentSource,
				selectedAgentSourcePort, scope.getCurrentValue(),
				newTreeSourceField.getCurrentValue(),
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("ERROR", "Failure creating the new tree source ! ");
			}

			public void onSuccess(String result) {
				if (result == null){
					Info.display("ERROR", "Failure creating the new tree source ! ");
					return;
				}

				//refresh left 
				//TO-DO
			}
		});
	}
	public void deleteTreeSource(){
		if(selectedDestCollection==null){
			Info.display("","Dest Collection is null!");
			return ;
		}
		
		schedulerService.deleteTreeSource(selectedAgentSource,
				selectedAgentSourcePort, scope.getCurrentValue(),
				selectedDestCollection,
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("ERROR", "Failure deleting the tree source ! ");
				
			}

			public void onSuccess(String result) {
				if (result == null){
					Info.display("ERROR", "Failure deleting the tree source ! ");
					return;
				}

				if(multiBoxTreeWriteSources==null || multiBoxTreeWriteSources.getItemCount()<2){
					// tree sources were less than 2 so after deleting this one
					//no tree source left
					folderResDestination = null;
					folderDestination = null;
					portlet.redrawEast();
				}
				else {
					dialogBoxGen = functions.createDialogBox(popups.asPopUpTreeWriteSources());
					dialogBoxGen.center();
					focusTimer.schedule(200);			
				}
			}
		});

	}
	public void getTreeReadSources(final boolean popUpDestTrees) {
		if (scope == null || ResourceName == null) {
			Info.display("Warning", "resource name or scope is null");
			return;
		}
		if(loadingIconForSource==null)loadingIconForSource=functions.createLoadingIcon();
		functions.startLoadingIcon(sourceTree,loadingIconForSource);
		
		
		schedulerService.getTreeSources(selectedAgentSource,
				selectedAgentSourcePort, scope.getCurrentValue(),"reader",
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("ERROR", "Failure getting the Tree Sources ! ");
				combo1.setValue(lastCombo1Value);
				functions.stopLoadingIcon(loadingIconForSource);
			}

			public void onSuccess(String result) {
				functions.stopLoadingIcon(loadingIconForSource);
				if (result == null){
					Info.display("ERROR", "Failure getting the Tree Sources ! ");
					return;
				}else if (result.compareTo("") == 0){
					return;
				}				
				//load the left panel with trees sources
				folderResSource = result;
				folderSource = null;

				//reset the dest panel
				if(popUpDestTrees){
					folderDestination=null;
					folderResDestination=null;
					destCombo.setValue(null);
					lastDestComboValue=null;
				}

				portlet.redrawEast();
				lastCombo1Value = combo1.getCurrentValue();
				if (folderResSource == null)
					Info.display("Message", "Items were not loaded");
				else
					Info.display("Message", "Items were loaded");

				if(popUpDestTrees)functions.popupDestTrees();			
			}
		});
	}
	public void getTreeWriteSources(final boolean popUpDestPanel) {
		if (scope == null || ResourceName == null) {
			Info.display("Warning", "resource name or scope is null");
			return;
		}
		schedulerService.getTreeSources(selectedAgentSource,
				selectedAgentSourcePort, scope.getCurrentValue(),"writer",
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("ERROR", "Failure getting the Tree Sources ! ");
				combo1.setValue(lastCombo1Value);
			}

			public void onSuccess(String result) {
				if (result == null){
					Info.display("ERROR", "Failure getting the Tree Sources ! ");
					return;
				}else if (result.compareTo("") == 0){
					return;
				}
				stringOfWriteSourceIDs = result;
				if (multiBoxTreeWriteSources != null)
					multiBoxTreeWriteSources.clear();
				else {
					multiBoxTreeWriteSources = new ListBox(false);
					multiBoxTreeWriteSources.setWidth("260px");
					multiBoxTreeWriteSources.setVisibleItemCount(5);
				}

				FolderDto dest = (FolderDto) FolderDto
						.createSerializer()
						.deSerialize(result,
								"org.gcube.datatransfer.portlets.user.shared.obj.FolderDto");

				for(FolderDto fold : dest.getChildren()){
					if(fold.getName().compareTo("")==0)continue;
					multiBoxTreeWriteSources.addItem(fold.getName());
				}
				folderDestination=null;
				folderResDestination=null;
				portlet.redrawEast();
				if(multiBoxTreeWriteSources.getItemCount()<1){
					makeNewFolder.hide();
					deleteCurrentFolder.hide();
					makeNewTreeSource.show();
					if(isAdmin){						
						deleteCurrentTreeSource.show();
					}
					destCombo.setValue(DESTTYPE.TreeBased.toString());
					selectedDestCollection = null;					

					if (combo1==null || combo1.getCurrentValue()==null ||
							!combo1.isAgentSource()
							|| selectedAgentSource == null) {
						//selection of agent should be transparent based on statistics
						functions.transparentSelectionOfAgent(FolderToRetrieve.NONE);
						//changed ...
						//dialogBoxGen = functions.createDialogBox(asPopUpAgents(FolderToRetrieve.DataStorageFolder));
						//dialogBoxGen.center();
						//focusTimer.schedule(200);
					} 
				}
				else if(popUpDestPanel){
					//pop up tree select
					dialogBoxGen = functions.createDialogBox(popups.asPopUpTreeWriteSources());
					dialogBoxGen.center();
					focusTimer.schedule(200);					
				}
			}
		});
	}
	/*
	 * getAgents input: Nothing -- returns: Nothing Remote Procedure Call:
	 * retrieving the agents
	 */
	public void getAgents() {
		if (scope == null || ResourceName == null) {
			Info.display("Warning", "agent or scope is null");
			return;
		}

		schedulerService.getObjectsFromIS("Agent", scope.getCurrentValue(),
				ResourceName.getCurrentValue(), new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("ERROR", "Failure getting the Agents ! ");
				//functions.printMsgInDialogBox("WARNING - Remote Procedure Call - Failure getting the Agents");
			}

			public void onSuccess(String result) {
				if (result == null){
					Info.display("ERROR", "Failure getting the Agents ! ");
					return;
				}
				//functions.printMsgInDialogBox("WARNING - Remote Procedure Call - Failure getting the Agents");
				else if (result.compareTo("") == 0)
					return;

				stringOfAgents = result;
				if (multiBoxAgents != null)
					multiBoxAgents.clear();
				else {
					multiBoxAgents = new ListBox(false);
					multiBoxAgents.setWidth("260px");
					multiBoxAgents.setVisibleItemCount(5);
				}
				String[] agentsArray = result.split("\n");
				for (String tmp : agentsArray) {
					// tmp contains: id--name--hostName--port
					String[] partsOfInfo = tmp.split("--");
					// we store only the host name
					multiBoxAgents.addItem(partsOfInfo[2]);
				}

				//getting agent statistics
				getAgentStatistics(null);

			}
		});
	}

	/*
	 * schedule input: Nothing -- returns: Nothing Remote Procedure Call:
	 * schedule a transfer
	 */
	public void schedule() {

		String text = "";
		if (typeOfSchedule.getCurrentValue().compareTo("direct") == 0)
			text = "direct";
		else if (typeOfSchedule.getCurrentValue().compareTo(
				"periodically scheduled") == 0)
			text = "periodically";
		else if (typeOfSchedule.getCurrentValue().compareTo(
				"manually scheduled") == 0)
			text = "manually";

		// functions.printMsgInDialogBox("You have submitted a "+text+" scheduled transfer .. ");
		Info.display("", "You have submitted a " + text
				+ " scheduled transfer .. ");

		SchedulerObj scheduleObj = new SchedulerObj();
		scheduleObj = functions.fillSchedulerObj(scheduleObj);
		if (scheduleObj == null)
			return;
		String jsonString = SchedulerObj.createSerializer().serialize(
				scheduleObj);

		toBeTransferredStore.clear();
		toBeTransferredStore.commitChanges();

		schedulerService.schedule(jsonString, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("","ERROR - Remote Procedure Call - Failure schedule");
			}

			public void onSuccess(String result) {
				if (result == null) {
					Info.display("","ERROR - Remote Procedure Call - Failure schedule (result is null)");
				} else {
					getTransferTimer.schedule(200);
					String transferIdResult = result;
					Info.display("schedule", "transfer id=" + transferIdResult);
					if(destCombo.isTreeBased()){targetStore.clear();targetStore.commitChanges();}
					// functions.printMsgInDialogBox("'schedule': transfer id="+transferIdResult);
				}
			}
		});

	}

	/*
	 * monitor input: Nothing -- returns: Nothing Remote Procedure Call: monitor
	 * a transfer
	 */
	public void monitor() {
		// functions.printMsgInDialogBox("You have submitted a monitor task .. ");
		Info.display("", "You have submitted a monitor task .. ");

		schedulerService.monitor(scope.getCurrentValue(),
				ResourceName.getCurrentValue(),
				transferId.getCurrentValue(), new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("","'monitor': Failure");
			}

			public void onSuccess(String result) {
				if (result == null)
					functions.printMsgInDialogBox("'monitor': result=null\n");
				else {
					callingSchedulerResult = (CallingSchedulerResult) CallingSchedulerResult
							.createSerializer()
							.deSerialize(result,
									"org.gcube.datatransfer.portlets.user.shared.obj.CallingSchedulerResult");
					String errors = "";
					int i = 0;
					for (String tmp : callingSchedulerResult
							.getErrors()) {
						if (tmp.compareTo("") == 0)
							continue;
						if (i == 0)
							errors = errors.concat("objervations:\n");
						errors = errors.concat("- " + tmp + "\n");
						i++;
					}
					functions.printMsgInDialogBox("'monitor':\n "
							+ callingSchedulerResult.getMonitorResult()
							+ "\n" + errors + "\n");
				}
			}
		});
	}

	/*
	 * getOutcomes input: Nothing -- returns: Nothing Remote Procedure Call:
	 * getting the outcomes of a transfer
	 */
	public void getOutcomes() {
		// functions.printMsgInDialogBox("You have submitted a getOutcomes task .. ");
		Info.display("", "You have submitted a getOutcomes task .. ");

		schedulerService.getOutcomes(scope.getCurrentValue(),
				ResourceName.getCurrentValue(),
				transferId.getCurrentValue(), new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("","'getOutcomes': Failure");
			}

			public void onSuccess(String result) {
				if (result == null)
					functions.printMsgInDialogBox("'getOutcomes': result=null\n");
				else {
					callingSchedulerResult = (CallingSchedulerResult) CallingSchedulerResult
							.createSerializer()
							.deSerialize(result,
									"org.gcube.datatransfer.portlets.user.shared.obj.CallingSchedulerResult");
					String errors = "";
					int i = 0;
					for (String tmp : callingSchedulerResult
							.getErrors()) {
						if (tmp.compareTo("") == 0)
							continue;
						if (i == 0)
							errors = errors.concat("objervations:\n");
						errors = errors.concat("- " + tmp + "\n");
						i++;
					}
					if (callingSchedulerResult.getSchedulerOutcomes() == null) {
						functions.printMsgInDialogBox("'getOutcomes':\n " +
								errors + "\n");
						return;
					}

					String[] outcomes = callingSchedulerResult
							.getSchedulerOutcomes().split("Outcome-");

					if (outcomes.length > 0) {

						boolean treeBased=false;
						for(String tmp:outcomes){
							if(tmp.indexOf("ReadTrees")!=-1){treeBased=true;break;}
						}

						//TREE-BASED
						if(treeBased){ 
							listTreeOutcomes = new ArrayList<TreeOutcomes>();
							for (String outcomeString : outcomes) {
								TreeOutcomes outcome = new TreeOutcomes();
								outcome.setTotalMessage("Outcome: "
										+ outcomeString);
								String[] linesOfOutcome = outcomeString.split("\n");
								for (String line : linesOfOutcome) {

									if (line.startsWith("SourceID: ")){
										outcome.setSourceID(line.replaceAll("SourceID: ",""));
									}
									else if (line.startsWith("DestID: ")){
										outcome.setDestID(line.replaceAll("DestID: ",""));
									}
									else if (line.startsWith("ReadTrees: ")){
										outcome.setReadTrees(line.replaceAll("ReadTrees: ",""));
									}
									else if (line.startsWith("WrittenTrees: ")){
										outcome.setWrittenTrees(line.replaceAll("WrittenTrees: ",""));
									}
									else if (line.startsWith("Success")){
										outcome.setSuccess(line.replaceAll("Success", "").substring(2));
									}
									else if (line.startsWith("Failure")){
										outcome.setFailure(line.replaceAll("Failure", "").substring(2));
									}
									else if (line.startsWith("Exception: ")){
										outcome.setException(line.replaceAll("Exception: ",""));
									}
								}
								if(outcome.getSourceID()!=null)listTreeOutcomes.add(outcome);
							}
							dialogBoxGen = functions.createDialogBox(popups.asPopUpTreeOutcomes());
							dialogBoxGen.center();
							focusTimer.schedule(200);
						}
						//FILE-BASED
						else{
							listOutcomes = new ArrayList<Outcomes>();
							for (String outcome : outcomes) {
								Outcomes tmpOutcome = new Outcomes();
								tmpOutcome.setTotalMessage("Outcome: "
										+ outcome);
								String[] linesOfOutcome = outcome
										.split("\n");
								for (String line : linesOfOutcome) {
									if (line.startsWith("FileName: "))
										tmpOutcome.setFileName(line
												.replaceAll("FileName: ",
														""));
									else if (line.startsWith("Dest: "))
										tmpOutcome.setDestination(line
												.replaceAll("Dest: ", ""));
									else if (line.startsWith("Exception: "))
										tmpOutcome.setException(line
												.replaceAll("Exception: ",
														""));
									else if (line
											.startsWith("TransferTime: "))
										tmpOutcome.setTransferTime(line
												.replaceAll(
														"TransferTime: ",
														"")
														+ " ms");
									else if (line.startsWith("TransferredBytes: "))
										tmpOutcome.setTransferredBytes(line.replaceAll(
												"TransferredBytes: ",
												""));
									else if (line.startsWith("Size: "))
										tmpOutcome.setSize(line.replaceAll(
												"Size: ",
												""));
									else if (line.startsWith("Success"))
										tmpOutcome.setSuccess(line
												.replaceAll("Success", "")
												.substring(2));
									else if (line.startsWith("Failure"))
										tmpOutcome.setFailure(line
												.replaceAll("Failure", "")
												.substring(2));
								}
								if (tmpOutcome.getFileName() != null)
									listOutcomes.add(tmpOutcome);
							}
							dialogBoxGen = functions.createDialogBox(popups.asPopUpOutcomes());
							dialogBoxGen.center();
							focusTimer.schedule(200);

						}
					}
					else{
						functions.printMsgInDialogBox("'getOutcomes':\n   outcomes:\n"
								+ callingSchedulerResult
								.getSchedulerOutcomes()
								+ "\n"
								+ errors + "\n");
					}
				}
			}
		});
	}

	/*
	 * cancel input: Nothing -- returns: Nothing Remote Procedure Call: cancel a
	 * transfer
	 */
	public void cancel() {
		// functions.printMsgInDialogBox("You have submitted a cancel task .. ");
		Info.display("", "You have submitted a cancel task .. ");

		schedulerService.cancel(scope.getCurrentValue(),
				ResourceName.getCurrentValue(),
				transferId.getCurrentValue(), force.getValue(),
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				dialogBoxGen.setText("'cancel': Failure");
				dialogBoxGen.center();
				closeButton.setFocus(true);
			}

			public void onSuccess(String result) {
				if (result == null)
					Info.display("","'cancel': result=null\n");
				else {
					callingSchedulerResult = (CallingSchedulerResult) CallingSchedulerResult
							.createSerializer()
							.deSerialize(result,
									"org.gcube.datatransfer.portlets.user.shared.obj.CallingSchedulerResult");
					String errors = "";
					int i = 0;
					for (String tmp : callingSchedulerResult
							.getErrors()) {
						if (tmp.compareTo("") == 0)
							continue;
						if (i == 0)
							errors = errors.concat("objervations:\n");
						errors = errors.concat("- " + tmp + "\n");
						i++;
					}
					functions.printMsgInDialogBox("'cancel': "
							+ callingSchedulerResult.getCancelResult()
							+ "\n" + errors + "\n");
				}
			}
		});
	}

	/*
	 * getTransfers input: Nothing -- returns: Nothing Remote Procedure Call:
	 * getting the transfers for a specific resource name and scope calling also
	 * the 'designTransferGrid' where a grid is being designed for showing the
	 * transfers
	 */
	public void getTransfers() {
		if(loadingIconForTransfers==null)loadingIconForTransfers=functions.createLoadingIcon();
		functions.startLoadingIcon(grid,loadingIconForTransfers);
		//getting scope /resource Name
		String scopeStr,recourceNameStr;
		if(scope==null || scope.getCurrentValue()==null || 
				scope.getCurrentValue().compareTo("")==0){
			scopeStr=defaultScope;
		}else scopeStr=scope.getCurrentValue();
		if(ResourceName==null || ResourceName.getCurrentValue()==null || 
				ResourceName.getCurrentValue().compareTo("")==0){
			recourceNameStr=defaultResourceName;
		}else recourceNameStr=ResourceName.getCurrentValue();
		//----------------------------------------
		schedulerService.getTransfers(scopeStr,
				recourceNameStr, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				functions.stopLoadingIcon(loadingIconForTransfers);
				Info.display("WARNING", "Failure getting the transfers ! ");
				//functions.printMsgInDialogBox("'getTransfers': Failure");
				functions.designTransferGrid();
			}

			public void onSuccess(String result) {
				functions.stopLoadingIcon(loadingIconForTransfers);
				if (result == null) {
					Info.display("WARNING", "Failure getting the transfers ! ");
				} else {
					callingManagementResultJson = result;					
				}
				//CHANGED- init panel when init also the others... 
				//functions.designTransferGrid();

				functions.loadTheTransfers();
			}
		});
	}

	public void deleteFolderInMongoDB(){
		String pathToDelete = folderDestination.getName();

		final String finalPathToDelete=pathToDelete;

		schedulerService.deleteFolderInMongoDB(
				smServiceClass.getCurrentValue(),
				smServiceName.getCurrentValue(),
				smOwner.getCurrentValue(),
				smAccessType.getCurrentValue(), finalPathToDelete,
				scope.getCurrentValue(), new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Info.display("Warning", "Failed to store new folder");
					}
					public void onSuccess(Void result) {
						//going to upper level .. 

						for(BaseDto tmp : folderDestination.getChildren()){
							if(tmp.getShortname().compareTo("<< Back")==0){
								TreeNode<BaseDto> node = targetTree.findNode(tmp);
								lastSelectedMongoDBFolderDestName = node.getModel()
										.getName();
								destinationF.setValue(node.getModel().getName());
								destinationAnchor.setBodyText(node.getModel().getName());
								getMongoDBFolderDest(node.getModel().getName());
								return;
							}
						}
						//in other case it's the root folder that you deleted .. so you get again that empty root to show
						destinationF.setValue("/");
						destinationAnchor.setBodyText("/");
						getMongoDBFolderDest("/");
					}
				});
	}
	public void storeNewFolderInMongoDB(){
		if (newFolderField == null)	return;
		if (newFolderField.getCurrentValue() == null) return;

		String path=newFolderField.getCurrentValue();
		path=path.replaceAll("(/)\\1+", "$1"); //remove multiple consecutive '/'

		if(!path.startsWith("/")){
			String rootPath = folderDestination.getName();
			if(!rootPath.endsWith("/"))rootPath=rootPath+"/";
			path=rootPath+path;
		}
		if(!path.endsWith("/"))path=path+"/";

		final String finalPathForFolder=path;

		schedulerService.createNewFolderInMongoDB(
				smServiceClass.getCurrentValue(),
				smServiceName.getCurrentValue(),
				smOwner.getCurrentValue(),
				smAccessType.getCurrentValue(), path,
				scope.getCurrentValue(), new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Info.display("Warning", "Failed to store new folder");
					}
					public void onSuccess(Void result) {
						destinationF.setValue(finalPathForFolder);
						destinationAnchor.setBodyText(finalPathForFolder);
						getMongoDBFolderDest(finalPathForFolder);
					}
				});

	}
	/*
	 * getDataSources input: Nothing -- returns: Nothing Remote Procedure Call:
	 * retrieving data sources
	 */
	public void getDataSources() {
		schedulerService.getObjectsFromIS("DataSource",
				scope.getCurrentValue(), ResourceName.getCurrentValue(),
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("Warning",
						"WARNING - Remote Procedure Call - Failure getting the DataSources");
			}

			public void onSuccess(String result) {
				if (result == null) {
					Info.display("Warning",
							"WARNING - Remote Procedure Call - Failure getting the DataSources");
					return;
				} else if (result.compareTo("") == 0) {
					Info.display("Warning",
							"WARNING - Remote Procedure Call - Failure getting the DataSources");
					return;
				}
				// storing the list of data source
				String[] sourcesArray = result.split("\n");
				int num = 0;
				dataSourcesList = new ArrayList<String>();
				for (String tmp : sourcesArray) {
					// tmp contains:
					// resultIdOfIS--name--description--endpoint--username--password--propertyFolders
					dataSourcesList.add(tmp);
					num++;
				}
				if (num == 0) {
					Info.display("Warning",
							"WARNING - Remote Procedure Call - Failure getting the DataSources");
					return;
				}
				// putting only the names in the multiBox
				multiBoxDataSources.clear();
				for (String tmp : dataSourcesList) {
					String[] parts = tmp.split("--");
					multiBoxDataSources.addItem(parts[1]);
				}
			}
		});

	}

	/*
	 * getWorkspaceFolder input: String with the id of a specific folderSource and a
	 * boolean indicating if we want to have the parent of this folderSource or not --
	 * returns: Nothing Remote Procedure Call: getting the workspace folderSource as a
	 * json object
	 */
	public void getWorkspaceFolder(final String idOfSpecificFolder,
			boolean needTheParent) {
		if (idOfSpecificFolder == null)
			callingWorkspaceRoot = true;
		if (gettingUserAndScope == false) {
			Info.display("Message",
					"User and scope were not loaded properly and we cannot retrieve workspace");
			combo1.setValue(lastCombo1Value);
			return;
		}

		// if jsonWorkspace is null we will load it again ..
		// if(jsonWorkspace==null){Info.display("Message",
		// "Workspace was not loaded");return;}
		if(loadingIconForSource==null)loadingIconForSource=functions.createLoadingIcon();
		functions.startLoadingIcon(sourceTree,loadingIconForSource);

		schedulerService.getWorkspaceFolder(jsonWorkspace, idOfSpecificFolder,
				needTheParent, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				functions.stopLoadingIcon(loadingIconForSource);
				//functions.printMsgInDialogBox("Remote Procedure Call - Failure list files");
				Info.display("Warning", "Items were not loaded");
				combo1.setValue(lastCombo1Value);
			}

			public void onSuccess(String folderResult) {
				functions.stopLoadingIcon(loadingIconForSource);
				folderResSource = folderResult;
				folderSource = null;

				//reset the dest panel
				if(idOfSpecificFolder==null){
					folderDestination=null;
					folderResDestination=null;
					destCombo.setValue(null);
					lastDestComboValue=null;
				}

				portlet.redrawEast();
				lastCombo1Value = combo1.getCurrentValue();
				if (folderResSource == null)
					Info.display("Message", "Items were not loaded");
				else
					Info.display("Message", "Items were loaded");
			}
		});
	}

	/*
	 * getWorkspace input: Nothing -- returns: Nothing Remote Procedure Call:
	 * getting the workspace as a json object
	 */
	public void getWorkspace() {
		schedulerService.getWorkspace(ResourceName.getCurrentValue(),
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("Warning", "Workspace was not loaded");
			}

			public void onSuccess(String result) {
				jsonWorkspace = result;
				if (jsonWorkspace == null) {
					Info.display("Message",
							"Workspace was not loaded in the first place");
				}
			}
		});
	}

	/*
	 * getUserAndScope input: Nothing -- returns: Nothing Remote Procedure Call:
	 * getting the user and scope from the portal liferay
	 */
	public void getUserAndScopeAndRole() {
		schedulerService.getUserAndScopeAndRole(new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("","Remote Procedure Call - Failure retrieving user and scope");
				gettingUserAndScope = false;

				// set the south widget for list the transfers in DB
				getTransfers();
				// auto refresh every 30s
				getTransferRepeatingTimer.scheduleRepeating(30000); // ms

			}
			public void onSuccess(String userAndScope) {
				if (userAndScope == null) {
					Info.display("Warning",
							"user and scope were not loaded properly");
					gettingUserAndScope = false;
				} else {
					gettingUserAndScope = true;
					String[] parts = userAndScope.split("--");
					defaultResourceName = parts[0];
					defaultScope = parts[1];
					if (parts[2].compareTo("true") == 0) {
						isAdmin = true;
						if (ResourceName != null && scope != null) {
							ResourceName.enable();
							scope.enable();
							destinationF.enable();
						} else {
							Timer enableTimer = new Timer() {
								@Override
								public void run() {
									ResourceName.enable();
									scope.enable();
									destinationF.enable();
								}
							};
							enableTimer.schedule(3000); // ms
						}
					}

					ResourceName.setValue(parts[0]);
					scope.setValue(parts[1]);
					Info.display("Message", "user:" + parts[0] + " - scope:"
							+ parts[1]);
					west.clear();
					west.add(panels.asWidgetScheduler());
				}
				//ONLY FOR TESTING YOU CAN PUT HERE ISADMIN=TRUE
				//isAdmin = true;
				
				// set the south widget for list the transfers in DB
				getTransfers();
				// auto refresh every 30s
				getTransferRepeatingTimer.scheduleRepeating(30000); // ms

			}
		});
	}
	/*
	 * getDataStorages input: Nothing -- returns: Nothing Loading the remote
	 * nodes
	 */
	public void getDataStorages() {
		// retrieving remote nodes
		// storing them in a public variable

		schedulerService.getObjectsFromIS("DataStorage",
				scope.getCurrentValue(), ResourceName.getCurrentValue(),
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("Warning",
						"WARNING - Remote Procedure Call - Failure getting the DataStorages");
			}

			public void onSuccess(String result) {
				if (result == null) {
					Info.display("Warning",
							"WARNING - Remote Procedure Call - Failure getting the DataStorages");
					return;
				} else if (result.compareTo("") == 0) {
					Info.display("Warning",
							"WARNING - Remote Procedure Call - Failure getting the DataStorages");
					return;
				}
				// storing the list of data source
				String[] storagesArray = result.split("\n");
				int num = 0;
				dataStoragesList = new ArrayList<String>();
				for (String tmp : storagesArray) {
					// tmp contains:
					// resultIdOfIS--name--description--endpoint--username--password--
					dataStoragesList.add(tmp);
					num++;
				}
				if (num == 0) {
					Info.display("Warning",
							"WARNING - Remote Procedure Call - Failure getting the DataStorages");
					return;
				}
				// putting only the names in the multiBox
				multiBoxDataStorages.clear();
				for (String tmp : dataStoragesList) {
					String[] parts = tmp.split("--");
					multiBoxDataStorages.addItem(parts[1]);
				}
			}
		});
	}

	/*
	 * getDatasourceFolder input: String with the data source id and the
	 * specific path -- returns: Nothing Remote Procedure Call: getting the path
	 * folderSource(FolderDto) from this data source as a json object
	 */
	public void getDatasourceFolder(final String dataSourceId, final String path) {
		if(loadingIconForSource==null)loadingIconForSource=functions.createLoadingIcon();
		functions.startLoadingIcon(sourceTree,loadingIconForSource);
		currentDataSourcePath = path;

		schedulerService.getFileListOfDSourceOrDStorage("DataSource",dataSourceId, path,
				scope.getCurrentValue(), ResourceName.getCurrentValue(),
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				functions.stopLoadingIcon(loadingIconForSource);
				//Info.display("","Remote Procedure Call - Failure list files");
				Info.display("Warning", "Items were not loaded");
				combo1.setValue(lastCombo1Value);
			}

			public void onSuccess(String folderResult) {
				functions.stopLoadingIcon(loadingIconForSource);
				folderResSource = folderResult;
				folderSource = null;

				//reset the dest panel
				if(path!=null){
					if(path.compareTo("")==0){
						folderDestination=null;
						folderResDestination=null;
						destCombo.setValue(null);
						lastDestComboValue=null;
					}
				}

				portlet.redrawEast();
				lastCombo1Value = combo1.getCurrentValue();
				if (folderResSource == null)
					Info.display("Message", "Items were not loaded");
				else
					Info.display("Message", "Items were loaded");
			}
		});
	}
	public void getDatastorageFolder(String dataStorageId, String path) {
		if(loadingIconForTarget==null)loadingIconForTarget=functions.createLoadingIcon();
		functions.startLoadingIcon(targetTree,loadingIconForTarget);
		currentDataStoragePath = path;

		schedulerService.getFileListOfDSourceOrDStorage("DataStorage",dataStorageId, path,
				scope.getCurrentValue(), ResourceName.getCurrentValue(),
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				functions.stopLoadingIcon(loadingIconForTarget);
				//Info.display("","Remote Procedure Call - Failure list files of Dest");
				Info.display("Warning", "Items were not loaded");
				destCombo.setValue(lastDestComboValue);
			}

			public void onSuccess(String folderResult) {
				functions.stopLoadingIcon(loadingIconForTarget);
				folderResDestination = folderResult;
				folderDestination = null;
				portlet.redrawEast();
				lastDestComboValue = destCombo.getCurrentValue();
				makeNewFolder.hide();
				makeNewTreeSource.hide();
				deleteCurrentFolder.hide();
				deleteCurrentTreeSource.hide();

				if (folderResDestination == null)
					Info.display("Message", "Items were not loaded");
				else
					Info.display("Message", "Items were loaded");
			}
		});
	}
	public void getAgentFolder(final String path) {
		if(loadingIconForSource==null)loadingIconForSource=functions.createLoadingIcon();
		functions.startLoadingIcon(sourceTree,loadingIconForSource);
		currentAgentSourcePath = path;

		schedulerService.getFileListOfAgent(path, selectedAgentSource,
				selectedAgentSourcePort, scope.getCurrentValue(),
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				functions.stopLoadingIcon(loadingIconForSource);
				//	functions.printMsgInDialogBox("Remote Procedure Call - Failure list files");
				Info.display("Warning", "Items were not loaded");
				combo1.setValue(lastCombo1Value);
			}

			public void onSuccess(String folderResult) {
				functions.stopLoadingIcon(loadingIconForSource);
				folderResSource = folderResult;
				folderSource = null;

				//reset the dest panel
				if(path!=null){
					if(path.compareTo("/")==0){						
						folderDestination=null;
						folderResDestination=null;
						destCombo.setValue(null);
						lastDestComboValue=null;
					}
				}

				portlet.redrawEast();
				lastCombo1Value = combo1.getCurrentValue();
				if (folderResSource == null)
					Info.display("Message", "Items were not loaded");
				else
					Info.display("Message", "Items were loaded");
			}
		});
	}
	public void getAgentFolderDest(String path) {
		if(loadingIconForTarget==null)loadingIconForTarget=functions.createLoadingIcon();
		functions.startLoadingIcon(targetTree,loadingIconForTarget);
		currentAgentDestinationPath = path;

		schedulerService.getFileListOfAgent(path, selectedAgentDestination,
				selectedAgentDestinationPort, scope.getCurrentValue(),
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				functions.stopLoadingIcon(loadingIconForTarget);
				//	functions.printMsgInDialogBox("Remote Procedure Call - Failure list files");
				Info.display("Warning", "Items were not loaded");
				destCombo.setValue(lastDestComboValue);
			}

			public void onSuccess(String folderResult) {
				functions.stopLoadingIcon(loadingIconForTarget);
				folderResDestination = folderResult;
				folderDestination = null;
				portlet.redrawEast();
				lastDestComboValue = destCombo.getCurrentValue();
				makeNewFolder.hide();
				makeNewTreeSource.hide();
				deleteCurrentFolder.hide();
				deleteCurrentTreeSource.hide();

				if (folderResDestination == null)
					Info.display("Message", "Items were not loaded");
				else
					Info.display("Message", "Items were loaded");
			}
		});
	}

	/*
	 * getMongoDBFolder input: String with the data source id and the specific
	 * path -- returns: Nothing Remote Procedure Call: getting the path
	 * folder(FolderDto) from this data source as a json object
	 */
	public void getMongoDBFolder(final String path) {
		if(loadingIconForSource==null)loadingIconForSource=functions.createLoadingIcon();
		functions.startLoadingIcon(sourceTree,loadingIconForSource);
		// currentDataSourcePath=path;
		currentMongoDBSourcePath = path;

		schedulerService.getFileListOfMongoDB(
				smServiceClassSource.getCurrentValue(),
				smServiceNameSource.getCurrentValue(),
				smOwnerSource.getCurrentValue(),
				smAccessTypeSource.getCurrentValue(), path,
				scope.getCurrentValue(), new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						functions.stopLoadingIcon(loadingIconForSource);
						//functions.printMsgInDialogBox("Remote Procedure Call - Failure list files");
						Info.display("Warning", "Items were not loaded");
						combo1.setValue(lastCombo1Value);
					}

					public void onSuccess(String folderResult) {
						functions.stopLoadingIcon(loadingIconForSource);
						folderResSource = folderResult;
						folderSource = null;

						//reset the dest panel
						if(path!=null){
							if(path.compareTo("/")==0){						
								folderDestination=null;
								folderResDestination=null;
								destCombo.setValue(null);
								lastDestComboValue=null;
							}
						}

						portlet.redrawEast();
						lastCombo1Value = combo1.getCurrentValue();
						if (folderResSource == null)
							Info.display("Message", "Items were not loaded");
						else
							Info.display("Message", "Items were loaded");
					}
				});
	}

	public void getMongoDBFolderDest(String path) {
		if(loadingIconForTarget==null)loadingIconForTarget=functions.createLoadingIcon();
		functions.startLoadingIcon(targetTree,loadingIconForTarget);
		// currentDataSourcePath=path;
		currentMongoDBDestinationPath = path;
		schedulerService.getFileListOfMongoDB(
				smServiceClass.getCurrentValue(),
				smServiceName.getCurrentValue(),
				smOwner.getCurrentValue(),
				smAccessType.getCurrentValue(), path,
				scope.getCurrentValue(), new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						functions.stopLoadingIcon(loadingIconForTarget);
						//functions.printMsgInDialogBox("Remote Procedure Call - Failure list files");
						Info.display("Warning", "Items were not loaded");
						destCombo.setValue(lastDestComboValue);
					}
					public void onSuccess(String folderResult) {
						functions.stopLoadingIcon(loadingIconForTarget);
						folderResDestination = folderResult;
						folderDestination = null;
						portlet.redrawEast();
						lastDestComboValue = destCombo.getCurrentValue();
						makeNewFolder.show();
						makeNewTreeSource.hide();
						deleteCurrentFolder.show();
						deleteCurrentTreeSource.hide();

						if (folderResDestination == null)
							Info.display("Message", "Items were not loaded");
						else
							Info.display("Message", "Items were loaded");
					}
				});
	}

	/*
	 * getAgentStatistics input: Nothing -- returns: Nothing Remote Procedure
	 * Call: getting the agent statistics Calling the 'createAgentStats' for
	 * creating the agentStats objects
	 */
	public void getAgentStatistics(FolderToRetrieve folderToRetrieve) {
		final FolderToRetrieve foldToRetrieve=folderToRetrieve;

		schedulerService.getAgentStatistics(scope.getCurrentValue(),
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Info.display("Warning",
						"Agent Statistics were not loaded");
			}

			public void onSuccess(String result) {
				if (result == null) {
					Info.display("Warning",
							"Agent Statistics were not loaded");
					return;
				}
				// functions.printMsgInDialogBox(result);
				stringOfAgentStats = result;
				boolean resultFromCreatingAgentStats=functions.createAgentStats();
				if(storeAgentStats!=null){
					storeAgentStats.replaceAll(listAgentStats);
					gridAgentStats.reconfigure(storeAgentStats, cmAgentStat);
				}			

				//functions.printMsgInDialogBox("toPopup="+toPopup+"listAgentStats.size()="+listAgentStats.size());
				if(resultFromCreatingAgentStats){
					functions.calculateAndSelectBestAgent(foldToRetrieve);
				}
			}
		});
	}
}
