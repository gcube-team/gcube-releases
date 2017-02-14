package org.gcube.portlets.user.templates.client.dialogs;

import java.util.ArrayList;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.user.templates.client.TGenConstants;
import org.gcube.portlets.user.templates.client.components.Coords;
import org.gcube.portlets.user.templates.client.components.D4sRichTextarea;
import org.gcube.portlets.user.templates.client.components.DroppingArea;
import org.gcube.portlets.user.templates.client.components.ExtButton;
import org.gcube.portlets.user.templates.client.components.images.Images;
import org.gcube.portlets.user.templates.client.model.TemplateModel;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;


/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 *
 */
public class ToolboxDialog extends Window {
	protected Images images = (Images) GWT.create(Images.class);
	Presenter presenter;
	
	final public static int dialogWidth = 100;
	final public static int dialogHeight = 600;
	
	final public static int buttonWidth = 165;
	final public static int buttonWidth2 = 80;
	
	final public static int textareaHeight = 35;
	
	
	
	protected static ExtButton titleb = new ExtButton("Title", ComponentType.TITLE);
	protected static ExtButton h1b = new ExtButton("Heading 1",  ComponentType.HEADING_1);
	protected static ExtButton h2b = new ExtButton("Heading 2",  ComponentType.HEADING_2);
	protected static ExtButton h3b = new ExtButton("Heading 3",  ComponentType.HEADING_3);
	protected static ExtButton h4b = new ExtButton("Heading 4",  ComponentType.HEADING_4);
	protected static ExtButton h5b = new ExtButton("Heading 5",  ComponentType.HEADING_5);
	protected static ExtButton imageb = new ExtButton("Image",  ComponentType.IMAGE);
	protected static ExtButton textb = new ExtButton("Text",  ComponentType.BODY);
	protected static ExtButton simpletextb = new ExtButton("String",  ComponentType.BODY_NOT_FORMATTED);
	protected static ExtButton tableb = new ExtButton("Table",  ComponentType.FLEX_TABLE);
	protected static ExtButton repeat = new ExtButton("Repetitive",  ComponentType.REPEAT_SEQUENCE);
	protected static ExtButton commentb = new ExtButton("Comment area",  ComponentType.COMMENT);
	protected static ExtButton instructionB = new ExtButton("Instruction area",  ComponentType.INSTRUCTION);
	protected static ExtButton attributeb = new ExtButton("Attribute (Multi)",  ComponentType.ATTRIBUTE_MULTI);
	protected static ExtButton attribute1b = new ExtButton("Attribute (Unique)",  ComponentType.ATTRIBUTE_UNIQUE);
	protected static ExtButton mixedTextb = new ExtButton("Text/Table/Image",  ComponentType.BODY_TABLE_IMAGE);
	protected static ExtButton pageB = new ExtButton("Page break",  ComponentType.PAGEBREAK);
	
	protected static ArrayList<ExtButton> repetitiveElements = new ArrayList<ExtButton>();
	
	public ToolboxDialog(final Presenter presenter, int left, int top) {
		super();
		this.presenter = presenter;
		
		
		setPosition(left, top+50);
	    setMaximizable(false);  
	    setResizable(false);
	    setHeading("Toolbox");  
	    setWidth(dialogWidth);  
	    setHeight(dialogHeight);  
	    //setIcon(Resources.ICONS.accordion());  
	    setLayout(new AccordionLayout());  
	    
	    VBoxLayout westLayout = new VBoxLayout();  
	    westLayout.setPadding(new Padding(5));  

	    westLayout.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);  
	    
	    VBoxLayout dcLayout = new VBoxLayout();  
	    dcLayout.setPadding(new Padding(5));  

	    dcLayout.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);  
	    
	    ContentPanel sc = new ContentPanel();  
	    sc.setSize(100, 400);
	    sc.setAnimCollapse(false);  
	    sc.setHeading("Fit layout");  
	    sc.setLayout(westLayout);  
	   // cp.getHeader().addTool(new ToolButton("x-tool-refresh"));  
	   
	
	    
	    ContentPanel dc = new ContentPanel();  
	    dc.setAnimCollapse(false);  
	    dc.setHeading("Double column layout");  
	    dc.setLayout(dcLayout);  
	   // cp.getHeader().addTool(new ToolButton("x-tool-refresh"));  
	  
	    add(sc); 
	   // add(dc); //double column not needed
	    
	    //
	  
	    titleb.setSize(buttonWidth2, 25);
	    titleb.setIconAlign(IconAlign.LEFT); 
	    titleb.setIcon(AbstractImagePrototype.create(images.title()));
	    titleb.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.insertStaticTextArea(ComponentType.TITLE, TemplateModel.TEMPLATE_WIDTH - 50, 40);
			}
	    });
	  
	    h1b.setSize(buttonWidth2, 25);
	    h1b.setIconAlign(IconAlign.LEFT); 
	    h1b.setIcon(AbstractImagePrototype.create(images.heading_1()));
	    h1b.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.insertStaticTextArea(ComponentType.HEADING_1, TemplateModel.TEMPLATE_WIDTH - 50, textareaHeight);
			}
	    });
	   
	    h2b.setSize(buttonWidth2, 25);
	    h2b.setIconAlign(IconAlign.LEFT); 
	    h2b.setIcon(AbstractImagePrototype.create(images.heading_2()));
	    h2b.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.insertStaticTextArea(ComponentType.HEADING_2, TemplateModel.TEMPLATE_WIDTH - 50, textareaHeight);
			}
	    });
	   
	    h3b.setSize(buttonWidth2, 25);
	    h3b.setIconAlign(IconAlign.LEFT); 
	    h3b.setIcon(AbstractImagePrototype.create(images.heading_3()));
	    h3b.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.insertStaticTextArea(ComponentType.HEADING_3, TemplateModel.TEMPLATE_WIDTH - 50, textareaHeight);
			}
	    });
	  
	    h4b.setSize(buttonWidth2, 25);
	    h4b.setIconAlign(IconAlign.LEFT); 
	    h4b.setIcon(AbstractImagePrototype.create(images.heading_4()));
	    h4b.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.insertStaticTextArea(ComponentType.HEADING_4, TemplateModel.TEMPLATE_WIDTH - 50, textareaHeight);
			}
	    });
	  
	    h5b.setSize(buttonWidth2, 25);
	    h5b.setIconAlign(IconAlign.LEFT); 
	    h5b.setIcon(AbstractImagePrototype.create(images.heading_5()));
	    h5b.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.insertStaticTextArea(ComponentType.HEADING_5, TemplateModel.TEMPLATE_WIDTH - 50, textareaHeight);
			}
	    });
	 
	    imageb.setSize(buttonWidth, 25);
	    imageb.setIconAlign(IconAlign.LEFT); 
	    imageb.setIcon(AbstractImagePrototype.create(images.image()));
	    imageb.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.insertDroppingArea(ComponentType.DYNA_IMAGE, TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT);
			}
	    });
	   
	    textb.setSize(buttonWidth2, 25);
	    textb.setIconAlign(IconAlign.LEFT); 
	    textb.setIcon(AbstractImagePrototype.create(images.text()));
	    textb.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.insertStaticTextArea(ComponentType.BODY, TemplateModel.TEMPLATE_WIDTH - 50, 75);
			}
	    });
	    
	    simpletextb.setSize(buttonWidth2, 25);
	    simpletextb.setIconAlign(IconAlign.LEFT); 
	    simpletextb.setIcon(AbstractImagePrototype.create(images.simple_text()));
	    simpletextb.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.insertStaticTextArea(ComponentType.BODY_NOT_FORMATTED, TemplateModel.TEMPLATE_WIDTH - 50, textareaHeight);
			}
	    });
	   
	    tableb.setSize(buttonWidth, 25);
	    tableb.setIconAlign(IconAlign.LEFT); 
	    tableb.setIcon(AbstractImagePrototype.create(images.table()));
	    tableb.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.showTablePopup(ComponentType.FLEX_TABLE, TemplateModel.TEMPLATE_WIDTH - 50, 50);
			}
	    });
	    
	    repeat.setSize(buttonWidth, 25);
	    repeat.setIconAlign(IconAlign.LEFT); 
	    repeat.setIcon(AbstractImagePrototype.create(images.repetitive()));
	    repeat.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.openGroupingDialog();
			}
	    });
	   
	    commentb.setSize(buttonWidth, 25);
	    commentb.setIconAlign(IconAlign.LEFT); 
	    commentb.setIcon(AbstractImagePrototype.create(images.comment_area()));
	    commentb.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.addCommentArea(TemplateModel.TEMPLATE_WIDTH - 50, 60, false);
			}
	    });
	   
	    attributeb.setSize(buttonWidth, 25);
	    attributeb.setIconAlign(IconAlign.LEFT); 
	    attributeb.setIcon(AbstractImagePrototype.create(images.attr_multi()));
	    attributeb.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.openAttributeDialog(TemplateModel.TEMPLATE_WIDTH - 50, 50, true);
			}
	    });
	    
	    attribute1b.setSize(buttonWidth, 25);
	    attribute1b.setIconAlign(IconAlign.LEFT); 
	    attribute1b.setIcon(AbstractImagePrototype.create(images.attr_unique()));
	    attribute1b.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.openAttributeDialog(TemplateModel.TEMPLATE_WIDTH - 50, 50, false);
			}
	    });
	    
	    instructionB.setSize(buttonWidth, 25);
	    instructionB.setIconAlign(IconAlign.LEFT); 
	    instructionB.setIcon(AbstractImagePrototype.create(images.instruction_area()));
	    instructionB.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.addInstructionArea(TemplateModel.TEMPLATE_WIDTH - 50, 60, false);				
			}
	    });
	    
	    mixedTextb.setSize(buttonWidth, 25);
	    mixedTextb.setIconAlign(IconAlign.LEFT); 
	    mixedTextb.setIcon(AbstractImagePrototype.create(images.textTableImage()));
	    mixedTextb.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.insertPlaceHolder(ComponentType.BODY_TABLE_IMAGE, TemplateModel.TEMPLATE_WIDTH - 50, 50);	
			}
	    });
	    
	    pageB.setSize(buttonWidth, 25);
	    pageB.setIconAlign(IconAlign.LEFT); 
	    pageB.setIcon(AbstractImagePrototype.create(images.page_break()));
	    pageB.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				presenter.addPageBreak();
			}
	    });
	    
	    //this is needed because the same buttons are used in repetitive elements
	    repetitiveElements.add(h1b);
	    repetitiveElements.add(h2b);
	    repetitiveElements.add(h3b);  
	    repetitiveElements.add(h4b);
	    repetitiveElements.add(h5b);
	    repetitiveElements.add(imageb);
	    repetitiveElements.add(textb);
	    repetitiveElements.add(simpletextb);
	    repetitiveElements.add(tableb);
	    repetitiveElements.add(attribute1b);
	    repetitiveElements.add(attributeb);
	    
	    
	 	    
	    HorizontalPanel hp1 = new HorizontalPanel();
	    hp1.add(titleb);
	    hp1.add(new HTML("&nbsp;"));
	    hp1.add(h1b);
	    sc.add(hp1);
	    
	    sc.add(new HTML("&nbsp;"));  //spacer
	    
	    HorizontalPanel hp2 = new HorizontalPanel();
	    hp2.add(h2b);
	    hp2.add(new HTML("&nbsp;"));
	    hp2.add(h3b);
	    sc.add(hp2);
	    	    
	    sc.add(new HTML("&nbsp;"));
	    
	    
	    HorizontalPanel hp3 = new HorizontalPanel();
	    hp3.add(h4b);
	    hp3.add(new HTML("&nbsp;"));
	    hp3.add(h5b);
	    sc.add(hp3);
	    
	    sc.add(new HTML("&nbsp;"));
	     	    
	    HorizontalPanel hp4 = new HorizontalPanel();
	    hp4.add(textb);
	    hp4.add(new HTML("&nbsp;"));
	    hp4.add(simpletextb);
	    sc.add(hp4);
	   
	    sc.add(new HTML("&nbsp;"));
	    sc.add(imageb);
	  
	    sc.add(new HTML("&nbsp;"));
	    sc.add(tableb);
	  
	    sc.add(new HTML("&nbsp;"));
	    sc.add(repeat);
	    sc.add(new HTML("&nbsp;"));
	    sc.add(attributeb);
	    sc.add(new HTML("&nbsp;"));
	    sc.add(attribute1b);
	    sc.add(new HTML("&nbsp;"));
	    sc.add(mixedTextb);
	    sc.add(new HTML("&nbsp;"));
	    sc.add(commentb);
	    sc.add(new HTML("&nbsp;"));
	    sc.add(instructionB);
	  
	    sc.add(new HTML("&nbsp;"));
	    sc.add(pageB);
	    
	    //**** Double Cols
	    
	    Button textextB = new Button("Text Text");
	    textextB.setSize(buttonWidth, 25);
	    textextB.setIconAlign(IconAlign.LEFT); 
	    textextB.setIcon(AbstractImagePrototype.create(images.text_text()));
	    textextB.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				Coords start = presenter.getInsertionPoint();		
				D4sRichTextarea leftItem = new D4sRichTextarea(ComponentType.BODY, presenter, start.getX(), start.getY(), TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT);
				D4sRichTextarea rightItem = new D4sRichTextarea(ComponentType.BODY, presenter, start.getX(), start.getY(),  TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT);
				presenter.insertDoubleColumnItems(leftItem, rightItem);
			}
	    });
	    Button texImageB = new Button("Text Image");
	    texImageB.setSize(buttonWidth, 25);
	    texImageB.setIconAlign(IconAlign.LEFT); 
	    texImageB.setIcon(AbstractImagePrototype.create(images.text_image()));
	    texImageB.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				Coords start = presenter.getInsertionPoint();
				D4sRichTextarea leftItem = new D4sRichTextarea(ComponentType.BODY, presenter, start.getX(), start.getY(),  TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT);
				leftItem.addStyleName("titleArea");
				DroppingArea rightItem = new DroppingArea(presenter, start.getX(), start.getY(), TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT, true);
				presenter.insertDoubleColumnItems(leftItem, rightItem);
			}
	    });
	    Button imageTextB = new Button("Image Text");
	    imageTextB.setSize(buttonWidth, 25);
	    imageTextB.setIconAlign(IconAlign.LEFT); 
	    imageTextB.setIcon(AbstractImagePrototype.create(images.text_image()));
	    imageTextB.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				Coords start = presenter.getInsertionPoint();		
				DroppingArea leftItem = new DroppingArea(presenter, start.getX(), start.getX(), TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT, true);
				D4sRichTextarea rightItem = new D4sRichTextarea(ComponentType.BODY, presenter, start.getX(), start.getY(),  TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT);
				presenter.insertDoubleColumnItems(leftItem, rightItem);
			}
	    });
	    Button imageimageB = new Button("Image Image");
	    imageimageB.setSize(buttonWidth, 25);
	    imageimageB.setIconAlign(IconAlign.LEFT); 
	    imageimageB.setIcon(AbstractImagePrototype.create(images.image()));
	    imageimageB.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				Coords start = presenter.getInsertionPoint();		
				DroppingArea leftItem = new DroppingArea(presenter, start.getX(), start.getX(), TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT, true);
				DroppingArea rightItem = new DroppingArea(presenter, start.getX(), start.getY(), TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT, true);
				presenter.insertDoubleColumnItems(leftItem, rightItem);
			}
	    });
	    
	    
	    dc.add(textextB);
	    dc.add(new HTML("&nbsp;"));
	    dc.add(texImageB);
	    dc.add(new HTML("&nbsp;"));
	    dc.add(imageTextB);
	    dc.add(new HTML("&nbsp;"));
	    dc.add(imageimageB);
	    dc.add(new HTML("&nbsp;"));

	}
	

}
