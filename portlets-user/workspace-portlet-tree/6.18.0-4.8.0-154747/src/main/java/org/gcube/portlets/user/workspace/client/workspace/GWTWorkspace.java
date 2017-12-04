/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class GWTWorkspace { //extends GWTWorkspaceEventSource implements Serializable {

//	private static final long serialVersionUID = 2973987311264718509L;
//
//	protected GWTWorkspaceServiceAsync gwtWorkspaceSvc;
//
//	protected GWTWorkspaceFolder root;
//
//	protected List<GWTItemSendRequest> requests = new LinkedList<GWTItemSendRequest>();
//
//	public GWTWorkspace()
//	{
//		gwtWorkspaceSvc = GWT.create(GWTWorkspaceService.class);
//		
//		/*GWTRemoteEventListener remoteEventListener = new GWTRemoteEventListener(gwtWorkspaceSvc, this);
//		remoteEventListener.setup();*/
//	}
//
//	public GWTWorkspaceFolder getRoot() {
//		return root;
//	}
//
//	public void refreshRoot()
//	{
//		fireOnBeforeRootUpdatedEvent();
//
//		gwtWorkspaceSvc.getRoot(new AsyncCallback<GWTWorkspaceFolder>() {
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - GetRoot ", caught);
//				operationFailed(caught.getMessage(), true);
//			}
//
//			public void onSuccess(GWTWorkspaceFolder result) {
//				Log.trace("CALLBACK - GetRoot "+result);
//				updateRoot(result);
//			}
//		});
//
//	}
//
//	public void updateRoot(GWTWorkspaceFolder root)
//	{
//		this.root = root;
//		fireRootUpdateEvent(root);
//	}
//
//	public void createWorkspace(final GWTWorkspaceFolder parent, final String tmpId, String newWorkspaceName) {
//		Log.debug("WA createWorkspace parent: "+parent+" tmpId: "+tmpId+" newWorkspaceName: "+newWorkspaceName);
//		fireOnBeforeWorkspaceCreateEvent(parent, tmpId, newWorkspaceName);
//
//		gwtWorkspaceSvc.createWorkspace(parent.getId(), newWorkspaceName, new AsyncCallback<GWTWorkspaceFolder>() {
//			
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - CreateWorkspace ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(GWTWorkspaceFolder result) {
//				Log.trace("CALLBACK - CreateWorkspace "+result);
//				workspaceCreated(tmpId, result, parent);
//			}
//		});
//	}
//
//	public void workspaceCreated(String tmpId, GWTWorkspaceFolder newWorkspace, GWTWorkspaceFolder parent)
//	{
//		try {
//			parent.addChild(newWorkspace);
//
//			//we have to do this because we can't receive the parent by deserializzation (is a new father)
//			newWorkspace.setParent(parent);
//			fireWorkspaceCreatedEvent(tmpId, newWorkspace);
//
//		} catch (WrongItemTypeException e) {
//			Log.error("Error during workspace creation", e);
//			return;
//		}
//	}
//
//	public void renameItem(final GWTWorkspaceItem item, final String newName) {
//
//		fireOnBeforeItemRenameEvent(item, newName);
//
//		gwtWorkspaceSvc.renameItem(item.getId(), newName, new AsyncCallback<Boolean>() {
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - RenameItem ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(Boolean result) {
//				Log.trace("CALLBACK - RenameItem "+result);
//				itemRenamed(result, item, newName);
//			}
//		});
//
//	}
//
//	public void itemRenamed(Boolean result, GWTWorkspaceItem item, String newName)
//	{
//		item.setName(newName);
//		fireItemRenamedEvent(item);
//	}
//
//	public void removeItem(final GWTWorkspaceItem item) {
//
//		fireOnBeforeItemRemoveEvent(item);
//
//		gwtWorkspaceSvc.removeItem(item.getId(), new AsyncCallback<Boolean>() {
//			
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - RemoveItem ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(Boolean result) {
//				Log.trace("CALLBACK - RemoveItem "+result);
//				itemRemoved(result, item);
//			}
//		});
//	}
//
//	public void itemRemoved(Boolean result, GWTWorkspaceItem item)
//	{
//		if (result.booleanValue()) {
//			item.getParent().removeChild(item);
//
//			fireItemRemovedEvent(item);
//		}
//	}
//
//	public void moveItem(final GWTWorkspaceItem item, final GWTWorkspaceFolder destination) {
//
//		fireOnBeforeItemMoveEvent(item);
//
//		gwtWorkspaceSvc.moveItem(item.getId(), destination.getId(), new AsyncCallback<Boolean>() {
//			
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - MoveItem ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(Boolean result) {
//				Log.trace("CALLBACK - MoveItem "+result);
//				itemMoved(result, item, destination);
//			}
//		});
//
//	}
//
//	public void itemMoved(Boolean result, GWTWorkspaceItem item, GWTWorkspaceFolder destination)
//	{
//		if (result){
//			GWTWorkspaceFolder itemParent = item.getParent();
//			itemParent.removeChild(item);
//
//			try {
//				item.setParent(destination);
//				destination.addChild(item);
//
//				fireItemMovedEvent(item);
//			} catch (WrongItemTypeException e) {
//				Log.error("Error during item moving", e);
//				return;
//			}
//		}
//	}
//
//	public void cloneItem(final GWTWorkspaceItem item, final String tmpId, String newItemName) {
//		Log.debug("WA cloneItem item: "+item+" tmpId: "+tmpId+" newItemName: "+newItemName);
//		
//		fireOnBeforeItemClonedEvent(item, tmpId, newItemName);
//
//		Log.trace("Calling the service");
//		gwtWorkspaceSvc.cloneItem(item.getId(), newItemName, new AsyncCallback<GWTWorkspaceItem>() {
//			
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - CreateWorkspace ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(GWTWorkspaceItem result) {
//				Log.trace("CALLBACK - CloneItem "+result);
//				itemCloned(tmpId, result, item.getParent());
//			}
//		});
//	}
//
//	public void itemCloned(String tmpId, GWTWorkspaceItem item, GWTWorkspaceFolder parent)
//	{
//		try {
//			parent.addChild(item);
//			//we have to do this because we can't receive the parent by deserializzation (is a new father)
//			item.setParent(parent);
//			fireItemClonedEvent(tmpId,item);
//
//		} catch (WrongItemTypeException e) {
//			Log.error("Error during item clonig", e);
//			return;
//		}
//
//	}
//
//	public void operationFailed(String message)
//	{
//		operationFailed(message, false);
//	}
//
//	public void operationFailed(String message, boolean isRoot)
//	{
//		fireOperationFailed(message);
//
//		if (!isRoot) refreshRoot();
//	}
//
//	public void setItemDescription(final GWTWorkspaceItem item, final String newDescription) {
//
//		fireOnBeforeSetItemDescriptionEvent(item, newDescription);
//
//		gwtWorkspaceSvc.setItemDescription(item.getId(), newDescription, new AsyncCallback<Boolean>() {
//			
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - SetItemDescription ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(Boolean result) {
//				Log.trace("CALLBACK - SetItemDescription "+result);
//				itemDescriptionSetted(result, item, newDescription);
//			}
//		});
//
//	}
//
//	public void itemDescriptionSetted(Boolean result, GWTWorkspaceItem item, String newDescription) {
//
//		if (result){
//			item.setDescription(newDescription);
//			fireItemDescriptionSetted(item);
//		}
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void getCapabilities(GWTWorkspaceItem item, AsyncCallback<GWTCapabilities> callback) {
//		gwtWorkspaceSvc.getCapabilities(item.getId(), callback);
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void getUserList(AsyncCallback<List<GWTUser>> callback) {
//		gwtWorkspaceSvc.getUsers(callback);
//	}
//
//	public void getLoadingFolders()
//	{
//		gwtWorkspaceSvc.getLoadingFolder(new AsyncCallback<List<GWTLoadingFolder>>() {
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - GetLoadingFolders ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(List<GWTLoadingFolder> result) {
//				Log.trace("CALLBACK - GetLoadingFolders # "+result.size());
//				updateLoadingFolders(result);
//			}
//		});
//	}
//
//	public void updateLoadingFolders(List<GWTLoadingFolder> loadingFolders)
//	{
//		fireLoadingFoldersGetted(loadingFolders);
//
//		for (GWTLoadingFolder loadingFolder: loadingFolders) getTermination(loadingFolder);
//	}
//
//	public void getTermination(final GWTLoadingFolder loadingFolder)
//	{
//		gwtWorkspaceSvc.getFolderLoadingTermination(loadingFolder.getKey(), new AsyncCallback<Void>() {
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - GetTermination ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(Void result) {
//				Log.trace("CALLBACK - GetTermination "+result);
//				folderLoadingTerminated(loadingFolder);
//			}
//		});
//	}
//
//	public void folderLoadingTerminated(GWTLoadingFolder loadingFolder)
//	{
//		fireLoadingFoldersTerminated(loadingFolder);
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void getSendRequest()
//	{
//		gwtWorkspaceSvc.getItemSendRequests(new AsyncCallback<List<GWTItemSendRequest>>() {
//			
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - GetSendRequest ", caught);
//				//workspace.operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(List<GWTItemSendRequest> result) {
//				Log.trace("CALLBACK - GetSendRequest "+result);
//				setSendRequest(result);
//			}
//		});
//
//	}
//
//	/**
//	 * Called when the list of ItemSendRequest is received.
//	 * @param requests
//	 */
//	public void setSendRequest(List<GWTItemSendRequest> requests){
//		this.requests = requests;
//		fireItemSendRequestList(requests);
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void sendItem(final GWTWorkspaceItem item, List<AddresseeUser> addressees) {
//
//		gwtWorkspaceSvc.sendRequest(item.getId(), addressees, new AsyncCallback<Void>() {
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - SendItem ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(Void result) {
//				Log.trace("CALLBACK - SendItem "+result);
//				itemSent(item);
//			}
//		});
//
//	}
//
//	/**
//	 * Called when a item has been sent.
//	 * @param item the item sent.
//	 */
//	public void itemSent(GWTWorkspaceItem item)
//	{
//		fireItemSent(item);
//	}
//
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public List<GWTItemSendRequest> getRequests() {
//		return requests;
//	}
//
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void acceptRequests(List<GWTItemSendRequest> requests) {
//		List<String> requestIds = new LinkedList<String>();
//		for (GWTItemSendRequest request:requests) requestIds.add(request.getId());
//
//		gwtWorkspaceSvc.acceptSendRequests(requestIds, new AsyncCallback<Boolean>() {
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - AcceptRequests ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(Boolean result) {
//				Log.trace("CALLBACK - AcceptRequests "+result);
//				refreshRoot();
//			}
//		});
//
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void declineRequests(List<GWTItemSendRequest> requests) {
//		List<String> requestIds = new LinkedList<String>();
//		for (GWTItemSendRequest request:requests) requestIds.add(request.getId());
//
//		gwtWorkspaceSvc.declineSendRequests(requestIds, new AsyncCallback<Boolean>() {
//			
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - DeclineRequests ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(Boolean result) {
//				Log.trace("CALLBACK - DeclineRequests "+result);
//				refreshRoot();
//			}
//		});
//
//	}
//
//	public void createExternalUrl(final GWTWorkspaceFolder parent, final String tmpId, String name, String description, String url)
//	{
//		fireOnBeforeUrlCreateEvent(parent, tmpId, name);
//
//		gwtWorkspaceSvc.createExternalUrl(name, description, url, parent.getId(), new AsyncCallback<GWTExternalUrl>() {
//			
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - CreateExternalUrl ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(GWTExternalUrl result) {
//				Log.trace("CALLBACK - CreateExternalUrl "+result);
//				urlCreated(tmpId, result, parent);
//			}
//		});
//
//	}
//
//	public void urlCreated(String tmpId, GWTExternalUrl url, GWTWorkspaceFolder parent){
//		try {
//			parent.addChild(url);
//			//we have to do this because we can't receive the parent by deserializzation (is a new father)
//			url.setParent(parent);
//			fireUrlCreatedEvent(tmpId, url);
//		} catch (WrongItemTypeException e) {
//			Log.error("Error during url creation", e);
//			return;
//		}
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void getDocumentAlternatives(String documentId,	AsyncCallback<List<GWTDocumentAlternativeLink>> callback) {
//		gwtWorkspaceSvc.getDocumentAlternatived(documentId, callback);
//
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void getDocumentParts(String documentId,	AsyncCallback<List<GWTDocumentPartLink>> callback) {
//		gwtWorkspaceSvc.getDocumentParts(documentId, callback);
//
//	}
//
//	public void importDocumentAlternatives(List<GWTDocumentAlternativeLink> alternatives, String destinationFolderId) {
//		List<String> alternativesOIDs = new LinkedList<String>();
//		for (GWTDocumentAlternativeLink alternative:alternatives) alternativesOIDs.add(alternative.getOID());
//
//		gwtWorkspaceSvc.importDocumentAlternatives(alternativesOIDs, destinationFolderId, new AsyncCallback<Void>() {
//			
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - ImportAlternatives ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(Void result) {
//				Log.trace("CALLBACK - ImportAlternatives "+result);
//				refreshRoot();
//			}
//		});
//
//	}
//
//	public void importDocumentParts(List<GWTDocumentPartLink> parts, String destinationFolderId) {
//		List<String> partsOIDs = new LinkedList<String>();
//		for (GWTDocumentPartLink part:parts) partsOIDs.add(part.getOID());
//
//		gwtWorkspaceSvc.importDocumentParts(partsOIDs, destinationFolderId, new AsyncCallback<Void>() {
//
//			public void onFailure(Throwable caught) {
//				Log.error("ERROR - CALLBACK - ImportParts ", caught);
//				operationFailed(caught.getMessage());
//			}
//
//			public void onSuccess(Void result) {
//				Log.trace("CALLBACK - ImportParts "+result);
//				refreshRoot();
//			}
//		});
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void setSessionValue(String name, String value,	AsyncCallback<Void> callback) {
//		gwtWorkspaceSvc.setValueInSession(name, value, callback);
//	}
//
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void saveSelectionState(WorkspaceSelectionState selectionState) {
//		gwtWorkspaceSvc.saveSelectionState(selectionState, new VoidCallBack());
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void loadSelectionState(AsyncCallback<WorkspaceSelectionState> callback) {
//		gwtWorkspaceSvc.loadSelectionState(callback);
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void getCurrentScope(AsyncCallback<String> callback) {
//		gwtWorkspaceSvc.getCurrentScope(callback);
//		
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void getPortalUsers(AsyncCallback<List<PortalUser>> callback) {
//		gwtWorkspaceSvc.getPortalUsers(callback);
//	}
//	
//	/**
//	 * {@inheritDoc}
//	 */
//	public void decomposeAquaMapsItem(String aquaMapsItemid) {
//		gwtWorkspaceSvc.decomposeAquaMapsItem(aquaMapsItemid, new AsyncCallback<Void>() {
//
//			public void onFailure(Throwable caught) {
//				Log.error("Error decomposing AquaMapsItem",caught);
//			}
//
//			public void onSuccess(Void result) {
//				refreshRoot();
//			}
//		});
//	}

}
