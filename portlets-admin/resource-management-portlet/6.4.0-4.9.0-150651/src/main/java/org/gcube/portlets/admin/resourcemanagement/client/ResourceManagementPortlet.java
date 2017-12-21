/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ResourceManagementPortlet.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client;


import org.gcube.portlets.admin.ishealthmonitor.client.dialog.ISMonitor;
import org.gcube.portlets.admin.resourcemanagement.client.forms.genericresources.DeployVirtualCollection;
import org.gcube.portlets.admin.resourcemanagement.client.remote.ProxyRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Commands;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Messages;
import org.gcube.portlets.admin.resourcemanagement.client.utils.OpCommands;
import org.gcube.portlets.admin.resourcemanagement.client.views.resourcedetails.ResourceDetailsPanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsolePanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.dialogs.ExtendedMessageBox;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.DetachablePanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.MainPanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.UIIdentifiers;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.WidgetsRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.viewport.MainContainer;
import org.gcube.portlets.admin.resourcesweeper.client.dialog.SweeperDialog;
import org.gcube.resourcemanagement.support.client.Resource_support;
import org.gcube.resourcemanagement.support.client.events.SetScopeEvent;
import org.gcube.resourcemanagement.support.client.events.SetScopeEventHandler;
import org.gcube.resourcemanagement.support.client.utils.CurrentStatus;
import org.gcube.resourcemanagement.support.client.utils.LocalStatus;
import org.gcube.resourcemanagement.support.client.utils.StatusHandler;
import org.gcube.resourcemanagement.support.shared.exceptions.InvalidParameterException;
import org.gcube.resourcemanagement.support.shared.operations.SupportedOperations;
import org.gcube.resourcemanagement.support.shared.types.RunningMode;
import org.gcube.resourcemanagement.support.shared.types.UserGroup;
import org.gcube.resourcemanagement.support.shared.util.Configuration;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ResourceManagementPortlet implements EntryPoint {

	public static final String CONTAINER_DIV = "MyUniqueDIV";

	private final HandlerManager eventBus = new HandlerManager(null);

	private void printStatus(final CurrentStatus status) {
		ConsoleMessageBroker.trace(this, "User: " + status.getCurrentUser());
		ConsoleMessageBroker.trace(this, "Scope: " + status.getCurrentScope());
		ConsoleMessageBroker.trace(this, "Credentials: " + status.getCredentials());
		ConsoleMessageBroker.trace(this, "Running Mode: " + status.getRunningMode()); 
	}
	/**
	 * events binder
	 */
	private void bind() {
		//set the eventbus to the support common classes
		new Resource_support(eventBus);

		eventBus.addHandler(SetScopeEvent.TYPE, new SetScopeEventHandler() {
			@Override
			public void onSetScope(SetScopeEvent event) {
				Commands.setStatusScope(event.getScope());
			}
		});  
	}
	/**
	 * This is the entry point method.
	 */
	public final void onModuleLoad() {
		//for event handling
		bind();

		MainContainer vp = buildUI();
		RootPanel.get(CONTAINER_DIV).add(vp);
		Commands.mask("Waiting servlet initialization", UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);

		ProxyRegistry.getProxyInstance().initStatus(new AsyncCallback<CurrentStatus>() {
			// Received status
			public void onSuccess(final CurrentStatus result) {
				StatusHandler.setStatus(result);
				printStatus(result);

				Commands.mask("Contacting the Infrastructure, please wait", UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);

				// loads the scopes
				ProxyRegistry.getProxyInstance().initScopes(true, new AsyncCallback<Void>() {
					public void onSuccess(final Void result) {
						// Now that both scopes and user credentials have been loaded
						// the menu and statusbar can be built.
						buildMenu();
						buildStatusBar();
						Commands.mask("Loading infrastructure scopes", UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
						Commands.doGetAvailableScopes(this);

						if (StatusHandler.getStatus().getCurrentScope() != null) {
							Commands.setStatusScope(StatusHandler.getStatus().getCurrentScope());
							Commands.doLoadResourceTree(this, StatusHandler.getStatus().getCurrentScope());
						}
					}
					public void onFailure(final Throwable caught) {
					}
				});


			}
			public void onFailure(final Throwable caught) {
				MessageBox.info("Failure", "cannot initialize servlet", null);
			}
		});
	}

	private MainContainer buildUI() {
		MainContainer viewport = new MainContainer();
		BorderLayout bl = new BorderLayout();
		viewport.setLayout(bl);
		WidgetsRegistry.registerWidget(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID, viewport);

		MainPanel northPanel = new MainPanel(56, "Main App", LayoutRegion.NORTH) {
			@Override
			public void init() {
				ConsoleMessageBroker.log(this, "Initializing north panel");
				this.setCollapsible(false);
				this.setMargins(new Margins(5, 5, 0, 5));

				this.setSplit(false);
				this.hideHeader();
			}
		};

		MainPanel eastPanel = new MainPanel(150, "Pinned Resources", LayoutRegion.EAST) {
			@Override
			public void init() {
				ConsoleMessageBroker.log(this, "Initializing east panel");
				this.setCollapsible(true);
				this.setMargins(new Margins(5, 5, 5, 0));
				this.getContainer().setScrollMode(Scroll.AUTOY);
				this.getContainer().setHeight(100);
				((ContentPanel) this.getContainer()).getBody().setStyleName("taskbar-pattern");
			}
		};

		MainPanel westPanel = new MainPanel(200, "Resources", LayoutRegion.WEST) {
			@Override
			public void init() {
				ConsoleMessageBroker.log(this, "Initializing west panel");
				this.setCollapsible(false);
				this.setMargins(new Margins(5, 0, 5, 5));
				Text widget = new Text();
				widget.setId("res-details-widget-fake");
				widget.setStyleName("left-panel-tree-background");
				this.getContainer().setLayout(new AccordionLayout());
				ContentPanel cp = new ContentPanel();
				cp.setAnimCollapse(false);
				cp.setHeading("Resources");
				cp.setLayout(new FitLayout());
				cp.add(widget);
				this.getContainer().add(cp);
			}
		};

		MainPanel southPanel = new MainPanel(180, "Console (debug mode)", LayoutRegion.SOUTH) {
			@Override
			public void init() {
				ConsoleMessageBroker.log(this, "Initializing south panel");
				this.setCollapsible(false);
				this.setSplit(true);
				this.setMargins(new Margins(0, 0, 0, 0));

				// Inserts the console
				this.getContainer().setLayout(new FitLayout());
				//this.getContainer().setScrollMode(Scroll.AUTOY);
				try {
					// Creates a detachable panel in which a console will be inserted
					DetachablePanel consolePanel = new DetachablePanel(
							this.getContainer(),
							"Console (debug mode)",
							UIIdentifiers.CONSOLE_COMPONENT_ID ,
							false);
					ConsolePanel console = new ConsolePanel(consolePanel);
					WidgetsRegistry.registerElem(UIIdentifiers.CONSOLE_WIDGET_ID, console);
					// initially the console will be hidden
					Commands.showHideConsole();
				} catch (InvalidParameterException e) {
					e.printStackTrace();
				}
			}
		};


		MainPanel centerPanel = new MainPanel(LayoutRegion.CENTER) {
			@Override
			public void init() {
				this.getContainer().setLayout(new FitLayout());
				this.hideHeader();
				ConsoleMessageBroker.log(this, "Initialiting center panel");
				this.setMargins(new Margins(5));
				ResourceDetailsPanel resourceDetailGrid = new ResourceDetailsPanel();
				this.add(resourceDetailGrid.getWidget(), false);
				WidgetsRegistry.registerElem(UIIdentifiers.RESOURCE_DETAIL_GRID_CONTAINER_ID, resourceDetailGrid);
			}
		};

		viewport.addPanel(UIIdentifiers.GLOBAL_MENU_CONTAINER_PANEL, northPanel);
		viewport.addPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL, westPanel);
		viewport.addPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL, centerPanel);
		viewport.addPanel(UIIdentifiers.TASKBAR_PANEL, eastPanel);
		viewport.addPanel(UIIdentifiers.CONSOLE_PANEL_ID, southPanel);

		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(final ResizeEvent event) {
				updateSize();
			}
		});

		// updates for the first time
		updateSize();

		//viewport.setAutoHeight(false);
		//viewport.setAutoWidth(false);
		return viewport;
	}

	private void updateSize() {
		RootPanel workspace = RootPanel.get(CONTAINER_DIV);

		if (workspace == null) {
			return;
		}

		int topBorder = workspace.getAbsoluteTop();
		int leftBorder = workspace.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootHeight = Window.getClientHeight() - topBorder - 4;
		int rootWidth = (Window.getClientWidth() - 2 * leftBorder - rightScrollBar);

		ConsoleMessageBroker.debug(this, "New workspace dimension Height: " + rootHeight + " Width: " + rootWidth);

		MainContainer viewport = (MainContainer) WidgetsRegistry.getWidget(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);

		viewport.setHeight(rootHeight);
		viewport.setWidth(rootWidth);
	}

	/**
	 * Internally used to build up the main panel menu.
	 * @return the menu
	 */
	private void buildMenu() {

		/***********************************************************
		 * MENU - OPTIONS
		 **********************************************************/
		Menu optionsMenu = new Menu();

		CheckMenuItem highlightInvalid = new CheckMenuItem("Highlight invalid fields") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Commands.getResourceDetailPanel().toggleHighlightInvalidFields();
			}
		};
		highlightInvalid.setChecked(false);
		optionsMenu.add(highlightInvalid);

		CheckMenuItem showConsole = new CheckMenuItem("Show Console") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Commands.showHideConsole();
			}
		};
		showConsole.setChecked(false);
		// Checks that this menu item is permitted to the current user
		Commands.evaluateCredentials(
				showConsole,
				UserGroup.ADMIN, UserGroup.DEBUG);
		optionsMenu.add(showConsole);

		CheckMenuItem superUser = new CheckMenuItem("Super User Mode") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);

				if (this.isChecked()) {
					ConsoleMessageBroker.info(this, "Going in super user mode");



					ExtendedMessageBox.password("Super User Authentication", new Listener<MessageBoxEvent>() {
						public void handleEvent(final MessageBoxEvent be) {
							String pwd = be.getValue();
							Commands.mask("Veryfing super user mode password, please wait ...", UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
							ProxyRegistry.getProxyInstance().enableSuperUserMode(pwd, new AsyncCallback<Boolean>() {
								@Override
								public void onFailure(Throwable caught) {
									MessageBox.alert("Server error", "Cannot reach server", null);	
								}
								@Override
								public void onSuccess(Boolean result) {
									if (result) {
										Commands.doSetSuperUser(true);
									} else {
										MessageBox.alert("Wrong code", "Invalid Password entered.", null);
										setChecked(false);
										Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
									}
									
								}
							});
							
						}
					});

					//reloadMenu();
				} else {
					ConsoleMessageBroker.info(this, "Going in debug mode");
					Commands.doSetSuperUser(false);
					//reloadMenu();

				}
			}
		};
		// Checks that this menu item is permitted to the current user
		Commands.evaluateCredentials(
				superUser,
				UserGroup.DEBUG);
		superUser.setChecked(StatusHandler.getStatus().getCredentials() == UserGroup.ADMIN);
		optionsMenu.add(superUser);


		CheckMenuItem useCache = new CheckMenuItem("Use Remote Cache") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				StatusHandler.getStatus().setUseCache(this.isChecked());
				ProxyRegistry.getProxyInstance().setUseCache(this.isChecked(),
						new AsyncCallback<Void>() {
					public void onSuccess(final Void result) {
					}
					public void onFailure(final Throwable caught) {
					}
				});
			}
		};
		useCache.setChecked(StatusHandler.getStatus().useCache());
		optionsMenu.add(useCache);

		CheckMenuItem openProfileOnLoad = new CheckMenuItem("Open Profile onLoad") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Configuration.openProfileOnLoad = !Configuration.openProfileOnLoad;
			}
		};
		openProfileOnLoad.setChecked(false);
		optionsMenu.add(openProfileOnLoad);


		CheckMenuItem allowMultipleProfiles = new CheckMenuItem("Allow Multiple Profiles") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Configuration.allowMultipleProfiles = !Configuration.allowMultipleProfiles;
			}
		};
		allowMultipleProfiles.setChecked(false);
		optionsMenu.add(allowMultipleProfiles);


		/***********************************************************
		 * MENU - SEARCH
		 **********************************************************/
		Menu searchMenu = new Menu();
		MenuItem getResource = new MenuItem("Get Resource By ID") {
			protected void onClick(final ComponentEvent be) {
				OpCommands.doGetResourceByID();
			};
		};
		getResource.setIconStyle("resources-icon");
		Commands.evaluateCredentials(
				getResource,
				SupportedOperations.SERVICE_GET_RESOURCE_BY_ID.getPermissions());
		searchMenu.add(getResource);

		MenuItem getReport = new MenuItem("Get Report") {
			protected void onClick(final ComponentEvent be) {
				OpCommands.doGetDeployReport();
			};
		};
		getReport.setIconStyle("getreport-icon");
		Commands.evaluateCredentials(
				getReport,
				SupportedOperations.SERVICE_GET_REPORT.getPermissions());
		searchMenu.add(getReport);

		/***********************************************************
		 * MENU - TOOLS
		 **********************************************************/
		Menu toolsMenu = new Menu();

		MenuItem testIS = new MenuItem("Check IS Health") {
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				if (LocalStatus.getInstance().getAvailableScopes() == null || LocalStatus.getInstance().getAvailableScopes().isEmpty()) {
					MessageBox.info("Sorry", "This functionality is not available in logged-in mode.", null);       
				} else {
					GWT.runAsync(ISMonitor.class, new RunAsyncCallback() {
						@Override
						public void onSuccess() {
							ISMonitor.pingIS();
						}
						public void onFailure(Throwable reason) {
							Window.alert("There are networks problem, please check your connection.");              
						}
					});
				}
			};
		};
		testIS.setIconStyle("is-icon");
		Commands.evaluateCredentials(
				testIS,
				SupportedOperations.SERVICE_GET_RESOURCE_BY_ID.getPermissions());
		toolsMenu.add(testIS);


		MenuItem cleanGHN = new MenuItem("Resource Sweeper") {
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				if (SupportedOperations.SWEEP_GHN.isAllowed(StatusHandler.getStatus().getCredentials())) {
					GWT.runAsync(SweeperDialog.class, new RunAsyncCallback() {
						@Override
						public void onSuccess() {
							String currentScope = StatusHandler.getStatus().getCurrentScope();
							new SweeperDialog(currentScope);
						}

						public void onFailure(Throwable reason) {
							Window.alert("There are networks problem, please check your connection.");              
						}
					});
				}
				else 
					MessageBox.alert("Resource Sweeper", "You are not allowed to execute this operation", null);

			};
		};
		cleanGHN.setIconStyle("sweeper-icon");
		toolsMenu.add(cleanGHN);

		MenuItem createSub = new MenuItem("Create");
		createSub.setIconStyle("new-icon");
		Menu createMnu = new Menu();

		// Create Generic Resource
		MenuItem createGR = new MenuItem("Generic Resource") {
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				OpCommands.doOpenGenericResourceForm();
			};
		};
		
		createGR.setIconStyle("genericresource-icon");
		createMnu.add(createGR);

		// Create Service Endpoint (former Runtime Resource)
		MenuItem createSE = new MenuItem("Service Endpoint") {
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				OpCommands.doOpenServiceEndpointForm();
			};
		};
		createSE.setIconStyle("runtimeresource-icon");
		createMnu.add(createSE);

		// Create Generic Resource
		MenuItem deployVirtualCollection = new MenuItem("Instantiate Virtual Collections") {
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				if (SupportedOperations.GENERIC_RESOURCE_CREATE.isAllowed(StatusHandler.getStatus().getCredentials())) {
					new DeployVirtualCollection().show();
				} else {
					MessageBox.alert("Instantiate Virtual Collections", "You are not allowed to execute this operation", null);
				}
			};
		};
		deployVirtualCollection.setIconStyle("install-icon");
		createMnu.add(deployVirtualCollection);

		createSub.setSubMenu(createMnu);
		Commands.evaluateCredentials(
				createSub,
				SupportedOperations.CREATE_MENU_SHOW.getPermissions());
		toolsMenu.add(createSub);


		toolsMenu.add(new SeparatorMenuItem());

		/***********************************************************
		 * SUBMENU - OPTIONS->REFRESH
		 **********************************************************/
		MenuItem sub = new MenuItem("Refresh");
		sub.setIconStyle("refresh-icon");
		Menu refresh = new Menu();
		refresh.add(new MenuItem("Resource Navigation") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Commands.refreshResourceTree();
			}
		});
		refresh.add(new MenuItem("Resource Details Grid") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Commands.refreshResourceGrid();
			}
		});
		refresh.add(new MenuItem("Console (UI)") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Commands.refreshConsole();
			}
		});
		refresh.add(new MenuItem("Resource Details (UI)") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Commands.refreshResourceDetails();
			}
		});
		refresh.add(new MenuItem("Desktop (UI)") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Commands.refreshViewport();
			}
		});
		sub.setSubMenu(refresh);
		toolsMenu.add(sub);

		MenuItem emptyCache = new MenuItem("Empty Cache") {
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				ProxyRegistry.getProxyInstance().emptyCache(new AsyncCallback<Void>() {
					public void onSuccess(final Void result) {
						Commands.showPopup("Empty cache", "The remote cache has been cleaned");
					}
					public void onFailure(final Throwable caught) {
						Commands.showPopup("Empty cache error", "The remote cache has not been cleaned");
					}
				});
			};
		};
		emptyCache.setIconStyle("clear-icon");
		toolsMenu.add(emptyCache);


		/***********************************************************
		 * MENU - ABOUT
		 **********************************************************/
		Menu helpMenu = new Menu();
		MenuItem aboutMnuItem = new MenuItem("About") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Dialog dlg = new Dialog();
				dlg.setHeading("About - Resource Management");
				dlg.addText("<br/>This software is part of the gCube Project.<br/>" +
						"Site: <a href=\"http://www.gcube-system.org/\">http://www.gcube-system.org/</a>" +
						"<p>The gCube/gCore software is licensed as Free Open Source software " +
						"conveying to the EUPL (<a href=\"http://ec.europa.eu/idabc/eupl\">http://ec.europa.eu/idabc/eupl</a>).</p><br/>" +
						"<p>The software and documentation is provided by its authors/distributors " +
						"\"as is\" and no expressed or " +
						"implied warranty is given for its use, quality or fitness for a " +
						"particular case.</p>" +
						"" +
						"<p><b>Issues</b> can be submitted <a href=\"https://support.d4science.research-infrastructures.eu/\">here</a>.</p>" +
						"<p><b>Notice:</b> specify this Component: <br/><i>Repository Path: /org/gcube/portlets/admin/resource-management</i></p>" +
						"<br/>This software was built over the <a href=\"http://gcube.wiki.gcube-system.org/gcube/index.php/Featherweight_Stack\">gCube Featherweight Stack (FWS) </a> and <a href=\"http://www.gwtproject.org\">Google Webtool Kit (GWT)</a> technologies.<br/>" +
						"<br/>");

				dlg.setClosable(true);
				dlg.setModal(true);
				dlg.setHideOnButtonClick(true);
				dlg.show();
			}
		};
		aboutMnuItem.setIconStyle("about-icon");
		helpMenu.add(aboutMnuItem);

		MenuBar mb = new MenuBar();
		mb.add(new MenuBarItem("Tools", toolsMenu));
		mb.add(new MenuBarItem("Options", optionsMenu));
		mb.add(new MenuBarItem("Search", searchMenu));
		mb.add(new MenuBarItem("Help", helpMenu));


		MainPanel menuPanel = WidgetsRegistry.getPanel(UIIdentifiers.GLOBAL_MENU_CONTAINER_PANEL);
		WidgetsRegistry.registerWidget(UIIdentifiers.GLOBAL_MENUBAR_ID, mb);
		menuPanel.add(mb, true);
	}

	private void buildStatusBar() {
		MainPanel menuPanel = WidgetsRegistry.getPanel(UIIdentifiers.GLOBAL_MENU_CONTAINER_PANEL);
		ToolBar statusbar = new ToolBar();
		statusbar.add(new SeparatorToolItem());

		if (StatusHandler.getStatus().getRunningMode() != RunningMode.PORTAL) {
			Button btnScope = new Button("Available Scopes");
			btnScope.setMenu(new Menu());
			WidgetsRegistry.registerWidget(UIIdentifiers.BUTTON_AVAILABLE_SCOPES_ID, btnScope);
			// Checks that this menu item is permitted to the current user
			statusbar.add(btnScope);
		} else {
			statusbar.add(new Label("Current role: " + StatusHandler.getStatus().getCredentials()));
		}

		//statusbar.add(new FillToolItem());
		statusbar.add(new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
		ProgressBar progressStatus = new ProgressBar();
		progressStatus.setWidth(200);
		progressStatus.updateText("Updating Infrastructure...");
		statusbar.add(progressStatus);
		progressStatus.hide();
		WidgetsRegistry.registerWidget(UIIdentifiers.STATUS_PROGRESS_BAR_ID, progressStatus);

		statusbar.add(new FillToolItem());
		statusbar.add(new Label("Current Scope:&nbsp;&nbsp;"));
		Status scopeInfo = new Status();
		scopeInfo.setWidth(350);
		scopeInfo.setText(Messages.NO_SCOPE_SELECTED);
		scopeInfo.setBox(true);
		statusbar.add(scopeInfo);
		WidgetsRegistry.registerWidget(UIIdentifiers.STATUS_SCOPE_INFO_ID, scopeInfo);

		statusbar.add(new Label("&nbsp;&nbsp;Loaded Resources:&nbsp;&nbsp;"));
		Status loadedResources = new Status();
		loadedResources.setWidth(35);
		loadedResources.setText("0");
		loadedResources.setBox(true);
		statusbar.add(loadedResources);
		WidgetsRegistry.registerWidget(UIIdentifiers.STATUS_LOADED_RESOURCES_ID, loadedResources);

		menuPanel.add(statusbar, false);

		menuPanel.getContainer().setBorders(false);
		menuPanel.getContainer().setShadow(true);
		WidgetsRegistry.registerWidget(UIIdentifiers.GLOBAL_STATUS_BAR_ID, statusbar);
	}


}
