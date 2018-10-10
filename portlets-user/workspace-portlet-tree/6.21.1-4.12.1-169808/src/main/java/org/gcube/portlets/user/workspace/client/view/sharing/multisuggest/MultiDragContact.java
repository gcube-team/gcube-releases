package org.gcube.portlets.user.workspace.client.view.sharing.multisuggest;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.model.ExtendedInfoContactModel;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxConfirm;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.QuickTip;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * The Class MultiDragContact.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 1, 2016
 */
public class MultiDragContact extends Dialog {

	/**
	 *
	 */
	private static final int HEIGHT_CONTAINER_GRID = 395;
	/**
	 *
	 */
	private static final int HEIGHT_CONTAINER_TEXT_AREA = 72;
	private static final int HEIGHT_TEXT_AREA = 55;
	private static final int WIDTH_CP = 597;
//	private static final int HEIGHT_DIALOG = 542;
	private static final int HEIGHT_DIALOG = 580;
	private static final int WIDTH_DIALOG = 630;
//	private static final int WIDTH_DIALOG = 625;
	private static final int HEIGHT_GRID = 310;

	private GridDropTarget dropSource;
	private GridDropTarget dropTarget;
	private ListStore<ExtendedInfoContactModel> storeSource = new ListStore<ExtendedInfoContactModel>();
	private ListStore<ExtendedInfoContactModel> storeTarget = new ListStore<ExtendedInfoContactModel>();
	private TextArea textAreaAlreadyShared;
	private Grid<ExtendedInfoContactModel> gridAllContacts;
	private Grid<ExtendedInfoContactModel> gridShareWith;
	private ColumnConfig columnConfigDisplayName;
	private Button buttonSelectedLeft;
	private Button buttonSelectedRight;
	private Button buttonAllRight;
	private Button buttonAllLeft;

  /**
   * Instantiates a new multi drag contact.
   */
  public MultiDragContact() {
	setStyleAttribute("margin", "10px");
    setSize(WIDTH_DIALOG, HEIGHT_DIALOG);
    setHeading("Group dragging contacts");
    setResizable(false);
    setMaximizable(false);
    setIcon(Resources.getIconUsers());
    setModal(true);
    setScrollMode(Scroll.AUTOY);
    setBodyStyle("padding: 9px; background: none");
    setResizable(true);
    setButtonAlign(HorizontalAlignment.CENTER);
    setButtons(Dialog.OKCANCEL);
    //SORTING STORE
    setGropUserStoreSorter(storeSource);
    setGropUserStoreSorter(storeTarget);

    ContentPanel cpAlreadyShared = new ContentPanel();
    cpAlreadyShared.setSize(WIDTH_CP, HEIGHT_CONTAINER_TEXT_AREA);
    cpAlreadyShared.setHeaderVisible(false);
    cpAlreadyShared.setLayout(new FitLayout());

    VerticalPanel vpShared = new VerticalPanel();
    vpShared.setVerticalAlign(VerticalAlignment.MIDDLE);
    vpShared.setHorizontalAlign(HorizontalAlignment.CENTER);
    vpShared.setStyleAttribute("padding", "5px");
    vpShared.setLayout(new FitLayout());
    HorizontalPanel hpSharedContacts = new HorizontalPanel();
    hpSharedContacts.setHorizontalAlign(HorizontalAlignment.CENTER);
    hpSharedContacts.setVerticalAlign(VerticalAlignment.MIDDLE);
    textAreaAlreadyShared = new TextArea();
//    textField.setFieldLabel("Already shared with");
//    textField.setHeight(30);
    textAreaAlreadyShared.setWidth(501);
    textAreaAlreadyShared.setHeight(HEIGHT_TEXT_AREA);
    cpAlreadyShared.setStyleAttribute("padding-bottom", "5px");
    textAreaAlreadyShared.setReadOnly(true);
    cpAlreadyShared.add(textAreaAlreadyShared);

    Label label = new Label("Already shared with");
    label.setStyleAttribute("padding-right", "10px");
    label.setStyleAttribute("font-size", "12px");

    hpSharedContacts.add(label);
    hpSharedContacts.add(textAreaAlreadyShared);
    vpShared.add(hpSharedContacts);
    cpAlreadyShared.add(vpShared);
    add(cpAlreadyShared);


    final ContentPanel cp = new ContentPanel();
    cp.setSize(WIDTH_CP, HEIGHT_CONTAINER_GRID);
    cp.setHeaderVisible(false);
    cp.setLayout(new RowLayout(Orientation.HORIZONTAL));

    ToolBar toolBar = new ToolBar();
    Button buttonHelp = new Button();
    buttonHelp.setIcon(Resources.getIconInfo());

    buttonHelp.addSelectionListener(new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			MessageBox.info("Group dragging action", "Drag one or more contacts from the left (All Contacts) to the right (Share with) to add users in your sharing list.", null);

		}
	});

    toolBar.add(buttonHelp);
    setTopComponent(toolBar);

    final VerticalPanel vpAllContacts = new VerticalPanel();
    vpAllContacts.setHorizontalAlign(HorizontalAlignment.CENTER);
    vpAllContacts.add(new Label("All Contacts"));

    gridAllContacts = new Grid<ExtendedInfoContactModel>(storeSource, createColumnModel());


    GridCellRenderer<ExtendedInfoContactModel> displayNameCellRender = new GridCellRenderer<ExtendedInfoContactModel>() {
        public String render(ExtendedInfoContactModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ExtendedInfoContactModel> store, Grid<ExtendedInfoContactModel> grid) {

        	if(model!=null){

		        String value = model.get (property);
		        if (value != null){
		            return "<span qtitle='' qtip='" + model.getLogin() +"'>" + value + "</span>";
		        }
        	}

        	return "";
       }
    };

    columnConfigDisplayName.setRenderer(displayNameCellRender);

    storeSource.setDefaultSort(InfoContactModel.FULLNAME, SortDir.ASC);
    storeSource.sort(InfoContactModel.FULLNAME, SortDir.ASC);

    storeTarget.setDefaultSort(InfoContactModel.FULLNAME, SortDir.ASC);
    storeTarget.sort(InfoContactModel.FULLNAME, SortDir.ASC);


	final StoreFilterField<ExtendedInfoContactModel> filter = new StoreFilterField<ExtendedInfoContactModel>() {

		@Override
		protected boolean doSelect(Store<ExtendedInfoContactModel> store,
				ExtendedInfoContactModel parent, ExtendedInfoContactModel record,
				String property, String filter) {

			String name = record.getName();
			name = name.toLowerCase();
			if (name.contains(filter.toLowerCase())) {
				return true;
			}
			return false;
		}

	};

    filter.setEmptyText("Filter All Contacts");
    HorizontalPanel hp = new HorizontalPanel();
    hp.setStyleAttribute("padding-top", "5px");
    hp.setStyleAttribute("padding-bottom", "5px");
    hp.add(filter);
    filter.bind(storeSource);

    gridAllContacts.setHeight(HEIGHT_GRID);
    gridAllContacts.setBorders(false);
    gridAllContacts.getView().setAutoFill(true);
    gridAllContacts.setBorders(true);

    vpAllContacts.add(hp);
    vpAllContacts.add(gridAllContacts);

    RowData rowData = new RowData(.4, 1);
    rowData.setMargins(new Margins(6));
    cp.add(vpAllContacts, rowData);

    rowData = new RowData(.2, 1);
    rowData.setMargins(new Margins(6));
    LayoutContainer lc = createMoveContactsContainer();
    cp.add(lc, rowData);

    rowData = new RowData(.4, 1);
    rowData.setMargins(new Margins(6, 6, 6, 0));

    final VerticalPanel vpShareWith = new VerticalPanel();
    vpShareWith.setHorizontalAlign(HorizontalAlignment.CENTER);
    vpShareWith.add(new Label("Share with..."));
    gridShareWith = new Grid<ExtendedInfoContactModel>(storeTarget, createColumnModel());

	final StoreFilterField<ExtendedInfoContactModel> filter2 = new StoreFilterField<ExtendedInfoContactModel>() {

		@Override
		protected boolean doSelect(Store<ExtendedInfoContactModel> store,
				ExtendedInfoContactModel parent, ExtendedInfoContactModel record,
				String property, String filter) {

			String name = record.getName();
			name = name.toLowerCase();
			if (name.contains(filter.toLowerCase())) {
				return true;
			}
			return false;
		}

	};

    filter2.setEmptyText("Filter Share with");

    hp = new HorizontalPanel();
    hp.setStyleAttribute("padding-top", "5px");
    hp.setStyleAttribute("padding-bottom", "5px");
    hp.add(filter2);
    filter2.bind(storeTarget);

    gridShareWith.setHeight(HEIGHT_GRID);
    gridShareWith.setBorders(false);
    gridShareWith.getView().setAutoFill(true);
//    gridShareWith.setAutoExpandColumn(InfoContactModel.FULLNAME);
    gridShareWith.setBorders(true);
    vpShareWith.add(hp);
    vpShareWith.add(gridShareWith);

    cp.add(vpShareWith, rowData);

    new GridDragSource(gridAllContacts);
    new GridDragSource(gridShareWith);

    dropSource = new GridDropTarget(gridAllContacts);
    dropSource.setAllowSelfAsSource(false);

    dropTarget = new GridDropTarget(gridShareWith);
    dropTarget.setAllowSelfAsSource(false);

    add(cp);

    this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			hide();
		}
	});


    this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {
			List<ExtendedInfoContactModel> shareContacts = storeTarget.getModels();

			if(shareContacts==null || shareContacts.isEmpty()){

				MessageBoxConfirm mbc = new MessageBoxConfirm("Confirm exit?", "You have not selected any contact to share, confirm exit?");

				mbc.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {

					@Override
					public void handleEvent(MessageBoxEvent be) {
						String clickedButton = be.getButtonClicked().getItemId();
						if(clickedButton.equals(Dialog.YES)){
							hide();
						}
					}
				});
			}else
				hide();
		}
	});

    // needed to enable quicktips (qtitle for the heading and qtip for the
    // content) that are setup in the change GridCellRenderer

    cp.addListener(Events.Render, new Listener<BaseEvent>() {

		@Override
		public void handleEvent(BaseEvent be) {
			int width = gridAllContacts.getWidth();
			filter.setWidth(width-2);
			filter2.setWidth(width-2);
			gridShareWith.setWidth(width+5);
			vpShareWith.setWidth(width+15);
			gridAllContacts.setWidth(width+5);
			vpAllContacts.setWidth(width+15);
			vpShareWith.layout();
			vpAllContacts.layout();
			cp.layout();
		}
	});

    new QuickTip(gridAllContacts);
  }

	/**
	 * Creates the move contacts container.
	 *
	 * @return the layout container
	 */
	private LayoutContainer createMoveContactsContainer() {

		LayoutContainer lc = new LayoutContainer();
		lc.setLayout(new CenterLayout());
		VerticalPanel vp1 = new VerticalPanel();
		vp1.setHorizontalAlign(HorizontalAlignment.CENTER);
		vp1.setVerticalAlign(VerticalAlignment.MIDDLE);

		buttonSelectedLeft = new Button();
		buttonSelectedLeft.setIcon(Resources.getSelectedLeft());
		buttonSelectedLeft
				.setToolTip("Move selected contact/s from 'All Contact' to 'Share with'");
		buttonSelectedLeft
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						List<ExtendedInfoContactModel> selectedItems = gridAllContacts
								.getSelectionModel().getSelectedItems();

						if (selectedItems != null && selectedItems.size() > 0) {

							for (ExtendedInfoContactModel extendedInfoContactModel : selectedItems) {
								storeTarget.add(extendedInfoContactModel);
								storeSource.remove(extendedInfoContactModel);
							}
						}
					}
				});

		buttonSelectedRight = new Button();
		buttonSelectedRight.setIcon(Resources.getSelectedRight());
		buttonSelectedRight
				.setToolTip("Move selected contact/s from 'Share with' to 'All Contact'");

		buttonSelectedRight
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						List<ExtendedInfoContactModel> selectedItems = gridShareWith
								.getSelectionModel().getSelectedItems();

						if (selectedItems != null && selectedItems.size() > 0) {

							for (ExtendedInfoContactModel extendedInfoContactModel : selectedItems) {
								storeSource.add(extendedInfoContactModel);
								storeTarget.remove(extendedInfoContactModel);
							}
						}
					}
				});

		buttonAllLeft = new Button();
		buttonAllLeft.setIcon(Resources.getAllLeft());
		buttonAllLeft
				.setToolTip("Move all contact/s from 'All Contact' to 'Share with'");

		buttonAllLeft
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						if (storeSource != null && storeSource.getCount() > 0) {
							for (ExtendedInfoContactModel extendedInfoContactModel : storeSource
									.getModels()) {
								storeTarget.add(extendedInfoContactModel);
								storeSource.remove(extendedInfoContactModel);
							}
						}

					}
				});

		buttonAllRight = new Button();
		buttonAllRight.setIcon(Resources.getAllRight());
		buttonAllRight
				.setToolTip("Move all contact/s from 'Share with' to 'All Contact'");

		buttonAllRight
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						if (storeTarget != null && storeTarget.getCount() > 0) {
							for (ExtendedInfoContactModel extendedInfoContactModel : storeTarget
									.getModels()) {
								storeSource.add(extendedInfoContactModel);
								storeTarget.remove(extendedInfoContactModel);
							}
						}

					}
				});

		vp1.add(buttonSelectedLeft);
		vp1.add(buttonAllLeft);
		vp1.add(buttonSelectedRight);
		vp1.add(buttonAllRight);

		lc.add(vp1);

		vp1.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				buttonSelectedLeft.el().setStyleAttribute("margin-top", "40px");
				buttonAllLeft.el().setStyleAttribute("margin-top", "5px");
				buttonSelectedRight.el().setStyleAttribute("margin-top", "20px");
				buttonAllRight.el().setStyleAttribute("margin", "5px");

			}
		});

		return lc;
	}

  /**
   * Creates the column model.
   *
   * @return the column model
   */
  private ColumnModel createColumnModel() {
    List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

    ColumnConfig icon = new ColumnConfig(ExtendedInfoContactModel.ICON, "", 25);
    configs.add(icon);

    columnConfigDisplayName = new ColumnConfig(ExtendedInfoContactModel.FULLNAME, "Name", 150);
    configs.add(columnConfigDisplayName);

	ColumnConfig type = new ColumnConfig(ExtendedInfoContactModel.ISGROUP, "Type", 50);
    configs.add(type);


    GridCellRenderer<ExtendedInfoContactModel> typeRender = new GridCellRenderer<ExtendedInfoContactModel>() {
		@Override
		public String render(ExtendedInfoContactModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ExtendedInfoContactModel> store, Grid<ExtendedInfoContactModel> grid) {
			  Boolean isGroup = (Boolean) model.get(property);
	          String color = "#0F4FA8";
	          String val = "";
	          if(isGroup){
	        	  val = "Group";
	        	  color = "#05316D";
	        	  return "<span style='font-weight: bold; color:" + color + "'>" + val + "</span>";
	          }else{
	        	  val = "User";
	        	  return "<span style='font-weight: bold; color:" + color + "'>" + val + "</span>";
	          }
		}
      };

    type.setRenderer(typeRender);
    return  new ColumnModel(configs);
  }

  /**
   * Adds the source contacts.
   *
   * @param listContact the list contact
   */
  public void addSourceContacts(List<InfoContactModel> listContact){

	  gridAllContacts.mask("", ConstantsExplorer.LOADINGSTYLE);
	  if(listContact!=null && listContact.size()>0){
		 List<ExtendedInfoContactModel> listExtended = new ArrayList<ExtendedInfoContactModel>();
		  //SETTING ICONS
		  for (InfoContactModel infoContactModel : listContact) {
			  ExtendedInfoContactModel ext = new ExtendedInfoContactModel(infoContactModel.getId(), infoContactModel.getLogin(), infoContactModel.getName(), infoContactModel.isGroup());
			  ext.setIcon();
			  listExtended.add(ext);
		  }

		  storeSource.add(listExtended);
//		  GWT.log("Added sources: "+listExtended.toString());
	  }

	  gridAllContacts.unmask();
	  gridAllContacts.repaint();
  }

  /**
   * Adds the already shared contacts.
   *
   * @param listContact the list contact
   */
  public void addAlreadySharedContacts(List<InfoContactModel> listContact){

	  gridShareWith.mask("", ConstantsExplorer.LOADINGSTYLE);
	  if(listContact!=null && listContact.size()>0){

		  String alreadyShared = "";
		  for (int i=0; i<listContact.size()-1; i++)
			  alreadyShared+=listContact.get(i).getName()+", ";

		  alreadyShared+=listContact.get(listContact.size()-1).getName();

		  textAreaAlreadyShared.setValue(alreadyShared);
	  }
	  gridShareWith.unmask();

  }

  /**
   * Adds the target contacts.
   *
   * @param listContact the list contact
   */
  public void addTargetContacts(List<InfoContactModel> listContact){
	  if(listContact!=null && listContact.size()>0){
		  storeTarget.add(convertFromInfoContactModel(listContact));
	  }
  }

  /**
   * Convert from info contact model.
   *
   * @param listContact the list contact
   * @return the list
   */
  private List<ExtendedInfoContactModel> convertFromInfoContactModel(List<InfoContactModel> listContact){

	  if(listContact!=null){
		  List<ExtendedInfoContactModel> listExtended = new ArrayList<ExtendedInfoContactModel>(listContact.size());
		  //SETTING ICONS
		  for (InfoContactModel infoContactModel : listContact) {
			  listExtended.add(convertFromInfoContactModel(infoContactModel));
		  }
		  return listExtended;
	  }
	  return new ArrayList<ExtendedInfoContactModel>();
  }

  /**
   * Convert from info contact model.
   *
   * @param infoContactModel the info contact model
   * @return the extended info contact model
   */
  private ExtendedInfoContactModel convertFromInfoContactModel(InfoContactModel infoContactModel){
	  if(infoContactModel!=null){
		  String fullName = infoContactModel.getName();
		  if(fullName==null || fullName.isEmpty())
			  fullName = infoContactModel.getLogin();

		  ExtendedInfoContactModel ext = new ExtendedInfoContactModel(infoContactModel.getId(), infoContactModel.getLogin(), fullName, infoContactModel.isGroup());
		  ext.setIcon();
		  return ext;
	  }
	  return new ExtendedInfoContactModel();
  }

  /**
   * Adds the target contact.
   *
   * @param contact the contact
   */
  public void addTargetContact(InfoContactModel contact){
	  if(contact!=null)
		  storeTarget.add(convertFromInfoContactModel(contact));
  }

  /**
   * Gets the target list contact.
   *
   * @return the target list contact
   */
  public List<InfoContactModel> getTargetListContact(){

	  List<? extends InfoContactModel> infoContacts = storeTarget.getModels();
	  return (List<InfoContactModel>) infoContacts;
  }


  /**
   * Sets the grop user store sorter.
   *
   * @param store the new grop user store sorter
   */
  private void setGropUserStoreSorter(ListStore<ExtendedInfoContactModel> store){

		// Sorting files
	  store.setStoreSorter(new StoreSorter<ExtendedInfoContactModel>() {

			@Override
			public int compare(Store<ExtendedInfoContactModel> store, ExtendedInfoContactModel m1, ExtendedInfoContactModel m2, String property) {
				boolean m1Folder = m1.isGroup();
				boolean m2Folder = m2.isGroup();

				if (m1Folder && !m2Folder) {
					return -1;
				} else if (!m1Folder && m2Folder) {
					return 1;
				}

				return m1.getName().compareToIgnoreCase(m2.getName());
			}
		});
	}

}
