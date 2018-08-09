package org.gcube.portlets.user.reportgenerator.client.model;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.BasicSection;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.d4sreporting.common.shared.RepTimeSeries;
import org.gcube.portlets.user.reportgenerator.client.ReportService;
import org.gcube.portlets.user.reportgenerator.client.ReportServiceAsync;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


/**
 * The <code> TemplateModel </code> class represents the current Template state, the model in the the MVC pattern 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class TemplateModel {

	private ReportServiceAsync modelService = (ReportServiceAsync) GWT.create(ReportService.class);
	private ServiceDefTarget endpoint = (ServiceDefTarget) modelService;
	/**
	 * default w and h 
	 */

	public static final int OLD_TEMPLATE_WIDTH = 950;
	/**
	 * 
	 */
	public static final int TEMPLATE_WIDTH = 750;

	/**
	 * DEFAULT_NAME
	 */
	public static final String DEFAULT_NAME = "No reports/templates loaded";
	public static final String BIBLIO_SECTION = "isBibliography";
	public static final String USER_COMMENT = "isComment";
	public static final String USER_COMMENT_HEIGHT = "isCommentHeight";
	/**
	 * The id of the model
	 */
	private String id;
	/**
	 * The name of the template
	 */
	private String templateName;
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

	private Presenter presenter;
	/**
	 * Constructs a Default Template Model
	 * @param presenter .
	 */

	public TemplateModel(Presenter presenter) {
		super();
		this.templateName = DEFAULT_NAME; 
		this.pageWidth = TEMPLATE_WIDTH;
		this.pageHeight = -1;
		this.currentPage = 1;
		this.totalPages = 1;
		this.marginLeft = 25;
		this.marginRight = 25;
		this.marginTop = 20;
		this.marginBottom = 20;
		this.columnWidth = pageWidth - (marginLeft + marginRight);
		this.author = "";
		this.lastEdit = null;
		this.lastEditBy = "";
		this.dateCreated = null;

		this.sections = new HashMap<String, TemplateSection>();
		this.metadata = new LinkedList<Metadata>();

		this.presenter = presenter;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ReportServiceImpl";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		
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
	 * @param pageNo .
	 * @return .
	 */
	public List<TemplateComponent> getSectionComponent(int pageNo) {
		List<TemplateComponent> toReturn = new LinkedList<TemplateComponent>();

		toReturn = sections.get(""+pageNo).getAllComponents();

		return toReturn;
	}
	/**
	 * look for the model in the current page and edits its size
	 * @param toResize .
	 * @param newWidth .
	 * @param newHeight .
	 */
	public void addCommentToComponent(Widget component, String comment2Add, int visibleHeight) {
		String tcPage = ""+currentPage;
		TemplateSection singleSection = sections.get(tcPage);
		singleSection.addCommentToComponent(component, comment2Add, visibleHeight);
	}
	
	public void removeComment(Widget toRemove) {
		String tcPage = ""+currentPage;
		TemplateSection singleSection = sections.get(tcPage);
		singleSection.discardComments(toRemove);
	}
	/**
	 *
	 */
	public void insertBiblioSection() {
		totalPages++;

		TemplateSection singleSection = new TemplateSection();
		singleSection.addMetadata(BIBLIO_SECTION, "true");

		BasicComponent references = new BasicComponent(0, 0,TEMPLATE_WIDTH - 50, 35,
				totalPages, ComponentType.HEADING_2, "", "REFERENCES", false, true, singleSection.getAllMetadata());
		TemplateComponent referencesTC = new TemplateComponent(this, references, presenter, false, null);

		singleSection.addComponent(referencesTC);
		sections.put(""+totalPages, singleSection);
	}
	/**
	 * 
	 * @param citeKey -
	 * @param citeText -
	 */
	public void addCitation(String citeKey, String citeText) {
		String citation ="<b>" + citeKey + ".</b>&nbsp;" + citeText;
		TemplateSection singleSection = getSection(totalPages);
		BasicComponent entry = new BasicComponent(0, 0,TEMPLATE_WIDTH - 50, 35,
				totalPages, ComponentType.HEADING_2, "", "Bibliographic Entry", false, true, singleSection.getAllMetadata());
		TemplateComponent entryTC = new TemplateComponent(this, entry, presenter, false, null);
		singleSection.addComponent(entryTC);
		BasicComponent entryText = new BasicComponent(0, 0,TEMPLATE_WIDTH - 50, 35,
				totalPages, ComponentType.BODY, "", citation, false, false, singleSection.getAllMetadata());
		TemplateComponent entryTextTC = new TemplateComponent(this, entryText, presenter, false, null);
		singleSection.addComponent(entryTextTC);

	}
	/**
	 * remove a Citation from the model
	 * @param citeKey .
	 */
	public boolean removeCitation(String citeKey) {
		TemplateSection singleSection = getSection(totalPages);
		List<TemplateComponent> components = singleSection.getAllComponents();
		for (int i = 0; i < components.size(); i++) {
			TemplateComponent tc = components.get(i);
			if (tc.getType() == ComponentType.BODY) {
				BasicComponent sc = tc.getSerializable();
				HTML citationHTML = new HTML(sc.getPossibleContent().toString(), true); ///to clean the HTML
				String citation = citationHTML.getText();
				if (citation.startsWith(citeKey)) {
					if (singleSection.removeComponent(tc)) { //removes also the previous heading 2
						TemplateComponent h2 = components.get(i-1);
						singleSection.removeComponent(h2);
						return true;
					}
						
				}
			}
		}
		return false;
	}
	/**
	 * @param pageNo .
	 * @return .
	 */
	public TemplateSection getSection(int pageNo) {
		return sections.get(""+pageNo);
	}
	/**
	 * generally used when reaing a model form disk
	 * @param toLoad the SerializableModel instance to load in the model
	 * @param presenter .
	 */
	public void loadModel(Model toLoad, Presenter presenter) {
		//loading template from disk

		this.id = toLoad.getUniqueID();
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



		//the sections to be transferred  
		Vector<BasicSection> sectionsSerialized = toLoad.getSections();

		//reset current sections container
		this.sections = new HashMap<String, TemplateSection>();

		//page Number, this model uses a HashMap for each page, the key is the page number
		int pageNo = 1;
		for (BasicSection serialazableSection : sectionsSerialized) { //for each section

			List<TemplateComponent> myTemplateSection = new Vector<TemplateComponent>();
			for (BasicComponent sc : serialazableSection.getComponents()) { 				//for each page component
				myTemplateSection.add(new TemplateComponent(this, sc, presenter, false, null));
			}
			//TODO: load also metadata
			GWT.log("Section Metadata:"+serialazableSection.getMetadata().size(), null);
			this.sections.put(""+pageNo, new TemplateSection(myTemplateSection, serialazableSection.getMetadata()));
			pageNo++;
		}

	}


	/**
	 * generally used when reading a model form disk
	 * @param toLoad the SerializableModel instance where toget the section
	 * @param sectionNoToimport section to import 0 -> n-1
	 * @param beforeSection say where to import this section (before)
	 * @param asLastSection say to import this section as last section in the curren template / report 
	 */
	public void importSectionInModel(Model toLoad, int sectionNoToimport, int beforeSection, boolean asLastSection) {

		int pageNo = totalPages+1;

		//the section to be imported -1 beacuse it stays in a vector		
		BasicSection toImport = toLoad.getSections().get(sectionNoToimport-1);
		List<TemplateComponent> myTemplateSection = new Vector<TemplateComponent>();
		for (BasicComponent sc : toImport.getComponents()) { 				//for each page component
			myTemplateSection.add(new TemplateComponent(this, sc, presenter, false, null));
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
					//GWT.log("Inserting " + insertIn + " into section " + i + " isAdded =" + (isAdded) , null);
				}
			}
			this.sections = newSections;
			//GWT.log("NEW SECTION SIZE"+sections.size(), null);
		}

		totalPages++;		
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
	 * 
	 * @return a serialized version od the model
	 */
	public Model getSerializableModel() {
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
				for (TemplateComponent tc : templateElements) 
					serialazableComponents.add(tc.getSerializable());

				aSection.setComponents(serialazableComponents);
				aSection.setMetadata(singleSection.getAllMetadata());

				//add the serialized section
				serializedsections.add(aSection);
			}


		}		
		Model toReturn = 
			new Model(id, author, dateCreated, lastEdit, lastEditBy, templateName, columnWidth, currentPage, marginBottom, marginLeft, marginRight, marginTop, 
					pageHeight, pageWidth, serializedsections, totalPages, metadata);

		return toReturn;
	}
	
	/**
	 * look for the model in the current page and edits its size
	 * @param toResize .
	 * @param newWidth .
	 * @param newHeight .
	 */
	public void resizeModelComponent(Widget toResize, int newWidth, int newHeight) {
		//GWT.log("LOOKING CORRESPONDANCE", null);

		String tcPage = ""+currentPage;
		TemplateSection singleSection = sections.get(tcPage);
		singleSection.resizeModelComponent(toResize, newWidth, newHeight);
	}


	
	/**
	 * 
	 * @param type a
	 * @param templateName  a
	 * return a URL which is lookable for on the web
	 * 
	 * @return .
	 */
	public String getExportedFileURL(ExportManifestationType type, String templateName) {
		/**
		 * PDFs will be stored under webapps/usersArea...
		 */
		// get e.g. http://dlib28.isti.cnr.it:9090/
		String host = Window.Location.getProtocol()+"//"+Window.Location.getHost()+"/";
		String exportedURL = "";

		switch (type) {
		case DOCX:
			exportedURL = host + "usersArea/"	+ presenter.getCurrentScope() + "/templates/" 
			+ presenter.getCurrentUser() + "/EXPORTS/" + templateName + ".docx";
			break;
		case PDF:
			exportedURL = host + "usersArea/"	+ presenter.getCurrentScope() + "/templates/" 
			+ presenter.getCurrentUser() + "/EXPORTS/" + templateName + ".pdf";
			break;
		case HTML:
			exportedURL = host + "usersArea/"	+ presenter.getCurrentScope() + "/templates/" 
			+ presenter.getCurrentUser() + "/EXPORTS/" + templateName + ".html";

		}
		return exportedURL;
	}

	//****** GETTERS n SETTERS
	/**
	 * @return .
	 */
	public int getCurrentPage() {return currentPage;}
	/**
	 * @param currentPage .
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	/**
	 * @return .
	 */
	public int getMarginBottom() {return marginBottom;}
	/**
	 * @param marginBottom .
	 */
	public void setMarginBottom(int marginBottom) {this.marginBottom = marginBottom;}
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
	}
	/**
	 * @return .
	 */
	public int getMarginTop() {return marginTop;}
	/**
	 * @param marginTop .
	 */
	public void setMarginTop(int marginTop) {this.marginTop = marginTop;}
	/**
	 * @return .
	 */
	public int getPageHeight() {return pageHeight;	}
	/**
	 * @param pageHeight .
	 */
	public void setPageHeight(int pageHeight) {	this.pageHeight = pageHeight;}
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
	}

	/**
	 * @return totalPages .
	 */
	public int getTotalPages() {return totalPages;	}

	/**
	 * @param totalPages .
	 */
	public void setTotalPages(int totalPages) {	this.totalPages = totalPages;}
	/**
	 * @return .
	 */
	public int getColumnWidth() {return columnWidth;}
	/**
	 * @param columnWidth .
	 */
	public void setColumnWidth(int columnWidth) {this.columnWidth = columnWidth;}
	/**
	 * @return .
	 */
	public ReportServiceAsync getModelService() {return modelService;}


	/**
	 * metadata .
	 * @return .
	 */
	public List<Metadata> getMetadata() {
		return metadata;
	}

	/**
	 * 
	 * @return .
	 * 
	 */
	public String getLastEditBy() {
		return lastEditBy;
	}


	/**
	 * 
	 * @param lastEditBy .
	 */
	public void setLastEditBy(String lastEditBy) {
		this.lastEditBy = lastEditBy;
	}

	/**
	 * 
	 * @return .
	 */
	public boolean containsLargeTS() {
		sections.entrySet();
		for (Entry entry : sections.entrySet()) {
			TemplateSection section = (TemplateSection) entry.getValue();
			for (TemplateComponent tc : section.getAllComponents()) {
				if (tc.getType() == ComponentType.TIME_SERIES) {
					BasicComponent sc = tc.getSerializable();
					RepTimeSeries sts = (RepTimeSeries) sc.getPossibleContent();
					if (sts.getFilter() != null) {
						int from = sts.getFilter().getFrom();
						int to = sts.getFilter().getTo();
						if (to - from > 200)
							return true;
					}
				}
			}
		}
		return false;		
	}

	public void updateWorkflowDocument(Model toSave, boolean update) {
	

	}
	

}

