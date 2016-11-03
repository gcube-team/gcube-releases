package org.gcube.portlets.user.reportgenerator.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.AttributeArea;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.RepTimeSeries;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.d4sreporting.common.shared.ReportReferences;
import org.gcube.portlets.d4sreporting.common.shared.Table;
import org.gcube.portlets.d4sreporting.common.shared.Tuple;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.targets.AttributeMultiSelection;
import org.gcube.portlets.user.reportgenerator.client.targets.AttributeSingleSelection;
import org.gcube.portlets.user.reportgenerator.client.targets.BasicTextArea;
import org.gcube.portlets.user.reportgenerator.client.targets.ClientImage;
import org.gcube.portlets.user.reportgenerator.client.targets.ClientRepeatableSequence;
import org.gcube.portlets.user.reportgenerator.client.targets.ClientReportReference;
import org.gcube.portlets.user.reportgenerator.client.targets.ClientSequence;
import org.gcube.portlets.user.reportgenerator.client.targets.D4sRichTextarea;
import org.gcube.portlets.user.reportgenerator.client.targets.ExtHidden;
import org.gcube.portlets.user.reportgenerator.client.targets.GenericTable;
import org.gcube.portlets.user.reportgenerator.client.targets.GroupingDelimiterArea;
import org.gcube.portlets.user.reportgenerator.client.targets.GroupingInnerArea;
import org.gcube.portlets.user.reportgenerator.client.targets.HeadingTextArea;
import org.gcube.portlets.user.reportgenerator.client.targets.ReportTextArea;
import org.gcube.portlets.user.reportgenerator.client.targets.TextTableImage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;



/**
 * 
 * This class represent all the possible template components IN THE MODEL
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class TemplateComponent {
	private String id = "";
	/**
	 * 
	 */
	public final static String DEFAULT_IMAGE_NAME= "image_placeholder.png";
	public final static String DEFAULT_IMAGE_PATH= GWT.getModuleBaseURL() + "../images/" + DEFAULT_IMAGE_NAME;

	private int x;
	private int y;
	private int width;
	private int height;
	private TemplateModel myModel;
	private int templatePage;



	private ComponentType type;

	private boolean locked;

	private boolean doubleColLayout;

	/**
	 * holds the metadata(s) for the sections
	 */
	private List<Metadata> metadata;
	/**
	 * the paramName for assigning it a value when exporting to pdf, valid only for Dynamic Content
	 */
	private String paramName;

	//what is in the template component may vary depending on its type
	private Widget content;


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
		this.metadata = new LinkedList<Metadata>();
	}

	/**
	 * create a template component which is displayable (create an actual Widget in the content field)
	 * @param myModel the model
	 * @param sc the serialiazble to convert
	 * @param presenter .
	 * @param showClose 
	 * @param owner needed  only for the TextTableImage case and because im in hurry
	 */
	public TemplateComponent(TemplateModel myModel, BasicComponent sc, Presenter presenter, boolean showClose, TextTableImage owner) {
		this.myModel = myModel;
		//
		//Coords start = new Coords(x, y);
		int width = sc.getWidth();
		int height = sc.getHeight();
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

		//Log.debug("TemplateComponent found type: " + sc.getType());

		switch (sc.getType()) {
		case DYNA_IMAGE:
			ClientImage dp = new ClientImage(sc, presenter, sc.getWidth(), sc.getHeight(), showClose, owner); 
			this.content = dp;			
			break;
		case HEADING_1:			
		case HEADING_2:
		case HEADING_3:	
		case HEADING_4:	
		case HEADING_5:	
		case TITLE:
			if (sc.isLocked()) {
				HTML area = new HTML();				
				area.setStyleName("report-ui-component");
				area.addStyleName(getStyle(sc.getType()));
				area.getElement().getStyle().setMarginTop(5, Unit.PX);
				area.setWidth(width+"px");
				area.setText((String) sc.getPossibleContent());
				this.content = area;
			}
			else {
				HeadingTextArea bToAdd = new HeadingTextArea(sc.getId(), sc.getType(), presenter, sc.getX(), sc.getY(), width, 25, getUserComments() != null, showClose);
				bToAdd.setText((String) sc.getPossibleContent());
				bToAdd.setMetadata(sc.getMetadata());
				this.content = bToAdd;		
			}
			break;
		case BODY_NOT_FORMATTED:
			if (sc.isLocked()) {
				HTML area = new HTML();				
				area.setStyleName("report-ui-component");
				area.addStyleName("readOnlyText");
				//area.addStyleName(getStyle(sc.getType()));
				area.getElement().getStyle().setMarginTop(5, Unit.PX);
				area.setWidth((width-30)+"px");
				String content = (String) sc.getPossibleContent();
				area.setText(preserveLineBreaks(content));			
				this.content = area;
			}
			else {
				int changedWidth = width -5; //because of the inner padding
				BasicTextArea bToAdd = new BasicTextArea(sc.getId(), sc.getType(), presenter, sc.getX(), sc.getY(), changedWidth, 25, getUserComments() != null, showClose);
				bToAdd.setText((String) sc.getPossibleContent());
				bToAdd.setMetadata(sc.getMetadata());
				this.content = bToAdd;		
			}
			break;
		case BODY:
			if (sc.isLocked()) {
				HTML area = new HTML();				
				area.setStyleName("report-ui-component");
				area.addStyleName("readOnlyText");
				//area.addStyleName(getStyle(sc.getType()));
				area.getElement().getStyle().setMarginTop(5, Unit.PX);
				area.setWidth(width+"px");
				area.setText((String) sc.getPossibleContent());
				this.content = area;
			}
			else {
				height = 40;
				D4sRichTextarea ta = new D4sRichTextarea(sc.getId(), sc.getType(), presenter, sc.getX(), sc.getY(),width, height, getUserComments() != null, showClose, owner);
				ta.setHTML((String) sc.getPossibleContent());
				ta.setMetadata(sc.getMetadata());
				//ta.setStyleName("cw-RichText");
				ta.setPixelSize(width, height);
				this.content = ta;
			}
			this.setLocked(sc.isLocked());
			break;
		case TOC:
			ReportTextArea dp2 = new ReportTextArea(sc.getId(), sc.getType(), presenter, sc.getX(), sc.getY(), width, height, getUserComments() != null, showClose);
			dp2.addStyleName("tocArea");
			this.content = dp2;
			break;
		case BIBLIO:
			ReportTextArea dp3 = new ReportTextArea(sc.getId(), sc.getType(), presenter, sc.getX(), sc.getY(), width, height, getUserComments() != null, showClose);
			dp3.addStyleName("biblioArea");
			this.content = dp3;
			break;
		case PAGEBREAK:
			ReportTextArea dp4 = new ReportTextArea(sc.getId(), sc.getType(), presenter, sc.getX(), sc.getY(), width, height, getUserComments() != null, showClose);
			dp4.addStyleName("pagebreak");
			this.content = dp4;
			break;
		case FLEX_TABLE:
			Table st  = (Table) sc.getPossibleContent();
			GenericTable table = new GenericTable(st, presenter, sc.getX(), sc.getY(),  TemplateModel.TEMPLATE_WIDTH - 50, 200, sc.isLocked(), showClose, owner);				
			this.content = table; 
			break;
		case ATTRIBUTE_MULTI:
			AttributeMultiSelection ta = null;
			//check the metatadata attr display
			boolean displayBlock = false;
			if (sc.getMetadata() != null && sc.getMetadata().size() > 0) {
				for (Metadata md : sc.getMetadata()) {
					if (md.getAttribute().equalsIgnoreCase("display") && md.getValue().equalsIgnoreCase("block")) {
						displayBlock = true;
						break;
					}
				}
			}
			//if it is saved as a Report is an instance of this class else is a simple text in a template
			if (sc.getPossibleContent() instanceof AttributeArea) {
				AttributeArea sata = (AttributeArea) sc.getPossibleContent();
				//in the metadata in this case there an attribute for diplayType
				ta = new AttributeMultiSelection(presenter, sc.getX(), sc.getY(), width, height, sata, displayBlock);
			}
			else {
				ta = new AttributeMultiSelection(presenter, sc.getX(), sc.getY(), width, height, sc.getPossibleContent().toString(), displayBlock);
			}
			ta.setMetadata(sc.getMetadata());
			this.content = ta; 
			break;
		case ATTRIBUTE_UNIQUE:
			AttributeSingleSelection atu = null;
			boolean displayBlock2 = false;
			if (sc.getMetadata() != null && sc.getMetadata().size() > 0) {
				for (Metadata md : sc.getMetadata()) {
					if (md.getAttribute().equalsIgnoreCase("display") && md.getValue().equalsIgnoreCase("block")) {
						displayBlock2 = true;
						break;
					}
				}
			}
			if (sc.getPossibleContent() instanceof AttributeArea) {
				AttributeArea sata = (AttributeArea) sc.getPossibleContent();  
				atu = new AttributeSingleSelection(presenter, sc.getX(), sc.getY(), width, height, sata, displayBlock2);
			}
			else {
				atu = new AttributeSingleSelection(presenter, sc.getX(), sc.getY(), width, height, sc.getPossibleContent().toString(), displayBlock2);
			}
			atu.setMetadata(sc.getMetadata());
			this.content = atu; 
			break;
		case COMMENT: {
			HTML comment = new HTML(); 
			comment.setStyleName("commentArea");
			String content = (String) sc.getPossibleContent();
			if (content == null) {
				content = "Empty Comment";
			}
			comment.setHTML(preserveLineBreaks(content));
			this.content = comment;
			break;
		}
		case INSTRUCTION:
			String content = (String) sc.getPossibleContent();
			if (content == null) {
				content = "Empty Instruction";
			}
			HTML instr = new HTML(); 
			instr.setStyleName("instructionArea");
			instr.setHTML(preserveLineBreaks(content));
			this.content = instr;

			break;
		case HIDDEN_FIELD:
			String hiddenValue = (String) sc.getPossibleContent();
			if (hiddenValue == null) {
				hiddenValue = "-1";
			}
			ExtHidden hiddenField = new  ExtHidden("SequenceId", hiddenValue); 
			hiddenField.setMetadata(sc.getMetadata());
			this.content = hiddenField;
			break;		
		case TIME_SERIES:
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
			//Log.debug("FOUND SEQUENCE trying getGroup");
			RepeatableSequence repeatableSequence = (RepeatableSequence) sc.getPossibleContent();
			ClientRepeatableSequence rps = new ClientRepeatableSequence(presenter, repeatableSequence);
			rps.setMetadata(sc.getMetadata());
			rps.setId(sc.getId());
			this.content = rps;
			break;
		case BODY_TABLE_IMAGE:
			TextTableImage tti = null;
			if (sc.getPossibleContent() == null) { //is is a template
				tti = new TextTableImage(presenter, true);
			} else {
				RepeatableSequence seq = (RepeatableSequence) sc.getPossibleContent();
				tti = new TextTableImage(presenter, seq);
				tti.setId(sc.getId());
			}				
			this.content = tti; 
			break;
		case REPORT_REFERENCE:
			GWT.log("FOUND Master SEQUENCE trying getGroup");

			ReportReferences refs = (ReportReferences) sc.getPossibleContent();
			if (refs != null) {
				ArrayList<Tuple> tuple = refs.getTuples();	
				ClientReportReference cmSeq = new ClientReportReference(presenter, refs.getRefType(), tuple, refs.isSingleRelation());
				cmSeq.setId(sc.getId());
				cmSeq.setMetadata(sc.getMetadata());
				this.content = cmSeq;
			} else
				this.content = new HTML("");
			break;
		}
	}
	/**
	 * 
	 * @param type
	 * @return
	 */
	private String getStyle(ComponentType type) {
		switch (type) {
		case TITLE:
			return "title-font";	
		case HEADING_1:
			return  "heading1-label";
		case HEADING_2:
			return  "heading2-label";
		case HEADING_3:
			return "heading3-label";
		case HEADING_4:
			return "heading4-label";
		case HEADING_5:
			return "heading5-label";
		default:
			return "";
		}		
	}
	/**
	 * replace all the line braks by <br/>, and all the double spaces by the html version &nbsp
	 * @param toPreserve
	 * @return an HTML with preserved line breaks and spaces 
	 */
	private String preserveLineBreaks(String toPreserve) {
		if (toPreserve != null) {
			String htmlPreservedLineBreaks = toPreserve.replaceAll("(\r\n|\n)","<br />"); //line breaks
			htmlPreservedLineBreaks = htmlPreservedLineBreaks.replaceAll("\\s\\s","&nbsp;&nbsp;"); //spaces
			return htmlPreservedLineBreaks;
		}
		return toPreserve;
	}
	/**
	 * return a template Component which is serializable
	 * @return .
	 */
	public BasicComponent getSerializable() {
		Serializable content = "";
		String id = "";
		List<Metadata> metas = null;
		switch (this.getType()) {
		case DYNA_IMAGE:
			ClientImage da = (ClientImage) this.content;
			content = da.getDroppedImage().getElement().getAttribute("src"); 
			id = da.getIdInBasket();
			if (((String) content).compareTo("") == 0)
				content = DEFAULT_IMAGE_PATH; 
			metas = da.getMetadata();
			width = da.getImageWidth();
			height = da.getImageHeight();
			break;
		case BODY:
			if (this.isLocked()) {
				content = ((HTML) this.content).getHTML();
			}
			else {
				content = ((D4sRichTextarea) this.content).getHTML();
				id = ((D4sRichTextarea) this.content).getId();
				metas = ((D4sRichTextarea) this.content).getMetadata();
			}
			break;
		case HEADING_1:
		case HEADING_2:
		case HEADING_3:
		case HEADING_4:
		case HEADING_5:
		case TITLE:
			if (this.isLocked()) {
				content = ((HTML) this.content).getText();
			}
			else {
				content = ((HeadingTextArea) this.content).getText();
				id =  ((HeadingTextArea) this.content).getId();
				metas = ((HeadingTextArea) this.content).getMetadata();
			}
			break;
		case BODY_NOT_FORMATTED:
			if (this.isLocked()) {
				content = ((HTML) this.content).getText();
			}
			else {
				content = ((BasicTextArea) this.content).getText();
				id = ((BasicTextArea) this.content).getId();
				metas = ((BasicTextArea) this.content).getMetadata();

			}
			break;
		case TIME_SERIES:
			GWT.log("Found Time Series, not supported anymore", null);			
			break;
		case FLEX_TABLE:
			GenericTable gt = (GenericTable) this.content;
			Table st = gt.getSerializable();
			content = st;
			break;
		case ATTRIBUTE_MULTI:
			AttributeMultiSelection att = (AttributeMultiSelection) this.content;
			metas = att.getMetadata();
			content = att.getSerializable(); 
			break;
		case ATTRIBUTE_UNIQUE:
			AttributeSingleSelection atu = (AttributeSingleSelection) this.content;
			metas = atu.getMetadata();
			content = atu.getSerializable(); 
			break;
		case COMMENT:
			content = ((HTML) this.content).getHTML();
			break;
		case INSTRUCTION:
			content = ((HTML) this.content).getHTML();
			break;			
		case HIDDEN_FIELD:
			ExtHidden hidden = (ExtHidden) this.content;
			metas = hidden.getMetadata();
			content = hidden.getValue();
			break;
		case REPEAT_SEQUENCE:
			ClientRepeatableSequence repSeq = (ClientRepeatableSequence) this.content;
			id = repSeq.getId();
			metas = repSeq.getMetadata();			
			RepeatableSequence toStore = new RepeatableSequence(getSerializableSequence(repSeq), repSeq.getKey(), height);
			
			content = toStore;			
			break;
		case BODY_TABLE_IMAGE:
			TextTableImage tti = (TextTableImage) this.content;
			id = tti.getId();
			metas = tti.getMetadata();			
			RepeatableSequence toSave = new RepeatableSequence(getSerializableSequence(tti), height);		
			content = toSave;			
			break;
		case REPORT_REFERENCE:
			ClientReportReference cRef = (ClientReportReference) this.content;
			id = cRef.getId();
			metas = cRef.getMetadata();
			ReportReferences ref = new ReportReferences(cRef.getRefType(), cRef.getTupleList(), cRef.isSingleRelation());
			content = ref;			
			break;
		}			
		BasicComponent bc = new BasicComponent(x, y, width, height, templatePage, type, id, "param empty", content,  this.doubleColLayout, isLocked(),  metas);
		return bc;
	}

	/**
	 * this method constructs a RepeatableSequence sequence that can be serializable from the ClientRepeatableSequence
	 * @param repSeq
	 * @return
	 */
	private ArrayList<BasicComponent> getSerializableSequence(ClientSequence repSeq) {
		GWT.log("Serializing sequence ... ");
		ArrayList<BasicComponent> sComps = new ArrayList<BasicComponent>();
		for (TemplateComponent tc : repSeq.getGroupedComponents()) {
			//GWT.log(" Got " + tc.getType());
			sComps.add(tc.getSerializable());
		}
		return sComps;
	}

	/**
	 * this method constructs a ReportSequence that can be serializable from the TextTableImage Widget
	 * @param repSeq
	 * @return
	 */
	private ArrayList<BasicComponent> getSerializableSequence(TextTableImage seq) {
		ArrayList<BasicComponent> sComps = new ArrayList<BasicComponent>();
		for (TemplateComponent tc : seq.getAddedComponents()) {
			sComps.add(tc.getSerializable());
		}
		return sComps;
	}



	/**
	 * @return .
	 */
	public int getHeight() {return height;}
	/**
	 * @param height .
	 */
	public void setHeight(int height) {	this.height = height; }
	/**
	 * @return .
	 */
	public ComponentType getType() {return type;}
	/**
	 * @param type .
	 */
	public void setType(ComponentType type) {	this.type = type;}
	/**
	 * @return .
	 */
	public int getWidth() {	return width;}
	/**
	 * @param width .
	 */
	public void setWidth(int width) {this.width = width;}
	/**
	 * @return .
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

	/**
	 * 
	 * @return .
	 */
	public boolean isLocked() {
		return locked;
	}
	/**
	 * 
	 * @param locked .
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	/**
	 * 
	 * @param attr . 
	 * @param value .
	 */
	public void addMetadata(String attr, String value) {
		if (attr != null && value != null) {
			metadata.add(new Metadata(attr, value));
		}
		else 
			throw new NullPointerException();
	}

	/**
	 * 
	 * @return .
	 */
	public List<Metadata> getAllMetadata() {
		if (metadata == null) {
			new LinkedList<Metadata>();
		}	
		return metadata;
	}
	/**
	 * 
	 * @return
	 */
	public String getUserComments() {
		if (metadata == null) return null;	
		for (Metadata md : metadata) {
			if (md.getAttribute().equals(TemplateModel.USER_COMMENT)) 
				return md.getValue();
		}
		return null;
	}
	/**
	 * 
	 * @return .
	 */
	public String getIdInBasket() {
		return id;
	}
	/**
	 * 
	 * @param id .
	 */
	public void setIdInBasket(String idInBasket) {
		this.id = idInBasket;
	}
	/**
	 * 
	 * @return .
	 */
	public boolean isDoubleColLayout() {
		return doubleColLayout;
	}


}
