package org.gcube.portlets.user.reportgenerator.client.targets;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.d4sreporting.common.shared.Table;
import org.gcube.portlets.d4sreporting.common.shared.TableCell;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateComponent;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class TextTableImage extends Composite {

	public static final int DEFAULT_HEIGHT = 100;
	public static final int DEFAULT_WIDTH = 700;
	
	public String id;
	private List<Metadata> metas;
	private FocusPanel focusPanel = new FocusPanel();
	private VerticalPanel mainPanel = new VerticalPanel();
	private	HorizontalPanel controlPanel;

	private Button addTextB = new Button("Add Text");
	private Button addImageB = new Button("Add Image");
	private Button addTableB = new Button("Add Table");
	private Presenter presenter;
	private ArrayList<TemplateComponent> addedComponents = new ArrayList<TemplateComponent>();

	/**
	 * constructor to be used when reading from a template
	 * @param presenter
	 */
	public TextTableImage(Presenter presenter, boolean isFromTemplate) {
		this.presenter = presenter;
		focusPanel.setStyleName("imageTableTextDelimiter");
		mainPanel.setStyleName("imageTableTextPanel");
		controlPanel = getControlPanel();
		controlPanel.setStyleName("imageTableTextControlPanel");
		mainPanel.add(controlPanel);

		focusPanel.add(mainPanel);
		initWidget(focusPanel);

		if (isFromTemplate) {
			int height = 40;
			D4sRichTextarea firstText = new D4sRichTextarea("-1", ComponentType.BODY, presenter, 0, 0, DEFAULT_WIDTH, height, false, true, this);
			firstText.setPixelSize(DEFAULT_WIDTH, height);
			mainPanel.add(firstText);
			//construct the first component and add it
			TemplateComponent toAdd = new TemplateComponent(presenter.getModel(), 0, 0, DEFAULT_WIDTH, height, 
					presenter.getModel().getCurrentPage(), ComponentType.BODY, "", firstText);
			add(toAdd);	
		}
	}
	/**
	 * constructor to be used when reading from a report
	 * @param presenter
	 * @param sRS
	 */
	public TextTableImage(Presenter presenter, RepeatableSequence sRS) {
		this(presenter, false);
		if (sRS != null && sRS.getGroupedComponents().size() > 0) {
			for (BasicComponent sComp : sRS.getGroupedComponents()) {
				add(new TemplateComponent(presenter.getModel(), sComp, presenter, true, this));
			}
		} 
	}
	
	public void add(TemplateComponent toAdd) {
		addedComponents.add(toAdd);
		GWT.log("ToAdd= getType " + toAdd.getType());
		mainPanel.add(toAdd.getContent());
	}

	private void addNewText() {
		int height = 40;
		D4sRichTextarea text = new D4sRichTextarea("-1", ComponentType.BODY, presenter, 0, 0, DEFAULT_WIDTH, height, false, true, this);
		text.setPixelSize(DEFAULT_WIDTH, height);
		TemplateComponent toAdd = new TemplateComponent(presenter.getModel(), 0, 0, DEFAULT_WIDTH, height, 
				presenter.getModel().getCurrentPage(), ComponentType.BODY, "", text);
		add(toAdd);
	}
	
	private void addNewTable() {
		Table st = getSerializableTable();
		GenericTable table = new GenericTable(st, presenter, 0, 0,  TemplateModel.TEMPLATE_WIDTH - 50, GenericTable.DEFAULT_HEIGHT, false, true, this);		
		TemplateComponent toAdd = new TemplateComponent(presenter.getModel(), 0, 0, DEFAULT_WIDTH, GenericTable.DEFAULT_HEIGHT, 
				presenter.getModel().getCurrentPage(), ComponentType.FLEX_TABLE, "", table);
		add(toAdd);		
	}
	
	private void addNewImage() {
		List<Metadata> emptyMetadata = new ArrayList<Metadata>();
		BasicComponent serImage = new BasicComponent(0, 0, ClientImage.DEFAULT_WIDTH, ClientImage.DEFAULT_HEIGHT, 
				presenter.getModel().getCurrentPage(), ComponentType.DYNA_IMAGE, "", "", false, false, emptyMetadata);
		ClientImage dp = new ClientImage(serImage, presenter,  ClientImage.DEFAULT_WIDTH, ClientImage.DEFAULT_HEIGHT, true, this); 		
		TemplateComponent toAdd = new TemplateComponent(presenter.getModel(), 0, 0, DEFAULT_WIDTH, GenericTable.DEFAULT_HEIGHT, 
				presenter.getModel().getCurrentPage(), ComponentType.DYNA_IMAGE, "", dp);
		add(toAdd);		
	}
	
	/**
	 * 
	 * @return
	 */
	private Table getSerializableTable() {
		Table toReturn = new Table(GenericTable.DEFAULT_COLS_NUM);
		for (int i = 0; i < GenericTable.DEFAULT_ROWS_NUM; i++) {
			toReturn.addRow(getRow(i));
		}

		return toReturn;
	}

	private ArrayList<TableCell> getRow(int i) {
		ArrayList<TableCell> toReturn = new ArrayList<TableCell>();
		int cellWidth = (DEFAULT_WIDTH) / GenericTable.DEFAULT_COLS_NUM;
		for (int j = 0; j <2; j++) {
			toReturn.add(new TableCell("", 1, cellWidth, 25));
		}
		return toReturn;
	}
	/**
	 * 
	 * @return
	 */
	private HorizontalPanel getControlPanel() {
		final HorizontalPanel toReturn = new HorizontalPanel();

		addTextB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				addNewText();
			}
		});

		addImageB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				addNewImage();
			}
		});

		addTableB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				addNewTable();
			}
		});
		toReturn.setSpacing(3);

		toReturn.add(addTextB);
		toReturn.add(addTableB);
		toReturn.add(addImageB);		


		//set style for buttons
		for (int i = 0; i < toReturn.getWidgetCount(); i++) {
			if (toReturn.getWidget(i) instanceof Button) {
				Button b = (Button) toReturn.getWidget(i);
				b.addStyleName("addEntryButton");
				b.getElement().getStyle().setMarginRight(10, Unit.PX);
			}
		}
		return toReturn;
	}

	public ArrayList<TemplateComponent> getAddedComponents() {
		return addedComponents;
	}
	
	public void removeFromParent(Widget w) {
		for (TemplateComponent tc : addedComponents) {
			if (tc.getType() == ComponentType.BODY) {
				D4sRichTextarea toCheck = (D4sRichTextarea) tc.getContent();
				if (toCheck.equals(w)) {
					addedComponents.remove(tc);
					break;
				}
			}
			if (tc.getType() == ComponentType.FLEX_TABLE) {
				GenericTable toCheck = (GenericTable) tc.getContent();
				if (toCheck.equals(w)) {
					addedComponents.remove(tc);
					break;
				}
			}
			if (tc.getType() == ComponentType.DYNA_IMAGE) {
				ClientImage toCheck = (ClientImage) tc.getContent();
				if (toCheck.equals(w)) {
					addedComponents.remove(tc);
					break;
				}
			}
		}
	}
	
	public List<Metadata> getMetadata() {
		return metas;
	}

	public void setMetadata(List<Metadata> metas) {
		this.metas = metas;
	}
	public String getId() {
		if (id == null)
			return "-1";
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
}
