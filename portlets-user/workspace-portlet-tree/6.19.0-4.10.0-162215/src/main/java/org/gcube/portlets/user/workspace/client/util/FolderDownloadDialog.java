package org.gcube.portlets.user.workspace.client.util;

import javax.annotation.Nonnull;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent;
import org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService;
import org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceServiceAsync;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;
import org.realityforge.gwt.websockets.client.WebSocket;
import org.realityforge.gwt.websockets.client.WebSocketListener;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FolderDownloadDialog extends GCubeDialog implements WebSocketListener {
	private final GWTWorkspaceServiceAsync rpcWorkspaceService = (GWTWorkspaceServiceAsync) GWT.create(GWTWorkspaceService.class);

	public static final int WIDTH = 300;
	public static final int HEIGHT = 50;

	private final WebSocket webSocket = WebSocket.newWebSocketIfSupported();
	private final String username;
	private VerticalPanel topPanel = new VerticalPanel();
	private Icon loading = new Icon();
	Button close = new Button("Cancel Download");
	private HTML toShow = new HTML("Locating folder, please wait ...");
	private String webSocketURL;
	public FolderDownloadDialog(final FileDownloadEvent folder2Download, String username) {
		this.webSocket.setListener( this );
		this.username = username;
		setText("Preparing folder download");
	
		toShow.getElement().getStyle().setFontSize(14, Unit.PX);
		loading.setSpin(true);
		loading.setType(IconType.ROTATE_RIGHT);

		topPanel.add(toShow);
		topPanel.add(loading);
		topPanel.setPixelSize(WIDTH, HEIGHT);
		

		VerticalPanel bPanel = new VerticalPanel();
		bPanel.setWidth(WIDTH+"px");
		bPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		bPanel.add(close);
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();	
			}
		});
		topPanel.add(bPanel);
		add(topPanel);

		rpcWorkspaceService.getServletContextPath(Window.Location.getProtocol(), new AsyncCallback<String>() {

			@Override
			public void onSuccess(String servletContextPath) {
				webSocketURL = getWebSocketURL(servletContextPath);
				startZipping(webSocketURL, folder2Download.getItemIdentifier());			
			}

			@Override
			public void onFailure(Throwable caught) {
				loading.setIcon(IconType.EXCLAMATION_SIGN);
				loading.setSpin(false);
				toShow.setText("Error trying contact the server, please refresh this page and retry");
			}
		});		
	}

	private boolean startZipping(String webSocketURL, final String folderIdToZip) {
		if ( null == webSocket ) {
			Window.alert( "WebSocket not available!" );
		}

		webSocket.connect( webSocketURL );
		//allow some timet to connect
		Timer t = new Timer() {			
			@Override
			public void run() {
				webSocket.send(ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_DO_ZIP+":"+folderIdToZip+":"+username);			
			}
		};
		t.schedule(2000);
		return true;

	}

	private String getWebSocketURL(String servletContextPath)  {
		String moduleBaseURL = servletContextPath;
		return moduleBaseURL.replaceFirst( "http", "ws" ) + "/" + ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_SERVICE;
	}

	@Override
	public void onOpen(WebSocket webSocket) {	}

	@Override
	public void onClose(WebSocket webSocket, boolean wasClean, int code, String reason) { }

	@Override
	public void onMessage( @Nonnull final WebSocket webSocket, @Nonnull final String textData ) {
		switch (textData) {
		case ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ZIPPING:
			toShow.setText("Compressing folder, this could take some time ...");
			break;
		case ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_NOT_FOUND:
			toShow.setText("Could not locate the folder on server, please report this issue");
			loading.setIcon(IconType.EXCLAMATION_SIGN);
			loading.setSpin(false);
			webSocket.close();
			break;
		case ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_DURING_COMPRESSION:
			toShow.setText("An error occurred while compressing this folder, please report this issue");
			loading.setIcon(IconType.EXCLAMATION_SIGN);
			loading.setSpin(false);
			webSocket.close();
			break;		
		case ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_NOT_A_FOLDER:
			toShow.setText("An error occurred, the folderId is not a valid folder, please report this issue");
			loading.setIcon(IconType.EXCLAMATION_SIGN);
			loading.setSpin(false);
			webSocket.close();
			break;		
		case ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_PROTOCOL_ERROR_SESSION_EXPIRED:
			toShow.setText("It seems your session expired, please refresh the page and try again");
			loading.setIcon(IconType.EXCLAMATION_SIGN);
			loading.setSpin(false);
			webSocket.close();
			break;		
		default:
			//the thread zipping has finished, is sending the zipped filepath on the server
			toShow.setHTML("Compressing folder success, <a title=\""+textData+"\" href=\""+textData+"\">click here</a> to download");
			loading.setIcon(IconType.OK_SIGN);
			loading.setSpin(false);
			close.setText("Close");
			this.setModal(false);
			webSocket.close();
			break;
		}
	}

	@Override
	public void onMessage( @Nonnull final WebSocket webSocket, @Nonnull final ArrayBuffer data ) {}


	@Override
	public void onError(WebSocket webSocket) {
		toShow.setText("Error contacting the server socket, please refresh this page and retry");		
	}
	
	public AsyncCallback<WindowOpenParameter> downloadHandlerCallback = new AsyncCallback<WindowOpenParameter>() {

		@Override
		public void onFailure(Throwable caught) {
			new MessageBoxAlert("Error", caught.getMessage(), null);
		}

		@Override
		public void onSuccess(WindowOpenParameter windowOpenParam) {
			String params = "?"+windowOpenParam.getParameters();

			if(params.length()>1)
				params+="&";

			params+=ConstantsExplorer.REDIRECTONERROR+"="+windowOpenParam.isRedirectOnError();

			windowOpenParam.getBrowserWindow().setUrl(ConstantsExplorer.DOWNLOAD_WORKSPACE_FOLDER_SERVLET+params);
		}
	};
}
