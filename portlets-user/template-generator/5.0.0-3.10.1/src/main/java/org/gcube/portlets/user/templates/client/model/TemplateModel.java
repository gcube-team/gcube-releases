package org.gcube.portlets.user.templates.client.model;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.d4sreporting.common.shared.BasicSection;
import org.gcube.portlets.user.templates.client.TemplateService;
import org.gcube.portlets.user.templates.client.TemplateServiceAsync;
import org.gcube.portlets.user.templates.client.components.Coords;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * The <code> TemplateModel </code> class represents the current Template state, the model in the the MVC pattern 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class TemplateModel {

	private final TemplateServiceAsync modelService = GWT.create(TemplateService.class);
	/**
	 * default w and h 
	 */	
	public static final int OLD_TEMPLATE_WIDTH = 950;

	public static final int TEMPLATE_WIDTH = 750;
	/**
	 * TEMPLATE_HEIGHT
	 */
	public static final int TEMPLATE_HEIGHT= 1000;
	/**
	 * DEFAULT_NAME
	 */
	public static final String DEFAULT_NAME = "UnnamedTemplate";

	private String uniqueID;
	/**
	 * The name of the template
	 */
	private String templateName;
	/**
	 * The name of the author
	 */
	private String author;
	/**
	 * The name of the author
	 */
	private String lastEditBy;


	/**
	 * The name of the author
	 */
	private Date dateCreated;
	/**
	 * The name of the author
	 */
	private Date lastEdit;


	/**
	 * 
	 */
	private int pageWidth;

	/**
	 * 
	 */
	private int pageHeight;

	/**
	 * 
	 */
	private int currentPage;

	/**
	 * Total number of template pages 
	 */
	private int totalPages;

	/**
	 * Template left margin
	 */
	private int marginLeft;
	/**
	 * Template right margin 
	 */
	private int marginRight;
	/**
	 * Template top margin
	 */
	private int marginTop;
	/**
	 * Template bottom margin
	 */
	private int marginBottom;

	/**
	 * columnWidth is the actual page width without margins, when columns is equal to 1 (which is always true in my case, since UI doesn't allow multi columns)
	 */
	private int columnWidth;

	/**
	 * each object of this Hahsmap its a <class>TemplateSection</class> containing all the TemplateComponent of a template section
	 *
	 * object: a <class>TemplateSection</class> of Component containing all the TemplateComponent of the section
	 */

	private HashMap<String, TemplateSection> sections;
	/**
	 * holds the metadata(s) for the model
	 */
	private List<Metadata> metadata;


	/**
	 * Constructs a Default Template Model
	 */
	public TemplateModel() {
		super();
		this.uniqueID = "";
		this.templateName = DEFAULT_NAME; 
		this.pageWidth = TEMPLATE_WIDTH;
		this.pageHeight = TEMPLATE_HEIGHT;
		this.currentPage = 1;
		this.totalPages = 1;
		this.marginLeft = 25;
		this.marginRight = 25;
		this.marginTop = 20;
		this.marginBottom = 20;
		this.columnWidth = pageWidth - (marginLeft + marginRight);
		this.author = null;
		this.dateCreated = null;
		this.lastEdit = null;
		this.sections = new HashMap<String, TemplateSection>();
		this.metadata = new LinkedList<Metadata>();

		Timer renewSessionTimer = new Timer() {

			@Override
			public void run() {
				modelService.renewHTTPSession(new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {}
					@Override
					public void onSuccess(Void result) {}
				});				
			}
		};
		renewSessionTimer.scheduleRepeating(1000*60*30); // renew session every 30 minutes
	}


	/**
	 * 
	 * @param tc .
	 */
	public void addComponentToModel(TemplateComponent tc, int index) {
		String tcPage = ""+tc.getTemplatePage();	
		GWT.log("ADDING TO MODEL-> X=" + tc.getX() + " Y: " + tc.getY() + " Type: " + tc.getType() , null);

		if (sections.containsKey(tcPage)) {
			TemplateSection singleSection = sections.get(tcPage);

			singleSection.addComponent(tc, index);
		} 
		//else create the new Section 
		else {
			TemplateSection singleSection = new TemplateSection();
			singleSection.addComponent(tc, 0);			
			sections.put(tcPage, singleSection);
		}

		//debug
		//		TemplateSection section = sections.get(tcPage);
		//		for (TemplateComponent tcomp : section.getAllComponents()) {
		//			GWT.log("Section contains: " + tcomp.getType() + " Y: " + tc.getY(), null);
		//		}
		// stores the change in the session 
		storeInSession();
	}


	/**
	 * 
	 * @param toRemove .
	 */
	public TemplateComponent removeComponentFromModel(Widget toRemove) {
		String tcPage = ""+currentPage;	

		TemplateSection singleSection = sections.get(tcPage);
		TemplateComponent toReturn = singleSection.removeComponent(toRemove);	

		// stores the change in the session so when the user refreshes
		//the page has the last template state restored
		storeInSession();
		return toReturn;
	}


	/**
	 * 
	 * @param toUpdate
	 * @param newIndex
	 */
	public void updateModelComponentIndex(Widget toUpdate, int newIndex) {	
		String tcPage = ""+currentPage;			
		TemplateSection singleSection = sections.get(tcPage);
		singleSection.updateModelComponentIndex(toUpdate, newIndex);
	}


	/**
	 *  stores the current model in the session 
	 */
	@SuppressWarnings("unchecked")
	public void storeInSession() {
		AsyncCallback callback = new AsyncCallback() {
			public void onFailure(Throwable caught) { }
			public void onSuccess(Object result) {	}
		};

		Model modelToSend = getSerializableModel();

		//GWT.log("Storing in session: currpage = " + modelToSend.getCurrPage(), null);

		modelService.storeTemplateInSession(modelToSend, callback);
	}


	public void resetModelInSession() {
		AsyncCallback callback = new AsyncCallback() {
			public void onFailure(Throwable caught) { }
			public void onSuccess(Object result) {	
				Window.Location.reload();
			}
		};
		modelService.storeTemplateInSession(null, callback);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void serializeModel(String basketidToSaveIn) {
		final MessageBox box = MessageBox.wait("Saving Operation",  
				"saving template", "please wait...");  
		AsyncCallback callback = new AsyncCallback() {
			public void onFailure(Throwable caught) {
				box.close(); 
				MessageBox.alert("Saving Operation Report", "System encountered problems during template processing, could not save template", null);  
			}
			public void onSuccess(Object result) {
				box.close();  
				MessageBox.alert("Saving Operation Report", "Template Saved Successfully", null);  
			}
		};

		Model modelToSend = getSerializableModel();
		modelService.saveTemplate(basketidToSaveIn, modelToSend, callback);

		Timer t = new Timer() {  
			@Override  
			public void run() {  }  
		};  
		t.schedule(2500);  

	}

	/**
	 * 
	 * @return the serialized model
	 */
	private Model getSerializableModel() {
		Vector<BasicSection> serializedsections = new Vector<BasicSection>();

		for (int i = 1; i <= sections.size(); i++) {
			//the pages
			String pageNo = ""+i;
			if (sections.get(pageNo) != null) {

				TemplateSection singleSection = sections.get(pageNo);
				List<TemplateComponent> templateElements = singleSection.getAllComponents();
				List<BasicComponent> serialazableComponents = new LinkedList<BasicComponent>();
				//construct the serialized section
				BasicSection aSection = new BasicSection();
				//copy the components
				for (TemplateComponent tc : templateElements) {

					//					if (tc.getType() == ComponentType.REPEAT_SEQUENCE) {
					//						GWT.log("REPEAT_SEQUENCE FOUND");
					//						RepeatableSequence rse = (RepeatableSequence) tc.getContent();
					//	
					//						SerializableComponent ser = tc.getSerializable();
					//					}						
					serialazableComponents.add(tc.getSerializable());
				}

				aSection.setComponents(serialazableComponents);
				aSection.setMetadata(singleSection.getAllMetadata());

				//add the serialized section
				serializedsections.add(aSection);
			}


		}		
		Model toReturn = 
				new Model(uniqueID, author, dateCreated, lastEdit, lastEditBy, templateName, columnWidth, currentPage, marginBottom, marginLeft, marginRight, marginTop, 
						pageHeight, pageWidth, serializedsections, totalPages, metadata);

		return toReturn;
	}


	/**
	 * 
	 * @param sectNo the section to discard
	 * @return the removed element
	 */
	public TemplateSection discardSection(int sectNo) {

		TemplateSection toRemove = sections.remove(""+sectNo);
		for (int i = sectNo+1; i <= totalPages; i++) {
			sections.put(""+(i-1), sections.get(""+i));
		}
		totalPages--;
		return toRemove;
	}

	/**
	 * look for the model in the current page and edits its size
	 * @param toResize .
	 * @param newWidth .
	 * @param newHeight .
	 */
	public void resizeModelComponent(Widget toResize, int newWidth, int newHeight) {
		GWT.log("LOOKING CORRESPONDANCE", null);

		String tcPage = ""+currentPage;
		TemplateSection singleSection = sections.get(tcPage);
		singleSection.resizeModelComponent(toResize, newWidth, newHeight);

		storeInSession();
	}

	/**
	 * look for the model in the current section and edits its size
	 * @param toLock l
	 * @param locked .
	 *  
	 */
	public void lockComponent(Widget toLock, boolean locked) {
		String tcPage = ""+currentPage;
		TemplateSection singleSection = sections.get(tcPage);
		singleSection.lockComponent(toLock, locked);
		storeInSession();
	}

	/**
	 * look for the model in the current section and edits its position
	 * @param toRepos .
	 * @param newX .
	 * @param newY .
	 */
	public void repositionModelComponent(Widget toRepos, int newX, int newY) {
		String tcPage = ""+currentPage;
		TemplateSection singleSection = sections.get(tcPage);
		singleSection.repositionModelComponent(toRepos, newX, newY);
		storeInSession();
	}
	/**
	 *
	 */
	public void insertNewPage() {
		currentPage++;
		totalPages++;
		storeInSession();
	}
	/**
	 * @param pageNo .
	 * @return .
	 */
	public List<TemplateComponent> getSectionComponent(int pageNo) {
		return sections.get(""+pageNo).getAllComponents();
	}

	/**
	 * @param pageNo .
	 * @return .
	 */
	public TemplateSection getSection(int pageNo) {
		return sections.get(""+pageNo);
	}

	/**
	 * generally used when reading a model from disk
	 * @param toLoad the SerializableModel instance to load in the model
	 * @param controller .
	 */
	public void loadModel(Model toLoad, Presenter controller) {
		//loading template from disk
		this.author = toLoad.getAuthor();
		this.dateCreated = toLoad.getDateCreated();
		this.lastEdit = toLoad.getLastEdit();
		this.lastEditBy = toLoad.getLastEditBy();
		this.templateName = toLoad.getTemplateName(); 
		this.pageWidth = toLoad.getPageWidth();
		this.pageHeight = toLoad.getPageHeight();
		this.currentPage = toLoad.getCurrPage();
		this.totalPages = toLoad.getTotalPages();
		this.marginLeft = toLoad.getMarginLeft();
		this.marginRight = toLoad.getMarginRight();
		this.marginTop = toLoad.getMarginTop();
		this.marginBottom = toLoad.getMarginBottom();
		this.metadata = toLoad.getMetadata();
		this.columnWidth = pageWidth - (marginLeft + marginRight);


		//the pages to be transferred  
		Vector<BasicSection> sectionsSerialized = toLoad.getSections();

		//reset current pages container
		this.sections = new HashMap<String, TemplateSection>();

		//page Number, this model uses a HashMap for each page, the key is the page number
		int pageNo = 1;
		for (BasicSection serialazableSection : sectionsSerialized) { //for each section

			List<TemplateComponent> myTemplateSection = new Vector<TemplateComponent>();
			for (BasicComponent sc : serialazableSection.getComponents()) { 				//for each page component
				myTemplateSection.add(new TemplateComponent(this, sc, controller, true));
			}
			//TODO: load also metadata
			GWT.log("Section Metadata:"+serialazableSection.getMetadata().size(), null);
			this.sections.put(""+pageNo, new TemplateSection(myTemplateSection, serialazableSection.getMetadata()));
			pageNo++;
		}

	}

	/**
	 * generally used when reaing a model form disk
	 * @param toLoad the SerializableModel instance where toget the section
	 * @param sectionNoToimport section to import 0 -> n-1
	 * @param beforeSection say where to import this section (before)
	 * @param asLastSection say to import this section as last section in the curren template / report 
	 */
	public void importSectionInModel(Presenter controller, Model toLoad, int sectionNoToimport, int beforeSection, boolean asLastSection) {

		int pageNo = totalPages+1;

		//the section to be imported -1 beacuse it stays in a vector		
		BasicSection toImport = toLoad.getSections().get(sectionNoToimport-1);
		List<TemplateComponent> myTemplateSection = new Vector<TemplateComponent>();
		for (BasicComponent sc : toImport.getComponents()) { 				//for each page component
			myTemplateSection.add(new TemplateComponent(this, sc, controller, true));
		}
		//TODO: load also metadata
		GWT.log("Section Metadata:"+toImport.getMetadata().size(), null);
		if (asLastSection)
			this.sections.put(""+pageNo, new TemplateSection(myTemplateSection, toImport.getMetadata()));
		else {
			//insertin gnew section
			HashMap<String, TemplateSection> newSections = new HashMap<String, TemplateSection>();
			boolean isAdded = false;
			for (int i = 1; i <= totalPages+1; i++) {
				if (beforeSection == i) {
					newSections.put(""+i, new TemplateSection(myTemplateSection, toImport.getMetadata()));
					isAdded = true;
				}
				else {
					int insertIn = (isAdded) ? (i-1): i;
					newSections.put(""+i, sections.get(""+insertIn));
					GWT.log("Inserting " + insertIn + " into section " + i + " isAdded =" + (isAdded) , null);
				}
			}
			this.sections = newSections;
			GWT.log("NEW SECTION SIZE"+sections.size(), null);
		}

		totalPages++;


	}


	/**
	 * Returns the other page coomponents in the page except me
	 * @param toCheck the tc to except
	 * @return a Vector of TemplateComponent containing all the tc of the page except the toCheck
	 */
	public Vector<TemplateComponent> getOthersPageTC(Widget toCheck) {
		String tcPage = ""+currentPage;
		TemplateSection singleSection = sections.get(tcPage);
		Vector<TemplateComponent> toReturn = new Vector<TemplateComponent>();
		for (int i = 0; i < singleSection.getAllComponents().size(); i++) {
			TemplateComponent tc = singleSection.getComponent(i);
			if (! tc.getContent().equals(toCheck)) {
				toReturn.add(tc);
			}
		}
		return toReturn;
	}


	//	/****** GETTERS n SETTERS
	/**
	 * @return .
	 */
	public int getCurrentPage() {return currentPage;}
	/**
	 * @param currentPage .
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		storeInSession();
	}
	/**
	 * @return .
	 */
	public int getMarginBottom() {return marginBottom;}
	/**
	 * @param marginBottom .
	 */
	public void setMarginBottom(int marginBottom) {
		this.marginBottom = marginBottom;
		storeInSession();
	}
	/**
	 * @return .
	 */
	public int getMarginLeft() {return marginLeft;	}
	/**
	 * @param marginLeft .
	 */
	public void setMarginLeft(int marginLeft) {
		this.marginLeft = marginLeft;
		this.columnWidth = pageWidth - (marginLeft + marginRight);
		storeInSession();
	}
	/**
	 * @return .
	 */
	public int getMarginRight() {return marginRight;}
	/**
	 * @param marginRight .
	 */
	public void setMarginRight(int marginRight) {
		this.marginRight = marginRight;
		this.columnWidth = pageWidth - (marginLeft + marginRight);
		storeInSession();
	}
	/**
	 * @return .
	 */
	public int getMarginTop() {
		return marginTop;

	}
	/**
	 * @param marginTop .
	 */
	public void setMarginTop(int marginTop) {
		this.marginTop = marginTop;
		storeInSession();
	}
	/**
	 * @return .
	 */
	public int getPageHeight() {return pageHeight;	}
	/**
	 * @param pageHeight .
	 */
	public void setPageHeight(int pageHeight) {
		this.pageHeight = pageHeight;
		storeInSession();
	}
	/**
	 * @return .
	 */
	public int getPageWidth() {	return pageWidth;}
	/**
	 * @param pageWidth .
	 */
	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
		this.columnWidth = pageWidth - (marginLeft + marginRight);
		storeInSession();
	}
	/**
	 * @return .
	 */
	public String getTemplateName() {return templateName;}
	/**
	 * @param templateName .
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
		storeInSession();
	}

	/**
	 * @return totalPages .
	 */
	public int getTotalPages() {return totalPages;	}

	/**
	 * @param totalPages .
	 */
	public void setTotalPages(int totalPages) {	
		this.totalPages = totalPages;
		storeInSession();	
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public Date getDateCreated() {
		return dateCreated;
	}


	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}


	public Date getLastEdit() {
		return lastEdit;
	}


	public void setLastEdit(Date lastEdit) {
		this.lastEdit = lastEdit;
	}


	/**
	 * @return .
	 */
	public int getColumnWidth() {return columnWidth;}
	/**
	 * @param columnWidth .
	 */
	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
		storeInSession();
	}
	/**
	 * @return .
	 */
	public TemplateServiceAsync getModelService() {return modelService;}

	/**
	 * metadata
	 * @return
	 */
	public List<Metadata> getMetadata() {
		return metadata;
	}


	public String getLastEditBy() {
		return lastEditBy;
	}


	public void setLastEditBy(String lastEditBy) {
		this.lastEditBy = lastEditBy;
	}
	/**
	 * set the insertion point after the last inserted element
	 * @param currSection
	 * @return
	 */
	public Coords getNewInsertionPoint(int currSection) 
	{
		List<TemplateComponent> elems = getSectionComponent(currSection);
		if (elems == null)
			return new Coords(25, 25);
		int maxY = 0;
		for (TemplateComponent templateComponent : elems) {
			GWT.log("->" + templateComponent.getY() + " height: " + templateComponent.getHeight(), null);
			if (maxY < templateComponent.getY() + templateComponent.getHeight())
				maxY = templateComponent.getY() + templateComponent.getHeight();

		}
		GWT.log("New insertion Point-> " + maxY, null);
		return new Coords(25, maxY-25);
	}
}
