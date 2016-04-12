package org.gcube.portlets.user.results.client.util;

import java.util.List;
import java.util.TreeMap;

import org.gcube.portlets.user.results.client.ResultsDisplayer;
import org.gcube.portlets.user.results.client.constants.StringConstants;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.dialogBox.GenericXMLViewerPopup;
import org.gcube.portlets.user.results.client.dialogBox.MetadataViewerPopup;
import org.gcube.portlets.user.results.client.dialogBox.URLContentViewerPopup;
import org.gcube.portlets.user.results.client.model.ActionType;
import org.gcube.portlets.user.results.client.panels.RecordsPanel;
import org.gcube.portlets.user.results.shared.GenericTreeRecordBean;
import org.gcube.portlets.user.results.shared.ObjectType;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Panagiota Koltsida, NKUA
 */
public class MyCommand implements Command {
	/**
	 * mode
	 */
	ActionType selectedOption;
	/**
	 * objectURI
	 */
	protected String objectURI;
	/**
	 * collectionId
	 */
	protected String collectionId;
	/**
	 * title
	 */
	protected String title;
	/**
	 * payload
	 */
	protected String payload;

	/**
	 * counter
	 */
	protected static int counter = 0;

	private Controller controller;

	private String username;

	protected String docURI;

	/**
	 * 
	 * @param selectedOption
	 * @param OID
	 * @param colId
	 * @param metadataCollectionId
	 * @param title
	 * @param username
	 */
	public MyCommand(Controller controller, ActionType selectedOption, String OID, String colId, String docURI, String title, String username)
	{
		this.controller = controller;
		this.selectedOption = selectedOption;
		this.objectURI = OID;
		this.collectionId = colId;
		this.title = title;
		this.username = username;
		this.docURI = docURI;
	}

	public static native String portalURL()/*-{
	 return $wnd.location.href;
	 }-*/;

	/**
	 * @param txt the text the should exist in the &lt;a&gt;&lt;/a&gt; statement
	 * @param j j 
	 * 
	 * @return the coresponding URL (form href attribute of "a" element
	 */
	public static native String anchor(String txt, int j)/*-{
	 var x = $doc.getElementsByTagName("a");
	 for(i=0;i<x.length;i++)
	 {
	 if(x[i].innerHTML.toLowerCase().indexOf(txt,0) != -1)
	 {
	 	if(j==0)
	 		return x[i].href;
	 	else
	 		j--;
	 }
	 }
	 return "";
	 }-*/;


	/** (non-Javadoc)
	 * @see com.google.gwt.user.client.Command#execute()
	 */
	public void execute() {
		switch(selectedOption)
		{
		case VIEW_CONTENT:
			//View Content for tree objects. When there is no specific visualizer display an info message
			if(objectURI.startsWith("http") && objectURI.contains("/tree/")) {
				AsyncCallback<GenericTreeRecordBean> callback = new AsyncCallback<GenericTreeRecordBean>() {

					public void onFailure(Throwable caught) {
						//ResultsDisplayer.unmask();
						RecordsPanel.get().loading.hide();
						Window.alert("Failed to retrieve the payload. Please try again");
					}

					public void onSuccess(GenericTreeRecordBean result) {
						//ResultsDisplayer.unmask();
						RecordsPanel.get().loading.hide();
						if (result != null) {
							// Use the oai_dc viewer
							if (result.getType().equals(ObjectType.OAI) || result.getType().equals(ObjectType.FIGIS)) {
								AsyncCallback<TreeMap<String, List<String>>> contentURLsCallback = new AsyncCallback<TreeMap<String,List<String>>>() {

									@Override
									public void onFailure(Throwable caught) {
										//ResultsDisplayer.unmask();
										RecordsPanel.get().loading.hide();
										Window.alert("Failed to retrieve the content of this object. Please try again");
									}

									@Override
									public void onSuccess(TreeMap<String, List<String>> result) {
										//ResultsDisplayer.unmask();
										RecordsPanel.get().loading.hide();
										if (result != null)
											new URLContentViewerPopup(result, true);
										else
											Window.alert("No content is available for this object");

									}
								};controller.getNewresultset().getModel().getResultService().getContentURLs(result, contentURLsCallback);
								//ResultsDisplayer.mask();
								RecordsPanel.get().loading.show();
							}
							else
								Window.alert("Cannot find a suitable visualizer to display the content of this object");
						}
						else
							Window.alert("Warning: Payload for this result is missing, nothing to show");
					}			
				};		
				controller.getNewresultset().getModel().getResultService().getObjectInfo(objectURI, callback);
				//ResultsDisplayer.mask();
				RecordsPanel.get().loading.show();
			}
			else if (objectURI.equals(""))
				Window.alert("Information is missing for this object. Cannot retrieve its content");

			else if (objectURI.startsWith("http"))
				Window.open(objectURI, "name"+counter, "width="+ StringConstants.POPUP_WINDOW_WIDTH + ",height=" + StringConstants.POPUP_WINDOW_HEIGHT + " ,toolbar=no, location=no,status=no,menubar=no,scrollbars=yes,resizable=yes");
			else
				Window.alert("Could not find a suitable viewer for this type of object");
			counter++;
			break;
			//		case SAVE_CONTENT:
			//			if (objectURI.startsWith("http"))
			//				Window.open(objectURI, "name"+counter, "width="+ StringConstants.POPUP_WINDOW_WIDTH + ",height=" + StringConstants.POPUP_WINDOW_HEIGHT + " ,toolbar=true, location=yes,status=no,menubar=yes,scrollbars=yes,resizable=yes");
			//			else
			//				Window.open("/aslHttpContentAccess/ContentViewer?documentURI=" + docURI+ "&username=" + username + "&save=true", "_blank", "");
			//			break;
		case VIEW_METADATA:
			if(objectURI.startsWith("http") && objectURI.contains("/tree/")) {
				AsyncCallback<GenericTreeRecordBean> getObjectInfoCalback = new AsyncCallback<GenericTreeRecordBean>() {

					@Override
					public void onFailure(Throwable caught) {
						RecordsPanel.get().loading.hide();
						//ResultsDisplayer.unmask();
						Window.alert("Failed to retrieve the object's metadata. Please try again");
					}

					@Override
					public void onSuccess(GenericTreeRecordBean result) {
						ResultsDisplayer.unmask();
						if (result != null) {
							// Use the Metadata Viewer dialog box
							if (result.getType().equals(ObjectType.OAI) || result.getType().equals(ObjectType.FIGIS)) {
								AsyncCallback<String> metadataCallback = new AsyncCallback<String>() {

									@Override
									public void onFailure(Throwable caught) {
										//ResultsDisplayer.unmask();
										RecordsPanel.get().loading.hide();
										Window.alert("Failed to retrieve the object's metadata. Please try again");
									}

									@Override
									public void onSuccess(String result) {
										//ResultsDisplayer.unmask();
										RecordsPanel.get().loading.hide();
										if (result != null)
											new MetadataViewerPopup(result, 600, 450);
									}
								};controller.getNewresultset().getModel().getResultService().transformMetadata(result.getPayload(), result.getType(), metadataCallback);
								//ResultsDisplayer.mask();
								RecordsPanel.get().loading.show();
							}
							// Use the Generic XML Viewer for all other types
							else
								new GenericXMLViewerPopup(result.getPayload(), "xmlviewer", true);
						}
						else
							Window.alert("Metadata are not available for this object");
					}
				};controller.getNewresultset().getModel().getResultService().getObjectInfo(objectURI, getObjectInfoCalback);
				//ResultsDisplayer.mask();
				RecordsPanel.get().loading.show();
			}
			else 
				Window.alert("No metadata are available for this type of object");

			counter++;
			break;
		default:
			Window.alert("No command was selected...");	
		}				
	}
}