package org.gcube.datatransfer.portlets.user.client;

import java.util.List;

import org.gcube.datatransfer.portlets.user.client.obj.AgentStat;
import org.gcube.datatransfer.portlets.user.client.obj.ComboForDestination;
import org.gcube.datatransfer.portlets.user.client.obj.ComboForSource;
import org.gcube.datatransfer.portlets.user.client.obj.Outcomes;
import org.gcube.datatransfer.portlets.user.client.obj.TreeOutcomes;
import org.gcube.datatransfer.portlets.user.client.obj.Uri;
import org.gcube.datatransfer.portlets.user.client.prop.AgentStatProperties;
import org.gcube.datatransfer.portlets.user.client.prop.OutcomesProperties;
import org.gcube.datatransfer.portlets.user.client.prop.TreeOutcomesProperties;
import org.gcube.datatransfer.portlets.user.client.prop.UriProperties;
import org.gcube.datatransfer.portlets.user.shared.SchedulerService;
import org.gcube.datatransfer.portlets.user.shared.SchedulerServiceAsync;
import org.gcube.datatransfer.portlets.user.shared.obj.BaseDto;
import org.gcube.datatransfer.portlets.user.shared.obj.CallingManagementResult;
import org.gcube.datatransfer.portlets.user.shared.obj.CallingSchedulerResult;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;
import org.gcube.datatransfer.portlets.user.shared.obj.TransferInfo;
import org.gcube.datatransfer.portlets.user.shared.prop.BaseDtoProperties;
import org.gcube.datatransfer.portlets.user.shared.prop.TransferInfoProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.TreeDropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.TimeField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class Common {
		//services
		public static final SchedulerServiceAsync schedulerService = GWT
				.create(SchedulerService.class);
		
		public static RPCalls rpCalls;
		public static Panels panels;
		public static Popups popups;
		public static Functionality functions;
		
		public static enum SOURCETYPE{
			TreeBased,
			Workspace,
			DataSource,
			URI,
			MongoDB,
			AgentSource;

		};
		public static enum DESTTYPE{
			TreeBased,
			MongoDBStorage,
			DataStorage,
			AgentDest,
		};
		// default values
		public static String specificationLink = "https://gcube.wiki.gcube-system.org/gcube/index.php/Web_Application_Scheduler_Portlet";
		public static String destinationFolder = "/devTest";
		public static String defaultResourceName = "testing"; // this specific value will
		// be used only in case of not retrieved session
		public static String defaultScope = "/gcube/devsec"; // this specific value will
		// be used only in case of not retrieved session
		public static boolean isAdmin = false;
		public static int minGenWidth = 800; // the minimum width of the portlet
		public static int totalWidth = 0;
		public static int minGenHeight = 450;// the minimum height of the portlet
		public static Timer focusTimer,resizeTimer;
		public static FocusPanel foc;
		public static PopupPanel loadingIconForSource,loadingIconForTarget,loadingIconForTransfers;

		// general
		public static FramedPanel panelGeneral;
		public static String agentHostname;
		public static List<String> inputUrls;
		public static boolean callingWorkspaceRoot;
		public static String idWorkspaceRoot;
		public static int autoId;
		public static Timer getTransferTimer, getTransferRepeatingTimer;

		//about agents
		public static enum FolderToRetrieve{MongoDBStorageFolder,AgentDestFolder,DataStorageFolder,NONE};
		public static boolean agentIsSelectedFromStatsPanel=false;
		// dialog box
		public static DialogBox dialogBoxGen;
		public static Button closeButton;

		// widget: layout
		public static VerticalPanel west, east;
		public static ContentPanel south;

		// widget: scheduler
		public static SimpleComboBox<String> frequency, typeOfSchedule, commands;
		public static TextField ResourceName, scope, transferId;
		public static FieldLabel dateLabel, timeLabel;
		public static CheckBox force, overwrite, unzip;
		public static DateField date;
		public static TimeField time;

		// widget: tool bar
		public static SimpleComboBox<String> comboAgent;
		public static ComboForSource combo1;
		public static ComboForDestination destCombo;
		public static TextField destinationF;
		public static LabelToolItem combo1Label, destLabel, destLabelF;
		public static TextButton hideSchedulerButton, showSchedulerButton, sendButton, makeNewFolder, deleteCurrentFolder, makeNewTreeSource, deleteCurrentTreeSource;
		public static TextButton showSourcesButton,showDestinationsButton;
		public static String lastCombo1Value, lastDestComboValue;
		public static ToolTipConfig destinationAnchor, sourceAnchor;

		// widget: list files
		public static TreeStore<BaseDto> sourceStore, targetStore, toBeTransferredStore;
		public static Tree<BaseDto, String> sourceTree, targetTree;
		public static TreeDropTarget<BaseDto> targetTreeDropTarget;
		public static HorizontalLayoutContainer vpListFiles;
		public static String lastSelectedFolderId;
		public static boolean neededParent;
		public static ToolBar toolbarSource,toolbarDestF;

		// pop up: type of source
		public static ListBox multiBoxSourceType;

		// pop up: type of destination
		public static ListBox multiBoxDestinationType;

		// pop up: operate specific transfer
		public static SimpleComboBox<String> commandsInGrid;
		public static TextField ResourceNameInGrid, scopeInGrid, transferIdInGrid;
		public static CheckBox forceInGrid;

		// pop up: create NewFolder in the destination tree
		public static TextField newFolderField;
		
		// pop up: create new source
		public static TextField newTreeSourceField;

		// pop up: Agent for Source
		public static String currentAgentSourcePath, selectedAgentSourcePath,
		lastSelectedAgentFolderName;
		public static String selectedAgentSource, selectedAgentSourcePort;

		//agent'node destination details
		public static String currentAgentDestinationPath,selectedAgentDestinationPath,
		lastSelectedAgentFolderDestName;
		public static String selectedAgentDestination, selectedAgentDestinationPort;

		// pop up: MongoDBStorage details for Source
		public static TextField smServiceNameSource, smServiceClassSource, smOwnerSource;
		public static SimpleComboBox<String> smAccessTypeSource;
		public static String currentMongoDBSourcePath, selectedMongoDBSourcePath,
		lastSelectedMongoDBFolderName;
		public static CheckBox defaultValuesStorage;

		// pop up: MongoDBStorage details
		public static TextField smServiceName, smServiceClass, smOwner;
		public static SimpleComboBox<String> smAccessType;
		public static String currentMongoDBDestinationPath, selectedMongoDBDestinationPath,
		lastSelectedMongoDBFolderDestName;
		public static CheckBox defaultValuesSource;
		
		// pop up: Workspace authentication
		public static PasswordField passWorkspace;

		// pop up: data storages
		public static List<String> dataStoragesList;
		public static String selectedDatastorageName;
		public static ListBox multiBoxDataStorages;
		public static String selectedDataStorageId;
		public static String currentDataStoragePath, selectedDatastoragePath,
		lastSelectedDatastorageFolderName;

		//pop up: TreeSources for destination
		public static String stringOfWriteSourceIDs;
		public static ListBox multiBoxTreeWriteSources;
		public static String selectedDestCollection;
		
		// pop up: URI's widget
		public static Grid<Uri> uriGrid;
		public static ListStore<Uri> storeForUris;
		public static GridEditing<Uri> editing;
		public static ColumnConfig<Uri, String> cc1Uris;
		public static ColumnConfig<Uri, String> cc2Uris;

		public static GridEditing<Uri> createGridEditing(Grid<Uri> editableGrid) {
			return new GridRowEditing<Uri>(editableGrid);
		}

		// pop up: data sources
		public static ListBox multiBoxDataSources;
		public static List<String> dataSourcesList;
		public static String selectedDataSourceId, selectedDatasourcePath,
		currentDataSourcePath,lastSelectedDataSourceFolderName;

		// pop up: agent statistics
		public static String stringOfAgentStats;
		public static List<AgentStat> listAgentStats;
		public static Grid<AgentStat> gridAgentStats;
		public static ListStore<AgentStat> storeAgentStats;
		public static ColumnModel<AgentStat> cmAgentStat;

		// json objects
		public static String callingManagementResultJson;
		public static String folderResSource, folderResDestination;
		public static String jsonWorkspace;

		// objects from json
		public static CallingManagementResult callingManagementResult;
		public static CallingSchedulerResult callingSchedulerResult;
		public static FolderDto folderSource,folderDestination;

		// object properties
		public static UriProperties uriProp = GWT.create(UriProperties.class);
		public static TransferInfoProperties transferInfoProp = GWT
				.create(TransferInfoProperties.class);
		public static AgentStatProperties agentStatProp = GWT
				.create(AgentStatProperties.class);
		public static OutcomesProperties outcomesProp = GWT
				.create(OutcomesProperties.class);
		public static TreeOutcomesProperties treeOutcomesProp = GWT
				.create(TreeOutcomesProperties.class);
		public static BaseDtoProperties baseDtoProp = GWT.create(BaseDtoProperties.class);

		// other
		public static ListStore<TransferInfo> store;
		public static Grid<TransferInfo> grid;
		public static boolean gettingUserAndScope = false;
		public static String stringOfAgents;
		public static ListBox multiBoxAgents;
		public static List<Outcomes> listOutcomes;
		public static List<TreeOutcomes> listTreeOutcomes;
		public static String printFolderString;
		public static ToolButton agentStats;
		public static ToolTipConfig agentStatsTooltip;
		public static Dialog forTreeElementDescription;
		public static SchedulerPortlet portlet;
}
