
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view.CKanLeaveFrame;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view.GCubeCkanDataCatalogPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GCubeCkanDataCatalog implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	public static final GcubeCkanDataCatalogServiceAsync service = GWT.create(GcubeCkanDataCatalogService.class);
	public static final String CKAN_LOGUT_SERVICE = GWT.getModuleBaseURL() + "gcubeckanlogout";

	private final String DIV_PORTLET_ID = "gCubeCkanDataCatalog";
	private CkanEventHandlerManager eventManager = new CkanEventHandlerManager();
	private CKanLeaveFrame frame;
	public static final String GET_PATH_PARAMETER = "path";
	public static final String GET_QUERY_PARAMETER = "query";
	public static final String GCUBE_CKAN_IFRAME = "gcube-ckan-iframe";

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		performLogoutOnBrowserClosedEvent(CKAN_LOGUT_SERVICE);

		/*Button butt = new Button("Click Me");

		butt.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				performsLogout(CKAN_LOGUT_SERVICE);
			}
		});

		RootPanel.get(DIV_PORTLET_ID).add(butt);*/

		/*Window.addWindowClosingHandler(new Window.ClosingHandler() {

			@Override
			public void onWindowClosing(ClosingEvent closingEvent) {
				// invoking logout
				performLogout();
			}
		});*/

		GCubeCkanDataCatalogPanel panel = new GCubeCkanDataCatalogPanel(RootPanel.get(DIV_PORTLET_ID), eventManager.getEventBus());
		eventManager.setPanel(panel);

		frame = new CKanLeaveFrame();
		DOM.appendChild(RootPanel.getBodyElement(), frame.getElement());


	}

//	public static native void performsLogout(String logoutService)/*-{
//
////		var frame = $wnd.frames['i-frame-logout'];
////		console.log(frame);
////		frame.src = logoutService;
//	//	frame.contentWindow.location.reload();
//
//		var xhttp = new XMLHttpRequest();
//		xhttp.onreadystatechange = function() {
//		    if (this.readyState == 4 && this.status == 200) {
//		     console.log("OK");
//		    }
//		  };
//		  xhttp.open("GET", logoutService, false);
//		  xhttp.send();
//	}-*/;


	/**
	 * Perform logouton browser closed event.
	 *
	 * @param logoutService the logout service
	 */
	public static native void performLogoutOnBrowserClosedEvent(String logoutService)/*-{

		var validNavigation = false;

		function wireUpEvents() {
	  		var dont_confirm_leave = 1; //set dont_confirm_leave to 1 when you want the user to be able to leave without confirmation
	  		var leave_message = 'You sure you want to leave?'
	  		function goodbye(e) {
	    		if (!validNavigation) {
	       			//PERFORMS A SYNCHRONOUS LOGOUT
	       		    var xhttp = new XMLHttpRequest();
					xhttp.onreadystatechange = function() {
					    if (this.readyState == 4 && this.status == 200) {
					     console.log("OK");
					    }
					  };
					  xhttp.open("GET", logoutService, false);
					  xhttp.send();

	      			 if (dont_confirm_leave!==1) {
	        			if(!e) e = window.event;
				        //e.cancelBubble is supported by IE - this will kill the bubbling process.
				        e.cancelBubble = true;
				        e.returnValue = leave_message;
				        //e.stopPropagation works in Firefox.
	        			if (e.stopPropagation) {
				          e.stopPropagation();
				          e.preventDefault();
	        			}
			        //return works for Chrome and Safari
			        return leave_message;
	      			}
	    		}
	  		}

		  window.onbeforeunload=goodbye;

		  // Attach the event keypress to exclude the F5 refresh
		  $wnd.$(document).bind('keypress', function(e) {
		    if (e.keyCode == 116){
		      validNavigation = true;
		      console.log("keypress: "+validNavigation);
		    }
		  });

		  // Attach the event click for all links in the page
		  $wnd.$("a").bind("click", function() {
		    validNavigation = true;
		    console.log("click: "+validNavigation);
		  });

		  // Attach the event submit for all forms in the page
		  $wnd.$("form").bind("submit", function() {
		    validNavigation = true;
		    console.log("form: "+validNavigation);
		  });

		  // Attach the event click for all inputs in the page
		 $wnd.$("input[type=submit]").bind("click", function() {
		    validNavigation = true;
		    console.log("submit: "+validNavigation);
		  });

		}

		// Wire up the events as soon as the DOM tree is ready
		$wnd.$(document).ready(function() {
		  wireUpEvents();
		});

	}-*/;
}
