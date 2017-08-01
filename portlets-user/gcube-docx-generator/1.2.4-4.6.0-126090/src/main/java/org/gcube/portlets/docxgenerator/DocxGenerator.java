package org.gcube.portlets.docxgenerator;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.convert.out.html.HtmlExporterNG2;
import org.docx4j.convert.out.html.AbstractHtmlExporter.HtmlSettings;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.docProps.core.CoreProperties;
import org.docx4j.docProps.core.dc.elements.SimpleLiteral;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.JaxbValidationEventHandler;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DocPropsCorePart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.Styles;

import org.gcube.portlets.d4sreporting.common.shared.*;
import org.gcube.portlets.docxgenerator.content.Content;
import org.gcube.portlets.docxgenerator.content.PContent;
import org.gcube.portlets.docxgenerator.content.RContent;
import org.gcube.portlets.docxgenerator.content.TableContent;
import org.gcube.portlets.docxgenerator.transformer.AttributesTransformer;
import org.gcube.portlets.docxgenerator.transformer.HeadingTransformer;
import org.gcube.portlets.docxgenerator.transformer.ImageTransformer;
import org.gcube.portlets.docxgenerator.transformer.PageBreakTransformer;
import org.gcube.portlets.docxgenerator.transformer.RTimeSeriesTransform;
import org.gcube.portlets.docxgenerator.transformer.TOCTransformer;
import org.gcube.portlets.docxgenerator.transformer.TableTransformer;
import org.gcube.portlets.docxgenerator.transformer.TextTransformer;
import org.gcube.portlets.docxgenerator.transformer.Transformer;
import org.gcube.portlets.docxgenerator.treemodel.TreeNode;

import com.thoughtworks.xstream.XStream;


/**
 * Main class. Used to generate a docx Word 2007 compatible document.
 *  
 * @author Antonio Gioia
 *
 */
public class DocxGenerator {


	
	private WordprocessingMLPackage wmlPack = null;
	
	private List<BasicComponent> cleanModel = null;
	private final boolean instructions;
	private final boolean comments;
	private final String hostName;
	

	private static final Log log = LogFactory.getLog(DocxGenerator.class);
	
	/**
	 * DocxGenerator constructor. Assigns the inputModel.
	 * 
	 * @param model
	 *            The input path file of serialized model that should be
	 *             transformed to a docx Word 2007 document.
	 */
	public DocxGenerator(final String pathFileModel, final String hostName){
		this(readRawModel(pathFileModel),hostName);
		
	}
	
	/**
	 * DocxGenerator constructor. Assigns the inputModel.
	 * 
	 * @param model
	 *            The input {@link org.gcube.portlets.d4sreporting.common.shared.Model}
	 *            that should be transformed to a docx Word 2007 document.
	 */
	public DocxGenerator(final Model model, final String hostName) {
		this(model, hostName, true, true);
	}
	
	/**
	 * DocxGenerator constructor. Assigns the inputModel.
	 * 
	 * @param instructions. If <code>false</code> the instructions will be skipped. 
	 * @param comments. If <code>false</code> the comments will be skipped.
	 * @param model
	 *            The input {@link org.gcube.portlets.d4sreporting.common.shared.Model}
	 *            that should be transformed to a docx Word 2007 document.
	 */
	public DocxGenerator(final Model model, final String hostName, final boolean instructions,
			final boolean comments) {
		
		this.instructions = instructions;
		this.comments = comments;
		this.cleanModel = getCleanModel(model);
		this.hostName = hostName;
		exportInDocx(instructions, comments);
	}
	
	/**
	 * DocxGenerator constructor. Assigns the inputModel.
	 * 
	 * @param instructions. If <code>false</code> the instructions will be skipped. 
	 * @param comments. If <code>false</code> the comments will be skipped.
	 * @param model
	 *            The path file input {@link org.gcube.portlets.d4sreporting.common.shared.Model}
	 *            that should be transformed to a docx Word 2007 document.
	 */
	public DocxGenerator(final String pathFileModel,final String hostName, final boolean instructions,
			final boolean comments) {
		this(readRawModel(pathFileModel),hostName, instructions,comments);
	}

	/**
	 * Convert a {@link org.gcube.portlets.d4sreporting.common.shared.Model}
	 * in a docx temporary file
	 *  
	 * @return The generated Docx temporary file.
	 * @throws Exception
	 *             General exceptions in writing the output file.
	 */
	public File outputTmpFile() throws Exception {
		
		File file = File.createTempFile("Output", ".docx");
		wmlPack.save(file);
		
		return file;
	}
	
	/**
	 * Convert a {@link org.gcube.portlets.d4sreporting.common.shared.Model}
	 * in a docx file
	 *  
	 * @param filePath.
	 *  
	 * @return The generated Docx file.
	 * @throws Exception
	 *             General exceptions in writing the output file.
	 */
	public File outputFile(final String filePath) throws Exception {
		
		File file = new File(filePath);
		wmlPack.save(file);
		
		return file;
	}

	/**
	 * Convert the current instance to a HTML passing through the HTML
	 * 
	 * @return The generated HTML file.
	 * @throws Exception
	 *             General exceptions in handling the wmlPack.
	 */
	public File outputHTMLTmpFile() throws Exception {
		
		File temp = java.io.File.createTempFile("output", ".html");
		OutputStream os = new java.io.FileOutputStream(temp);
		
		HtmlSettings settings = new HtmlSettings();
		settings.setImageDirPath("");
		
		HtmlExporterNG2 exporter = new HtmlExporterNG2();
		exporter.setHtmlSettings(settings);
		exporter.setWmlPackage(wmlPack);
		
		javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(
				os);
		exporter.output(result);
		
		

		return temp;
		
	}

	/**
	 * Convert the current instance to a PDF temporary file
	 * 
	 * @return The generated PDF file.
	 * @throws Exception
	 *             General exceptions in handling the wmlPack.
	 */
	public File outputPDFTmpFile() throws Exception {
			
		File tempFO = java.io.File.createTempFile("output", ".fo");
		File tempPDF = File.createTempFile("output", ".pdf");
		
		// Set up font mapper
		Mapper fontMapper = new IdentityPlusMapper();
		wmlPack.setFontMapper(fontMapper);
		
		// Example of mapping missing font Algerian to installed font Comic Sans MS
		PhysicalFont font 
				= PhysicalFonts.getPhysicalFonts().get("Comic Sans MS");
		fontMapper.getFontMappings().put("Algerian", font);
		
		
	    PdfConversion c = new org.docx4j.convert.out.pdf.viaXSLFO.Conversion(wmlPack);
	    ((org.docx4j.convert.out.pdf.viaXSLFO.Conversion)c).setSaveFO(tempFO);
	    OutputStream os = new java.io.FileOutputStream(tempPDF);			
		c.output(os, new PdfSettings() );
	    
	    return tempPDF;
	}
	
	public File outputXMLTmpFile() throws Exception {
		
		XStream xstream = new XStream();
		
		File file = File.createTempFile("output", ".xml");
		OutputStream out = new FileOutputStream(file);
		xstream.toXML(cleanModel, out);
		out.close();
		return file;
	}
	
	
	private static Model readRawModel(final String pathToModel) {
		
		Model toConvert = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(pathToModel);
			in = new ObjectInputStream(fis);
			toConvert = (Model) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return toConvert;
	}
	
	private boolean contentIsEmpty(BasicComponent component) {
		
		Serializable content = component.getPossibleContent();
		switch (component.getType()) {
		case BODY: {
			return content.toString().equals("<br>");
		}
		case FLEX_TABLE: {
			Table t = (Table)content;
			ArrayList<ArrayList<TableCell>> cells = t.getTable();
			for(ArrayList<TableCell> row: cells) {
				for (TableCell cell: row) {
					if(!cell.getContent().isEmpty())
						return false;
				}
			}
			return true;
		}
		case DYNA_IMAGE: {
			return ((String)content).endsWith("image_placeholder.png");
		}
		default: {
			
			
			return content.toString().isEmpty();
		}
		}		
	}
	
	private TreeNode<BasicComponent> getRoot(TreeNode<BasicComponent> node, int level) {
	
		if (node.getParent() == null)
			return node;
		if ( node.getParent().getValue() == null)
			return node.getParent();
		
		int parentLevel = node.getParent().getValue().getType().ordinal();
		if (level > parentLevel )
			return node.getParent();
		else
			return getRoot(node.getParent(), level);
	}
	
	private TreeNode<BasicComponent> buildTreeModel(List<BasicComponent> components) {
		 
		TreeNode<BasicComponent> rootNode = new TreeNode<BasicComponent>(null);
		TreeNode<BasicComponent> rootNodeRef = rootNode;
		BasicComponent previousComponent = null;
		for (BasicComponent nextComponent : components) {
			
			if (contentIsEmpty(nextComponent))
				continue;
			
			TreeNode<BasicComponent> child = new TreeNode<BasicComponent>(nextComponent);
			switch (nextComponent.getType()) {			
			case HEADING_1: 
			case HEADING_2:	
			case HEADING_3:
			case HEADING_4:
			case HEADING_5: {
				if ((previousComponent != null) && nextComponent.getType().ordinal() <=
						previousComponent.getType().ordinal()){
					rootNodeRef = getRoot(rootNodeRef,
							child.getValue().getType().ordinal());
				}
				
				rootNodeRef.addChild(child);
				rootNodeRef = child;
				
				previousComponent = nextComponent;
				break;
			}
			default:
				rootNodeRef.addChild(child);
				break;
			}
		}
		return rootNode;
	}
	
	private void getTree(TreeNode<BasicComponent> root, List<BasicComponent> components) {
		for (TreeNode<BasicComponent> node : root.getChildren()){
			components.add(node.getValue());
			getTree(node, components);
		}
	}
	
	private List<BasicComponent> getCleanModel(final Model model) {
		
		List<BasicComponent> allComponents = new ArrayList<BasicComponent>();
		for (BasicSection section : model.getSections()) {
			for (BasicComponent component : section.getComponents()) {
				if ((component.getType() == ComponentType.REPEAT_SEQUENCE) ||
						(component.getType() == ComponentType.BODY_TABLE_IMAGE)) {
					allComponents.addAll(((RepeatableSequence)component.getPossibleContent()).getGroupedComponents());
				} else
					allComponents.add(component);
			}
		}
		
		TreeNode<BasicComponent> root = buildTreeModel(allComponents);
		TreeNode.purgeTree(root);
		List<BasicComponent> cleanComponents = new ArrayList<BasicComponent>();
		getTree(root, cleanComponents);
		return cleanComponents;
	}

	
	private boolean exportInDocx(final boolean instructions,
			final boolean comments) {	
		
		try {
		
			wmlPack = WordprocessingMLPackage.createPackage();
			
			// Add docProps
			DocPropsCorePart dpcp = new DocPropsCorePart();
			org.docx4j.docProps.core.ObjectFactory factory = new org.docx4j.docProps.core.ObjectFactory();
			CoreProperties cp = factory.createCoreProperties();
			org.docx4j.docProps.core.dc.elements.ObjectFactory dcElfactory = new org.docx4j.docProps.core.dc.elements.ObjectFactory();
	        SimpleLiteral literal = dcElfactory.createSimpleLiteral();
	        literal.getContent().add("17 FCP-NFSO.docx");
			cp.setTitle(dcElfactory.createTitle(literal));
			dpcp.setJaxbElement(cp);
			wmlPack.addTargetPart(dpcp);
			
			
			//Import foreign Styles Part
			StyleDefinitionsPart styledefPart = wmlPack.getMainDocumentPart().getStyleDefinitionsPart();
			Styles styles = createStyles();
			styledefPart.setPackage(wmlPack);
			styledefPart.setJaxbElement(styles);
			MainDocumentPart wordDocumentPart = wmlPack.getMainDocumentPart();
			wordDocumentPart.addTargetPart(styledefPart);
		} catch (Exception e) {
			log.error(e);
			return false;
		}
		
			
		List<BasicComponent> listComponent = cleanModel;
		for (int i = 0; i < listComponent.size(); i++) { 
			BasicComponent component1 = listComponent.get(i);				

			log.debug("FOUND: " + component1.getType() 
					+ " - " + component1.getPossibleContent() 
					+ " isDoubleCol" + component1.isDoubleColLayout());

			if (component1.getType() == ComponentType.COMMENT && !comments)
				continue;
			
			if (component1.getType() == ComponentType.INSTRUCTION && !instructions)
				continue;
			

			if(component1.isDoubleColLayout()) {
				BasicComponent component2 = listComponent.get(++i);
				convertDoubleCol(component1, component2, wmlPack);
			} else {
				convertSingleCol(component1, wmlPack);
			}

		}

		return true;
	}
	
	
	private Transformer chooseTransformer(BasicComponent component) {
		switch (component.getType()) {
		case TITLE:
		case HEADING_1:
		case HEADING_2:
		case HEADING_3:
		case HEADING_4:
		case HEADING_5:
		case INSTRUCTION:
		case COMMENT:
			return new HeadingTransformer();
		case BODY:
		case BODY_NOT_FORMATTED:	
			return new TextTransformer(hostName);
		case DYNA_IMAGE:
			return new ImageTransformer(hostName);
		case FLEX_TABLE:
			return new TableTransformer();
		case ATTRIBUTE:
		case ATTRIBUTE_MULTI:
		case ATTRIBUTE_UNIQUE:	
			return new AttributesTransformer();
		case TOC:
			return  new TOCTransformer();
		case PAGEBREAK:
			return new PageBreakTransformer();
		case TIME_SERIES:
			return new RTimeSeriesTransform();
		}
		return null;
	}
	
	private Styles createStyles() throws JAXBException, FileNotFoundException {
				
		InputStream is = getClass().getResourceAsStream("newstyles.xml");
		JAXBContext jc = Context.jc;
		Unmarshaller u = jc.createUnmarshaller();
		u.setEventHandler(new JaxbValidationEventHandler());
		Styles styles = (org.docx4j.wml.Styles) u.unmarshal(is);
		return styles;
	}
	
	private void convertSingleCol(final BasicComponent component, final WordprocessingMLPackage wmlPack) {
				
		Transformer transformer = chooseTransformer(component);
		if (transformer != null) {
			ArrayList<Content> listContent = transformer.transform(component, wmlPack);
			for(Content c: listContent)
				wmlPack.getMainDocumentPart().addObject(c.getContent());
		}
	}
	
	private void convertDoubleCol(final BasicComponent component1,
			final BasicComponent component2,final WordprocessingMLPackage wmlPack) {
	
		int writableWidthTwips = wmlPack.getDocumentModel().getSections().get(0).getPageDimensions().getWritableWidthTwips();
		int tableWidthTwips = new Double(Math.floor(writableWidthTwips)).intValue();
		TableContent tablecontent = new TableContent(tableWidthTwips);
		
		log.info("*************\n\n******** Double component........\n" + component1.getPossibleContent() + "|\n" + component1.getType()
					+ "......\n" + component2.getPossibleContent() + "\n|" + component2.getType()+"\n\n\n*****");

		Transformer transformer = chooseTransformer(component1);
		ArrayList<Content>	contents1 = transformer.transform(component1, wmlPack);
		
		Transformer transformer2 = chooseTransformer(component2);
		ArrayList<Content>	contents2 = transformer2.transform(component2, wmlPack);
		
		
		tablecontent.addRow();
		tablecontent.insertContent(contents1, contents2);
		
		wmlPack.getMainDocumentPart().addObject(tablecontent.getContent());
		// Added an empty P
		PContent p = new PContent();
		wmlPack.getMainDocumentPart().addObject(p.getContent());
		
	}
	

}
