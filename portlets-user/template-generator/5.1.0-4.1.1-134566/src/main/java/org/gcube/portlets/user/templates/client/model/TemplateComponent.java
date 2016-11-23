package org.gcube.portlets.user.templates.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.Table;
import org.gcube.portlets.d4sreporting.common.shared.RepTimeSeries;
import org.gcube.portlets.user.templates.client.components.AttributeArea;
import org.gcube.portlets.user.templates.client.components.BasicTextArea;
import org.gcube.portlets.user.templates.client.components.CommentArea;
import org.gcube.portlets.user.templates.client.components.Coords;
import org.gcube.portlets.user.templates.client.components.D4sRichTextarea;
import org.gcube.portlets.user.templates.client.components.DefaultArea;
import org.gcube.portlets.user.templates.client.components.DroppingArea;
import org.gcube.portlets.user.templates.client.components.FakeTextArea;
import org.gcube.portlets.user.templates.client.components.GenericTable;
import org.gcube.portlets.user.templates.client.components.GroupingDelimiterArea;
import org.gcube.portlets.user.templates.client.components.GroupingInnerArea;
import org.gcube.portlets.user.templates.client.components.ImageArea;
import org.gcube.portlets.user.templates.client.components.InstructionArea;
import org.gcube.portlets.user.templates.client.components.ClientRepeatableSequence;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * This class represent all the possible template components in the Model, they basically are 2D rectangles
 * 
 * A <code>TemplateComponent</code> specifies an area in a coordinate space that is
 * enclosed by the <code>TemplateComponent</code> object's top-left point
 * (<i>x</i>,&nbsp;<i>y</i>)
 * in the coordinate space, its width, and its height.
 * <p>
 * 
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * 
 * 
 */
public class TemplateComponent {

	/**
	 *  The <i>x</i> coordinate of the <code>TemplateComponent</code>.
	 */
	private int x;
	/**
	 *  The <i>y</i> coordinate of the <code>TemplateComponent</code>.
	 */
	private int y;
	/**
	 * The width of the <code>TemplateComponent</code>.
	 * @see #setWidth(int)
	 * @see #getWidth()
	 */
	private int width;
	private int height;
	private TemplateModel myModel;
	private int templatePage;
	/**
	 * the paramName for assigning it a value when exporting to pdf, valid only for Dynamic Content
	 */
	private String paramName;
	//what is in the template component may vary depending on its type
	private Widget content;


	private ComponentType type;

	private boolean locked;

	private boolean doubleColLayout;
	
	private TemplateComponent child;


	/**
	 * holds the metadata(s) for the sections
	 */
	private List<Metadata> metadata;

	/**
	 * Creates a empty TemplateComponent
	 */
	public TemplateComponent() {
		super();
	}

	/**
	 * Creates a TemplateComponent with the given charactheristics in double column
	 *  
	 * @param myModel .
	 * @param x .
	 * @param y .
	 * @param width .
	 * @param height .
	 * @param templatePage .
	 * @param content the inserted widget
	 * @param type the type of the inserted widget
	 * @param paramName for assigning it a value when exporting to pdf, valid only for Dynamic Content
	 * @param doubleColLayout to specify that its layout is double columned
	 */
	public TemplateComponent(TemplateModel myModel, int x, int y, int width, int height, int templatePage, ComponentType type, String paramName, Widget content, boolean doubleColLayout) {
		this.myModel = myModel;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.templatePage = templatePage;
		this.content = content;
		this.type = type;
		this.paramName = paramName;
		this.doubleColLayout = doubleColLayout;
		this.metadata = new LinkedList<Metadata>();
	}

	/**
	 * 
	 * Creates a TemplateComponent with the given charactheristics and single column
	 *  
	 * @param myModel .
	 * @param x .
	 * @param y .
	 * @param width .
	 * @param height .
	 * @param templatePage .
	 * @param content the inserted widget
	 * @param type the type of the inserted widget
	 * @param paramName for assigning it a value when exporting to pdf, valid only for Dynamic Content
	 */
	public TemplateComponent(TemplateModel myModel, int x, int y, int width, int height, int templatePage, ComponentType type, String paramName, Widget content) {
		this(myModel, x, y, width, height, templatePage, type, paramName, content, new ArrayList<Metadata>());
		this.doubleColLayout = false;
	}
	
	/**
	 * 
	 * Creates a TemplateComponent with the given charactheristics and single column
	 *  
	 * @param myModel .
	 * @param x .
	 * @param y .
	 * @param width .
	 * @param height .
	 * @param templatePage .
	 * @param content the inserted widget
	 * @param type the type of the inserted widget
	 * @param paramName for assigning it a value when exporting to pdf, valid only for Dynamic Content
	 */
	public TemplateComponent(TemplateModel myModel, int x, int y, int width, int height, int templatePage, ComponentType type, String paramName, Widget content, ArrayList<Metadata> metadata) {
		this.myModel = myModel;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.templatePage = templatePage;
		this.content = content;
		this.type = type;
		this.paramName = paramName;
		this.doubleColLayout = false;
		this.metadata = metadata;
	}


	/**
	 * create a template component which is displayable (create an actual Widget in the content field)
	 * @param myModel the model
	 * @param sc the serialiazble to convert
	 * @param controller .
	 */
	public TemplateComponent(TemplateModel myModel, BasicComponent sc, Presenter controller, boolean addControls) {
		this.myModel = myModel;

		Coords start = new Coords(x, y);
		
		int width = sc.getWidth();
		int height = sc.getHeight();
	
		D4sRichTextarea toAdd = new D4sRichTextarea();
		switch (sc.getType()) {
		case DYNA_IMAGE:
			DroppingArea dp = new DroppingArea(controller, start.getX(), start.getY(), width, height, sc.isDoubleColLayout());
			dp.setExpectedContent((String) sc.getPossibleContent());
			this.content = dp;
			break;
		case STATIC_IMAGE:
			String imagePath = (String) sc.getPossibleContent();
			this.content = new ImageArea(controller, true, imagePath, myModel.getTemplateName(),start.getX(), start.getY(), width, height);
			break;
		case HEADING_1:
		case HEADING_2:
		case HEADING_3:	
		case HEADING_4:
		case HEADING_5:
		case BODY_NOT_FORMATTED:
		case TITLE:
			BasicTextArea bToAdd = new BasicTextArea(sc.getType(), controller, start.getX(), start.getY(), width, height);
			if (!addControls)
				bToAdd.hideCloseButton();
			bToAdd.setText((String) sc.getPossibleContent());
			bToAdd.setLocked(sc.isLocked());
			this.content = bToAdd;		
			break;
		case BODY:
			toAdd = new D4sRichTextarea(sc.getType(), controller, start.getX(), start.getY(), width, height);
			if (!addControls)
				toAdd.hideCloseButton();
			toAdd.getArea().setHTML((String) sc.getPossibleContent());
			this.content = toAdd;		
			break;
		case FAKE_TEXTAREA:
			FakeTextArea ft = new FakeTextArea(0, controller);
			this.content = ft;
			break;
		case TOC:
			DefaultArea dp2 = new DefaultArea(sc.getType(), controller, start.getX(), start.getY(), width, height);
			dp2.addStyleName("tocArea");
			this.content = dp2;
			break;
		case BIBLIO:
			DefaultArea dp3 = new DefaultArea(sc.getType(), controller, start.getX(), start.getY(), width, height);
			dp3.addStyleName("biblioArea");
			this.content = dp3;
			break;
		case BODY_TABLE_IMAGE:
			DefaultArea dpBo = new DefaultArea(sc.getType(), controller, start.getX(), start.getY(), width, height);
			dpBo.addStyleName("text-table-image");
			this.content = dpBo;
			break;
		case PAGEBREAK:
			DefaultArea dp4 = new DefaultArea(sc.getType(), controller, start.getX(), start.getY(), width, height);
			dp4.addStyleName("pagebreak");
			this.content = dp4;
			break;
		case TIME_SERIES:
			DefaultArea dp45 = new DefaultArea(sc.getType(), controller, start.getX(), start.getY(), width, height);
			dp45.addStyleName("timeseriesArea");
			this.content = dp45;
			break;
		case FLEX_TABLE:
			Table st  = (Table) sc.getPossibleContent();
			GenericTable table = new GenericTable(st, controller, start.getX(), start.getY(),  TemplateModel.TEMPLATE_WIDTH - 50, 200, sc.isLocked());				
			this.content = table; 
			break;
		case COMMENT:
			CommentArea ca2Add = new CommentArea(controller, start.getX(), start.getY(), width, height);
			ca2Add.getTextArea().setText((String) sc.getPossibleContent());
			this.content = ca2Add;	
			break;
		case ATTRIBUTE:
		case ATTRIBUTE_MULTI:
		case ATTRIBUTE_UNIQUE:
			AttributeArea atAdd = new AttributeArea(controller, start.getX(), start.getY(), width, height, (String) sc.getPossibleContent(), sc.getType());
			if (!addControls)
				atAdd.hideCloseButton();
			this.content = atAdd;	
			break;
		case INSTRUCTION:
			InstructionArea ins = new InstructionArea(controller, start.getX(), start.getY(), width, height);
			ins.getTextArea().setText((String) sc.getPossibleContent());
			this.content = ins;	
			break;
		case REPEAT_SEQUENCE_DELIMITER:
			GroupingDelimiterArea gp = new GroupingDelimiterArea(width, height);
			this.content = gp;
			break;
		case REPEAT_SEQUENCE_INNER:
			GroupingInnerArea spacer = new GroupingInnerArea();
			this.content = spacer;
			break;
		case REPEAT_SEQUENCE:
			GWT.log("FOUND SEQUENCE trying getGroup");
			RepeatableSequence repeatableSequence = (RepeatableSequence) sc.getPossibleContent();
			
			GWT.log("getGroup: " + repeatableSequence.toString());
			
			ClientRepeatableSequence rps = new ClientRepeatableSequence(controller, repeatableSequence);
			this.content = rps;
			break;
		}
		toAdd.setLocked(sc.isLocked());
		this.x = sc.getX();
		this.y = sc.getY();
		this.width = sc.getWidth();
		this.height = sc.getHeight();
		this.templatePage = sc.getTemplatePage();
		this.type = sc.getType();
		this.paramName = sc.getParamName();
		this.doubleColLayout = sc.isDoubleColLayout();
		this.locked = sc.isLocked();
		this.metadata = sc.getMetadata();

	}


	/**
	 * used only for text areas	
	 * @return
	 */
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * return a template Component which is serializable
	 * @return .a
	 */
	public BasicComponent getSerializable() {
		Serializable content = null;
		D4sRichTextarea d4sText = null;
		 
		switch (this.getType()) {
		case BODY:
			d4sText = (D4sRichTextarea) this.content;
			content = d4sText.getArea().getHTML();
			break;
		case HEADING_1:					
		case HEADING_2:					
		case HEADING_3:				
		case HEADING_4:					
		case HEADING_5:
		case BODY_NOT_FORMATTED:
		case TITLE:					
			BasicTextArea textArea = (BasicTextArea) this.content;
			content = textArea.getText();
			break;

		case STATIC_IMAGE:

			String imageName = ((ImageArea) this.content).getImageName();
			content = ((ImageArea) this.content).getImageURL(imageName, "CURRENT_OPEN");
			break;			
		case DYNA_IMAGE:
			content = ((DroppingArea) this.content).getExpectedContent();
			break;

		case FAKE_TEXTAREA:	
		case REPEAT_SEQUENCE_DELIMITER:
		case TOC:		
		case BIBLIO:
		case PAGEBREAK:
		case BODY_TABLE_IMAGE:
			break;
		case TIME_SERIES:
			RepTimeSeries toAdd = new RepTimeSeries();
			content = toAdd;
			break;
		case FLEX_TABLE:
			GenericTable gt = (GenericTable) this.content;
			GWT.log("GT Cols No: "+gt.getCols());
			Table st = gt.getSerializable();
			content = st;
			break;
		case COMMENT:
			CommentArea ca = (CommentArea) this.content;
			content = ca.getTextArea().getText();
			break;
		case ATTRIBUTE:
		case ATTRIBUTE_UNIQUE:
		case ATTRIBUTE_MULTI:
			AttributeArea at = (AttributeArea) this.content;
			content = at.getText();
			break;	
		case INSTRUCTION:
			InstructionArea instr = (InstructionArea) this.content;
			content = instr.getTextArea().getText();
			break;	
		case REPEAT_SEQUENCE:
			ClientRepeatableSequence repSeq = (ClientRepeatableSequence) this.content;
			RepeatableSequence toStore = new RepeatableSequence(getSerializableSequence(repSeq), height);
			content = toStore;			
			break;
		}		
		return new BasicComponent(x, y, width, height, templatePage, type, paramName, content, this.doubleColLayout, this.locked, metadata);
	}
	/**
	 * this method constructs a SerializableRepeatableSequence sequence that can be serializable
	 * @param repSeq
	 * @return
	 */
	private ArrayList<BasicComponent> getSerializableSequence(ClientRepeatableSequence repSeq) {
		ArrayList<BasicComponent> sComps = new ArrayList<BasicComponent>();
		for (TemplateComponent tc : repSeq.getGroupedComponents()) {
			sComps.add(tc.getSerializable());
		}
		return sComps;
	}
	
	/**
	 * Returns the height of the bounding <code>TemplateComponent</code>
	 * @return the height of the bounding <code>TemplateComponent</code>. 
	 */
	public int getHeight() {return height;}
	/**
	 * 
	 * @param height .
	 */
	public void setHeight(int height) {	this.height = height; }
	/**
	 * Returns the type of the <code>TemplateComponent</code>
	 * A <code>TemplateComponent</code> can be of different types depeding on its content
	 * @see org.gcube.portlets.d4sreporting.common.client.CommonConstants class
	 * 
	 * @return .
	 */
	public ComponentType getType() {return type;}
	/**
	 * @param type .
	 */
	public void setType(ComponentType type) {	this.type = type;}
	/**
	 * Returns the width of the bounding <code>TemplateComponent</code>
	 * @return the width of the bounding <code>TemplateComponent</code>. 
	 */
	public int getWidth() {	return width;}
	/**
	 * @param width .
	 */
	public void setWidth(int width) {this.width = width;}
	/**
	 * Returns the  X coordinate of the bounding <code>TemplateComponent</code>
	 * @return the width of the bounding <code>TemplateComponent</code>. 
	 */
	public int getX() {	return x;}
	/**
	 * @param x .
	 */
	public void setX(int x) {this.x = x;}
	/**
	 * @return .
	 */
	public int getY() {return y;}
	/**
	 * @param y .
	 */
	public void setY(int y) {this.y = y;}
	/**
	 * @return .
	 */
	public int getTemplatePage() {	return templatePage;	}
	/**
	 * @param templatePage .
	 */
	public void setTemplatePage(int templatePage) {	this.templatePage = templatePage;}
	/**
	 * @return .
	 */
	public Widget getContent() {return content;	}
	/**
	 * @param content .
	 */
	public void setContent(Widget content) {
		this.content = content;
	}

	/**
	 * 
	 * @return .
	 */
	public String getParamName() {
		return paramName;
	}

	/**
	 * 
	 * @param paramName .
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public boolean isDoubleColLayout() {
		return doubleColLayout;
	}

	public void setDoubleColLayout(boolean doubleColLayout) {
		this.doubleColLayout = doubleColLayout;
	}

	public void addMetadata(String attr, String value) {
		if (attr != null && value != null) {
			metadata.add(new Metadata(attr, value));
		}
		else 
			throw new NullPointerException();
	}

	public List<Metadata> getAllMetadata() {
		if (metadata == null) {
			new LinkedList<Metadata>();
		}	
		return metadata;
	}
	public TemplateModel getMyModel() {
		return myModel;
	}

	/**
	 * 
	 * @return
	 */
	public TemplateComponent getChild() {
		return child;
	}
	/**
	 * 
	 * @param child
	 */
	public void setChild(TemplateComponent child) {
		this.child = child;
	}
	public boolean hasChild() {
		return (child!=null);
	}
}
